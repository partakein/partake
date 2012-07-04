package in.partake.controller.api.ticket;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class RemoveAttendantAPITest extends APIControllerTest {

    @Test
    public void testToRemoveAttendant() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_REMOVE_ATTENDANT);
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "userId", EVENT_ENROLLED_USER_ID);
        addValidSessionTokenToParameter(proxy);

        loginAs(proxy, EVENT_OWNER_ID);

        UserTicket enrollment = loadEnrollment(EVENT_ENROLLED_USER_ID, DEFAULT_EVENT_TICKET_ID);
        assertThat(enrollment.getStatus(), is(ParticipationStatus.ENROLLED));

        proxy.execute();
        assertResultOK(proxy);

        enrollment = loadEnrollment(EVENT_ENROLLED_USER_ID, DEFAULT_EVENT_TICKET_ID);
        assertThat(enrollment, is(nullValue()));
    }

    @Test
    public void testToRemoveAttendantWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_REMOVE_ATTENDANT);
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "userId", EVENT_ENROLLED_USER_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToRemoveAttendantWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_REMOVE_ATTENDANT);
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "userId", EVENT_ENROLLED_USER_ID);
        addInvalidSessionTokenToParameter(proxy);

        loginAs(proxy, EVENT_OWNER_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

    @Test
    public void testToRemoveAttendantWithInvalidticketId() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_REMOVE_ATTENDANT);
        addFormParameter(proxy, "ticketId", INVALID_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "userId", EVENT_ENROLLED_USER_ID);
        addValidSessionTokenToParameter(proxy);

        loginAs(proxy, EVENT_OWNER_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_TICKET_ID);
    }

}
