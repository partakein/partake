package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventMessage;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventMessageTestDataProvider extends TestDataProvider<EventMessage> {
    @Override
    public EventMessage create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        EventMessage message = new EventMessage(uuid.toString(), "eventId", "senderId", "messageId", new DateTime(objNumber), null);
        return message;
    }

    @Override
    public List<EventMessage> createSamples() {
        List<EventMessage> array = new ArrayList<EventMessage>();
        array.add(new EventMessage(new UUID(0, 0).toString(), "eventId", "senderId", "messageId", new DateTime(0), null));
        array.add(new EventMessage(new UUID(0, 1).toString(), "eventId", "senderId", "messageId", new DateTime(0), null));
        array.add(new EventMessage(new UUID(0, 0).toString(), "eventId1", "senderId", "messageId", new DateTime(0), null));
        array.add(new EventMessage(new UUID(0, 0).toString(), "eventId", "senderId1", "messageId", new DateTime(0), null));
        array.add(new EventMessage(new UUID(0, 0).toString(), "eventId", "senderId", "messageId1", new DateTime(0), null));
        array.add(new EventMessage(new UUID(0, 0).toString(), "eventId", "senderId", "messageId", new DateTime(1), null));
        array.add(new EventMessage(new UUID(0, 0).toString(), "eventId", "senderId", "messageId", new DateTime(0), new DateTime(1)));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        daos.getEventMessageAccess().truncate(con);
    }
}
