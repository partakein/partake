package in.partake.controller.action.errorpage;

import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

public class StaticLoginRequiredPageTest extends ActionControllerTest {
    @Test
    public void testAccessWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/loginRequired");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultRedirect(proxy, "/");
   }

    @Test
    public void testAccessWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/loginRequired");

        proxy.execute();
        assertResultSuccess(proxy);
    }
}
