package in.partake.daemon.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.MessageEnvelope;
import in.partake.model.dto.TwitterMessage;
import in.partake.model.dto.UserNotification;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.model.dto.auxiliary.NotificationType;
import in.partake.model.fixture.TestDataProviderConstants;
import in.partake.view.util.Helper;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import twitter4j.TwitterException;
import twitter4j.internal.http.HttpResponse;

public class SendMessageEnvelopeTaskTest extends AbstractPartakeControllerTest implements TestDataProviderConstants {
    private static final String TWITTER_MESSAGE_WILLFAIL_MESSAGE = "Sending this message should fail";

    @Before
    public void setUp() throws Exception {
        super.setUp();

        PartakeTestApp.getTestService().setDefaultFixtures();

        // Removes MessageEnvelope.
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                daos.getMessageEnvelopeAccess().truncate(con);
                return null;
            }
        }.execute();

        PartakeTestApp.instance().reinitializeTwitterService();
    }

    @Test
    public void sendEmpty() throws Exception {
        new SendMessageEnvelopeTask().run();
    }

    @Test
    public void sendTwitterMessage() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(uuid.toString(), TWITTER_MESSAGE_INQUEUE_ID, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        List<MessageEnvelope> rest = loadEnvelopes();
        assertThat(rest.isEmpty(), is(true));
        verify(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq("message"));

        assertThat(loadTwitterMessage(TWITTER_MESSAGE_INQUEUE_ID).getDelivery(), is(MessageDelivery.SUCCESS));
    }

    @Test
    public void sendTwitterMessageBeforeTryAfter() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, DEFAULT_USER_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = new MessageEnvelope(envelopeId.toString(), null, twitterMessageId, null, 0, null, null, now.nHourAfter(1), now, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        // |modified| should not be changed.
        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified.getNumTried(), is(0));
        assertThat(modified.getLastTriedAt(), is(nullValue()));
    }

    @Test
    public void sendTwitterMessageWithTwitterException() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, DEFAULT_USER_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified.getNumTried(), is(1));
        assertThat(modified.getLastTriedAt(), is(now));
        assertThat(modified.getTryAfter(), is(now.nSecAfter(600)));
    }

    @Test
    public void sendTwitterMessageWithTwitterExceptionCausedByNetworkError() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        // When |cause| is IOException, TwitterException thinks it's a network error.
        doThrow(new TwitterException("message", new IOException(""))).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, DEFAULT_USER_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified.getNumTried(), is(1));
        assertThat(modified.getLastTriedAt(), is(now));
        assertThat(modified.getTryAfter(), is(now.nSecAfter(600))); // 5 min later
    }

    @Test
    public void sendTwitterMessageWithTwitterExceptionExceededLimit() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(400).when(res).getStatusCode();
        doReturn("100").when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn("100").when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn("1").when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, DEFAULT_USER_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified.getNumTried(), is(1));
        assertThat(modified.getLastTriedAt(), is(now));
        // TODO: We should think how to test this.
        // assertThat(modified.getTryAfter(), is(now.nSecAfter(1))); // 1 sec after.
    }

    @Test
    public void sendTwitterMessageWithTwitterExceptionCausedByUnauthorized() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(401).when(res).getStatusCode();
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, DEFAULT_USER_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        // Message Envelop should be removed.
        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified, is(nullValue()));

        // User should be unauthorized.
        UserEx user = loadUserEx(DEFAULT_USER_ID);
        assertThat(user.getTwitterLinkage().getAccessToken(), is(nullValue()));
        assertThat(user.getTwitterLinkage().getAccessTokenSecret(), is(nullValue()));
    }

    @Test
    public void sendTwitterMessageWithInvalidId() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), UUID.randomUUID().toString(), null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        // The message should be removed from the queue.
        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified, is(nullValue()));
    }

    @Test
    public void sendTwitterMessageWithInvalidUserId() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, INVALID_USER_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        // The message should be removed from the queue.
        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified, is(nullValue()));
    }

    @Test
    public void sendTwitterMessageWithNoTwitterLinkUser() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, USER_NO_TWITTER_LINK_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        // The message should be removed from the queue.
        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified, is(nullValue()));
    }

    @Test
    public void sendTwitterMessageWithNoAuthorizedTwitterLinkUser() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, USER_TWITTER_NOAUTH_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        // The message should be removed from the queue.
        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified, is(nullValue()));
    }

    // ----------------------------------------------------------------------

    @Test
    public void sendUserNotification() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForUserNotification(uuid.toString(), USER_NOTIFICATION_INQUEUE_ID, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        List<MessageEnvelope> rest = loadEnvelopes();
        assertThat(rest.isEmpty(), is(true));
        verify(PartakeApp.getTwitterService()).sendDirectMesage(anyString(), anyString(), eq(DEFAULT_TWITTER_ID), anyString());

        assertThat(loadUserNotification(USER_NOTIFICATION_INQUEUE_ID).getDelivery(), is(MessageDelivery.SUCCESS));
    }

    @Test
    public void sendUserMessage() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForUserMessage(uuid.toString(), USER_RECEIVED_MESSAGE_INQUEUE_ID.toString(), null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        List<MessageEnvelope> rest = loadEnvelopes();
        assertThat(rest.isEmpty(), is(true));
        verify(PartakeApp.getTwitterService()).sendDirectMesage(anyString(), anyString(), eq(DEFAULT_RECEIVER_TWITTER_ID), anyString());

        assertThat(loadUserReceivedMessage(USER_RECEIVED_MESSAGE_INQUEUE_ID).getDelivery(), is(MessageDelivery.SUCCESS));
    }

    @Test
    public void sendInvalidatedMessage() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        DateTime before = now.nDayBefore(1);

        UUID uuid = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(uuid.toString(), TWITTER_MESSAGE_INQUEUE_ID, before);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        List<MessageEnvelope> rest = loadEnvelopes();
        assertThat(rest.isEmpty(), is(true));
        verify(PartakeApp.getTwitterService(), never()).updateStatus(anyString(), anyString(), anyString());
        assertThat(loadTwitterMessage(TWITTER_MESSAGE_INQUEUE_ID).getDelivery(), is(MessageDelivery.FAIL));
    }

    // ----------------------------------------------------------------------

    @Test
    public void testToBuildUserNotificationMessageBodyForOnedayBeforeReminder() throws Exception{
        UserNotification notification = loadUserNotification(USER_NOTIFICATION_INQUEUE_ID);
        Event event = loadEvent(DEFAULT_EVENT_ID);
        EventTicket ticket = loadEventTicket(DEFAULT_EVENT_TICKET_ID);

        UserNotification userNotification = new UserNotification(notification);
        userNotification.setNotificationType(NotificationType.EVENT_ONEDAY_BEFORE_REMINDER);

        String messageBody = SendMessageEnvelopeTask.buildUserNotificationMessageBody(userNotification, event, ticket);
        String expected = String.format("[PRTK] 「title」は%sに開始です。あなたの参加は確定しています。 http://127.0.0.1:9000/events/00000000-0000-0002-0000-000000000000", Helper.readableDate(event.getBeginDate()));
        assertThat(messageBody, is(expected));
    }

    @Test
    public void testToBuildUserNotificationMessageBodyForOnedayBeforeReservationReminder() throws Exception{
        UserNotification notification = loadUserNotification(USER_NOTIFICATION_INQUEUE_ID);
        Event event = loadEvent(DEFAULT_EVENT_ID);
        EventTicket ticket = loadEventTicket(DEFAULT_EVENT_TICKET_ID);

        UserNotification userNotification = new UserNotification(notification);
        userNotification.setNotificationType(NotificationType.ONE_DAY_BEFORE_REMINDER_FOR_RESERVATION);

        String messageBody = SendMessageEnvelopeTask.buildUserNotificationMessageBody(userNotification, event, ticket);
        String expected = String.format("[PRTK] 「title」の締め切りは%sです。参加・不参加を確定してください。 http://127.0.0.1:9000/events/00000000-0000-0002-0000-000000000000", Helper.readableDate(event.getBeginDate()));
        assertThat(messageBody, is(expected));
    }

    @Test
    public void testToBuildUserNotificationMessageBodyForHalfdayBeforeReservationReminder() throws Exception{
        UserNotification notification = loadUserNotification(USER_NOTIFICATION_INQUEUE_ID);
        Event event = loadEvent(DEFAULT_EVENT_ID);
        EventTicket ticket = loadEventTicket(DEFAULT_EVENT_TICKET_ID);

        UserNotification userNotification = new UserNotification(notification);
        userNotification.setNotificationType(NotificationType.HALF_DAY_BEFORE_REMINDER_FOR_RESERVATION);

        String messageBody = SendMessageEnvelopeTask.buildUserNotificationMessageBody(userNotification, event, ticket);
        String expected = String.format("[PRTK] 「title」の締め切りは%sです。参加・不参加を確定してください。 http://127.0.0.1:9000/events/00000000-0000-0002-0000-000000000000", Helper.readableDate(event.getBeginDate()));
        assertThat(messageBody, is(expected));
    }

    @Test
    public void testToBuildUserNotificationMessageBodyForEnrolledReminder() throws Exception{
        UserNotification notification = loadUserNotification(USER_NOTIFICATION_INQUEUE_ID);
        Event event = loadEvent(DEFAULT_EVENT_ID);
        EventTicket ticket = loadEventTicket(DEFAULT_EVENT_TICKET_ID);

        UserNotification userNotification = new UserNotification(notification);
        userNotification.setNotificationType(NotificationType.BECAME_TO_BE_ENROLLED);

        String messageBody = SendMessageEnvelopeTask.buildUserNotificationMessageBody(userNotification, event, ticket);
        String expected = "[PRTK] 「title」で補欠から参加者へ繰り上がりました。 http://127.0.0.1:9000/events/00000000-0000-0002-0000-000000000000";
        assertThat(messageBody, is(expected));
    }

    @Test
    public void testToBuildUserNotificationMessageBodyForCancelledReminder() throws Exception{
        UserNotification notification = loadUserNotification(USER_NOTIFICATION_INQUEUE_ID);
        Event event = loadEvent(DEFAULT_EVENT_ID);
        EventTicket ticket = loadEventTicket(DEFAULT_EVENT_TICKET_ID);

        UserNotification userNotification = new UserNotification(notification);
        userNotification.setNotificationType(NotificationType.BECAME_TO_BE_CANCELLED);

        String messageBody = SendMessageEnvelopeTask.buildUserNotificationMessageBody(userNotification, event, ticket);
        String expected = "[PRTK] 「title」で参加者から補欠へ繰り下がりました。 http://127.0.0.1:9000/events/00000000-0000-0002-0000-000000000000";
        assertThat(messageBody, is(expected));
    }



    // ----------------------------------------------------------------------

    private void storeTwitterMessage(final TwitterMessage message) throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                daos.getTwitterMessageAccess().put(con, message);
                return null;
            }
        }.execute();
    }

    private void queueEnvelope(final MessageEnvelope envelope) throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                daos.getMessageEnvelopeAccess().put(con, envelope);
                return null;
            }
        }.execute();
    }
}
