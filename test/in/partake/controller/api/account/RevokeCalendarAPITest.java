package in.partake.controller.api.account;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;
import net.sf.json.JSONObject;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class RevokeCalendarAPITest extends APIControllerTest {

    @Test
    public void testToRevokeCalendar() throws Exception {
        String currentCalendarId = loadCalendarIdFromUser(TestDataProvider.DEFAULT_USER_ID);
        ActionProxy proxy = getActionProxy(POST, "/api/account/revokeCalendar");

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);

        assertThat(obj.getString("calendarId"), is(not(currentCalendarId)));
        assertThat(obj.getString("calendarId"), is(loadCalendarIdFromUser(TestDataProvider.DEFAULT_USER_ID)));
    }
}
