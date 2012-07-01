package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dto.EventActivity;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventActivityTestDataProvider extends TestDataProvider<EventActivity> {
    @Override
    public EventActivity create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new EventActivity(uuid.toString(), "eventId", "title", "content", new DateTime(0));
    }

    @Override
    public List<EventActivity> createSamples() {
        List<EventActivity> array = new ArrayList<EventActivity>();
        array.add(new EventActivity(new UUID(0, 0).toString(), "eventId", "title", "content", new DateTime(0)));
        array.add(new EventActivity(new UUID(0, 1).toString(), "eventId", "title", "content", new DateTime(0)));
        array.add(new EventActivity(new UUID(0, 0).toString(), "eventId1", "title", "content", new DateTime(0)));
        array.add(new EventActivity(new UUID(0, 0).toString(), "eventId", "title1", "content", new DateTime(0)));
        array.add(new EventActivity(new UUID(0, 0).toString(), "eventId", "title", "content1", new DateTime(0)));
        array.add(new EventActivity(new UUID(0, 0).toString(), "eventId", "title", "content", new DateTime(1)));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IEventActivityAccess dao = daos.getEventActivityAccess();
        dao.truncate(con);
    }
}
