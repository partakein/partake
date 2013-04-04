package in.partake.controller.api.event;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.resource.UserErrorCode;
import in.partake.service.IEventSearchService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import play.mvc.Result;

// search API can take
//  1) query (String)
//  2) category (String)
//  3) beforeDeadlineOnly (Boolean)
//  4) sortOrder (String)
//  5) maxNum (integer)

public class SearchAPI extends AbstractPartakeAPI {
    private static final String DEFAULT_CATEGORY = EventCategory.getAllEventCategory();
    private static final boolean DEFAULT_BEFORE_DEADLINE_ONLY = true;
    private static final String DEFAULT_SORT_ORDER = "score";
    private static final int DEFAULT_MAX_NUM = 10;
    public static final int MAX_NUM = 100;

    public static Result get() throws DAOException, PartakeException {
        return new SearchAPI().execute();
    }

    public static Result post() throws DAOException, PartakeException {
        return new SearchAPI().execute();
    }

    public Result doExecute() throws DAOException, PartakeException {
        String query = getQuery();
        String category = getCategory();

        String sortOrder = getSortOrder();
        if (sortOrder == null)
            return renderInvalid(UserErrorCode.MISSING_SEARCH_ORDER);

        boolean beforeDeadlineOnly =
                optBooleanParameter("beforeDeadlineOnly", DEFAULT_BEFORE_DEADLINE_ONLY);

        int offset = optIntegerParameter("offset", 0);
        if (offset < 0)
            return renderInvalid(UserErrorCode.INVALID_ARGUMENT);

        int maxNum = optIntegerParameter("maxNum", DEFAULT_MAX_NUM);
        maxNum = Util.ensureRange(maxNum, 0, MAX_NUM);
        if (maxNum <= 0)
            return renderInvalid(UserErrorCode.INVALID_ARGUMENT);

        List<Event> events = new SearchTransaction(query, category, sortOrder, beforeDeadlineOnly, offset, maxNum).execute();

        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode jsonEventsArray = obj.putArray("events");
        for (Event event : events) {
            jsonEventsArray.add(event.toSafeJSON());
        }

        return renderOK(obj);
    }

    private String getQuery() {
        String query = getParameter("query");
        return StringUtils.trimToEmpty(query);
    }

    private String getCategory() {
        String category = getParameter("category");
        if (category == null)
            return DEFAULT_CATEGORY;

        category = category.trim();
        if (EventCategory.getAllEventCategory().equals(category) || EventCategory.isValidCategoryName(category))
            return category;
        else
            return DEFAULT_CATEGORY;
    }

    private String getSortOrder() {
        String sortOrder = getParameter("sortOrder");
        if (sortOrder == null)
            return DEFAULT_SORT_ORDER;

        sortOrder = sortOrder.trim();
        if ("score".equalsIgnoreCase(sortOrder))       { return "score"; }
        if ("createdAt".equalsIgnoreCase(sortOrder))   { return "createdAt"; }
        if ("deadline".equalsIgnoreCase(sortOrder))    { return "deadline"; }
        if ("deadline-r".equalsIgnoreCase(sortOrder))  { return "deadline-r"; }
        if ("beginDate".equalsIgnoreCase(sortOrder))   { return "beginDate"; }
        if ("beginDate-r".equalsIgnoreCase(sortOrder)) { return "beginDate-r"; }

        return DEFAULT_SORT_ORDER;
    }
}

class SearchTransaction extends DBAccess<List<Event>> {
    private String query;
    private String category;
    private String sortOrder;
    private boolean beforeDeadlineOnly;
    private int offset;
    private int maxNum;

    public SearchTransaction(String query, String category, String sortOrder, boolean beforeDeadlineOnly, int offset, int maxNum) {
        this.query = query;
        this.category = category;
        this.sortOrder = sortOrder;
        this.beforeDeadlineOnly = beforeDeadlineOnly;
        this.offset = offset;
        this.maxNum = maxNum;
    }

    @Override
    protected List<Event> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IEventSearchService searchService = PartakeApp.getEventSearchService();

        List<String> eventIds = searchService.search(query, category, sortOrder, beforeDeadlineOnly, offset, maxNum);
        List<Event> events = new ArrayList<Event>();

        for (String eventId : eventIds) {
            Event event = daos.getEventAccess().find(con, eventId);
            if (event != null && event.isSearchable())
                events.add(event);
        }

        return events;
    }
}
