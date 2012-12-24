package in.partake.controller.api.account;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import in.partake.controller.ActionProxy;

public class GetImagesAPITest extends APIControllerTest {

    @Test
    public void testToGetImages() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/account/images");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();

        assertResultOK(proxy);

        ObjectNode obj = getJSON(proxy);

        assertThat(obj.get("count").asInt(), is(10));
        JsonNode ids = obj.get("imageIds");
        for (int i = 0; i < ids.size(); ++i)
            assertThat(ids.get(i).asText(), is(TestDataProvider.IMAGE_OWNED_BY_DEFAULT_USER_ID[i]));
    }

    @Test
    public void testToGetImagesWithOffsetAndLimit() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/account/images?offset=0&limit=10");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        proxy.execute();

        assertResultOK(proxy);

        ObjectNode obj = getJSON(proxy);

        assertThat(obj.get("count").asInt(), is(10));
        JsonNode ids = obj.get("imageIds");
        for (int i = 0; i < ids.size(); ++i)
            assertThat(ids.get(i).asText(), is(TestDataProvider.IMAGE_OWNED_BY_DEFAULT_USER_ID[i]));
    }

    @Test
    public void testToGetImagesWithOffsetAndLimit2() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/account/images?offset=0&limit=5");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        proxy.execute();

        assertResultOK(proxy);

        ObjectNode obj = getJSON(proxy);

        assertThat(obj.get("count").asInt(), is(10));
        JsonNode ids = obj.get("imageIds");
        for (int i = 0; i < ids.size(); ++i)
            assertThat(ids.get(i).asText(), is(TestDataProvider.IMAGE_OWNED_BY_DEFAULT_USER_ID[i]));
    }

    @Test
    public void testToGetImagesWithOffsetAndLimit3() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/account/images?offset=3&limit=5");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        proxy.execute();

        assertResultOK(proxy);

        ObjectNode obj = getJSON(proxy);

        assertThat(obj.get("count").asInt(), is(10));
        JsonNode ids = obj.get("imageIds");
        for (int i = 0; i < ids.size(); ++i)
            assertThat(ids.get(i).asText(), is(TestDataProvider.IMAGE_OWNED_BY_DEFAULT_USER_ID[i + 3]));
    }
}
