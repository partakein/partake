package in.partake.controller.action.calendar;

import in.partake.base.PartakeException;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.EventCategory;

import org.apache.commons.lang.StringUtils;

import play.mvc.Result;


public class CategoryCalendarAction extends AbstractCalendarAction {
    private String categoryName;

    public static Result get(String categoryName) throws DAOException, PartakeException {
        CategoryCalendarAction action = new CategoryCalendarAction();
        action.categoryName = categoryName;
        return action.execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        String categoryName = getParameter("category");
        if (StringUtils.isEmpty(categoryName))
            return renderNotFound();

        if (!EventCategory.isValidCategoryName(categoryName))
            return renderNotFound();

        return showByCategory(categoryName);
    }


}
