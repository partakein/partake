package in.partake.service;

import static org.junit.Assert.*;

import static org.hamcrest.Matchers.*;
import org.junit.Test;

public class EventSortOrderTest {
    @Test
    public void testEventSortOrder() {
        assertThat(EventSortOrder.getSortOrders(), is(notNullValue()));
    }
}
