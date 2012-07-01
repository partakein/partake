package in.partake.controller.api.event;

import in.partake.base.Util;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class SendMessageAPITest extends APIControllerTest {
    @Test
    public void testToSendMessageForOwnedEvent() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/sendMessage");

        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "subject", "subject");
        addParameter(proxy, "body", "message");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        // TODO: Check DB.
    }

    @Test
    public void testToSendLongMessage() throws Exception {
        String longMessage = Util.randomString(800);

        ActionProxy proxy = getActionProxy(POST, "/api/event/sendMessage");

        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "subject", "subject");
        addParameter(proxy, "body", longMessage);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        // TODO: Check DB.
    }

    @Test
    public void testToSendMessageForManagedEvent() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/sendMessage");

        loginAs(proxy, TestDataProvider.EVENT_EDITOR_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "subject", "subject");
        addParameter(proxy, "body", "hogehogehoge");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testToSendMessageForNotOwnedEvent() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/sendMessage");

        loginAs(proxy, TestDataProvider.EVENT_UNRELATED_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "subject", "subject");
        addParameter(proxy, "body", "hogehogehoge");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testToSendMessageWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/sendMessage");

        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "subject", "subject");
        addParameter(proxy, "body", "hogehogehoge");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToSendMessageWithoutTitle() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/sendMessage");
        loginAs(proxy, TestDataProvider.EVENT_UNRELATED_USER_ID);

        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "body", "hogehogehoge");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_MESSAGE_SUBJECT);
    }
}
