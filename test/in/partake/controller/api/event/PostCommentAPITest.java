package in.partake.controller.api.event;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class PostCommentAPITest extends APIControllerTest {

    @Test
    public void testToCommentByOwner() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/postComment");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addFormParameter(proxy, "comment", "comment");

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testToCommentByUnrelatedUser() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/postComment");
        loginAs(proxy, TestDataProvider.EVENT_UNRELATED_USER_ID);

        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addFormParameter(proxy, "comment", "comment");

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testToCommentWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/postComment");

        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addFormParameter(proxy, "comment", "comment");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToCommentWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/postComment");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addInvalidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addFormParameter(proxy, "comment", "comment");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

    @Test
    public void testToCommentWithInvalidEventId() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/postComment");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", TestDataProvider.INVALID_EVENT_ID);
        addFormParameter(proxy, "comment", "comment");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_EVENT_ID);
    }

    @Test
    public void testToCommentWithoutEventId() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/postComment");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "comment", "comment");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_EVENT_ID);
    }

    @Test
    public void testToCommentWithoutComment() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/postComment");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_COMMENT);
    }

    @Test
    public void testToCommentWithEmptyComment() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/postComment");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addFormParameter(proxy, "comment", "");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_COMMENT);
    }

    @Test
    public void testToCommentWithBlankComment() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/postComment");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addFormParameter(proxy, "comment", "   ");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_COMMENT);
    }

    @Test
    public void testToCommentWithTooLongComment() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/postComment");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < PostCommentAPI.MAX_COMMENT_LENGTH * 2; ++i)
            buffer.append((char)((i % 26) + 'a'));

        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addFormParameter(proxy, "comment", buffer.toString());

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_COMMENT_TOOLONG);
    }
}
