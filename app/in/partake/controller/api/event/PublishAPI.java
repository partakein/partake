package in.partake.controller.api.event;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.model.dto.EventTicket;
import in.partake.resource.UserErrorCode;
import in.partake.service.IEventSearchService;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.mvc.Result;

public class PublishAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new PublishAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        String eventId = getValidEventIdParameter();
        ensureValidSessionToken();

        PublishTransaction transaction = new PublishTransaction(user, eventId);
        Event event = transaction.execute();
        List<EventTicket> tickets = transaction.getTickets();

        IEventSearchService searchService = PartakeApp.getEventSearchService();
        if (!event.isSearchable())
            searchService.remove(event.getId());
        else
            searchService.create(event, tickets);

        return renderOK();
    }
}

class PublishTransaction extends Transaction<Event> {
    private UserEx user;
    private String eventId;
    private List<EventTicket> tickets;

    public PublishTransaction(UserEx user, String eventId) {
        this.user = user;
        this.eventId = eventId;
    }

    @Override
    protected Event doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        Event event = daos.getEventAccess().find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        if (!StringUtils.equals(event.getOwnerId(), user.getId()))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        if (!event.isDraft())
            throw new PartakeException(UserErrorCode.EVENT_ALREADY_PUBLISHED);

        event = new Event(event);
        event.setDraft(false);
        daos.getEventAccess().put(con, event);

        // Event Activity に挿入
        IEventActivityAccess eaa = daos.getEventActivityAccess();
        EventActivity activity = new EventActivity(eaa.getFreshId(con), event.getId(), "イベントが更新されました : " + event.getTitle(), event.getDescription(), event.getCreatedAt());
        eaa.put(con, activity);

        // さらに、twitter bot がつぶやく (private の場合はつぶやかない)
        if (event.isSearchable())
            EventDAOFacade.tweetNewEventArrival(con, daos, event);

        this.tickets = daos.getEventTicketAccess().findEventTicketsByEventId(con, eventId);

        return event;
    }

    public List<EventTicket> getTickets() {
        return this.tickets;
    }
}
