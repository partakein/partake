package in.partake.daemon.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.app.PartakeTestApp;
import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.UserNotification;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.NotificationType;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.auxiliary.TicketAmountType;
import in.partake.model.fixture.TestDataProviderConstants;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class SendParticipationStatusChangeNotificationTaskTest extends AbstractPartakeControllerTest implements TestDataProviderConstants {
    @Before
    public void setUp() throws Exception {
        super.setUp();

        PartakeTestApp.getTestService().setDefaultFixtures();
    }

    @Test
    public void testSendEmpty() throws Exception {
        truncate();
        new SendParticipationStatusChangeNotificationsTask().run();
    }

    @Test
    public void testChangedToEnrolled() throws Exception {
        truncate();

        DateTime now = TimeUtil.getCurrentDateTime();

        Event event = PartakeTestApp.getTestService().getTestDataProviderSet().getEventProvider().create();
        event.setOwnerId(EVENT_OWNER_ID);
        event.setBeginDate(now.nHourAfter(48));
        storeEvent(event);

        UUID ticketId = UUID.randomUUID();
        EventTicket ticket = EventTicket.createDefaultTicket(ticketId, event.getId());
        storeEventTicket(ticket);

        String[] enrollmentIds = new String[] { UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), };

        UserTicket[] enrollments = new UserTicket[] {
                new UserTicket(enrollmentIds[0], DEFAULT_USER_IDS[0], ticketId, event.getId(), "comment", ParticipationStatus.ENROLLED,
                        ModificationStatus.NOT_ENROLLED, AttendanceStatus.PRESENT, null, now, now, null),
                new UserTicket(enrollmentIds[1], DEFAULT_USER_IDS[1], ticketId, event.getId(), "comment", ParticipationStatus.RESERVED,
                        ModificationStatus.NOT_ENROLLED, AttendanceStatus.PRESENT, null, now, now, null),
                new UserTicket(enrollmentIds[2], DEFAULT_USER_IDS[2], ticketId, event.getId(), "comment", ParticipationStatus.ENROLLED,
                        ModificationStatus.CHANGED, AttendanceStatus.PRESENT, null, now, now, null)
        };

        for (UserTicket enrollment : enrollments)
            storeEnrollment(enrollment);
        new SendParticipationStatusChangeNotificationsTask().run();

        // The status should be changed to ENROLLED
        UserTicket[] changed = new UserTicket[] {
                loadEnrollment(enrollmentIds[0]), loadEnrollment(enrollmentIds[1]), loadEnrollment(enrollmentIds[2]),
        };
        assertThat(changed[0].getModificationStatus(), is(ModificationStatus.ENROLLED));
        assertThat(changed[1].getModificationStatus(), is(ModificationStatus.ENROLLED));
        assertThat(changed[2].getModificationStatus(), is(ModificationStatus.ENROLLED));

        // The user should received notification.
        {
            List<UserNotification> notifications = loadUserNotificationsByUserId(DEFAULT_USER_IDS[0]);
            assertThat(notifications.size(), is(1));
            assertThat(notifications.get(0).getTicketId(), is(ticket.getId()));
            assertThat(notifications.get(0).getNotificationType(), is(NotificationType.BECAME_TO_BE_ENROLLED));
            assertThat(notifications.get(0).getDelivery(), is(MessageDelivery.INQUEUE));
        }
        {
            List<UserNotification> notifications = loadUserNotificationsByUserId(DEFAULT_USER_IDS[1]);
            assertThat(notifications.size(), is(1));
            assertThat(notifications.get(0).getTicketId(), is(ticket.getId()));
            assertThat(notifications.get(0).getNotificationType(), is(NotificationType.BECAME_TO_BE_ENROLLED));
            assertThat(notifications.get(0).getDelivery(), is(MessageDelivery.INQUEUE));
        }
        {
            List<UserNotification> notifications = loadUserNotificationsByUserId(DEFAULT_USER_IDS[2]);
            assertThat(notifications.size(), is(0));
        }
    }

    @Test
    public void testChangedToCancelled() throws Exception {
        truncate();

        DateTime now = TimeUtil.getCurrentDateTime();

        Event event = PartakeTestApp.getTestService().getTestDataProviderSet().getEventProvider().create();
        event.setOwnerId(EVENT_OWNER_ID);
        event.setBeginDate(now.nHourAfter(48));
        storeEvent(event);

        UUID ticketId = UUID.randomUUID();
        EventTicket ticket = EventTicket.createDefaultTicket(ticketId, event.getId());
        ticket.setAmount(1);
        ticket.setAmountType(TicketAmountType.LIMITED);
        storeEventTicket(ticket);

        String[] enrollmentIds = new String[] {
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
        };

        UserTicket[] enrollments = new UserTicket[] {
                new UserTicket(enrollmentIds[0], DEFAULT_USER_IDS[0], ticketId, event.getId(), "comment", ParticipationStatus.ENROLLED,
                        ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, now, now, null),
                new UserTicket(enrollmentIds[1], DEFAULT_USER_IDS[1], ticketId, event.getId(), "comment", ParticipationStatus.RESERVED,
                        ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, new DateTime(now.getTime() + 1), now, null),
                new UserTicket(enrollmentIds[2], DEFAULT_USER_IDS[2], ticketId, event.getId(), "comment", ParticipationStatus.ENROLLED,
                        ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, new DateTime(now.getTime() + 2), now, null),
                new UserTicket(enrollmentIds[3], DEFAULT_USER_IDS[3], ticketId, event.getId(), "comment", ParticipationStatus.ENROLLED,
                        ModificationStatus.CHANGED, AttendanceStatus.PRESENT, null, new DateTime(now.getTime() + 3), now, null)
        };

        for (UserTicket enrollment : enrollments)
            storeEnrollment(enrollment);
        new SendParticipationStatusChangeNotificationsTask().run();

        // The status should be changed to NOT_ENROLLED.
        // Only the first person can enroll.
        UserTicket[] changed = new UserTicket[] {
                loadEnrollment(enrollmentIds[0]), loadEnrollment(enrollmentIds[1]), loadEnrollment(enrollmentIds[2]), loadEnrollment(enrollmentIds[3]),
        };
        assertThat(changed[0].getModificationStatus(), is(ModificationStatus.ENROLLED));
        assertThat(changed[1].getModificationStatus(), is(ModificationStatus.NOT_ENROLLED));
        assertThat(changed[2].getModificationStatus(), is(ModificationStatus.NOT_ENROLLED));
        assertThat(changed[3].getModificationStatus(), is(ModificationStatus.NOT_ENROLLED));

        // The user should received notification.
        {
            List<UserNotification> notifications = loadUserNotificationsByUserId(DEFAULT_USER_IDS[0]);
            assertThat(notifications.size(), is(0));
        }
        {
            List<UserNotification> notifications = loadUserNotificationsByUserId(DEFAULT_USER_IDS[1]);
            assertThat(notifications.size(), is(1));
            assertThat(notifications.get(0).getTicketId(), is(ticket.getId()));
            assertThat(notifications.get(0).getNotificationType(), is(NotificationType.BECAME_TO_BE_CANCELLED));
            assertThat(notifications.get(0).getDelivery(), is(MessageDelivery.INQUEUE));
        }
        {
            List<UserNotification> notifications = loadUserNotificationsByUserId(DEFAULT_USER_IDS[2]);
            assertThat(notifications.size(), is(1));
            assertThat(notifications.get(0).getTicketId(), is(ticket.getId()));
            assertThat(notifications.get(0).getNotificationType(), is(NotificationType.BECAME_TO_BE_CANCELLED));
            assertThat(notifications.get(0).getDelivery(), is(MessageDelivery.INQUEUE));
        }
        {
            List<UserNotification> notifications = loadUserNotificationsByUserId(DEFAULT_USER_IDS[3]);
            assertThat(notifications.size(), is(0));
        }
    }

    private void truncate() throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                daos.getEventAccess().truncate(con);
                daos.getEnrollmentAccess().truncate(con);
                daos.getUserNotificationAccess().truncate(con);
                daos.getUserNotificationAccess().truncate(con);
                return null;
            }
        }.execute();
    }
}
