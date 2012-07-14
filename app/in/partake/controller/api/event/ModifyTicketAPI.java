package in.partake.controller.api.event;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventTicketAccess;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.TicketAmountType;
import in.partake.model.dto.auxiliary.TicketApplicationEnd;
import in.partake.model.dto.auxiliary.TicketApplicationStart;
import in.partake.model.dto.auxiliary.TicketPriceType;
import in.partake.model.dto.auxiliary.TicketReservationEnd;
import in.partake.resource.UserErrorCode;
import in.partake.service.IEventSearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import play.mvc.Result;

public class ModifyTicketAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new ModifyTicketAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();

        String[] ids = getParameters("id[]");
        int N = ids != null ? ids.length : 0;

        String[] names = ensureParameters("name[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] startDateTypes = ensureParameters("applicationStart[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] startDateDays = ensureParameters("applicationStartDayBeforeEvent[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] customStartDates = ensureParameters("customApplicationStartDate[]", N, UserErrorCode.INVALID_ARGUMENT);

        String[] endDateTypes = ensureParameters("applicationEnd[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] endDateDays = ensureParameters("applicationEndDayBeforeEvent[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] customEndDates = ensureParameters("customApplicationEndDate[]", N, UserErrorCode.INVALID_ARGUMENT);

        String[] reservationEndDateTypes = ensureParameters("reservationEnd[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] reservationEndHourBeforeApplications = ensureParameters("reservationEndHourBeforeApplication[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] customReservationEndDates = ensureParameters("customReservationEndDate[]", N, UserErrorCode.INVALID_ARGUMENT);

        String[] priceTypes = ensureParameters("priceType[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] prices = ensureParameters("price[]", N, UserErrorCode.INVALID_ARGUMENT);

        String[] amountTypes = ensureParameters("amountType[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] amounts = ensureParameters("amount[]", N, UserErrorCode.INVALID_ARGUMENT);

        List<EventTicket> tickets = new ArrayList<EventTicket>();
        try {
            for (int i = 0; i < N; ++i) {
                if (!StringUtils.isBlank(ids[i]) && !Util.isUUID(ids[i]))
                    return renderInvalid(UserErrorCode.INVALID_ARGUMENT);
                UUID id = StringUtils.isBlank(ids[i]) ? null : UUID.fromString(ids[i]);
                EventTicket ticket = new EventTicket(id, eventId, i, names[i],
                        TicketApplicationStart.safeValueOf(startDateTypes[i]), Integer.parseInt(startDateDays[i]), TimeUtil.parseForEvent(customStartDates[i]),
                        TicketApplicationEnd.safeValueOf(endDateTypes[i]), Integer.parseInt(endDateDays[i]), TimeUtil.parseForEvent(customEndDates[i]),
                        TicketReservationEnd.safeValueOf(reservationEndDateTypes[i]), Integer.parseInt(reservationEndHourBeforeApplications[i]), TimeUtil.parseForEvent(customReservationEndDates[i]),
                        TicketPriceType.safeValueOf(priceTypes[i]), Integer.parseInt(prices[i]),
                        TicketAmountType.safeValueOf(amountTypes[i]), Integer.parseInt(amounts[i]),
                        TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime());

                if (!ticket.validate())
                    return renderInvalid(UserErrorCode.INVALID_ARGUMENT);

                tickets.add(ticket);
            }
        } catch (NumberFormatException e) {
            return renderInvalid(UserErrorCode.INVALID_ARGUMENT);
        }

        ModifyTicketTransaction transaction = new ModifyTicketTransaction(user, eventId, tickets);
        transaction.execute();

        Event event = transaction.getEvent();
        IEventSearchService searchService = PartakeApp.getEventSearchService();
        if (!event.isSearchable())
            searchService.remove(eventId);
        else if (searchService.hasIndexed(eventId))
            searchService.update(event, tickets);
        else
            searchService.create(event, tickets);

        return renderOK();
    }
}

class ModifyTicketTransaction extends Transaction<Void> {
    private UserEx user;
    private String eventId;
    private Event event;
    private List<EventTicket> tickets;

    public ModifyTicketTransaction(UserEx user, String eventId, List<EventTicket> tickets) {
        this.user = user;
        this.eventId = eventId;
        this.tickets = tickets;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        event = daos.getEventAccess().find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);
        if (!EventEditPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        modifyTickets(con, daos, event.isDraft());
        return null;
    }

    private void modifyTickets(PartakeConnection con, IPartakeDAOs daos, boolean forDraft) throws DAOException, PartakeException {
        IEventTicketAccess dao = daos.getEventTicketAccess();
        List<EventTicket> originalTickets = dao.findEventTicketsByEventId(con, eventId);
        boolean[] processed = new boolean[originalTickets.size()];

        // |tickets| should contain all the original ticket.
        for (EventTicket ticket : tickets) {
            EventTicket originalTicket = null;

            for (int i = 0; i < originalTickets.size(); ++i) {
                if (!originalTickets.get(i).getId().equals(ticket.getId()))
                    continue;
                if (processed[i])
                    throw new PartakeException(UserErrorCode.INVALID_TICKET_DUPLICATE_ID);

                // Found the original ticket.
                processed[i] = true;
                originalTicket = originalTickets.get(i);
                break;
            }

            if (originalTicket == null) {
                // If new ticket has id, it's strange. Otherwise, add a new ticket id.
                if (ticket.getId() != null)
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS);
                ticket.setId(daos.getEventTicketAccess().getFreshId(con));
            }
        }

        // If the event has already been published, all ticket should be preserved.
        // However, it's OK to remove the ticket that no participants.
        if (!forDraft) {
            for (int i = 0; i < processed.length; ++i) {
                UUID eventTicketId = originalTickets.get(i).getId();
                if (!processed[i] && daos.getEnrollmentAccess().countByTicketId(con, eventTicketId) > 0)
                    throw new PartakeException(UserErrorCode.INVALID_TICKET_REMOVAL_ENROLLED);
            }
        }

        // OK. let's save the tickets.
        dao.removeByEventId(con, eventId);
        for (EventTicket ticket : tickets)
            dao.put(con, ticket);
    }

    public Event getEvent() {
        return event;
    }
}
