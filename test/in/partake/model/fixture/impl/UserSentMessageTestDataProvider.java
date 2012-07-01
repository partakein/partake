package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserSentMessage;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UserSentMessageTestDataProvider extends TestDataProvider<UserSentMessage> {
    @Override
    public UserSentMessage create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        List<String> receiverIds = new ArrayList<String>();
        return new UserSentMessage(uuid, "senderId", receiverIds, "eventId", "messageId", new DateTime(objNumber), null);
    }

    @Override
    public List<UserSentMessage> createSamples() {
        List<UserSentMessage> array = new ArrayList<UserSentMessage>();
        array.add(new UserSentMessage(new UUID(0, 0), "senderId", new ArrayList<String>(), "eventId", "messageId", new DateTime(0L), null));
        array.add(new UserSentMessage(new UUID(0, 1), "senderId", new ArrayList<String>(), "eventId", "messageId", new DateTime(0L), null));
        array.add(new UserSentMessage(new UUID(0, 0), "senderId1", new ArrayList<String>(), "eventId", "messageId", new DateTime(0L), null));
        array.add(new UserSentMessage(new UUID(0, 0), "senderId", Arrays.asList(new String[] { "hoge" }), "eventId", "messageId", new DateTime(0L), null));
        array.add(new UserSentMessage(new UUID(0, 0), "senderId", new ArrayList<String>(), "eventId1", "messageId", new DateTime(0L), null));
        array.add(new UserSentMessage(new UUID(0, 0), "senderId", new ArrayList<String>(), "eventId", "messageId1", new DateTime(0L), null));
        array.add(new UserSentMessage(new UUID(0, 0), "senderId", new ArrayList<String>(), "eventId", "messageId", new DateTime(1L), null));
        array.add(new UserSentMessage(new UUID(0, 0), "senderId", new ArrayList<String>(), "eventId", "messageId", new DateTime(0L), new DateTime(2L)));
        return array;
    }

    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        daos.getUserSentMessageAccess().truncate(con);
    }
}
