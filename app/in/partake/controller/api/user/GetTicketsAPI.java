package in.partake.controller.api.user;

import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTicketAccess;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.auxiliary.CalculatedEnrollmentStatus;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import play.mvc.Result;

class TicketAndStatus {
    public EventTicket ticket;
    public Event event;
    public CalculatedEnrollmentStatus status;

    public TicketAndStatus(EventTicket ticket, Event event, CalculatedEnrollmentStatus status) {
        this.ticket = ticket;
        this.event = event;
        this.status = status;
    }
}

public class GetTicketsAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new GetTicketsAPI().execute();
    }

    @Override
    public Result doExecute() throws DAOException, PartakeException {
        String userId = getValidUserIdParameter();

        int offset = optIntegerParameter("offset", 0);
        offset = Util.ensureRange(offset, 0, Integer.MAX_VALUE);

        int limit = optIntegerParameter("limit", 10);
        limit = Util.ensureRange(limit, 0, 100);

        GetEnrollmentsTransaction transaction = new GetEnrollmentsTransaction(userId, offset, limit);
        transaction.execute();

        ArrayNode statuses = new ArrayNode(JsonNodeFactory.instance);
        for (TicketAndStatus ticketAndStatus : transaction.getStatuses()) {
            // TODO: We should consider how to join EventEntities and EnrollmentEntities.
            // If the event is private (or draft), its information does not be fed.
            // However, we show the existence of the event now.
            if (ticketAndStatus.event == null || !ticketAndStatus.event.isSearchable())
                continue;

            ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
            obj.put("ticket", ticketAndStatus.ticket.toSafeJSON());
            obj.put("event", ticketAndStatus.event.toSafeJSON());
            obj.put("status", ticketAndStatus.status.toString());
            statuses.add(obj);
        }

        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("totalTicketCount", transaction.getTotalTicketCount());
        obj.put("ticketStatuses", statuses);

        return renderOK(obj);
    }
}

class GetEnrollmentsTransaction extends DBAccess<Void> {
    private String userId;
    private int offset;
    private int limit;

    private int totalTicketCount;
    private List<TicketAndStatus> statuses;

    public GetEnrollmentsTransaction(String userId, int offset, int limit) {
        this.userId = userId;
        this.offset = offset;
        this.limit = limit;
        this.statuses = new ArrayList<TicketAndStatus>();
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IUserTicketAccess enrollmentAccess = daos.getEnrollmentAccess();
        List<UserTicket> enrollments = enrollmentAccess.findByUserId(con, userId, offset, limit);

        this.totalTicketCount = enrollmentAccess.countByUserId(con, userId);

        for (UserTicket enrollment : enrollments) {
            if (enrollment == null)
                continue;

            Event event = daos.getEventAccess().find(con, enrollment.getEventId());
            if (event == null)
                continue;

            EventTicket ticket = daos.getEventTicketAccess().find(con, enrollment.getTicketId());
            if (ticket == null)
                continue;

            CalculatedEnrollmentStatus calculatedEnrollmentStatus = EnrollmentDAOFacade.calculateEnrollmentStatus(con, daos, userId, ticket, event);
            TicketAndStatus status = new TicketAndStatus(ticket, event, calculatedEnrollmentStatus);
            statuses.add(status);
        }

        return null;
    }

    public int getTotalTicketCount() {
        return totalTicketCount;
    }

    public List<TicketAndStatus> getStatuses() {
        return this.statuses;
    }
}

