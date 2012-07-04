package in.partake.controller.api;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import play.test.Helpers;

import in.partake.controller.ActionProxy;

public abstract class APIControllerTest extends AbstractPartakeControllerTest {
    /**
     * Returns JSON from <code>proxy</code>. If not available, null will be returned.
     * @param proxy
     * @return
     * @throws IOException
     */
    protected String getJSONString(ActionProxy proxy) throws Exception {
        byte[] streamData = Helpers.contentAsBytes(proxy.getResult());
        if (streamData == null) {
            return null;
        } else {
            return new String(streamData, "UTF-8");
        }
    }

    /**
     * proxy から JSON を取得する。
     * @param proxy
     * @return
     * @throws IOException
     */
    protected JSONObject getJSON(ActionProxy proxy) throws Exception {
        String str = getJSONString(proxy);
        return JSONObject.fromObject(str);
    }

    // ----------------------------------------------------------------------

    protected void assertResultOK(ActionProxy proxy) throws Exception {
        Assert.assertEquals(200, Helpers.status(proxy.getResult()));

        JSONObject obj = getJSON(proxy);
        assertThat(obj.getString("result"), is("ok"));
    }

    protected void assertResultInvalid(ActionProxy proxy, UserErrorCode ec) throws Exception {
        Assert.assertEquals(400, Helpers.status(proxy.getResult()));

        JSONObject obj = getJSON(proxy);
        assertThat(obj.getString("result"), is("invalid"));
        assertThat(obj.getString("reason"), is(ec.getReasonString()));
    }

    protected void assertResultInvalid(ActionProxy proxy, UserErrorCode ec, String additional) throws Exception {
        Assert.assertEquals(400, Helpers.status(proxy.getResult()));

        JSONObject obj = getJSON(proxy);
        assertThat(obj.getString("result"), is("invalid"));
        assertThat(obj.getString("reason"), is(ec.getReasonString()));

        JSONObject additionalObj = obj.getJSONObject("additional");
        assertThat(additionalObj.containsKey(additional), is(true));
    }


    protected void assertResultLoginRequired(ActionProxy proxy) throws Exception {
        Assert.assertEquals(401, Helpers.status(proxy.getResult()));

        String authenticate = (String) Helpers.header("WWW-Authenticate", proxy.getResult());
        Assert.assertNotNull(authenticate);
        Assert.assertTrue(authenticate.contains("OAuth"));

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("auth", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }

    protected void assertResultForbidden(ActionProxy proxy) throws Exception {
        // status code should be 403
        Assert.assertEquals(403, Helpers.status(proxy.getResult()));

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("forbidden", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }

    protected void assertResultForbidden(ActionProxy proxy, UserErrorCode ec) throws Exception {
        assert ec.getStatusCode() == 403;
        // status code should be 403
        Assert.assertEquals(403, Helpers.status(proxy.getResult()));

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("forbidden", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
        // TODO: Check errorCode here.
    }

    protected void assertResultError(ActionProxy proxy, ServerErrorCode ec) throws Exception {
        Assert.assertEquals(500, Helpers.status(proxy.getResult()));

        JSONObject obj = getJSON(proxy);
        assertThat(obj.getString("result"), is("error"));
        assertThat(obj.getString("reason"), is(ec.getReasonString()));
    }
}
