package in.partake.model.dao.access;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventCommentAccess;
import in.partake.model.dto.EventComment;
import in.partake.model.fixture.impl.EventCommentTestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventCommentAccessTest extends AbstractDaoTestCaseBase<IEventCommentAccess, EventComment, String> {
    private EventCommentTestDataProvider provider;

    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getCommentAccess());
        provider = PartakeTestApp.getTestService().getTestDataProviderSet().getCommentDataProvider();
    }

    @Override
    protected EventComment create(long pkNumber, String pkSalt, int objNumber) {
        return provider.create(pkNumber, pkSalt, objNumber);
    }

    @Test
    // TODO tell about order of the DataIterator's value.
    public void testToFindByEventId() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                String prefix = daos.getCommentAccess().getFreshId(con);

                String[][] ids = new String[10][10];

                for (int i = 0; i < 10; ++i) {
                    for (int j = 0; j < 10; ++j) {
                        ids[i][j] = UUID.randomUUID().toString();
                        EventComment original = new EventComment(ids[i][j], prefix + "eventId" + i, "userId", "comment content", false, TimeUtil.getCurrentDateTime());
                        daos.getCommentAccess().put(con, original);
                        TimeUtil.waitForTick();
                    }
                }

                for (int i = 0; i < 10; ++i) {
                    DataIterator<EventComment> it = daos.getCommentAccess().getCommentsByEvent(con, prefix + "eventId" + i);
                    try {
                        List<String> strs = new ArrayList<String>();
                        while (it.hasNext()) {
                            EventComment comment = it.next();
                            String id = comment.getId();
                            if (id == null) { continue; }
                            strs.add(id);
                        }
                        Assert.assertEquals(10, strs.size());

                        for (int j = 0; j < 10; ++j) {
                            Assert.assertEquals(ids[i][j], strs.get(j));
                        }
                    } finally {
                        it.close();
                    }
                }
                return null;
            }
        }.execute();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToUpdateByIterator() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                String prefix = daos.getCommentAccess().getFreshId(con);

                String[] ids = new String[10];
                // create
                for (int i = 0; i < 10; ++i) {
                    ids[i] = UUID.randomUUID().toString();
                    EventComment original = new EventComment(ids[i], prefix + "eventId", "userId", "comment content", false, TimeUtil.getCurrentDateTime());
                    daos.getCommentAccess().put(con, original);
                }

                // update
                {
                    DataIterator<EventComment> it = daos.getCommentAccess().getCommentsByEvent(con, prefix + "eventId");
                    try {
                        while (it.hasNext()) {
                            EventComment comment = it.next();

                            EventComment updated = new EventComment(comment);
                            updated.setComment("New comment!");

                            it.update(updated);
                        }
                    } finally {
                        it.close();
                    }
                }
                // get them
                {
                    DataIterator<EventComment> it = daos.getCommentAccess().getCommentsByEvent(con, prefix + "eventId");
                    try {
                        while (it.hasNext()) {
                            EventComment comment = it.next();
                            Assert.assertEquals("New comment!", comment.getComment());
                        }
                    } finally {
                        it.close();
                    }
                }
                return null;
            }
        }.execute();
    }
}
