package in.partake.app;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.service.IDBService;
import in.partake.service.IEventSearchService;
import in.partake.service.IOpenIDService;
import in.partake.service.ITwitterService;

public class PartakeApp {
    public static final DateTime initializedAt = TimeUtil.getCurrentDateTime();

    protected static IDBService dbService;
    protected static IEventSearchService eventSearchService;
    protected static ITwitterService twitterService;
    protected static IOpenIDService openIDService;

    public static IDBService getDBService() {
        return dbService;
    }

    public static IEventSearchService getEventSearchService() {
        return eventSearchService;
    }

    public static ITwitterService getTwitterService() {
        return twitterService;
    }

    public static IOpenIDService getOpenIDService() {
        return openIDService;
    }
}
