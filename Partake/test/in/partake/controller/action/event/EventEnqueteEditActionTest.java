package in.partake.controller.action.event;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.action.ActionControllerTest;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class EventEnqueteEditActionTest extends ActionControllerTest {

    @Test
    public void testToShowWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/edit/enquete/" + TestDataProvider.DEFAULT_EVENT_ID);
        loginAs(proxy, EVENT_OWNER_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        EventEnqueteEditAction action = (EventEnqueteEditAction) proxy.getAction();
        assertThat(action.getEvent().getId(), is(DEFAULT_EVENT_ID));
    }

    @Test
    public void testToShowWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/edit/enquete/" + TestDataProvider.DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToShowPrivateEventByNonOwner() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/edit/enquete/" + TestDataProvider.DEFAULT_EVENT_ID);
        loginAs(proxy, DEFAULT_USER_ID);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testToShowInvalidEventId() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/edit/enquete/" + TestDataProvider.INVALID_EVENT_ID);
        loginAs(proxy, EVENT_OWNER_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_EVENT_ID);
    }

    @Test
    public void testToShowWithoutId() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/edit/enquete/");
        loginAs(proxy, EVENT_OWNER_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_EVENT_ID);
    }

    @Test
    public void testToShowWithNonUUID() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/edit/enquete/some-invalid-event-id");
        loginAs(proxy, EVENT_OWNER_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_EVENT_ID);
    }
}
