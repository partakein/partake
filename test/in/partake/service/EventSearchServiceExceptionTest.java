package in.partake.service;

import static org.junit.Assert.*;
import in.partake.resource.ServerErrorCode;

import static org.hamcrest.Matchers.*;
import org.junit.Test;

public class EventSearchServiceExceptionTest {
    @Test
    public void testEventSortOrder() {
        assertThat(new EventSearchServiceException().getServerErrorCode(), is(ServerErrorCode.EVENT_SEARCH_SERVICE_ERROR));
    }
}
