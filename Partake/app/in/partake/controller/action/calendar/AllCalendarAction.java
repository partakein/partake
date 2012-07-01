package in.partake.controller.action.calendar;

import in.partake.base.PartakeException;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.EventCategory;
import play.mvc.Result;


public class AllCalendarAction extends AbstractCalendarAction {

    public static Result get() throws DAOException, PartakeException {
        return new AllCalendarAction().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        return showByCategory(EventCategory.getAllEventCategory());
    }
}
