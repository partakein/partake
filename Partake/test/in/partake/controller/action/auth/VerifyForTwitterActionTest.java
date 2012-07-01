package in.partake.controller.action.auth;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.action.ActionControllerTest;

import org.junit.Test;

public class VerifyForTwitterActionTest extends ActionControllerTest {

    @Test
    public void shouldUpdateThisTest() {
        assertThat(true, is(false));
    }

//    @Test
//    public void testForValidVerifier() throws Exception {
//        ActionProxy proxy = getActionProxy(GET, "/auth/verifyForTwitter");
//        addParameter(proxy, "oauth_verifier", "valid");
//        setValidLoginInformation(proxy);
//        proxy.execute();
//
//        assertResultRedirect(proxy, "/");
//
//        assertThat(getPartakeSession(proxy).takeTwitterLoginInformation(), is(nullValue()));
//    }
//
//    private void setValidLoginInformation(ActionProxy proxy) {
//        TwitterLoginInformation loginInformation = Mockito.mock(TwitterLoginInformation.class);
//        getPartakeSession(proxy).setTwitterLoginInformation(loginInformation);
//    }
}
