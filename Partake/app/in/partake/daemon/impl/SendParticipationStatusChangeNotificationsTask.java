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
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.NotificationType;

import java.util.ArrayList;
import java.util.List;

class SendParticipationStatusChangeNotificationsTask extends Transaction<Void> implements IPartakeDaemonTask {

    @Override
    public void run() throws Exception {
        this.execute();
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        sendParticipationStatusChangeNotifications(con, daos);
        return null;
    }

    public void sendParticipationStatusChangeNotifications(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        DateTime now = TimeUtil.getCurrentDateTime();

        // TODO: 開催前のイベントだけiterateすれば充分
        DataIterator<Event> it = daos.getEventAccess().getIterator(con);
        try {
            while (it.hasNext()) {
                Event e = it.next();
                if (e == null) { continue; }
                String eventId = e.getId();
                if (eventId == null) { continue; }
                EventEx event = EventDAOFacade.getEventEx(con, daos, eventId);
                if (event == null) { continue; }

                if (!now.isBefore(event.getBeginDate())) { continue; }

                List<EventTicket> tickets = daos.getEventTicketAccess().findEventTicketsByEventId(con, eventId);
                for (EventTicket ticket : tickets)
                    sendChangeNotificationImpl(con, daos, ticket, eventId, event);
            }
        } finally {
            it.close();
        }
    }

    private void sendChangeNotificationImpl(PartakeConnection con, IPartakeDAOs daos, EventTicket ticket, String eventId, EventEx event) throws DAOException {
        List<UserTicketEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, daos, ticket, event);
        EventTicketHolderList list = ticket.calculateParticipationList(event, participations);

        // TODO: ここのソース汚い。同一化できる。とくに、あとの２つは一緒。
        List<String> userIdsToBeEnrolled = new ArrayList<String>();
        for (UserTicket p : list.getEnrolledParticipations()) {
            // -- 参加者向

            ModificationStatus status = p.getModificationStatus();
            if (status == null) { continue; }

            switch (status) {
            case CHANGED: { // 自分自身の力で変化させていた場合は status を enrolled にのみ変更して対応
                updateLastStatus(con, daos, eventId, p, ModificationStatus.ENROLLED);
                break;
            }
            case NOT_ENROLLED: {
                userIdsToBeEnrolled.add(p.getUserId());
                updateLastStatus(con, daos, eventId, p, ModificationStatus.ENROLLED);
                break;
            }
            case ENROLLED:
                break;
            }
        }
        if (!userIdsToBeEnrolled.isEmpty()) {
            String eventNotificationId = daos.getEventNotificationAccess().getFreshId(con);
            EventTicketNotification notification = new EventTicketNotification(eventNotificationId, ticket.getId(), ticket.getEventId(), userIdsToBeEnrolled, NotificationType.BECAME_TO_BE_ENROLLED, TimeUtil.getCurrentDateTime());
            daos.getEventNotificationAccess().put(con, notification);

            for (String userId : userIdsToBeEnrolled) {
                String userNotificationid = daos.getUserNotificationAccess().getFreshId(con);
                UserNotification userNotification = new UserNotification(userNotificationid, ticket.getId(), userId, NotificationType.BECAME_TO_BE_ENROLLED, MessageDelivery.INQUEUE, TimeUtil.getCurrentDateTime(), null);
                daos.getUserNotificationAccess().put(con, userNotification);

                String envelopeId = daos.getMessageEnvelopeAccess().getFreshId(con);
                MessageEnvelope envelope = MessageEnvelope.createForUserNotification(envelopeId, userNotificationid, null);
                daos.getMessageEnvelopeAccess().put(con, envelope);
            }
        }

        List<String> userIdsToBeCancelled = new ArrayList<String>();
        for (UserTicket p : list.getSpareParticipations()) {
            ModificationStatus status = p.getModificationStatus();
            if (status == null) { continue; }

            switch (status) {
            case CHANGED: // 自分自身の力で変化させていた場合は status を not_enrolled にのみ変更して対応
                updateLastStatus(con, daos, eventId, p, ModificationStatus.NOT_ENROLLED);
                break;
            case NOT_ENROLLED:
                break;
            case ENROLLED:
                updateLastStatus(con, daos, eventId, p, ModificationStatus.NOT_ENROLLED);
                userIdsToBeCancelled.add(p.getUserId());
                break;
            }
        }

        for (UserTicket p : list.getCancelledParticipations()) {
            ModificationStatus status = p.getModificationStatus();
            if (status == null) { continue; }

            switch (status) {
            case CHANGED: // 自分自身の力で変化させていた場合は status を not_enrolled にのみ変更して対応
                updateLastStatus(con, daos, eventId, p, ModificationStatus.NOT_ENROLLED);
                break;
            case NOT_ENROLLED:
                break;
            case ENROLLED:
                updateLastStatus(con, daos, eventId, p, ModificationStatus.NOT_ENROLLED);
                userIdsToBeCancelled.add(p.getUserId());
                break;
            }
        }

        if (!userIdsToBeCancelled.isEmpty()) {
            String notificationId = daos.getEventNotificationAccess().getFreshId(con);
            EventTicketNotification notification = new EventTicketNotification(notificationId, ticket.getId(), ticket.getEventId(), userIdsToBeEnrolled, NotificationType.BECAME_TO_BE_CANCELLED, TimeUtil.getCurrentDateTime());
            daos.getEventNotificationAccess().put(con, notification);

            for (String userId : userIdsToBeCancelled) {
                String userNotificationid = daos.getUserNotificationAccess().getFreshId(con);
                UserNotification userNotification = new UserNotification(userNotificationid, ticket.getId(), userId, NotificationType.BECAME_TO_BE_CANCELLED, MessageDelivery.INQUEUE, TimeUtil.getCurrentDateTime(), null);
                daos.getUserNotificationAccess().put(con, userNotification);

                String envelopeId = daos.getMessageEnvelopeAccess().getFreshId(con);
                MessageEnvelope envelope = MessageEnvelope.createForUserNotification(envelopeId, userNotificationid, null);
                daos.getMessageEnvelopeAccess().put(con, envelope);
            }
        }
    }

    private void updateLastStatus(PartakeConnection con, IPartakeDAOs daos, String eventId, UserTicket enrollment, ModificationStatus status) throws DAOException {
        UserTicket newEnrollment = new UserTicket(enrollment);
        newEnrollment.setModificationStatus(status);
        daos.getEnrollmentAccess().put(con, newEnrollment);
    }

}
