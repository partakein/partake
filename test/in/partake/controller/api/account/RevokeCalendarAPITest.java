package in.partake.controller.api.account;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.codehaus.jackson.node.ObjectNode;
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

        ObjectNode obj = getJSON(proxy);

        assertThat(obj.get("calendarId").asText(), is(not(currentCalendarId)));
        assertThat(obj.get("calendarId").asText(), is(loadCalendarIdFromUser(TestDataProvider.DEFAULT_USER_ID)));
    }
}
