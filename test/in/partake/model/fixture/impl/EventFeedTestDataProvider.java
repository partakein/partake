package in.partake.model.fixture.impl;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventFeed;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventFeedTestDataProvider extends TestDataProvider<EventFeed> {
    @Override
    public EventFeed create(long pkNumber, String pkSalt, int objNumber) {
        return new EventFeed(new UUID(pkNumber, pkSalt.hashCode()).toString(), "eventId" + objNumber);
    }

    @Override
    public List<EventFeed> createSamples() {
        List<EventFeed> array = new ArrayList<EventFeed>();
        array.add(new EventFeed(new UUID(0, 0).toString(), "eventId"));
        array.add(new EventFeed(new UUID(0, 1).toString(), "eventId"));
        array.add(new EventFeed(new UUID(0, 0).toString(), "eventId1"));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        daos.getEventFeedAccess().truncate(con);
    }
}
