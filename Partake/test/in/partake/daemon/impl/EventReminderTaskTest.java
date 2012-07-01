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
import in.partake.model.dto.EventTicketNotification;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.UserNotification;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.NotificationType;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.fixture.TestDataProviderConstants;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class EventReminderTaskTest extends AbstractPartakeControllerTest implements TestDataProviderConstants {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        PartakeTestApp.getTestService().setDefaultFixtures();
    }

    @Test
    public void sendReminderWhenEmpty() throws Exception {
        truncate();
        new EventReminderTask().run();
    }

    @Test
    public void sendReminder() throws Exception {
        truncate();

        DateTime now = TimeUtil.getCurrentDateTime();
        Event event = PartakeTestApp.getTestService().getTestDataProviderSet().getEventProvider().create();
        event.setOwnerId(DEFAULT_USER_ID);
        event.setBeginDate(now.nHourAfter(12));
        storeEvent(event);

        UUID ticketId = UUID.randomUUID();
        EventTicket ticket = EventTicket.createDefaultTicket(ticketId, event.getId());
        storeEventTicket(ticket);

        String id = UUID.randomUUID().toString();
        UserTicket enrollment = new UserTicket(id, DEFAULT_USER_ID, ticketId, event.getId(), "comment", ParticipationStatus.ENROLLED,
                ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, now, now, now);
        storeEnrollment(enrollment);

        // This event should be reminded.
        new EventReminderTask().run();

        // Check EventNotification and UserNotification.
        List<UserNotification> userNotifications = loadUserNotificationsByUserId(DEFAULT_USER_ID);
        assertThat(userNotifications.size(), is(1));
        UserNotification userNotification = userNotifications.get(0);
        assertThat(userNotification.getTicketId(), is(ticket.getId()));
        assertThat(userNotification.getDelivery(), is(MessageDelivery.INQUEUE));
        assertThat(userNotification.getNotificationType(), is(NotificationType.EVENT_ONEDAY_BEFORE_REMINDER));

        List<EventTicketNotification> eventNotifications = loadEventTicketNotificationsByEventId(ticketId);
        assertThat(eventNotifications.size(), is(3));

        // Re-run.
        new EventReminderTask().run();

        userNotifications = loadUserNotificationsByUserId(DEFAULT_USER_ID);
        assertThat(userNotifications.size(), is(1));
        eventNotifications = loadEventTicketNotificationsByEventId(ticketId);
        assertThat(eventNotifications.size(), is(3));
    }

    private void truncate() throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                daos.getEventAccess().truncate(con);
                daos.getEnrollmentAccess().truncate(con);
                daos.getEventNotificationAccess().truncate(con);
                daos.getUserNotificationAccess().truncate(con);
                return null;
            }
        }.execute();
    }
}
