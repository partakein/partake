package in.partake.controller.action.calendar;

import in.partake.base.CalendarUtil;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.CalendarDAOFacade;
import in.partake.resource.ServerErrorCode;

import java.io.IOException;
import java.io.InputStream;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.commons.lang.StringUtils;

import play.mvc.Result;

public abstract class AbstractCalendarAction extends AbstractPartakeAction {

    protected Result showByCategory(String categoryName) throws DAOException, PartakeException {
        assert(!StringUtils.isEmpty(categoryName));

        InputStream is = new CalendarActionTransaction(categoryName).execute();
        return renderInlineStream(is, "text/calendar; charset=utf-8");
    }
}

class CalendarActionTransaction extends DBAccess<InputStream> {
    private String categoryName;

    CalendarActionTransaction(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    protected InputStream doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        try {
            Calendar calendar = CalendarUtil.createCalendarSkeleton();
            CalendarDAOFacade.addCalendarByCategoryName(con, daos, categoryName, calendar);
            return CalendarUtil.outputCalendar(calendar);
        } catch (IOException e) {
            throw new PartakeException(ServerErrorCode.CALENDAR_CREATION_FAILURE, e);
        } catch (ValidationException e) {
            throw new PartakeException(ServerErrorCode.CALENDAR_INVALID_FORMAT, e);
        }
    }
}
