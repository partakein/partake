package in.partake.controller.action.calendar;

import in.partake.base.CalendarUtil;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.User;
import in.partake.model.dto.UserCalendarLink;
import in.partake.model.dto.UserTicket;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.IOException;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import play.mvc.Result;

public class ShowCalendarAction extends AbstractCalendarAction {
    private final String calendarId;

    private ShowCalendarAction(String calendarId) {
        this.calendarId = calendarId;
    }

    public static Result get(String calendarId) throws DAOException, PartakeException {
        return new ShowCalendarAction(calendarId).execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        checkIdParameterIsValid(calendarId, UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        // TODO: CalendarLinkage should have cache. Maybe ShowCalendarTransaction should return
        // InputStream instead of Calendar?
        Calendar calendar = new ShowCalendarTransaction(calendarId).execute();
        try {
            byte[] body = CalendarUtil.outputCalendar(calendar);
            return render(body, "text/calendar; charset=utf-8", "inline");
        } catch (IOException e) {
            return renderError(ServerErrorCode.CALENDAR_CREATION_FAILURE, e);
        } catch (ValidationException e) {
            return renderError(ServerErrorCode.CALENDAR_INVALID_FORMAT, e);
        }
    }
}

class ShowCalendarTransaction extends DBAccess<Calendar> {
    private String calendarId;

    public ShowCalendarTransaction(String calendarId) {
        this.calendarId = calendarId;
    }

    @Override
    protected Calendar doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        // CalendarLinkage should have cache.
        UserCalendarLink calendarLinkage = daos.getCalendarAccess().find(con, calendarId);
        if (calendarLinkage == null)
            throw new PartakeException(UserErrorCode.INVALID_NOTFOUND);

        User user = daos.getUserAccess().find(con, calendarLinkage.getUserId());
        if (user == null)
            throw new PartakeException(UserErrorCode.INVALID_NOTFOUND);

        Calendar calendar = CalendarUtil.createCalendarSkeleton();

        // TODO: We only consider the first 1000 entries of enrollments due to memory limit.
        List<UserTicket> enrollments =
                daos.getEnrollmentAccess().findByUserId(con, user.getId(), 0, 1000);
        for (UserTicket enrollment : enrollments) {
            // TODO: Event should be search-able by ticket-id.
            EventTicket ticket = daos.getEventTicketAccess().find(con, enrollment.getTicketId());
            Event event = daos.getEventAccess().find(con, ticket.getEventId());
            if (event == null)
                continue;
            CalendarUtil.addToCalendar(calendar, event);
        }

        return calendar;
    }
}
