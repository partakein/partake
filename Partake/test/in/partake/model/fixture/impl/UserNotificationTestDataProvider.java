package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserNotificationAccess;
import in.partake.model.dto.UserNotification;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.model.dto.auxiliary.NotificationType;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserNotificationTestDataProvider extends TestDataProvider<UserNotification> {
    @Override
    public UserNotification create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new UserNotification(uuid.toString(), new UUID(1, 0), "userId", NotificationType.BECAME_TO_BE_CANCELLED, MessageDelivery.SUCCESS, new DateTime(objNumber), null);
    }

    @Override
    public List<UserNotification> createSamples() {
        List<UserNotification> array = new ArrayList<UserNotification>();
        array.add(new UserNotification(new UUID(0, 0).toString(), new UUID(1, 0), "userId", NotificationType.BECAME_TO_BE_CANCELLED, MessageDelivery.SUCCESS, new DateTime(0), null));
        array.add(new UserNotification(new UUID(0, 1).toString(), new UUID(1, 0), "userId", NotificationType.BECAME_TO_BE_CANCELLED, MessageDelivery.SUCCESS, new DateTime(0), null));
        array.add(new UserNotification(new UUID(0, 0).toString(), new UUID(1, 1), "userId", NotificationType.BECAME_TO_BE_CANCELLED, MessageDelivery.SUCCESS, new DateTime(0), null));
        array.add(new UserNotification(new UUID(0, 0).toString(), new UUID(1, 0), "userId1", NotificationType.BECAME_TO_BE_CANCELLED, MessageDelivery.SUCCESS, new DateTime(0), null));
        array.add(new UserNotification(new UUID(0, 0).toString(), new UUID(1, 0), "userId", NotificationType.BECAME_TO_BE_ENROLLED, MessageDelivery.SUCCESS, new DateTime(0), null));
        array.add(new UserNotification(new UUID(0, 0).toString(), new UUID(1, 0), "userId", NotificationType.BECAME_TO_BE_CANCELLED, MessageDelivery.FAIL, new DateTime(0), null));
        array.add(new UserNotification(new UUID(0, 0).toString(), new UUID(1, 0), "userId", NotificationType.BECAME_TO_BE_CANCELLED, MessageDelivery.SUCCESS, new DateTime(1), null));
        array.add(new UserNotification(new UUID(0, 0).toString(), new UUID(1, 0), "userId", NotificationType.BECAME_TO_BE_CANCELLED, MessageDelivery.SUCCESS, new DateTime(0), new DateTime(1)));
        return array;
    }

    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IUserNotificationAccess dao = daos.getUserNotificationAccess();
        dao.truncate(con);

        dao.put(con, new UserNotification(USER_NOTIFICATION_INQUEUE_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_USER_ID, NotificationType.EVENT_ONEDAY_BEFORE_REMINDER, MessageDelivery.INQUEUE, new DateTime(0), null));
    }
}
