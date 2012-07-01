package in.partake.controller.action.auth;

import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;

import org.junit.Test;

public class LogoutActionTest extends ActionControllerTest {

    @Test
    public void testWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/auth/logout");
        loginAs(proxy, DEFAULT_USER_ID);

        proxy.execute();
        assertLoggedOut(proxy);
        assertResultRedirect(proxy, "/");
   }

    @Test
    public void testWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/auth/logout");

        proxy.execute();
        assertLoggedOut(proxy);
        assertResultRedirect(proxy, "/");
    }
}
