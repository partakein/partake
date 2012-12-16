package in.partake.model.dao.access;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.auxiliary.EventFilterCondition;
import in.partake.model.dto.Event;
import in.partake.model.fixture.impl.EventTestDataProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventAccessTest extends AbstractDaoTestCaseBase<IEventAccess, Event, String> {
    private EventTestDataProvider provider;

    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEventAccess());
        provider = PartakeTestApp.getTestService().getTestDataProviderSet().getEventProvider();
    }

    @Override
    protected Event create(long pkNumber, String pkSalt, int objNumber) {
        return provider.create(pkNumber, pkSalt, objNumber);
    }

    @Test
    public void testToFindByOwnerId() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                String userId = "userId-getbyowner-" + System.currentTimeMillis();
                Set<String> eventIds = new HashSet<String>();

                for (int i = 0; i < 10; ++i) {
                    String eventId;

                    Event original = createEvent(null, userId);
                    {
                        con.beginTransaction();
                        eventId = dao.getFreshId(con);
                        original.setId(eventId);

                        dao.put(con, original);
                        con.commit();

                        eventIds.add(eventId);
                    }
                }

                List<Event> targetEvents = dao.findByOwnerId(con, userId, EventFilterCondition.ALL_EVENTS, 0, Integer.MAX_VALUE);
                Set<String> targetEventIds = new HashSet<String>();
                for (Event e : targetEvents) {
                    targetEventIds.add(e.getId());
                }

                Assert.assertEquals(eventIds, targetEventIds);
                return null;
            }
        }.execute();
    }

    @Test
    public void testToFindByInvalidOwner() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                String userId = "userId-getbyowner-" + System.currentTimeMillis();
                String invalidUserId = "userId-invalid-" + System.currentTimeMillis();

                Set<String> eventIds = new HashSet<String>();

                for (int i = 0; i < 10; ++i) {
                    String eventId;

                    Event original = createEvent(null, userId);
                    {
                        con.beginTransaction();
                        eventId = dao.getFreshId(con);
                        original.setId(eventId);

                        dao.put(con, original);
                        con.commit();

                        eventIds.add(eventId);
                    }
                }

                List<Event> targetEvents = dao.findByOwnerId(con, invalidUserId, EventFilterCondition.ALL_EVENTS, 0, Integer.MAX_VALUE);

                Assert.assertNotNull(targetEvents);
                Assert.assertTrue(targetEvents.isEmpty());
                return null;
            }
        }.execute();
    }

    private Event createEvent(String eventId, String userId) {
        EventTestDataProvider provider = PartakeTestApp.getTestService().getTestDataProviderSet().getEventProvider();
        Event event = provider.create();

        event.setId(eventId);
        event.setOwnerId(userId);

        return event;
    }
}
