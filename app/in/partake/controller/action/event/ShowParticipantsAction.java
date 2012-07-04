package in.partake.controller.action.event;

import in.partake.base.Pair;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.controller.base.permission.EventParticipationListPermission;
import in.partake.model.EventEx;
import in.partake.model.EventTicketHolderList;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.UserTicketEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.EventTicket;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.mvc.Result;
import scala.actors.threadpool.Arrays;

public class ShowParticipantsAction extends AbstractPartakeAction {
    private String eventId;

    private EventEx event;
    private List<Pair<EventTicket, EventTicketHolderList>> ticketAndHolders;
    private Map<String, List<String>> userTicketInfoMap;

    public static Result get(String eventId) throws DAOException, PartakeException {
        ShowParticipantsAction action = new ShowParticipantsAction();
        action.eventId = eventId;
        return action.execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        checkIdParameterIsValid(eventId, UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        ParticipantsListTransaction transaction = new ParticipantsListTransaction(user, eventId);
        transaction.execute();

        event = transaction.getEvent();
        ticketAndHolders = transaction.getTicketAndHolders();
        userTicketInfoMap = transaction.getUserTicketInfoMap();

        return render(views.html.events.participants.show.render(context(), event, ticketAndHolders, userTicketInfoMap));
    }

    public EventEx getEvent() {
        return event;
    }

    public List<Pair<EventTicket, EventTicketHolderList>> getTicketAndHolders() {
        return ticketAndHolders;
    }

    public Map<String, List<String>> getUserTicketInfoMap() {
        return userTicketInfoMap;
    }
}

class ParticipantsListTransaction extends DBAccess<Void> {
    private UserEx user;
    private String eventId;

    private EventEx event;
    private List<EventTicket> tickets;
    private List<Pair<EventTicket, EventTicketHolderList>> ticketAndHolders;
    private Map<String, List<String>> userTicketInfoMap;

    public ParticipantsListTransaction(UserEx user, String eventId) {
        this.user = user;
        this.eventId = eventId;
        this.ticketAndHolders = new ArrayList<Pair<EventTicket, EventTicketHolderList>>();
        this.userTicketInfoMap = new HashMap<String, List<String>>();
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        event = EventDAOFacade.getEventEx(con, daos, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_NOTFOUND);

        // Only owner can retrieve the participants list.
        if (!EventParticipationListPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);

        tickets = daos.getEventTicketAccess().findEventTicketsByEventId(con, eventId);
        for (int i = 0; i < tickets.size(); ++i) {
            EventTicket ticket = tickets.get(i);
            List<UserTicketEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, daos, ticket, event);
            EventTicketHolderList list = ticket.calculateParticipationList(event, participations);
            ticketAndHolders.add(new Pair<EventTicket, EventTicketHolderList>(ticket, list));

            for (UserTicketEx participation : list.getEnrolledParticipations()) {
                if (!userTicketInfoMap.containsKey(participation.getUserId())) {
                    userTicketInfoMap.put(participation.getUserId(), Arrays.asList(new String[tickets.size()]));
                }
                userTicketInfoMap.get(participation.getUserId()).set(i, participation.getStatus().toHumanReadableString(false));
            }
            for (UserTicketEx participation : list.getSpareParticipations()) {
                if (!userTicketInfoMap.containsKey(participation.getUserId()))
                    userTicketInfoMap.put(participation.getUserId(), Arrays.asList(new String[tickets.size()]));
                userTicketInfoMap.get(participation.getUserId()).set(i, participation.getStatus().toHumanReadableString(true));
            }
            for (UserTicketEx participation : list.getCancelledParticipations()) {
                if (!userTicketInfoMap.containsKey(participation.getUserId()))
                    userTicketInfoMap.put(participation.getUserId(), Arrays.asList(new String[tickets.size()]));
                userTicketInfoMap.get(participation.getUserId()).set(i, participation.getStatus().toHumanReadableString(false));
            }
        }

        return null;
    }

    public EventEx getEvent() {
        return event;
    }

    public List<Pair<EventTicket, EventTicketHolderList>> getTicketAndHolders() {
        return ticketAndHolders;
    }

    public Map<String, List<String>> getUserTicketInfoMap() {
        return userTicketInfoMap;
    }
}
