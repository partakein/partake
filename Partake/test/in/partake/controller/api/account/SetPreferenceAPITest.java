package in.partake.controller.api.account;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.UserPreference;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;
import junit.framework.Assert;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class SetPreferenceAPITest extends APIControllerTest {
    @Test
    public void testToSetPreferenceWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/account/setPreference");

        UserPreference pref = loadUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(false, pref.tweetsAttendanceAutomatically());

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        addValidSessionTokenToParameter(proxy);
        addParameter(proxy, "profilePublic", "false");
        addParameter(proxy, "receivingTwitterMessage", "false");
        addParameter(proxy, "tweetingAttendanceAutomatically", "false");
        proxy.execute();

        assertResultOK(proxy);

        pref = loadUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(false, pref.isProfilePublic());
        Assert.assertEquals(false, pref.isReceivingTwitterMessage());
        Assert.assertEquals(false, pref.tweetsAttendanceAutomatically());
    }

    @Test
    public void testToSetPreferenceWithLoginWithoutPreference() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/account/setPreference");
        loginAs(proxy, TestDataProvider.USER_WITHOUT_PREF_ID);

        addValidSessionTokenToParameter(proxy);
        addParameter(proxy, "profilePublic", "false");
        addParameter(proxy, "receivingTwitterMessage", "false");
        addParameter(proxy, "tweetingAttendanceAutomatically", "false");
        proxy.execute();

        assertResultOK(proxy);

        UserPreference pref = loadUserPreference(TestDataProvider.USER_WITHOUT_PREF_ID);
        Assert.assertEquals(false, pref.isProfilePublic());
        Assert.assertEquals(false, pref.isReceivingTwitterMessage());
        Assert.assertEquals(false, pref.tweetsAttendanceAutomatically());
    }

    @Test
    public void testToSetPreferenceWithLoginWithoutArgument() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/account/setPreference");

        UserPreference pref = loadUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(false, pref.tweetsAttendanceAutomatically());

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        addValidSessionTokenToParameter(proxy);
        proxy.execute();

        assertResultOK(proxy);

        pref = loadUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(false, pref.tweetsAttendanceAutomatically());
    }

    @Test
    public void testToSetPreferenceWithLoginWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/account/setPreference");

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        addInvalidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

    @Test
    public void testToSetPreferenceWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/account/setPreference");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }
}
