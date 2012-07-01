package in.partake.controller.api.event;

import in.partake.controller.api.APIControllerTest;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class RemoveAPITest extends APIControllerTest {
    @Test
    public void testToRemove() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/remove");
        loginAs(proxy, EVENT_OWNER_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testToRemoveByEventEditor() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/remove");
        loginAs(proxy, EVENT_EDITOR_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testToRemoveWithoutValidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/remove");
        loginAs(proxy, EVENT_OWNER_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

    @Test
    public void testToRemoveWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/remove");
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToRemoveWithalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/remove");
        loginAs(proxy, EVENT_OWNER_ID);
        addParameter(proxy, "eventId", INVALID_EVENT_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_EVENT_ID);
    }
}
