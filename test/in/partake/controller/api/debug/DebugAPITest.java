package in.partake.controller.api.debug;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.status;
import in.partake.controller.ActionProxy;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

public class DebugAPITest extends APIControllerTest {

    @Test
    public void testSuccess() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/debug/success");

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testEchoWithData() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/debug/echo?data=test");

        proxy.execute();
        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("test", obj.get("data"));
    }

    @Test
    public void testEchoWithDataForPost() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/debug/echo");
        proxy.addFormParameter("data", "test");

        proxy.execute();
        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("test", obj.get("data"));
    }

    @Test
    public void testEchoWithoutData() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/debug/echo");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_ARGUMENT);
    }

    @Test
    public void testSuccessIfLoginWhenLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/debug/successIfLogin");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testSuccessIfLoginWhenNotLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/debug/successIfLogin");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testInvalid() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/debug/invalid");

        proxy.execute();

        assertThat(status(proxy.getResult()), is(400));
        assertResultInvalid(proxy, UserErrorCode.INTENTIONAL_USER_ERROR);
    }

    @Test
    public void testError() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/debug/error");

        proxy.execute();
        assertResultError(proxy, ServerErrorCode.INTENTIONAL_ERROR);
    }

    @Test
    public void testErrorException() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/debug/errorException");

        proxy.execute();
        assertResultError(proxy, ServerErrorCode.UNKNOWN_ERROR);
    }

    @Test
    public void testErrorDB() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/debug/errorDB");

        proxy.execute();
        assertResultError(proxy, ServerErrorCode.DB_ERROR);
    }

    @Test
    public void testErrorDBException() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/debug/errorDBException");

        proxy.execute();
        assertResultError(proxy, ServerErrorCode.DB_ERROR);
    }

}
