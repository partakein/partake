package in.partake.base;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

public class PartakeRuntimeExceptionTest {

    @Test
    public void testServerErrorCode() {
        PartakeRuntimeException e = new PartakeRuntimeException(ServerErrorCode.INTENTIONAL_ERROR);
        assertThat(e.isServerError(), is(true));
        assertThat(e.isUserError(), is(false));
        assertThat(e.getServerErrorCode(), is(ServerErrorCode.INTENTIONAL_ERROR));
        assertThat(e.getUserErrorCode(), is(nullValue()));
        assertThat(e.getStatusCode(), is(500));
    }
    
    @Test
    public void testServerErrorCodeWithCause() {
        Throwable t = new RuntimeException();
        PartakeRuntimeException e = new PartakeRuntimeException(ServerErrorCode.INTENTIONAL_ERROR, t);
        assertThat(e.getCause(), is(t));
    }

    @Test
    public void testUserErrorCode() {
        PartakeRuntimeException e = new PartakeRuntimeException(UserErrorCode.INTENTIONAL_USER_ERROR);
        assertThat(e.isServerError(), is(false));
        assertThat(e.isUserError(), is(true));
        assertThat(e.getServerErrorCode(), is(nullValue()));
        assertThat(e.getUserErrorCode(), is(UserErrorCode.INTENTIONAL_USER_ERROR));
        assertThat(e.getStatusCode(), is(400));
    }

    @Test
    public void testUserErrorCodeWithCause() {
        Throwable t = new RuntimeException();
        PartakeRuntimeException e = new PartakeRuntimeException(UserErrorCode.INTENTIONAL_USER_ERROR, t);
        assertThat(e.getCause(), is(t));
    }
}
