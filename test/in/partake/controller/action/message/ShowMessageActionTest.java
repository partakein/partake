package in.partake.controller.action.message;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.action.ActionControllerTest;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class ShowMessageActionTest extends ActionControllerTest {

    @Test
    public void testToGetMessage() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/messages/" + USER_RECEIVED_MESSAGE_INQUEUE_ID.toString());
        loginAs(proxy, DEFAULT_RECEIVER_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        ShowAction action = (ShowAction) proxy.getAction();
        assertThat(action.getMessage().getId(), is(USER_RECEIVED_MESSAGE_INQUEUE_ID));
    }

    @Test
    public void testToGetMessageInvalid() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/messages/" + INVALID_USER_RECEIVED_MESSAGE_ID.toString());
        loginAs(proxy, DEFAULT_RECEIVER_ID);

        proxy.execute();
        assertResultNotFound(proxy);
    }

    @Test
    public void testToGetMessageWithInvalidUser() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/messages/" + USER_RECEIVED_MESSAGE_INQUEUE_ID.toString());
        loginAs(proxy, DEFAULT_USER_ID);

        proxy.execute();
        assertResultForbidden(proxy);
    }
}
