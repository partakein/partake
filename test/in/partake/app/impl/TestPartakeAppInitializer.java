package in.partake.app.impl;

import static org.mockito.Mockito.mock;
import in.partake.app.IPartakeAppInitializer;
import in.partake.app.PartakeTestApp;
import in.partake.daemon.impl.DaemonInitializer;
import in.partake.model.dto.UserTwitterLink;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.PartakeProperties;
import in.partake.service.IDBService;
import in.partake.service.IDaemonInitializer;
import in.partake.service.IEventSearchService;
import in.partake.service.IOpenIDService;
import in.partake.service.ITestService;
import in.partake.service.ITwitterService;
import in.partake.service.impl.EventSearchService;
import in.partake.service.impl.OpenIDService;
import in.partake.service.impl.Postgres9DBService;
import in.partake.service.impl.TestService;
import in.partake.session.TwitterLoginInformation;

import org.mockito.Mockito;

import twitter4j.TwitterException;

public class TestPartakeAppInitializer extends PartakeTestApp implements IPartakeAppInitializer {

    @Override
    public void initialize() throws Exception {
        PartakeProperties.get().reset("test");

        dbService = createDBService();
        eventSearchService = createEventSearchService();
        twitterService = createTwitterService();
        openIDService = createOpenIDService();
        testService = createTestService();

        IDaemonInitializer daemonInitializer = createDaemonInitializer();

        if (dbService != null)
            dbService.initialize();
        if (daemonInitializer != null)
            daemonInitializer.initialize();

        try {
            if (testService != null)
                testService.initialize();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void cleanUp() throws Exception {
        if (eventSearchService != null)
            eventSearchService.cleanUp();
    }

    private IDBService createDBService() throws Exception{
        return new Postgres9DBService();
    }

    private IEventSearchService createEventSearchService() throws Exception {
        return new EventSearchService();
    }

    private ITwitterService createTwitterService() throws Exception {
        ITwitterService twitterService = mock(ITwitterService.class);

        TwitterLoginInformation mockInfo = mock(TwitterLoginInformation.class);
        Mockito.doReturn(mockInfo).when(twitterService).createLoginInformation(Mockito.anyString());
        Mockito.doReturn(mockInfo).when(twitterService).createLoginInformation(null);
        Mockito.doThrow(new TwitterException("MockException")).when(twitterService).createLoginInformation("http://www.example.com/throwException");

        UserTwitterLink twitterLinkage = new UserTwitterLink(
                TestDataProvider.DEFAULT_TWITTER_LINK_ID, TestDataProvider.DEFAULT_TWITTER_ID, TestDataProvider.DEFAULT_USER_ID,
                TestDataProvider.DEFAULT_TWITTER_SCREENNAME, "testUser 1", "accessToken", "accessTokenSecret", "http://www.example.com/");

        Mockito.doReturn(twitterLinkage).when(twitterService).createTwitterLinkageFromLoginInformation((TwitterLoginInformation) Mockito.any(), Mockito.anyString());


        Mockito.doReturn("http://www.example.com/validAuthenticationURL").when(mockInfo).getAuthenticationURL();

        return twitterService;
    }

    private IOpenIDService createOpenIDService() throws Exception {
        return new OpenIDService();
    }

    private ITestService createTestService() throws Exception {
        return new TestService();
    }

    private IDaemonInitializer createDaemonInitializer() throws Exception {
        return new DaemonInitializer();
    }
}
