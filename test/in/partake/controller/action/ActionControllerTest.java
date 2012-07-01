package in.partake.controller.action;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.redirectLocation;
import static play.test.Helpers.status;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.controller.ActionProxy;
import in.partake.resource.UserErrorCode;
import play.mvc.Result;

public abstract class ActionControllerTest extends AbstractPartakeControllerTest {
    protected void assertResultSuccess(ActionProxy proxy) throws Exception {
        Result result = proxy.getResult();
        assertThat(status(result), is(200));
    }

    protected void assertResultInvalid(ActionProxy proxy) throws Exception {
        String redirectLocation = redirectLocation(proxy.getResult());
        assertThat(redirectLocation, startsWith("/invalid"));
    }

    protected void assertResultInvalid(ActionProxy proxy, UserErrorCode ec) throws Exception {
        String redirectLocation = redirectLocation(proxy.getResult());
        assertThat(redirectLocation, startsWith("/invalid?errorCode=" + ec.getErrorCode()));
    }
}
