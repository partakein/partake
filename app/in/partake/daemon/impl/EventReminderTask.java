package in.partake.daemon.impl;

import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.daemon.IPartakeDaemonTask;
import in.partake.model.UserTicketEx;
import in.partake.model.EventEx;
import in.partake.model.EventTicketHolderList;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.EventTicketNotification;
import in.partake.model.dto.MessageEnvelope;
import in.partake.model.dto.UserNotification;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.model.dto.auxiliary.NotificationType;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.PartakeProperties;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

class EventReminderTask extends Transaction<Void> implements IPartakeDaemonTask {
    private static final Logger logger = Logger.getLogger(EventReminderTask.class);

    @Override
    public void run() throws Exception {
        this.execute();
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        String topPath = PartakeProperties.get().getTopPath();
        DateTime now = TimeUtil.getCurrentDateTime();

        // TODO: 開始時刻が現在時刻より後の event のみを取り出したい、というかリマインダーを送るべりイベントのみを取り出したい
        DataIterator<Event> it = daos.getEventAccess().getIterator(con);
        try {
            while (it.hasNext()) {
                Event e = it.next();
                if (e == null) { continue; }
                String eventId = e.getId();
                if (eventId == null) { continue; }
                EventEx event = EventDAOFacade.getEventEx(con, daos, eventId);
                if (event == null) { continue; }
                if (event.getBeginDate().isBefore(now)) { continue; }

                List<EventTicket> tickets = daos.getEventTicketAccess().findEventTicketsByEventId(con, eventId);
                for (EventTicket ticket : tickets)
                    sendEventNotification(con, daos, ticket, event, topPath, now);
            }
        } finally {
            it.close();
        }

        return null;
    }

    private static boolean needsToSend(DateTime now, DateTime targetDate, DateTime lastSent) {
        if (now.isBefore(targetDate)) { return false; }
        if (lastSent == null) { return true; }
        if (targetDate.isBefore(lastSent)) { return false; }
        if (now.isBefore(new DateTime(lastSent.getTime() + 1000 * 3600))) { return false; }
        return true;
    }

    /**
     * message の内容で、仮参加者にのみメッセージを送る。
     * @param event
     * @param message
     * @throws DAOException
     */
    private void sendNotificationOnlyForReservedParticipants(PartakeConnection con, IPartakeDAOs daos, EventTicket ticket, Event event, NotificationType notificationType) throws DAOException {
        List<UserTicket> participations = daos.getEnrollmentAccess().findByTicketId(con, ticket.getId(), 0, Integer.MAX_VALUE);

        List<String> userIds = new ArrayList<String>();
        for (UserTicket participation : participations) {
            if (!ParticipationStatus.RESERVED.equals(participation.getStatus())) { continue; }
            userIds.add(participation.getUserId());
        }

        // TODO: ここから下のコードは、参加者のみにおくる場合と仮参加者のみに送る場合で共有するべき
        String eventNotificationId = daos.getEventNotificationAccess().getFreshId(con);
        EventTicketNotification notification = new EventTicketNotification(eventNotificationId, ticket.getId(), ticket.getEventId(), userIds, notificationType, TimeUtil.getCurrentDateTime());
        daos.getEventNotificationAccess().put(con, notification);

        DateTime invalidAfter = ticket.acceptsReservationTill(event);
        for (String userId : userIds) {
            String notificationId = daos.getUserNotificationAccess().getFreshId(con);
            UserNotification userNotification = new UserNotification(notificationId, ticket.getId(), userId, notificationType, MessageDelivery.INQUEUE, TimeUtil.getCurrentDateTime(), null);
            daos.getUserNotificationAccess().put(con, userNotification);

            String envelopeId = daos.getMessageEnvelopeAccess().getFreshId(con);
            MessageEnvelope envelope = MessageEnvelope.createForUserNotification(envelopeId, notificationId, invalidAfter);
            daos.getMessageEnvelopeAccess().put(con, envelope);
            logger.info("sendEnvelope : " + userId + " : " + notificationType);
        }
    }

    private void sendEventNotification(PartakeConnection con, IPartakeDAOs daos, EventTicket ticket, EventEx event, String topPath, DateTime now) throws DAOException {
        DateTime beginDate = event.getBeginDate();
        DateTime deadline = event.getBeginDate();

        // TODO: isBeforeDeadline() とかわかりにくいな。
        // 締め切り１日前になっても RESERVED ステータスの人がいればメッセージを送付する。
        // 次の条件でメッセージを送付する
        //  1. 現在時刻が締め切り２４時間前よりも後
        //  2. 次の条件のいずれか満たす
        //    2.1. まだメッセージが送られていない
        //    2.2. 前回送った時刻が締め切り２４時間以上前で、かつ送った時刻より１時間以上経過している。
        {
            EventTicketNotification notification = daos.getEventNotificationAccess().findLastNotification(con, ticket.getId(), NotificationType.ONE_DAY_BEFORE_REMINDER_FOR_RESERVATION);
            DateTime lastSent = notification != null ? notification.getCreatedAt() : null;
            if (needsToSend(now, deadline.nDayBefore(1), lastSent))
                sendNotificationOnlyForReservedParticipants(con, daos, ticket, event, NotificationType.ONE_DAY_BEFORE_REMINDER_FOR_RESERVATION);
        }

        // 締め切り１２時間前になっても RESERVED な人がいればメッセージを送付する。
        {
            EventTicketNotification notification = daos.getEventNotificationAccess().findLastNotification(con, ticket.getId(), NotificationType.HALF_DAY_BEFORE_REMINDER_FOR_RESERVATION);
            DateTime lastSent = notification != null ? notification.getCreatedAt() : null;
            if (needsToSend(now, deadline.nHourBefore(12), lastSent))
                sendNotificationOnlyForReservedParticipants(con, daos, ticket, event, NotificationType.HALF_DAY_BEFORE_REMINDER_FOR_RESERVATION);
        }

        // イベント１日前で、参加が確定している人にはメッセージを送付する。
        // 参加が確定していない人には、RESERVED なメッセージが送られている。
        {
            EventTicketNotification notification = daos.getEventNotificationAccess().findLastNotification(con, ticket.getId(), NotificationType.EVENT_ONEDAY_BEFORE_REMINDER);
            DateTime lastSent = notification != null ? notification.getCreatedAt() : null;
            if (needsToSend(now, beginDate.nDayBefore(1), lastSent))
                sendNotificationOnlyForParticipants(con, daos, ticket, event, NotificationType.EVENT_ONEDAY_BEFORE_REMINDER);
        }
    }

    /**
     * message の内容で、参加確定者にのみメッセージを送る
     * @param event
     * @param message
     * @throws DAOException
     */
    private void sendNotificationOnlyForParticipants(PartakeConnection con, IPartakeDAOs daos, EventTicket ticket, EventEx event, NotificationType notificationType) throws DAOException {
        List<UserTicketEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, daos, ticket, event);
        EventTicketHolderList list = ticket.calculateParticipationList(event, participations);

        List<String> userIds = new ArrayList<String>();
        for (UserTicketEx p : list.getEnrolledParticipations()) {
            if (!ParticipationStatus.ENROLLED.equals(p.getStatus()))
                continue;
            userIds.add(p.getUserId());
        }

        String eventNotificationId = daos.getEventNotificationAccess().getFreshId(con);
        EventTicketNotification notification = new EventTicketNotification(eventNotificationId, ticket.getId(), ticket.getEventId(), userIds, notificationType, TimeUtil.getCurrentDateTime());
        daos.getEventNotificationAccess().put(con, notification);

        DateTime invalidAfter = event.getBeginDate();
        for (String userId : userIds) {
            String notificationId = daos.getUserNotificationAccess().getFreshId(con);
            UserNotification userNotification = new UserNotification(notificationId, ticket.getId(), userId, notificationType, MessageDelivery.INQUEUE, TimeUtil.getCurrentDateTime(), null);
            daos.getUserNotificationAccess().put(con, userNotification);

            String envelopeId = daos.getMessageEnvelopeAccess().getFreshId(con);
            MessageEnvelope envelope = MessageEnvelope.createForUserNotification(envelopeId, notificationId, invalidAfter);
            daos.getMessageEnvelopeAccess().put(con, envelope);
            logger.info("sendEnvelope : " + userId + " : " + notificationType);
        }
    }
}
