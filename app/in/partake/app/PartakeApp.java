package in.partake.app;

import java.util.UUID;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.daemon.PartakeDaemon;
import in.partake.daemon.impl.DaemonInitializer;
import in.partake.service.IDBService;
import in.partake.service.IDaemonInitializer;
import in.partake.service.IEventSearchService;
import in.partake.service.IOpenIDService;
import in.partake.service.ITwitterService;
import in.partake.service.impl.EventSearchService;
import in.partake.service.impl.OpenIDService;
import in.partake.service.impl.Postgres9DBService;
import in.partake.service.impl.TwitterService;
import play.Configuration;

public class PartakeApp {
    public static final DateTime initializedAt = TimeUtil.getCurrentDateTime();

    private static PartakeApp instance;

    private IDBService dbService;
    private IEventSearchService eventSearchService;
    private ITwitterService twitterService;
    private IOpenIDService openIDService;
    private IDaemonInitializer daemonInitializer;

    public static void setInstance(PartakeApp instance) {
        PartakeApp.instance = instance;
    }

    public static PartakeApp instance() {
        return instance;
    }

    public static boolean isTestMode() {
        return instance.isTestModeImpl();
    }

    public static IDBService getDBService() {
        return instance.dbService;
    }

    public static IEventSearchService getEventSearchService() {
        return instance.eventSearchService;
    }

    public static ITwitterService getTwitterService() {
        return instance.twitterService;
    }

    public static IOpenIDService getOpenIDService() {
        return instance.openIDService;
    }

    public void createServices() throws Exception {
        dbService = createDBService();
        eventSearchService = createEventSearchService();
        twitterService = createTwitterService();
        openIDService = createOpenIDService();
        daemonInitializer = createDaemonInitializer();
    }

    public void initializeDBService() throws Exception {
        if (dbService != null)
            dbService.initialize();
    }

    public void loadConfiguration(Configuration conf) throws Exception {
        PartakeConfiguration.loadFromDB();
        PartakeConfiguration.set(conf);
    }

    public void initializeOtherServices() throws Exception {
        if (twitterService != null)
            twitterService.initialize();
        if (eventSearchService != null)
            eventSearchService.initialize();

        if (PartakeConfiguration.isTwitterDaemonEnabled()) {
            if (daemonInitializer != null)
                daemonInitializer.initialize();
            PartakeDaemon.getInstance().schedule();
        }
    }

    public void cleanUp() throws Exception {
        if (PartakeConfiguration.isTwitterDaemonEnabled())
            PartakeDaemon.getInstance().cancel();

        if (eventSearchService != null)
            eventSearchService.cleanUp();
    }

    public void reinitializeTwitterService() throws Exception {
        twitterService = createTwitterService();
    }

    protected IDBService createDBService() throws Exception{
        return new Postgres9DBService();
    }

    protected IEventSearchService createEventSearchService() throws Exception {
        return new EventSearchService();
    }

    protected ITwitterService createTwitterService() throws Exception{
        return new TwitterService();
    }

    protected IOpenIDService createOpenIDService() throws Exception {
        return new OpenIDService();
    }

    protected IDaemonInitializer createDaemonInitializer() throws Exception {
        return new DaemonInitializer();
    }

    protected boolean isTestModeImpl() {
        return false;
    }
}
