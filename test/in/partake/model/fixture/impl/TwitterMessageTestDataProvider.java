package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.ITwitterMessageAccess;
import in.partake.model.dto.TwitterMessage;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TwitterMessageTestDataProvider extends TestDataProvider<TwitterMessage> {
    @Override
    public TwitterMessage create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new TwitterMessage(uuid.toString(), "userId", "message", MessageDelivery.SUCCESS, new DateTime(objNumber), null);
    }

    @Override
    public List<TwitterMessage> createSamples() {
        List<TwitterMessage> array = new ArrayList<TwitterMessage>();
        array.add(new TwitterMessage(new UUID(0, 0).toString(), "userId", "message", MessageDelivery.SUCCESS, new DateTime(0), null));
        array.add(new TwitterMessage(new UUID(0, 1).toString(), "userId", "message", MessageDelivery.SUCCESS, new DateTime(0), null));
        array.add(new TwitterMessage(new UUID(0, 0).toString(), "userId1", "message", MessageDelivery.SUCCESS, new DateTime(0), null));
        array.add(new TwitterMessage(new UUID(0, 0).toString(), "userId", "message1", MessageDelivery.FAIL, new DateTime(0), null));
        array.add(new TwitterMessage(new UUID(0, 0).toString(), "userId", "message", MessageDelivery.SUCCESS, new DateTime(1), new DateTime(0)));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        ITwitterMessageAccess dao = daos.getTwitterMessageAccess();
        dao.truncate(con);

        // Do not add TWITTER_MESSAGE_NONEXIST_ID here.
        dao.put(con, new TwitterMessage(TWITTER_MESSAGE_INQUEUE_ID, DEFAULT_USER_ID, "message", MessageDelivery.INQUEUE, new DateTime(0), null));
    }
}
