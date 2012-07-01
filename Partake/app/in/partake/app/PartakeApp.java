package in.partake.app;

import in.partake.service.IDBService;
import in.partake.service.IEventSearchService;
import in.partake.service.IOpenIDService;
import in.partake.service.ITwitterService;

public class PartakeApp {
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
