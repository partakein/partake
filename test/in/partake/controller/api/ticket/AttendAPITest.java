package in.partake.controller.api.ticket;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;
import junit.framework.Assert;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class AttendAPITest extends APIControllerTest {

    @Test
    public void testShouldChangeToPresence() throws Exception {
        //
        {
            UserTicket enrollment = loadEnrollment(TestDataProvider.ATTENDANCE_ABSENT_USER_ID, TestDataProvider.DEFAULT_EVENT_TICKET_ID);
            Assert.assertEquals(AttendanceStatus.ABSENT, enrollment.getAttendanceStatus());
        }

        ActionProxy proxy = getActionProxy(POST, API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addFormParameter(proxy, "userId", TestDataProvider.ATTENDANCE_ABSENT_USER_ID);
        addFormParameter(proxy, "ticketId", TestDataProvider.DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        // Check status is changed.
        {
            UserTicket enrollment = loadEnrollment(TestDataProvider.ATTENDANCE_ABSENT_USER_ID, TestDataProvider.DEFAULT_EVENT_TICKET_ID);
            Assert.assertEquals(AttendanceStatus.PRESENT, enrollment.getAttendanceStatus());
        }
    }

    @Test
    public void testShouldChangeToAbsence() throws Exception {
        //
        {
            UserTicket enrollment = loadEnrollment(TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID, TestDataProvider.DEFAULT_EVENT_TICKET_ID);
            Assert.assertEquals(AttendanceStatus.UNKNOWN, enrollment.getAttendanceStatus());
        }

        ActionProxy proxy = getActionProxy(POST, API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addFormParameter(proxy, "userId", ATTENDANCE_UNKNOWN_USER_ID);
        addFormParameter(proxy, "ticketId", DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "status", "absent");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        // Check status is changed.
        {
            UserTicket enrollment = loadEnrollment(ATTENDANCE_UNKNOWN_USER_ID, DEFAULT_EVENT_TICKET_ID);
            Assert.assertEquals(AttendanceStatus.ABSENT, enrollment.getAttendanceStatus());
        }
    }

    @Test
    public void testShouldChangeToUnknown() throws Exception {
        //
        {
            UserTicket enrollment = loadEnrollment(TestDataProvider.ATTENDANCE_PRESENT_USER_ID, TestDataProvider.DEFAULT_EVENT_TICKET_ID);
            Assert.assertEquals(AttendanceStatus.PRESENT, enrollment.getAttendanceStatus());
        }

        ActionProxy proxy = getActionProxy(POST, API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addFormParameter(proxy, "userId", TestDataProvider.ATTENDANCE_PRESENT_USER_ID);
        addFormParameter(proxy, "ticketId", TestDataProvider.DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "status", "unknown");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        // Check status is changed.
        {
            UserTicket enrollment = loadEnrollment(TestDataProvider.ATTENDANCE_PRESENT_USER_ID, TestDataProvider.DEFAULT_EVENT_TICKET_ID);
            Assert.assertEquals(AttendanceStatus.UNKNOWN, enrollment.getAttendanceStatus());
        }
    }

    @Test
    public void testLoginRequired() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_ATTEND_URL);

        addFormParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addFormParameter(proxy, "ticketId", TestDataProvider.DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testUserIdRequired() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        // addFormParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addFormParameter(proxy, "ticketId", TestDataProvider.DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_USER_ID);
    }

    @Test
    public void testEventIdRequired() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addFormParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        // addFormParameter(proxy, "ticketId", TestDataProvider.DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_TICKET_ID);
    }

    @Test
    public void testStatusRequired() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addFormParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addFormParameter(proxy, "ticketId", TestDataProvider.DEFAULT_EVENT_TICKET_ID.toString());
        // addFormParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_ATTENDANCE_STATUS);
    }

    @Test
    public void testInvalidOwner() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_UNRELATED_USER_ID);

        addFormParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addFormParameter(proxy, "ticketId", TestDataProvider.DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testInvalidArgument() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addFormParameter(proxy, "userId", TestDataProvider.ATTENDANCE_PRESENT_USER_ID);
        addFormParameter(proxy, "ticketId", TestDataProvider.DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "status", "hogehoge");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_ATTENDANCE_STATUS);
    }

    @Test
    public void testInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(POST, API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addFormParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addFormParameter(proxy, "ticketId", TestDataProvider.DEFAULT_EVENT_TICKET_ID.toString());
        addFormParameter(proxy, "status", "present");
        addInvalidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

}
