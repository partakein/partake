package in.partake.session;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PartakeSessionTest {

    @Test
    public void testCreate() {
        PartakeSession session = PartakeSession.createInitialPartakeSession();
        assertThat(session.getCSRFPrevention(), is(notNullValue()));
    }
}
