package in.partake.controller.action.user;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

public class ShowUserTest extends ActionControllerTest {
    @Test
    public void testShowDefaultUser() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/users/" + TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        ShowAction action = (ShowAction) proxy.getAction();
        assertThat(action.getUser().getId(), is(TestDataProvider.DEFAULT_USER_ID));
    }

    @Test
    public void testShowUserWithoutPref() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/users/" + TestDataProvider.USER_WITHOUT_PREF_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        ShowAction action = (ShowAction) proxy.getAction();
        assertThat(action.getUser().getId(), is(TestDataProvider.USER_WITHOUT_PREF_ID));
    }

    @Test
    public void testShowPrivatePrefUser() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/users/" + TestDataProvider.USER_WITH_PRIVATE_PREF_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        ShowAction action = (ShowAction) proxy.getAction();
        assertThat(action.getUser(), is(nullValue()));
        // TODO(mayah): check private version's HTML is displayed.
//        assertThat(action.getLocation(), is("users/private.jsp"));
    }

    @Test
    public void testShowInvalidUser() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/users/" + INVALID_USER_ID);

        proxy.execute();
        assertResultNotFound(proxy);
    }
}
