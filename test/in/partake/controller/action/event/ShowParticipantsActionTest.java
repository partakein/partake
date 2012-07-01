package in.partake.controller.action.event;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;

import org.junit.Test;

public class ShowParticipantsActionTest extends ActionControllerTest {

    @Test
    public void testToShow() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/participants/" + DEFAULT_EVENT_ID);
        loginAs(proxy, EVENT_OWNER_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        ShowParticipantsAction action = (ShowParticipantsAction) proxy.getAction();
        assertThat(action.getEvent().getId(), is(DEFAULT_EVENT_ID));

        // TODO: Check participants.
    }

    @Test
    public void testToShowWithInvalidUser() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/participants/" + DEFAULT_EVENT_ID);
        loginAs(proxy, EVENT_UNRELATED_USER_ID);

        proxy.execute();
        assertResultForbidden(proxy);
    }
}
