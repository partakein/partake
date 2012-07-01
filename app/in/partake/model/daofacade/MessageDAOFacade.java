package in.partake.model.daofacade;

import in.partake.base.TimeUtil;
import in.partake.model.EventMessageEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.UserMessageEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventMessage;
import in.partake.model.dto.Message;
import in.partake.model.dto.MessageEnvelope;
import in.partake.model.dto.TwitterMessage;
import in.partake.model.dto.User;
import in.partake.model.dto.UserReceivedMessage;
import in.partake.model.dto.auxiliary.MessageDelivery;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageDAOFacade {

    public static UserMessageEx findUserReceivedMessage(PartakeConnection con, IPartakeDAOs daos, UUID messageId) throws DAOException {
        UserReceivedMessage receivedMessage = daos.getUserReceivedMessageAccess().find(con, messageId);
        if (receivedMessage == null)
            return null;

        UserEx sender = UserDAOFacade.getUserEx(con, daos, receivedMessage.getSenderId());
        if (sender == null)
            return null;

        Event event = daos.getEventAccess().find(con, receivedMessage.getEventId());
        if (event == null)
            return null;

        Message message = daos.getMessageAccess().find(con, UUID.fromString(receivedMessage.getMessageId()));
        if (message == null)
            return null;

        return new UserMessageEx(receivedMessage, sender, event, message);
    }

    public static List<UserMessageEx> findUserMessageExByReceiverId(PartakeConnection con, IPartakeDAOs daos, String userId, int offset, int limit) throws DAOException {
        List<UserReceivedMessage> userMessages = daos.getUserReceivedMessageAccess().findByReceiverId(con, userId, offset, limit);
        List<UserMessageEx> userMessageExs = new ArrayList<UserMessageEx>();
        for (UserReceivedMessage userMessage : userMessages) {
            if (userMessage == null)
                continue;

            UserEx sender = UserDAOFacade.getUserEx(con, daos, userMessage.getSenderId());
            if (sender == null)
                continue;

            Event event = daos.getEventAccess().find(con, userMessage.getEventId());
            if (event == null)
                return null;


            Message message = daos.getMessageAccess().find(con, UUID.fromString(userMessage.getMessageId()));
            if (message == null)
                continue;

            UserMessageEx messageEx = new UserMessageEx(userMessage, sender, event, message);
            userMessageExs.add(messageEx);
        }

        return userMessageExs;
    }

    public static List<EventMessageEx> findEventMessageExs(PartakeConnection con, IPartakeDAOs daos, String eventId, int offset, int limit) throws DAOException {
        List<EventMessage> messages = daos.getEventMessageAccess().findByEventId(con, eventId, 0, 100);
        List<EventMessageEx> messageExs = new ArrayList<EventMessageEx>();
        for (EventMessage eventMessage : messages) {
            if (eventMessage == null)
                continue;

            UserEx sender = UserDAOFacade.getUserEx(con, daos, eventMessage.getSenderId());
            if (sender == null)
                continue;

            Message message = daos.getMessageAccess().find(con, UUID.fromString(eventMessage.getMessageId()));
            if (message == null)
                continue;

            EventMessageEx ex = new EventMessageEx(eventMessage, sender, message);
            messageExs.add(ex);
        }

        return messageExs;
    }



    public static void tweetMessageImpl(PartakeConnection con, IPartakeDAOs daos, User user, String messageStr) throws DAOException {
        String twitterMessageId = daos.getTwitterMessageAccess().getFreshId(con);
        TwitterMessage message = new TwitterMessage(twitterMessageId, user.getId(), messageStr, MessageDelivery.INQUEUE, TimeUtil.getCurrentDateTime(), null);
        daos.getTwitterMessageAccess().put(con, message);

        String envelopeId = daos.getMessageEnvelopeAccess().getFreshId(con);
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId, twitterMessageId, null);
        daos.getMessageEnvelopeAccess().put(con, envelope);
    }

}
