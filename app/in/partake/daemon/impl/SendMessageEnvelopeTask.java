package in.partake.daemon.impl;

import in.partake.app.PartakeApp;
import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.daemon.IPartakeDaemonTask;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTwitterLinkAccess;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.Message;
import in.partake.model.dto.MessageEnvelope;
import in.partake.model.dto.UserTwitterLink;
import in.partake.model.dto.TwitterMessage;
import in.partake.model.dto.UserNotification;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.UserReceivedMessage;
import in.partake.model.dto.auxiliary.MessageDelivery;

import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import twitter4j.TwitterException;

class SendMessageEnvelopeTask extends Transaction<Void> implements IPartakeDaemonTask {
    private static final Logger logger = Logger.getLogger(SendMessageEnvelopeTask.class);

    @Override
    public String getName() {
        return "SendMessageEnvelopeTask";
    }

    @Override
    public void run() throws Exception {
        this.execute();
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        DataIterator<MessageEnvelope> it = daos.getMessageEnvelopeAccess().getIterator(con);
        try {
            while (it.hasNext()) {
                MessageEnvelope envelope = it.next();
                if (envelope == null) {
                    it.remove();
                    continue;
                }

                // InvalidAfter 後であれば、message を update して envelope を消去
                // TODO: Refine this code!
                if (envelope.getInvalidAfter() != null && envelope.getInvalidAfter().isBefore(TimeUtil.getCurrentDateTime())) {
                    logger.warn("run : envelope id " + envelope.getId() + " could not be sent : Time out.");
                    if (envelope.getUserMessageId() != null) {
                        UserReceivedMessage userMessage = daos.getUserReceivedMessageAccess().find(con, UUID.fromString(envelope.getUserMessageId()));
                        if (userMessage != null) {
                            UserReceivedMessage message = new UserReceivedMessage(userMessage);
                            message.setDelivery(MessageDelivery.FAIL);
                            message.setModifiedAt(TimeUtil.getCurrentDateTime());
                            daos.getUserReceivedMessageAccess().put(con, message);
                        }
                    }

                    if (envelope.getTwitterMessageId() != null) {
                        TwitterMessage twitterMessage = daos.getTwitterMessageAccess().find(con, envelope.getTwitterMessageId());
                        if (twitterMessage != null) {
                            TwitterMessage message = new TwitterMessage(twitterMessage);
                            message.setDelivery(MessageDelivery.FAIL);
                            message.setModifiedAt(TimeUtil.getCurrentDateTime());
                            daos.getTwitterMessageAccess().put(con, message);
                        }
                    }

                    if (envelope.getUserNotificationId() != null) {
                        UserNotification notification = daos.getUserNotificationAccess().find(con, envelope.getUserNotificationId());
                        if (notification != null) {
                            UserNotification message = new UserNotification(notification);
                            message.setDelivery(MessageDelivery.FAIL);
                            message.setModifiedAt(TimeUtil.getCurrentDateTime());
                            daos.getUserNotificationAccess().put(con, message);
                        }
                    }

                    it.remove();
                    continue;
                }

                // tryAfter 前であれば送らない。
                if (envelope.getTryAfter() != null && !envelope.getTryAfter().isBefore(TimeUtil.getCurrentDateTime())) {
                    logger.debug("run : envelope id " + envelope.getId() + " should be sent after " + envelope.getTryAfter());
                    continue;
                }

                if (envelope.getTwitterMessageId() != null)
                    sendTwitterMessage(con, daos, it, envelope);
                else if (envelope.getUserMessageId() != null)
                    sendUserMessage(con, daos, it, envelope);
                else if (envelope.getUserNotificationId() != null)
                    sendUserNotification(con, daos, it, envelope);
                else {
                    // Hmm... shouldn't happen.
                    logger.error("Shouldn't happen");
                    assert false;
                    it.remove();
                }
            }
        } finally {
            it.close();
        }

        return null;
    }

    /**
     * Envelope を送信する。true を返すと送ることができた / もうこれ以上送ってはいけないという意味になる。
     * @param envelope
     * @return
     */
    private void sendUserMessage(PartakeConnection con, IPartakeDAOs daos, DataIterator<MessageEnvelope> it, MessageEnvelope envelope) throws DAOException {
        UserReceivedMessage userMessage = daos.getUserReceivedMessageAccess().find(con, UUID.fromString(envelope.getUserMessageId()));
        if (userMessage == null) {
            didSendUserMessage(con, daos, it, envelope, userMessage, MessageDelivery.FAIL);
            return;
        }

        UserEx receiver = UserDAOFacade.getUserEx(con, daos, userMessage.getReceiverId());
        if (receiver == null) {
            didSendUserMessage(con, daos, it, envelope, userMessage, MessageDelivery.FAIL);
            return;
        }

        UserPreference pref = daos.getUserPreferenceAccess().find(con, receiver.getId());
        if (pref == null)
            pref = UserPreference.getDefaultPreference(receiver.getId());

        // twitter message を受け取らない設定になっていれば送らない。
        if (!pref.isReceivingTwitterMessage()) {
            didSendUserMessage(con, daos, it, envelope, userMessage, MessageDelivery.NOT_DELIVERED);
            return;
        }

        UserTwitterLink twitterLinkage = receiver.getTwitterLinkage();
        if (twitterLinkage == null || !twitterLinkage.isAuthorized()) {
            logger.warn("sendDirectMessage : envelope id " + envelope.getId() + " could not be sent : No access token");
            didSendUserMessage(con, daos, it, envelope, userMessage, MessageDelivery.FAIL);
            return;
        }

        try {
            Message message = daos.getMessageAccess().find(con, UUID.fromString(userMessage.getMessageId()));
            long twitterId = twitterLinkage.getTwitterId();

            Event event = null;
            if (userMessage.getEventId() != null)
                event = daos.getEventAccess().find(con, userMessage.getEventId());

            String url = "http://partake.in/messages/" + userMessage.getId();
            String messageBody;
            if (event != null) {
                int rest = 140;
                String format = "[PRTK] %s 「%s」に関する新着メッセージがあります。 : %s";
                rest -= Util.codePointCount(format);

                rest -= 20; // for URL. Twitter will shorten URL if its length is more than 20.

                String title = Util.shorten(event.getTitle(), 30);
                rest -= Util.codePointCount(title);

                String body = Util.shorten(message.getSubject(), rest);
                messageBody = String.format(format, url, title, body);
            } else {
                int rest = 140;

                String format = "[PRTK] %s 新着メッセージがあります。: %s";
                rest -= Util.codePointCount(format);

                rest -= 20; // for URL.

                String subject = Util.shorten(message.getSubject(), rest);

                messageBody = String.format(format, url, subject);
            }

            PartakeApp.getTwitterService().sendDirectMesage(
                    twitterLinkage.getAccessToken(), twitterLinkage.getAccessTokenSecret(), twitterId, messageBody);
            didSendUserMessage(con, daos, it, envelope, userMessage, MessageDelivery.SUCCESS);
            logger.info("sendDirectMessage : direct message has been sent to " + twitterLinkage.getScreenName());
        } catch (NumberFormatException e) {
            logger.error("twitterId has not a number.", e);
            didSendUserMessage(con, daos, it, envelope, userMessage, MessageDelivery.FAIL);
        } catch (TwitterException e) {
            if (updateEnvelopeByTwitterException(con, daos, receiver, envelope, it, e))
                didSendUserMessage(con, daos, it, envelope, userMessage, MessageDelivery.FAIL);
        }
    }

    private void didSendUserMessage(PartakeConnection con, IPartakeDAOs daos,
            DataIterator<MessageEnvelope> it, MessageEnvelope envelope, UserReceivedMessage message, MessageDelivery delivery) throws DAOException {
        UserReceivedMessage userMessage = new UserReceivedMessage(message);
        userMessage.setDelivery(delivery);
        userMessage.setModifiedAt(TimeUtil.getCurrentDateTime());

        daos.getUserReceivedMessageAccess().put(con, userMessage);
        it.remove();
    }

    private void sendTwitterMessage(PartakeConnection con, IPartakeDAOs daos, DataIterator<MessageEnvelope> it, MessageEnvelope envelope) throws DAOException {
        TwitterMessage message = daos.getTwitterMessageAccess().find(con, envelope.getTwitterMessageId());

        if (message == null) {
            logger.warn("SendMessageEnvelopeTask.sendTwitterMessage : message was null.");

            // Since the message was null, we cannot update the message status. So we silently remove this MessageEnvelope.
            it.remove();
            return;
        }

        UserEx sender = UserDAOFacade.getUserEx(con, daos, message.getUserId());
        if (sender == null) {
            logger.warn("sendTwitterMessage : sender is null.");
            failedSendingTwitterMessage(con, daos, it, envelope, message);
            return;
        }

        UserTwitterLink twitterLinkage = sender.getTwitterLinkage();
        if (twitterLinkage == null || !twitterLinkage.isAuthorized()) {
            logger.warn("sendTwitterMessage : envelope id " + envelope.getId() + " could not be sent : No access token");
            failedSendingTwitterMessage(con, daos, it, envelope, message);
            return;
        }

        try {
            PartakeApp.getTwitterService().updateStatus(twitterLinkage.getAccessToken(), twitterLinkage.getAccessTokenSecret(), message.getMessage());
            succeededSendingTwitterMessage(con, daos, it, envelope, message);
            return;
        } catch (TwitterException e) {
            if (updateEnvelopeByTwitterException(con, daos, sender, envelope, it, e))
                failedSendingTwitterMessage(con, daos, it, envelope, message);
        }
    }

    private void succeededSendingTwitterMessage(PartakeConnection con, IPartakeDAOs daos, DataIterator<MessageEnvelope> it, MessageEnvelope envelope, TwitterMessage message) throws DAOException {
        TwitterMessage twitterMessage = new TwitterMessage(message);
        twitterMessage.setDelivery(MessageDelivery.SUCCESS);
        twitterMessage.setModifiedAt(TimeUtil.getCurrentDateTime());

        daos.getTwitterMessageAccess().put(con, twitterMessage);
        it.remove();
    }

    private void failedSendingTwitterMessage(PartakeConnection con, IPartakeDAOs daos, DataIterator<MessageEnvelope> it, MessageEnvelope envelope, TwitterMessage message) throws DAOException {
        TwitterMessage twitterMessage = new TwitterMessage(message);
        twitterMessage.setDelivery(MessageDelivery.FAIL);
        twitterMessage.setModifiedAt(TimeUtil.getCurrentDateTime());

        daos.getTwitterMessageAccess().put(con, twitterMessage);
        it.remove();
    }

    private void sendUserNotification(PartakeConnection con, IPartakeDAOs daos, DataIterator<MessageEnvelope> it, MessageEnvelope envelope) throws DAOException {
        UserNotification notification = daos.getUserNotificationAccess().find(con, envelope.getUserNotificationId());
        if (notification == null) {
            failedSendingUserNotification(con, daos, it, envelope, notification);
            return;
        }

        UserEx sender = UserDAOFacade.getUserEx(con, daos, notification.getUserId());
        if (sender == null) {
            logger.warn("sendTwitterMessage : sender is null.");
            failedSendingUserNotification(con, daos, it, envelope, notification);
            return;
        }

        UserTwitterLink twitterLinkage = sender.getTwitterLinkage();
        if (twitterLinkage == null || !twitterLinkage.isAuthorized()) {
            logger.warn("sendTwitterMessage : envelope id " + envelope.getId() + " could not be sent : No access token");
            failedSendingUserNotification(con, daos, it, envelope, notification);
            return;
        }

        EventTicket ticket = daos.getEventTicketAccess().find(con, notification.getTicketId());
        if (ticket == null) {
            failedSendingUserNotification(con, daos, it, envelope, notification);
            return;
        }

        Event event = daos.getEventAccess().find(con, ticket.getEventId());
        if (event == null) {
            failedSendingUserNotification(con, daos, it, envelope, notification);
            return;
        }

        String messageBody;
        switch (notification.getNotificationType()) {
        case EVENT_ONEDAY_BEFORE_REMINDER: {
            int rest = 140;
            String format = "[PRTK] イベントの１日前です。あなたの参加は確定しています。 %s : %s";
            rest -= Util.codePointCount(format);
            rest -= 20; // For URL
            String title = Util.shorten(event.getTitle(), rest);
            messageBody = String.format(format, event.getEventURL(), title);
            break;
        }
        case HALF_DAY_BEFORE_REMINDER_FOR_RESERVATION: {
            int rest = 140;
            String format = "[PRTK] 締め切り１２時間前です。参加・不参加を確定してください。 %s : %s";
            rest -= Util.codePointCount(format);
            rest -= 20; // For URL
            String title = Util.shorten(event.getTitle(), rest);
            messageBody = String.format(format, event.getEventURL(), title);
            break;
        }
        case ONE_DAY_BEFORE_REMINDER_FOR_RESERVATION: {
            int rest = 140;
            String format = "[PRTK] 締め切り１日前です。参加・不参加を確定してください。 %s : %s";
            rest -= Util.codePointCount(format);
            rest -= 20; // For URL
            String title = Util.shorten(event.getTitle(), rest);
            messageBody = String.format(format, event.getEventURL(), title);
            break;
        }
        case BECAME_TO_BE_CANCELLED: {
            int rest = 140;
            String format = "[PRTK] 参加者から補欠へ繰り下がりました。 %s : %s";
            rest -= Util.codePointCount(format);
            rest -= 20; // For URL
            String title = Util.shorten(event.getTitle(), rest);
            messageBody = String.format(format, event.getEventURL(), title);
            break;
        }
        case BECAME_TO_BE_ENROLLED: {
            int rest = 140;
            String format = "[PRTK] 補欠から参加者へ繰り上がりました。 %s : %s";
            rest -= Util.codePointCount(format);
            rest -= 20; // For URL
            String title = Util.shorten(event.getTitle(), rest);
            messageBody = String.format(format, event.getEventURL(), title);
            break;
        }
        default:
            assert false;
            failedSendingUserNotification(con, daos, it, envelope, notification);
            return;
        }

        try {
            PartakeApp.getTwitterService().sendDirectMesage(
                    twitterLinkage.getAccessToken(), twitterLinkage.getAccessTokenSecret(), twitterLinkage.getTwitterId(), messageBody);
            succeededSendingUserNotification(con, daos, it, envelope, notification);
            return;
        } catch (TwitterException e) {
            if (updateEnvelopeByTwitterException(con, daos, sender, envelope, it, e))
                failedSendingUserNotification(con, daos, it, envelope, notification);
        }
    }

    private void succeededSendingUserNotification(PartakeConnection con, IPartakeDAOs daos, DataIterator<MessageEnvelope> it, MessageEnvelope envelope, UserNotification notification) throws DAOException {
        UserNotification userNotification = new UserNotification(notification);
        userNotification.setDelivery(MessageDelivery.SUCCESS);
        userNotification.setModifiedAt(TimeUtil.getCurrentDateTime());

        daos.getUserNotificationAccess().put(con, userNotification);
        it.remove();
    }

    private void failedSendingUserNotification(PartakeConnection con, IPartakeDAOs daos, DataIterator<MessageEnvelope> it, MessageEnvelope envelope, UserNotification notification) throws DAOException {
        UserNotification userNotification = new UserNotification(notification);
        userNotification.setDelivery(MessageDelivery.FAIL);
        userNotification.setModifiedAt(TimeUtil.getCurrentDateTime());

        daos.getUserNotificationAccess().put(con, userNotification);
        it.remove();
    }

    /**
     * @return true if <code>it</code> was removed.
     */
    private boolean updateEnvelopeByTwitterException(PartakeConnection con, IPartakeDAOs daos,
            UserEx user, MessageEnvelope envelope, DataIterator<MessageEnvelope> it, TwitterException e) throws DAOException {
        if (e.isCausedByNetworkIssue()) {
            logger.warn("Twitter Unreachable?", e);
            // Retry after 10 minutes later.
            DateTime retryAfter = new DateTime(TimeUtil.getCurrentTime() + 600 * 1000);
            MessageEnvelope newEnvelope = new MessageEnvelope(envelope);
            newEnvelope.updateForSendingFailure(retryAfter);
            it.update(newEnvelope);
            return false;
        }

        if (e.exceededRateLimitation()) {
            logger.warn("Twitter Rate Limination : " + envelope.getId() + " was failed to deliver.", e);
            DateTime retryAfter = new DateTime(TimeUtil.getCurrentTime() + e.getRetryAfter() * 1000);

            MessageEnvelope newEnvelope = new MessageEnvelope(envelope);
            newEnvelope.updateForSendingFailure(retryAfter);
            it.update(newEnvelope);
            return false;
        }

        if (e.getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
            markAsUnauthorizedUser(con, daos, user);
            logger.info("Unauthorized User : " + envelope.getId() + " was failed to deliver.", e);

            // We cannot send envelopes to unauthorized user.
            it.remove();
            return true;
        }

        // Unknown error. Retry.
        logger.warn("Unknown Error : " + envelope.getId() + " was failed to deliver.", e);
        // Retry after 5 minutes later.
        DateTime retryAfter = new DateTime(TimeUtil.getCurrentTime() + 600 * 1000);
        MessageEnvelope newEnvelope = new MessageEnvelope(envelope);
        newEnvelope.updateForSendingFailure(retryAfter);
        it.update(newEnvelope);
        return false;
    }

    private void markAsUnauthorizedUser(PartakeConnection con, IPartakeDAOs daos, UserEx user) {
        IUserTwitterLinkAccess access = daos.getTwitterLinkageAccess();
        UserTwitterLink linkage = new UserTwitterLink(user.getTwitterLinkage());
        linkage.markAsUnauthorized();

        try {
            // TODO UserExが参照するTwitterLinkageが更新されたため、UserExのキャッシュを破棄あるいは更新する必要がある
            access.put(con, linkage);
        } catch (DAOException ignore) {
            logger.warn("DAOException is thrown but it's ignored.", ignore);
        }
    }
}
