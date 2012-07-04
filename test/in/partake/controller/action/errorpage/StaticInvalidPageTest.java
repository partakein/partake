package in.partake.controller.action.errorpage;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

public class StaticInvalidPageTest extends ActionControllerTest {
    @Test
    public void testAccessWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/invalid");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);
   }

    @Test
    public void testAccessWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/invalid");

        proxy.execute();
        assertResultSuccess(proxy);
    }

    @Test
    public void testAccessWithErrorCode() throws Exception {
        String url = "/invalid?errorCode=" + UserErrorCode.INTENTIONAL_USER_ERROR.getErrorCode();
        ActionProxy proxy = getActionProxy(GET, url);
        proxy.execute();
        assertResultSuccess(proxy);

        StaticInvalidPageAction action = (StaticInvalidPageAction) proxy.getAction();

        assertThat(action.getUserErrorCode(), is(UserErrorCode.INTENTIONAL_USER_ERROR));
    }

    @Test
    public void testAccessWithInvalidErrorCode() throws Exception {
        String url = "/invalid?errorCode=hogehoge";
        ActionProxy proxy = getActionProxy(GET, url);
        proxy.execute();
        assertResultSuccess(proxy);

        StaticInvalidPageAction action = (StaticInvalidPageAction) proxy.getAction();
        assertThat(action.getUserErrorCode(), is(nullValue()));
    }
}
