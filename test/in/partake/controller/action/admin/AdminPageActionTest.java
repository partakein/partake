package in.partake.controller.action.admin;

import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;

import org.junit.Test;

public class AdminPageActionTest extends ActionControllerTest {

    @Test
    public void testWithAdminLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/admin/");
        loginAs(proxy, ADMIN_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);
    }

    @Test
    public void testWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/admin/");
        loginAs(proxy, ADMIN_USER_ID);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/admin/");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }
}
