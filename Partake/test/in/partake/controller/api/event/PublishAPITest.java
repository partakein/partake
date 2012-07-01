package in.partake.controller.api.event;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.Event;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class PublishAPITest extends APIControllerTest {

    @Test
    public void testPublish() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/publish");
        loginAs(proxy, EVENT_OWNER_ID);
        addParameter(proxy, "eventId", UNPUBLISHED_EVENT_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        Event event = loadEvent(UNPUBLISHED_EVENT_ID);
        assertThat(event.isDraft(), is(false));
    }

    @Test
    public void testPublishEventAlreadyPublished() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/publish");
        loginAs(proxy, EVENT_OWNER_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.EVENT_ALREADY_PUBLISHED);
    }

    @Test
    public void testPublishWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/publish");
        addParameter(proxy, "eventId", UNPUBLISHED_EVENT_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testPublishWithEditor() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/publish");
        loginAs(proxy, EVENT_EDITOR_ID);
        addParameter(proxy, "eventId", UNPUBLISHED_EVENT_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultForbidden(proxy, UserErrorCode.FORBIDDEN_EVENT_EDIT);
    }

    @Test
    public void testPublishWithUnrelatedUser() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/publish");
        loginAs(proxy, EVENT_UNRELATED_USER_ID);
        addParameter(proxy, "eventId", UNPUBLISHED_EVENT_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultForbidden(proxy, UserErrorCode.FORBIDDEN_EVENT_EDIT);
    }

    @Test
    public void testPublishWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/publish");
        loginAs(proxy, EVENT_OWNER_ID);
        addParameter(proxy, "eventId", UNPUBLISHED_EVENT_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

    @Test
    public void testPublishWithInvalidEventId() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/publish");
        loginAs(proxy, EVENT_OWNER_ID);
        addParameter(proxy, "eventId", INVALID_EVENT_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_EVENT_ID);
    }


}
