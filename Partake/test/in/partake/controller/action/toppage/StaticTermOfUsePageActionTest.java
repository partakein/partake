package in.partake.controller.action.toppage;

import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

public class StaticTermOfUsePageActionTest extends ActionControllerTest {
    @Test
    public void testToExecute() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/termofuse");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);
    }

    @Test
    public void testToExecuteWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/termofuse");

        proxy.execute();
        assertResultSuccess(proxy);
    }
}
