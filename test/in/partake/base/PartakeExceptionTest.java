package in.partake.base;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.util.Collections;

import org.junit.Test;

public class PartakeExceptionTest {

    @Test
    public void testServerErrorCode() {
        PartakeException e = new PartakeException(ServerErrorCode.INTENTIONAL_ERROR);
        assertThat(e.isServerError(), is(true));
        assertThat(e.isUserError(), is(false));
        assertThat(e.getServerErrorCode(), is(ServerErrorCode.INTENTIONAL_ERROR));
        assertThat(e.getUserErrorCode(), is(nullValue()));
        assertThat(e.getStatusCode(), is(500));
        assertThat(e.getCause(), is(nullValue()));
    }

    @Test
    public void testServerErrorCodeWithCause() {
        Throwable t = new RuntimeException();
        PartakeException e = new PartakeException(ServerErrorCode.INTENTIONAL_ERROR, t);
        assertThat(e.getCause(), is(t));
    }

    @Test
    public void testServerErrorCodeWithAdditionalInfo() {
        PartakeException e = new PartakeException(ServerErrorCode.INTENTIONAL_ERROR, Collections.singletonMap("K", "V"));
        assertThat(e.getAdditionalInfo().get("K"), is("V"));
        assertThat(e.getAdditionalInfo().get("V"), is(nullValue()));
    }

    @Test
    public void testServerErrorCodeWithAdditionalSingleInfo() {
        PartakeException e = new PartakeException(ServerErrorCode.INTENTIONAL_ERROR, "K", "V");
        assertThat(e.getAdditionalInfo().get("K"), is("V"));
        assertThat(e.getAdditionalInfo().get("V"), is(nullValue()));
    }

    @Test
    public void testUserErrorCode() {
        PartakeException e = new PartakeException(UserErrorCode.INTENTIONAL_USER_ERROR);
        assertThat(e.isServerError(), is(false));
        assertThat(e.isUserError(), is(true));
        assertThat(e.getServerErrorCode(), is(nullValue()));
        assertThat(e.getUserErrorCode(), is(UserErrorCode.INTENTIONAL_USER_ERROR));
        assertThat(e.getStatusCode(), is(400));
        assertThat(e.getCause(), is(nullValue()));
    }

    @Test
    public void testUserErrorCodeWithCause() {
        Throwable t = new RuntimeException();
        PartakeException e = new PartakeException(UserErrorCode.INTENTIONAL_USER_ERROR, t);
        assertThat(e.getCause(), is(t));
    }

    @Test
    public void testUserErrorCodeWithAdditionalInfo() {
        PartakeException e = new PartakeException(UserErrorCode.INTENTIONAL_USER_ERROR, Collections.singletonMap("K", "V"));
        assertThat(e.getAdditionalInfo().get("K"), is("V"));
        assertThat(e.getAdditionalInfo().get("V"), is(nullValue()));
    }

    @Test
    public void testUserErrorCodeWithAdditionalSingleInfo() {
        PartakeException e = new PartakeException(UserErrorCode.INTENTIONAL_USER_ERROR, "K", "V");
        assertThat(e.getAdditionalInfo().get("K"), is("V"));
        assertThat(e.getAdditionalInfo().get("V"), is(nullValue()));
    }
}
