package in.partake.controller.action.auth;

import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;
import in.partake.resource.ServerErrorCode;

import org.junit.Test;

public class LoginByTwitterActionTest extends ActionControllerTest {
    @Test
    public void testLoginWithOAuthError() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/auth/loginByTwitter");

        addParameter(proxy, "redirectURL", "http://www.example.com/throwException");
        proxy.execute();

        assertResultError(proxy, ServerErrorCode.TWITTER_OAUTH_ERROR);
    }

}
