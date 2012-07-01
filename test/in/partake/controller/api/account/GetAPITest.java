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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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

        JSONObject obj = getJSON(proxy);
        assertThat((String) obj.get("id"), is(TestDataProvider.DEFAULT_USER_ID));

        // TODO: Checks Twitter?

        // Checks UserPreference.
        JSONObject prefObj = obj.getJSONObject("preference");
        UserPreference pref = UserPreference.getDefaultPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(pref.isProfilePublic(), prefObj.getBoolean("profilePublic"));
        Assert.assertEquals(pref.isReceivingTwitterMessage(), prefObj.getBoolean("receivingTwitterMessage"));
        Assert.assertEquals(pref.tweetsAttendanceAutomatically(), prefObj.getBoolean("tweetingAttendanceAutomatically"));

        // Checks OpenIds
        JSONArray array = obj.getJSONArray("openIds");
        List<String> openIds = new ArrayList<String>();
        for (int i = 0; i < array.size(); ++i)
            openIds.add(array.getJSONObject(i).getString("identifier"));

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
