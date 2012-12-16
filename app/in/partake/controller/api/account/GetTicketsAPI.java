package in.partake.controller.api.account;

import in.partake.base.DateTime;
import in.partake.base.Pair;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
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
import java.util.Collections;
import java.util.Comparator;
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
        UserEx user = ensureLogin();

        String queryType = getParameter("queryType");
        if (queryType == null)
            queryType = "all";

        int offset = optIntegerParameter("offset", 0);
        offset = Util.ensureRange(offset, 0, Integer.MAX_VALUE);

        int limit = optIntegerParameter("limit", 10);
        limit = Util.ensureRange(limit, 0, 100);

        GetEnrollmentsTransaction transaction = new GetEnrollmentsTransaction(user.getId(), queryType, offset, limit);
        transaction.execute();

        ArrayNode statuses = new ArrayNode(JsonNodeFactory.instance);
        for (TicketAndStatus ticketAndStatus : transaction.getStatuses()) {
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
    private String queryType;
    private int offset;
    private int limit;

    private int numTotalTickets;
    private List<TicketAndStatus> statuses;

    public GetEnrollmentsTransaction(String userId, String queryType, int offset, int limit) {
        this.userId = userId;
        this.queryType = queryType;
        this.offset = offset;
        this.limit = limit;
        this.statuses = new ArrayList<TicketAndStatus>();
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        if ("upcoming".equalsIgnoreCase(queryType))
            return doUpcomingQuery(con, daos);
        else
            return doAllQuery(con, daos);
    }

    private Void doAllQuery(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IUserTicketAccess enrollmentAccess = daos.getEnrollmentAccess();
        List<UserTicket> enrollments = enrollmentAccess.findByUserId(con, userId, offset, limit);
        this.numTotalTickets = enrollmentAccess.countByUserId(con, userId);

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

    // TODO(mayah): too slow. Actually this should be done in DB.
    private Void doUpcomingQuery(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IUserTicketAccess dao = daos.getEnrollmentAccess();

        DateTime now = TimeUtil.getCurrentDateTime();

        List<UserTicket> rawTickets = dao.findByUserId(con, userId, 0, Integer.MAX_VALUE);
        List<Pair<UserTicket, Event>> filtered = new ArrayList<Pair<UserTicket, Event>>();

        for (UserTicket userTicket : rawTickets) {
            if (userTicket == null)
                continue;

            Event event = daos.getEventAccess().find(con, userTicket.getEventId());
            if (event == null)
                continue;

            if (now.isBefore(event.getBeginDate()))
                filtered.add(new Pair<UserTicket, Event>(userTicket, event));
        }

        // filtered を開始時刻順にソート
        Collections.sort(filtered, new Comparator<Pair<UserTicket, Event>>() {
            @Override
            public int compare(Pair<UserTicket, Event> o1, Pair<UserTicket, Event> o2) {
                long t1 = o1.getSecond().getBeginDate().getTime();
                long t2 = o2.getSecond().getBeginDate().getTime();
                if (t1 < t2)
                    return -1;
                if (t1 == t2)
                    return 0;
                return 1;
            }
        });

        for (int i = 0; i < limit && i + offset < filtered.size(); ++i) {
            UserTicket userTicket = filtered.get(i + offset).getFirst();
            Event event = filtered.get(i + offset).getSecond();

            EventTicket ticket = daos.getEventTicketAccess().find(con, userTicket.getTicketId());
            if (ticket == null)
                continue;

            CalculatedEnrollmentStatus calculatedEnrollmentStatus = EnrollmentDAOFacade.calculateEnrollmentStatus(con, daos, userId, ticket, event);
            TicketAndStatus status = new TicketAndStatus(ticket, event, calculatedEnrollmentStatus);
            statuses.add(status);
        }

        return null;
    }


    public int getTotalTicketCount() {
        return numTotalTickets;
    }

    public List<TicketAndStatus> getStatuses() {
        return this.statuses;
    }
}
