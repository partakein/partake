package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventTicketNotification;
import in.partake.model.dto.auxiliary.NotificationType;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EventTicketNotificationTestDataProvider extends TestDataProvider<EventTicketNotification> {
    @Override
    public EventTicketNotification create(long pkNumber, String pkSalt, int objNumber) {
        String id = new UUID(pkNumber, pkSalt.hashCode()).toString();
        return new EventTicketNotification(id, new UUID(0, 0), "eventId",  new ArrayList<String>(), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(objNumber));
    }

    @Override
    public List<EventTicketNotification> createSamples() {
        List<EventTicketNotification> array = new ArrayList<EventTicketNotification>();
        array.add(new EventTicketNotification(new UUID(0, 0).toString(), new UUID(1, 0), "eventId", Arrays.asList(new String[] { "" }), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0)));
        array.add(new EventTicketNotification(new UUID(0, 1).toString(), new UUID(1, 0), "eventId", Arrays.asList(new String[] { "" }), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0)));
        array.add(new EventTicketNotification(new UUID(0, 0).toString(), new UUID(1, 1), "eventId", Arrays.asList(new String[] { "" }), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0)));
        array.add(new EventTicketNotification(new UUID(0, 0).toString(), new UUID(1, 0), "eventId1", Arrays.asList(new String[] { "" }), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0)));
        array.add(new EventTicketNotification(new UUID(0, 0).toString(), new UUID(1, 0), "eventId", Arrays.asList(new String[] { "1" }), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0)));
        array.add(new EventTicketNotification(new UUID(0, 0).toString(), new UUID(1, 0), "eventId", Arrays.asList(new String[] { "" }), NotificationType.BECAME_TO_BE_ENROLLED, new DateTime(0)));
        array.add(new EventTicketNotification(new UUID(0, 0).toString(), new UUID(1, 0), "eventId", Arrays.asList(new String[] { "" }), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(1)));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        daos.getEventNotificationAccess().truncate(con);
    }
}
