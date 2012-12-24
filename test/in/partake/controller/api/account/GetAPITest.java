package in.partake.controller.api.account;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.ActionProxy;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.UserPreference;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

public class GetAPITest extends APIControllerTest {
    @Test
    public void testToGetWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/account/get");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultOK(proxy);

        // Check JSON

        ObjectNode obj = getJSON(proxy);
        assertThat(obj.get("id").asText(), is(TestDataProvider.DEFAULT_USER_ID));

        // TODO: Checks Twitter?

        // Checks UserPreference.
        JsonNode prefObj = obj.get("preference");
        UserPreference pref = UserPreference.getDefaultPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(pref.isProfilePublic(), prefObj.get("profilePublic").asBoolean());
        Assert.assertEquals(pref.isReceivingTwitterMessage(), prefObj.get("receivingTwitterMessage").asBoolean());
        Assert.assertEquals(pref.tweetsAttendanceAutomatically(), prefObj.get("tweetingAttendanceAutomatically").asBoolean());

        // Checks OpenIds
        JsonNode array = obj.get("openIds");
        List<String> openIds = new ArrayList<String>();
        for (int i = 0; i < array.size(); ++i)
            openIds.add(array.get(i).get("identifier").asText());

        assertThat(openIds, hasItem(TestDataProvider.DEFAULT_USER_OPENID_IDENTIFIER));
        assertThat(openIds, hasItem(TestDataProvider.DEFAULT_USER_OPENID_ALTERNATIVE_IDENTIFIER));
    }

    @Test
    public void testToGetWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/account/get");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }
}
