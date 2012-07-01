package in.partake.controller.action.event;

import in.partake.base.KeyValuePair;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.service.EventSortOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import play.mvc.Result;

public class EventSearchAction extends AbstractPartakeAction {
    public static final List<KeyValuePair> CATEGORIES_FOR_SEARCH;
    static {
        List<KeyValuePair> categories = new ArrayList<KeyValuePair>();
        categories.add(new KeyValuePair(EventCategory.getAllEventCategory(), "全て"));
        categories.addAll(EventCategory.getCategories());
        CATEGORIES_FOR_SEARCH = Collections.unmodifiableList(categories);
    }

    public static Result get() throws DAOException, PartakeException {
        return new EventSearchAction().execute();
    }

    @Override
    protected Result doExecute() throws PartakeException, DAOException {
        return render(views.html.events.search.render(context()));
    }

    // ----------------------------------------------------------------------

    public List<KeyValuePair> getCategories() {
        return CATEGORIES_FOR_SEARCH;
    }

    public List<KeyValuePair> getSortOrders() {
        return EventSortOrder.getSortOrders();
    }
}
