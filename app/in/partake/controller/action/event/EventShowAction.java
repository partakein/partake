package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.controller.base.permission.DraftEventEditPermission;
import in.partake.controller.base.permission.PrivateEventShowPermission;
import in.partake.model.EventCommentEx;
import in.partake.model.EventEx;
import in.partake.model.EventMessageEx;
import in.partake.model.EventTicketHolderList;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.UserTicketEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.daofacade.MessageDAOFacade;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.UserTicket;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import play.mvc.Result;

public class EventShowAction extends AbstractPartakeAction {
    private String eventId;

    private EventEx event;
    private boolean needsPasscode;
    private List<EventTicket> tickets;
    private Map<UUID, UserTicket> userTicketMap;
    private Map<UUID, EventTicketHolderList> ticketHolderListMap;
    private List<EventCommentEx> comments;
    private List<EventMessageEx> eventMessages;

    public static Result get(String eventId) throws DAOException, PartakeException {
        EventShowAction action = new EventShowAction();
        action.eventId = eventId;

        return action.execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        checkIdParameterIsValid(eventId, UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        // User might be logged in, might not be logged in. So using ensureLoginUser() is inappropriate.
        UserEx user = getLoginUser();

        EventShowTransaction transaction = new EventShowTransaction(user, eventId, session());
        transaction.execute();

        event = transaction.getEvent();
        if (event == null)
            return renderNotFound();

        if (transaction.isNeedsPasscode()) {
            this.event = null;
            return renderRedirect("/events/passcode?eventId=" + eventId);
        }

        this.tickets = transaction.getEventTickets();
        this.userTicketMap = transaction.getUserTicketMap();
        this.ticketHolderListMap = transaction.getTicketHolderListMap();
        this.comments = transaction.getComments();
        this.eventMessages = transaction.getEventMessages();

        return render(views.html.events.show.render(context(), event, user, tickets, userTicketMap, ticketHolderListMap, comments, eventMessages));
    }

    public EventEx getEvent() {
        return event;
    }

    public boolean isNeedsPasscode() {
        return needsPasscode;
    }

    public Map<UUID, EventTicketHolderList> getTicketHolderListMap() {
        return ticketHolderListMap;
    }

    public List<EventCommentEx> getComments() {
        return comments;
    }

    public List<EventMessageEx> getEventMessages() {
        return eventMessages;
    }

    public List<EventTicket> getTickets() {
        return tickets;
    }

    public Map<UUID, UserTicket> getUserTicketMap() {
        return this.userTicketMap;
    }
}

class EventShowTransaction extends DBAccess<Void> {
    private UserEx user;
    private String eventId;
    private Map<String, String> session; // TODO: Bad style...

    private EventEx event;
    private boolean needsPasscode;

    private Map<UUID, EventTicketHolderList> ticketHolderListMap;
    private Map<UUID, UserTicket> userTicketMap;

    private List<EventTicket> tickets;
    private List<EventCommentEx> comments;
    private List<EventMessageEx> eventMessages;

    public EventShowTransaction(UserEx user, String eventId, Map<String, String> session) {
        this.user = user;
        this.eventId = eventId;
        this.session = session;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        event = EventDAOFacade.getEventEx(con, daos, eventId);
        if (event == null)
            return null;

        if (event.isDraft()) {
            // If the event is draft, only owner can see it.
            if (user == null || !DraftEventEditPermission.check(event, user))
                throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);
        }

        if (!StringUtils.isBlank(event.getPasscode())) {
            // owner および manager は見ることが出来る。
            String passcode = (String) session.get("event:" + eventId);
            if (user != null && PrivateEventShowPermission.check(event, user)) {
                // OK. You have the right to show this event.
            } else if (StringUtils.equals(event.getPasscode(), passcode)) {
                // OK. The same passcode.
            } else {
                // public でなければ、passcode を入れなければ見ることが出来ない
                // We make this.event null for foolproof.
                this.needsPasscode = true;
                return null;
            }
        }

        tickets = daos.getEventTicketAccess().findEventTicketsByEventId(con, eventId);

        // ----- participants を反映
        ticketHolderListMap = new HashMap<UUID, EventTicketHolderList>();
        userTicketMap = new HashMap<UUID, UserTicket>();
        for (EventTicket ticket : tickets) {
            List<UserTicketEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, daos, ticket, event);
            if (participations == null)
                throw new PartakeException(ServerErrorCode.PARTICIPATIONS_RETRIEVAL_ERROR);

            ticketHolderListMap.put(ticket.getId(), ticket.calculateParticipationList(event, participations));
            if (user != null)
                userTicketMap.put(ticket.getId(), daos.getEnrollmentAccess().findByTicketIdAndUserId(con, ticket.getId(), user.getId()));
        }

        comments = EventDAOFacade.getCommentsExByEvent(con, daos, eventId);
        eventMessages = MessageDAOFacade.findEventMessageExs(con, daos, eventId, 0, 100);

        return null;
    }

    public EventEx getEvent() {
        return event;
    }

    public boolean isNeedsPasscode() {
        return needsPasscode;
    }

    public Map<UUID, EventTicketHolderList>getTicketHolderListMap() {
        return ticketHolderListMap;
    }

    public Map<UUID, UserTicket> getUserTicketMap() {
        return userTicketMap;
    }

    public List<EventCommentEx> getComments() {
        return comments;
    }

    public List<EventMessageEx> getEventMessages() {
        return eventMessages;
    }

    public List<EventTicket> getEventTickets() {
        return tickets;
    }
}
