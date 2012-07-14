package in.partake.app;

import static org.mockito.Mockito.mock;
import in.partake.model.dto.UserTwitterLink;
import in.partake.model.fixture.TestDataProvider;
import in.partake.service.ITestService;
import in.partake.service.ITwitterService;
import in.partake.service.impl.TestService;
import in.partake.session.TwitterLoginInformation;

import org.mockito.Mockito;

import twitter4j.TwitterException;

public class PartakeTestApp extends PartakeApp {
    protected ITestService testService;

    public static ITestService getTestService() {
        if (instance() instanceof PartakeTestApp)
            return ((PartakeTestApp) instance()).testService;

        return null;
    }

    public void createServices() throws Exception {
        super.createServices();
        testService = new TestService();
    }

    public void initializeOtherServices() throws Exception {
        super.initializeOtherServices();
        testService.initialize();
    }

    @Override
    protected ITwitterService createTwitterService() throws Exception {
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
}
