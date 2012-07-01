package in.partake.controller.api.account;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class GetImagesAPITest extends APIControllerTest {

    @Test
    public void testToGetImages() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/account/images");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();

        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);

        assertThat(obj.getInt("count"), is(10));
        JSONArray ids = obj.getJSONArray("imageIds");
        for (int i = 0; i < ids.size(); ++i)
            assertThat(ids.getString(i), is(TestDataProvider.IMAGE_OWNED_BY_DEFAULT_USER_ID[i]));
    }

    @Test
    public void testToGetImagesWithOffsetAndLimit() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/account/images");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        addParameter(proxy, "offset", "0");
        addParameter(proxy, "limit", "10");
        proxy.execute();

        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);

        assertThat(obj.getInt("count"), is(10));
        JSONArray ids = obj.getJSONArray("imageIds");
        for (int i = 0; i < ids.size(); ++i)
            assertThat(ids.getString(i), is(TestDataProvider.IMAGE_OWNED_BY_DEFAULT_USER_ID[i]));
    }

    @Test
    public void testToGetImagesWithOffsetAndLimit2() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/account/images");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        addParameter(proxy, "offset", "0");
        addParameter(proxy, "limit", "5");
        proxy.execute();

        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);

        assertThat(obj.getInt("count"), is(10));
        JSONArray ids = obj.getJSONArray("imageIds");
        for (int i = 0; i < ids.size(); ++i)
            assertThat(ids.getString(i), is(TestDataProvider.IMAGE_OWNED_BY_DEFAULT_USER_ID[i]));
    }

    @Test
    public void testToGetImagesWithOffsetAndLimit3() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/account/images");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        addParameter(proxy, "offset", "3");
        addParameter(proxy, "limit", "5");
        proxy.execute();

        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);

        assertThat(obj.getInt("count"), is(10));
        JSONArray ids = obj.getJSONArray("imageIds");
        for (int i = 0; i < ids.size(); ++i)
            assertThat(ids.getString(i), is(TestDataProvider.IMAGE_OWNED_BY_DEFAULT_USER_ID[i + 3]));
    }
}
