package in.partake.controller.api.account;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;
import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import in.partake.controller.ActionProxy;

public class SessionAPITest extends APIControllerTest {
    private final String SESSION_TOKEN_PATH = "/api/account/sessionToken";

    @Test
    public void testToGetSessionTokenWithoutLogin() throws Exception {
        // Even if not logged in, the token session should be available.
        ActionProxy proxy = getActionProxy(GET, SESSION_TOKEN_PATH);

        proxy.execute();
        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);
        Assert.assertNotNull(obj.get("token"));
    }

    @Test
    public void testToGetSessionTokenWithLogin() throws Exception {
        // If logged in, the token session should be available also.
        ActionProxy proxy = getActionProxy(GET, SESSION_TOKEN_PATH);

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);
        Assert.assertNotNull(obj.get("token"));
    }

}
