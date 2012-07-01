package in.partake.model.dao.access;

import in.partake.app.PartakeApp;
import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dto.EventActivity;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventActivityAccessTest extends AbstractDaoTestCaseBase<IEventActivityAccess, EventActivity, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEventActivityAccess());
    }

    @Override
    public EventActivity create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("activity" + pkSalt).hashCode());
        return new EventActivity(uuid.toString(), "eventId", "title" + objNumber, "content", new DateTime(objNumber % 10));
    }

    @Test
    public void testToIterator1() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                String eventId = "eventId-findByEventId-0-" + System.currentTimeMillis();

                con.beginTransaction();
                for (int i = 0; i < 10; ++i) {
                    EventActivity activity = new EventActivity(dao.getFreshId(con), eventId, "title-" + i, "content", new DateTime(i));
                    dao.put(con, activity);
                }
                con.commit();

                List<EventActivity> activities = dao.findByEventId(con, eventId, 10);

                Assert.assertEquals(10, activities.size());
                for (int i = 0; i < 10; ++i) {
                    Assert.assertEquals(9 - i, activities.get(i).getCreatedAt().getTime());
                }

                // TODO Auto-generated method stub
                return null;
            }
        }.execute();
    }

    @Test
    public void testToIterator2() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {

                String eventId1 = "eventId-findByEventId-1-" + System.currentTimeMillis();
                String eventId2 = "eventId-findByEventId-2-" + System.currentTimeMillis();



                con.beginTransaction();
                for (int i = 0; i < 10; ++i) {
                    EventActivity activity = new EventActivity(dao.getFreshId(con), eventId1, "title-" + i, "content", new DateTime(i));
                    dao.put(con, activity);
                }
                for (int i = 0; i < 10; ++i) {
                    EventActivity activity = new EventActivity(dao.getFreshId(con), eventId2, "title-" + i, "content", new DateTime(i));
                    dao.put(con, activity);
                }
                con.commit();

                List<EventActivity> activities1 = dao.findByEventId(con, eventId1, 10);
                List<EventActivity> activities2 = dao.findByEventId(con, eventId2, 10);

                Assert.assertEquals(10, activities1.size());
                Assert.assertEquals(10, activities2.size());

                for (int i = 0; i < 10; ++i) {
                    Assert.assertEquals(9 - i, activities1.get(i).getCreatedAt().getTime());
                    Assert.assertEquals(9 - i, activities2.get(i).getCreatedAt().getTime());
                }
                // TODO Auto-generated method stub
                return null;
            }
        }.execute();
    }


    @Test
    public void testToIterator3() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {

                String eventId1 = "eventId-findByEventId-1-" + System.currentTimeMillis();
                String eventId2 = "eventId-findByEventId-2-" + System.currentTimeMillis();

                con.beginTransaction();
                for (int i = 0; i < 100; ++i) {
                    EventActivity activity = new EventActivity(dao.getFreshId(con), eventId1, "title-" + i, "content", new DateTime(i));
                    dao.put(con, activity);
                }
                con.commit();
                con.beginTransaction();
                for (int i = 0; i < 100; ++i) {
                    EventActivity activity = new EventActivity(dao.getFreshId(con), eventId2, "title-" + i, "content", new DateTime(i));
                    dao.put(con, activity);
                }
                con.commit();


                List<EventActivity> activities1 = dao.findByEventId(con, eventId1, 100);
                List<EventActivity> activities2 = dao.findByEventId(con, eventId2, 100);

                Assert.assertEquals(100, activities1.size());
                Assert.assertEquals(100, activities2.size());

                // TODO: JPA ではミリ秒が考慮されてないっぽい。(10 ミリ秒単位で四捨五入された文字列が生成されている)
                // FIXME: これキャストとかしないとだめなんか？
                for (int i = 0; i < 100; ++i) {
                    Assert.assertEquals(99 - i, activities1.get(i).getCreatedAt().getTime());
                    Assert.assertEquals(99 - i, activities2.get(i).getCreatedAt().getTime());
                }

                return null;
            }
        }.execute();
    }
}
