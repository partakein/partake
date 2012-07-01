package in.partake.controller.api.event;

import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventSendMessagePermission;
import in.partake.model.EventEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.EventMessage;
import in.partake.model.dto.Message;
import in.partake.model.dto.MessageEnvelope;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.UserReceivedMessage;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.resource.UserErrorCode;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import play.mvc.Result;

public class SendMessageAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new SendMessageAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();

        String subject = getParameter("subject");
        String body = getParameter("body");

        if (StringUtils.isBlank(subject))
            return renderInvalid(UserErrorCode.MISSING_MESSAGE_SUBJECT);
        if (subject.length() > 100)
            return renderInvalid(UserErrorCode.INVALID_MESSAGE_SUBJECT_TOOLONG);

        if (StringUtils.isBlank(body))
            return renderInvalid(UserErrorCode.MISSING_MESSAGE);
        if (body.length() > 1000)
            return renderInvalid(UserErrorCode.INVALID_MESSAGE_TOOLONG);

        try {
            new SendMessageTransaction(user, eventId, subject, body).execute();
            return renderOK();
        } catch (PartakeException e) {
            return renderException(e);
        }
    }
}

class SendMessageTransaction extends Transaction<Void> {
    private UserEx sender;
    private String eventId;
    private String subject;
    private String body;

    public SendMessageTransaction(UserEx sender, String eventId, String subject, String body) {
        this.sender = sender;
        this.eventId = eventId;
        this.subject = subject;
        this.body = body;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        sendMessage(con, daos, sender, eventId, subject, body);
        return null;
    }

    private void sendMessage(PartakeConnection con, IPartakeDAOs daos, UserEx sender, String eventId, String subject, String body) throws PartakeException, DAOException {
        assert sender != null;
        assert eventId != null;
        assert subject != null;
        assert body != null;

        EventEx event = EventDAOFacade.getEventEx(con, daos, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        if (!EventSendMessagePermission.check(event, sender))
            throw new PartakeException(UserErrorCode.INVALID_PROHIBITED);

        ensureNotSendingTooMuch(con, daos);

        UUID messageId = daos.getMessageAccess().getFreshId(con);
        Message message = new Message(messageId, subject, body, TimeUtil.getCurrentDateTime(), null);
        daos.getMessageAccess().put(con, message);

        String eventMessageId = daos.getEventMessageAccess().getFreshId(con);
        EventMessage eventMessage = new EventMessage(eventMessageId, eventId, sender.getId(), messageId.toString(), TimeUtil.getCurrentDateTime(), null);
        daos.getEventMessageAccess().put(con, eventMessage);

        List<UserTicket> participations = daos.getEnrollmentAccess().findByEventId(con, eventId, 0, Integer.MAX_VALUE);
        for (UserTicket participation : participations) {
            if (!participation.getStatus().isEnrolled())
                continue;

            UserPreference pref = daos.getUserPreferenceAccess().find(con, participation.getUserId());
            if (pref == null)
                pref = UserPreference.getDefaultPreference(participation.getUserId());

            MessageDelivery delivery = pref.isReceivingTwitterMessage() ? MessageDelivery.INQUEUE : MessageDelivery.NOT_DELIVERED;

            UUID userMessageId = daos.getUserReceivedMessageAccess().getFreshId(con);
            UserReceivedMessage userMessage = new UserReceivedMessage(userMessageId, sender.getId(), participation.getUserId(), eventId, messageId.toString(),
                    false, delivery, null, null, TimeUtil.getCurrentDateTime(), null);
            daos.getUserReceivedMessageAccess().put(con, userMessage);

            if (delivery == MessageDelivery.NOT_DELIVERED)
                continue;

            String envelopeId = daos.getMessageEnvelopeAccess().getFreshId(con);
            MessageEnvelope envelope = MessageEnvelope.createForUserMessage(envelopeId, userMessageId.toString(), null);
            daos.getMessageEnvelopeAccess().put(con, envelope);
        }
    }

    private void ensureNotSendingTooMuch(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        DateTime currentTime = TimeUtil.getCurrentDateTime();

        // ５つ取ってきて、制約を満たしているかどうか確認する。
        List<EventMessage> eventMessages = daos.getEventMessageAccess().findByEventId(con, eventId, 0, 5);

        if (eventMessages.size() >= 3) {
            EventMessage eventMessage = eventMessages.get(2);
            if (eventMessage != null) {
                DateTime msgDate = eventMessage.getCreatedAt();
                DateTime thresholdDate = new DateTime(msgDate.getTime() + 1000 * 60 * 60); // one hour later after the message was sent.
                if (currentTime.isBefore(thresholdDate)) // NG
                    throw new PartakeException(UserErrorCode.INVALID_MESSAGE_TOOMUCH);
            }
        }

        if (eventMessages.size() >= 5) {
            EventMessage eventMessage = eventMessages.get(2);
            if (eventMessage != null) {
                DateTime msgDate = eventMessage.getCreatedAt();
                DateTime thresholdDate = new DateTime(msgDate.getTime() + 1000 * 60 * 60 * 24); // one hour later after the message was sent.
                if (currentTime.isBefore(thresholdDate)) // NG
                    throw new PartakeException(UserErrorCode.INVALID_MESSAGE_TOOMUCH);
            }
        }
    }
}
