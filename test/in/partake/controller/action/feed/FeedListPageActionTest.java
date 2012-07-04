package in.partake.controller.action.feed;

import in.partake.controller.action.ActionControllerTest;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class FeedListPageActionTest extends ActionControllerTest {

    @Test
    public void testCalendar() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/feed");
        proxy.execute();

        assertResultSuccess(proxy);
    }
}
