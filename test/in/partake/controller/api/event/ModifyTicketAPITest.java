package in.partake.controller.api.event;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.EventEx;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.TicketAmountType;
import in.partake.model.dto.auxiliary.TicketApplicationEnd;
import in.partake.model.dto.auxiliary.TicketApplicationStart;
import in.partake.model.dto.auxiliary.TicketPriceType;
import in.partake.model.dto.auxiliary.TicketReservationEnd;

import java.util.List;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class ModifyTicketAPITest extends APIControllerTest {

    @Test
    public void testToModifyTicket() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modifyTicket");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);

        addParameter(proxy, "eventId", UNPUBLISHED_EVENT_ID);
        addParameter(proxy, "id[]", new String[] { "" });
        addParameter(proxy, "name[]", new String[] { "name" });
        addParameter(proxy, "applicationStart[]", new String[] { "anytime" });
        addParameter(proxy, "applicationStartDayBeforeEvent[]", new String[] { "0" });
        addParameter(proxy, "customApplicationStartDate[]", new String[] { "2012-01-01 00:00" });
        addParameter(proxy, "applicationEnd[]", new String[] { "till_time_before_event" });
        addParameter(proxy, "applicationEndDayBeforeEvent[]", new String[] { "0" });
        addParameter(proxy, "customApplicationEndDate[]", new String[] { "2012-01-01 00:00" });
        addParameter(proxy, "reservationEnd[]", new String[] { "till_time_before_application" });
        addParameter(proxy, "reservationEndHourBeforeApplication[]", new String[] { "0" });
        addParameter(proxy, "customReservationEndDate[]", new String[] { "2012-01-01 00:00" });
        addParameter(proxy, "priceType[]", new String[] { "free" });
        addParameter(proxy, "price[]", new String[] { "0" });
        addParameter(proxy, "amountType[]", new String[] { "unlimited" });
        addParameter(proxy, "amount[]", new String[] { "0" });

        proxy.execute();
        assertResultOK(proxy);

        EventEx modified = loadEventEx(UNPUBLISHED_EVENT_ID);
        List<EventTicket> tickets = modified.getTickets();

        assertThat(tickets.size(), is(1));
        assertThat(tickets.get(0).getName(), is("name"));
        assertThat(tickets.get(0).getApplicationStart(), is(TicketApplicationStart.ANYTIME));
        assertThat(tickets.get(0).getApplicationEnd(), is(TicketApplicationEnd.TILL_TIME_BEFORE_EVENT));
        assertThat(tickets.get(0).getReservationEnd(), is(TicketReservationEnd.TILL_TIME_BEFORE_APPLICATION));
        assertThat(tickets.get(0).getPriceType(), is(TicketPriceType.FREE));
        assertThat(tickets.get(0).getAmountType(), is(TicketAmountType.UNLIMITED));
    }
}
