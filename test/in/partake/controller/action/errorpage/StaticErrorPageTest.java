package in.partake.controller.action.errorpage;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;
import in.partake.resource.ServerErrorCode;

import org.junit.Test;

public class StaticErrorPageTest extends ActionControllerTest {
    @Test
    public void testAccessWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/error");
        loginAs(proxy, DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);
   }

    @Test
    public void testAccessWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/error");

        proxy.execute();
        assertResultSuccess(proxy);
    }

    @Test
    public void testAccessWithErrorCode() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/error?errorCode=" + ServerErrorCode.INTENTIONAL_ERROR.getErrorCode());
        proxy.execute();
        assertResultSuccess(proxy);

        StaticErrorPageAction action = (StaticErrorPageAction) proxy.getAction();

        assertThat(action.getServerErrorCode(), is(ServerErrorCode.INTENTIONAL_ERROR));
    }

    @Test
    public void testAccessWithInvalidErrorCode() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/error?errorCode=hogehoge");
        proxy.execute();
        assertResultSuccess(proxy);

        StaticErrorPageAction action = (StaticErrorPageAction) proxy.getAction();
        assertThat(action.getServerErrorCode(), is(nullValue()));
    }

}
