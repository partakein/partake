package in.partake.app.impl;

import in.partake.app.IPartakeAppInitializer;
import in.partake.app.PartakeApp;
import in.partake.daemon.PartakeDaemon;
import in.partake.daemon.impl.DaemonInitializer;
import in.partake.resource.PartakeProperties;
import in.partake.service.IDBService;
import in.partake.service.IDaemonInitializer;
import in.partake.service.IEventSearchService;
import in.partake.service.IOpenIDService;
import in.partake.service.ITwitterService;
import in.partake.service.impl.EventSearchService;
import in.partake.service.impl.OpenIDService;
import in.partake.service.impl.Postgres9DBService;
import in.partake.service.impl.TwitterService;

public class ReleasePartakeAppInitializer extends PartakeApp implements IPartakeAppInitializer {

    @Override
    public void initialize() throws Exception {
        PartakeProperties.get().reset("release");

        dbService = createDBService();
        eventSearchService = createEventSearchService();
        twitterService = createTwitterService();
        openIDService = createOpenIDService();

        IDaemonInitializer daemonInitializer = createDaemonInitializer();

        if (dbService != null)
            dbService.initialize();
        if (eventSearchService != null)
            eventSearchService.initialize();
        if (daemonInitializer != null)
            daemonInitializer.initialize();

        PartakeDaemon.getInstance().schedule();
    }

    public void cleanUp() throws Exception {
        PartakeDaemon.getInstance().cancel();

        if (eventSearchService != null)
            eventSearchService.cleanUp();
    }

    private IDBService createDBService() throws Exception{
        return new Postgres9DBService();
    }

    private IEventSearchService createEventSearchService() throws Exception {
        return new EventSearchService();
    }

    private ITwitterService createTwitterService() throws Exception{
        return new TwitterService();
    }

    private IOpenIDService createOpenIDService() throws Exception {
        return new OpenIDService();
    }

    private IDaemonInitializer createDaemonInitializer() throws Exception {
        return new DaemonInitializer();
    }
}
