package in.partake.controller.api.ticket;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class ApplyAPITest extends APIControllerTest {

    @Test
    public void testEnroll() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_APPLY);
        loginAs(proxy, DEFAULT_USER_ID);
        addFormParameter(proxy, "status", "enroll");
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "comment", "comment");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        UserTicket enrollment = loadEnrollment(DEFAULT_USER_ID, DEFAULT_EVENT_TICKET_ID);
        assertThat(enrollment.getStatus(), is(ParticipationStatus.ENROLLED));
    }

    @Test
    public void testReserve() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_APPLY);
        loginAs(proxy, DEFAULT_USER_ID);
        addFormParameter(proxy, "status", "reserve");
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "comment", "comment");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        UserTicket enrollment = loadEnrollment(DEFAULT_USER_ID, DEFAULT_EVENT_TICKET_ID);
        assertThat(enrollment.getStatus(), is(ParticipationStatus.RESERVED));
    }

    @Test
    public void testCancel() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_APPLY);
        loginAs(proxy, DEFAULT_USER_ID);
        addFormParameter(proxy, "status", "cancel");
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "comment", "comment");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        UserTicket enrollment = loadEnrollment(DEFAULT_USER_ID, DEFAULT_EVENT_TICKET_ID);
        assertThat(enrollment.getStatus(), is(ParticipationStatus.CANCELLED));
    }

    @Test
    public void testWithInvalidStatus() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_APPLY);
        loginAs(proxy, DEFAULT_USER_ID);
        addFormParameter(proxy, "status", "invalid");
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "comment", "comment");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_ENROLL_STATUS);
    }

    @Test
    public void testEnrollWithoutComment() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_APPLY);
        loginAs(proxy, DEFAULT_USER_ID);
        addFormParameter(proxy, "status", "enroll");
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        UserTicket enrollment = loadEnrollment(DEFAULT_USER_ID, DEFAULT_EVENT_TICKET_ID);
        assertThat(enrollment.getStatus(), is(ParticipationStatus.ENROLLED));
        assertThat(enrollment.getComment(), is(""));
    }

    @Test
    public void testEnrollWithLongComment() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_APPLY);
        loginAs(proxy, DEFAULT_USER_ID);
        addFormParameter(proxy, "status", "enroll");
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1025; ++i)
            builder.append('a');
        addFormParameter(proxy, "comment", builder.toString());
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_COMMENT_TOOLONG);
    }

    @Test
    public void testEnrollWontChangeEnrolledAt() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_APPLY);
        loginAs(proxy, EVENT_RESERVED_USER_ID);
        addFormParameter(proxy, "status", "enroll");
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "comment", "comment");
        addValidSessionTokenToParameter(proxy);

        UserTicket original = loadEnrollment(EVENT_RESERVED_USER_ID, DEFAULT_EVENT_TICKET_ID);

        proxy.execute();
        assertResultOK(proxy);

        UserTicket enrollment = loadEnrollment(EVENT_RESERVED_USER_ID, DEFAULT_EVENT_TICKET_ID);

        assertThat(enrollment.getStatus(), is(ParticipationStatus.ENROLLED));
        assertThat(enrollment.getModifiedAt(), is(original.getModifiedAt()));
    }

    @Test
    public void testWithoutValidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_APPLY);
        loginAs(proxy, DEFAULT_USER_ID);
        addFormParameter(proxy, "status", "enroll");
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "comment", "comment");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

    @Test
    public void testWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_APPLY);
        addFormParameter(proxy, "status", "enroll");
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "comment", "comment");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }
}
