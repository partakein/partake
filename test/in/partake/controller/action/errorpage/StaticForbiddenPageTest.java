package in.partake.controller.action.errorpage;

import in.partake.controller.action.ActionControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class StaticForbiddenPageTest extends ActionControllerTest {
    @Test
    public void testAccessWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/prohibited");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);
   }

    @Test
    public void testAccessWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/prohibited");

        proxy.execute();
        assertResultSuccess(proxy);
    }
}
