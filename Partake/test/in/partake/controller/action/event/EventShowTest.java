package in.partake.controller.action.event;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

public class EventShowTest extends ActionControllerTest {

    @Test
    public void testToShowWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/" + TestDataProvider.DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        EventShowAction action = (EventShowAction) proxy.getAction();
        assertThat(action.getEvent().getId(), is(TestDataProvider.DEFAULT_EVENT_ID));
    }

    @Test
    public void testToShowWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/" + TestDataProvider.DEFAULT_EVENT_ID);
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        EventShowAction action = (EventShowAction) proxy.getAction();
        assertThat(action.getEvent().getId(), is(TestDataProvider.DEFAULT_EVENT_ID));
    }

    @Test
    public void testToShowPrivateEventByOwner() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/" + TestDataProvider.PRIVATE_EVENT_ID);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        EventShowAction action = (EventShowAction) proxy.getAction();
        assertThat(action.getEvent().getId(), is(TestDataProvider.PRIVATE_EVENT_ID));
    }

    @Test
    public void testToShowPrivateEventByNonOwner() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/" + TestDataProvider.PRIVATE_EVENT_ID);

        proxy.execute();
        assertResultRedirect(proxy, "/events/passcode?eventId=" + TestDataProvider.PRIVATE_EVENT_ID);

        EventShowAction action = (EventShowAction) proxy.getAction();
        assertThat(action.getEvent(), is(nullValue()));
    }

    @Test
    public void testToShowInvalidEventId() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/" + TestDataProvider.INVALID_EVENT_ID);

        proxy.execute();
        assertResultNotFound(proxy);

        EventShowAction action = (EventShowAction) proxy.getAction();
        assertThat(action.getEvent(), is(nullValue()));
    }

    @Test
    public void testToShowWithoutId() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/");

        proxy.execute();
        assertResultNotFound(proxy);

        EventShowAction action = (EventShowAction) proxy.getAction();
        assertThat(action.getEvent(), is(nullValue()));
    }

    @Test
    public void testToShowWithNonUUID() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/" + "some-invalid-event-id");

        proxy.execute();
        assertResultNotFound(proxy);

        EventShowAction action = (EventShowAction) proxy.getAction();
        assertThat(action.getEvent(), is(nullValue()));
    }

}
