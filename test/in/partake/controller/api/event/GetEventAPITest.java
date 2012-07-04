package in.partake.controller.api.event;

import in.partake.controller.ActionProxy;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

public class GetEventAPITest extends APIControllerTest {
    @Test
    public void testGetEvent() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/event/get?eventId=" + DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testGetEventWithoutId() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/event/get");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_EVENT_ID);
    }

    @Test
    public void testGetEventWithInvalidId() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/event/get?eventId=" + INVALID_EVENT_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_EVENT_ID);
    }


    @Test
    public void testGetPrivateEventByOwner() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/event/get?eventId=" + PRIVATE_EVENT_ID);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testGetPrivateEventWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/event/get?eventId=" + PRIVATE_EVENT_ID);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testGetPrivateEventWithInvalidLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/event/get?eventId=" + PRIVATE_EVENT_ID);
        loginAs(proxy, TestDataProvider.EVENT_UNRELATED_USER_ID);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testGetPrivateEventWithCorrectPasscode() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/event/get?eventId=" + PRIVATE_EVENT_ID + "&passcode=passcode");

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testGetPrivateEventwithInvalidPasscode() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/event/get?eventId=" + PRIVATE_EVENT_ID + "&passcode=invalid");

        proxy.execute();
        assertResultForbidden(proxy);
    }

}
