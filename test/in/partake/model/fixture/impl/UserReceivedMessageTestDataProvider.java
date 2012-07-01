package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserReceivedMessageAccess;
import in.partake.model.dto.UserReceivedMessage;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserReceivedMessageTestDataProvider extends TestDataProvider<UserReceivedMessage> {
    @Override
    public UserReceivedMessage create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new UserReceivedMessage(uuid, "senderId", "receiverId", "eventId", "messageId",
                false, MessageDelivery.SUCCESS, null, null, new DateTime(objNumber), null);
    }

    @Override
    public List<UserReceivedMessage> createSamples() {
        List<UserReceivedMessage> array = new ArrayList<UserReceivedMessage>();
        array.add(new UserReceivedMessage(new UUID(0, 0), "senderId", "receiverId", "eventId", "messageId", false, MessageDelivery.SUCCESS, null, null, new DateTime(0L), null));
        array.add(new UserReceivedMessage(new UUID(0, 1), "senderId", "receiverId", "eventId", "messageId", false, MessageDelivery.SUCCESS, null, null, new DateTime(0L), null));
        array.add(new UserReceivedMessage(new UUID(0, 0), "senderId1", "receiverId", "eventId", "messageId", false, MessageDelivery.SUCCESS, null, null, new DateTime(0L), null));
        array.add(new UserReceivedMessage(new UUID(0, 0), "senderId", "receiverId1", "eventId", "messageId", false, MessageDelivery.SUCCESS, null, null, new DateTime(0L), null));
        array.add(new UserReceivedMessage(new UUID(0, 0), "senderId", "receiverId", "eventId1", "messageId", false, MessageDelivery.SUCCESS, null, null, new DateTime(0L), null));
        array.add(new UserReceivedMessage(new UUID(0, 0), "senderId", "receiverId", "eventId", "messageId1", false, MessageDelivery.SUCCESS, null, null, new DateTime(0L), null));
        array.add(new UserReceivedMessage(new UUID(0, 0), "senderId", "receiverId", "eventId", "messageId", true, MessageDelivery.SUCCESS, null, null, new DateTime(0L), null));
        array.add(new UserReceivedMessage(new UUID(0, 0), "senderId", "receiverId", "eventId", "messageId", false, MessageDelivery.FAIL, null, null, new DateTime(0L), null));
        array.add(new UserReceivedMessage(new UUID(0, 0), "senderId", "receiverId", "eventId", "messageId", false, MessageDelivery.SUCCESS, new DateTime(1L), null, new DateTime(0L), null));
        array.add(new UserReceivedMessage(new UUID(0, 0), "senderId", "receiverId", "eventId", "messageId", false, MessageDelivery.SUCCESS, null, new DateTime(1L), new DateTime(0L), null));
        array.add(new UserReceivedMessage(new UUID(0, 0), "senderId", "receiverId", "eventId", "messageId", false, MessageDelivery.SUCCESS, null, null, new DateTime(1L), null));
        array.add(new UserReceivedMessage(new UUID(0, 0), "senderId", "receiverId", "eventId", "messageId", false, MessageDelivery.SUCCESS, null, null, new DateTime(0L), new DateTime(1L)));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IUserReceivedMessageAccess dao = daos.getUserReceivedMessageAccess();
        dao.truncate(con);

        dao.put(con, new UserReceivedMessage(USER_RECEIVED_MESSAGE_INQUEUE_ID,
                DEFAULT_SENDER_ID, DEFAULT_RECEIVER_ID, DEFAULT_EVENT_ID, DEFAULT_MESSAGE_ID.toString(),
                false, MessageDelivery.INQUEUE, null, null, new DateTime(0L), null));
    }
}
