package in.partake.model.dao.access;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.access.Transaction;
import in.partake.model.dao.AbstractConnectionTestCaseBase;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IAccess;
import in.partake.model.dto.PartakeModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Dao のテストケースのベース。
 *
 * @author shinyak
 *
 */
public abstract class AbstractDaoTestCaseBase<DAO extends IAccess<T, PK>, T extends PartakeModel<T>, PK> extends AbstractConnectionTestCaseBase {
    protected DAO dao;

    // TODO: setUp should take DAO and TestDataProvider.

    // setup() should be implemented in each test case.
    //
    @Before
    protected abstract void setup() throws Exception;

    protected void setup(final DAO dao) throws Exception {
        // remove the current data
        TimeUtil.resetCurrentDate();

        this.dao = dao;

        if (dao == null)
            return;

        // truncate all data.
        new Transaction<T>() {
            @Override
            protected T doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                dao.truncate(con);
                return null;
            }
        }.execute();
    }

    // 同じ (pkNumber, pkSalt) なら同じ結果を返すようにする。
    // TODO: We should use TestDataProvider instead.
    protected abstract T create(long pkNumber, String pkSalt, int objNumber);

    // ------------------------------------------------------------

    @Test
    public final void testToCreate() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                T t1 = create(0, "create", 0);
                T t2 = create(i, "create", j);

                if (i == 0 && j == 0) {
                    Assert.assertEquals(t1, t2);
                } else {
                    Assert.assertFalse(t1.equals(t2));
                }

                if (i == 0) {
                    Assert.assertEquals(t1.getPrimaryKey(), t2.getPrimaryKey());
                } else {
                    Assert.assertFalse(t1.getPrimaryKey().equals(t2.getPrimaryKey()));
                }
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public final void testToPutFind() throws Exception {
        new DBAccess<T>() {
            @Override
            protected T doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                con.beginTransaction();
                T t1 = create(System.currentTimeMillis(), "putfind", 0);
                dao.put(con, t1);
                con.commit();

                T t2 = dao.find(con, (PK) t1.getPrimaryKey());
                Assert.assertEquals(t1, t2);
                Assert.assertNotSame(t1, t2);
                Assert.assertFalse(t1.isFrozen());
                Assert.assertTrue(t2.isFrozen());
                return null;
            }
        }.execute();
    }

    @Test
    @SuppressWarnings("unchecked")
    public final void testToPutFindInTransaction() throws Exception {
        new DBAccess<T>() {
            @Override
            protected T doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                con.beginTransaction();
                T t1 = create(System.currentTimeMillis(), "putfindintran", 0);
                dao.put(con, t1);

                T t2 = dao.find(con, (PK) t1.getPrimaryKey());
                con.commit();

                Assert.assertEquals(t1, t2);
                Assert.assertFalse(t1.isFrozen());
                Assert.assertTrue(t2.isFrozen());
                return null;
            }
        }.execute();
    }


    @Test
    @SuppressWarnings("unchecked")
    public final void testToPutPutFind() throws Exception {
        new DBAccess<T>() {
            @Override
            protected T doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                long time = System.currentTimeMillis();

                con.beginTransaction();
                T t1 = create(time, "putputfind", 0);
                dao.put(con, t1);
                con.commit();

                TimeUtil.waitForTick();

                con.beginTransaction();
                T t2 = create(time, "putputfind", 1);
                dao.put(con, t2);
                con.commit();

                Assert.assertEquals(t1.getPrimaryKey(), t2.getPrimaryKey());

                con.beginTransaction();
                T t3 = dao.find(con, (PK) t1.getPrimaryKey());
                con.commit();

                Assert.assertFalse(t1.equals(t3));
                Assert.assertEquals(t2, t3);
                return null;
            }
        }.execute();
    }

    @Test
    @SuppressWarnings("unchecked")
    public final void testToPutRemoveFind() throws Exception {
        new DBAccess<T>() {
            @Override
            protected T doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                TimeUtil.waitForTick();

                con.beginTransaction();
                T t1 = create(System.currentTimeMillis(), "putremovefind", 0);
                dao.put(con, t1);
                con.commit();

                TimeUtil.waitForTick();

                con.beginTransaction();
                dao.remove(con, (PK) t1.getPrimaryKey());
                con.commit();

                T t2 = dao.find(con, (PK) t1.getPrimaryKey());
                Assert.assertNull(t2);
                return null;
            }
        }.execute();
    }

    @Test
    @SuppressWarnings("unchecked")
    public final void testToPutRemovePutFind() throws Exception {
        new DBAccess<T>() {
            @Override
            protected T doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                con.beginTransaction();
                T t1 = create(System.currentTimeMillis(), "putremovefind", 0);
                dao.put(con, t1);
                con.commit();

                TimeUtil.waitForTick();

                con.beginTransaction();
                dao.remove(con, (PK) t1.getPrimaryKey());
                con.commit();

                TimeUtil.waitForTick();

                con.beginTransaction();
                dao.put(con, t1);
                con.commit();

                T t2 = dao.find(con, (PK) t1.getPrimaryKey());

                Assert.assertEquals(t1, t2);
                return null;
            }
        }.execute();
    }


    @Test
    @SuppressWarnings("unchecked")
    public final void testToRemoveInvalidObject() throws Exception {
        new DBAccess<T>() {
            @Override
            protected T doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                T t1 = create(System.currentTimeMillis(), "removeinvalid", 0);

                con.beginTransaction();
                dao.remove(con, (PK) t1.getPrimaryKey());
                con.commit();
                return null;
            }
        }.execute();
    }

    @Test
    @SuppressWarnings("unchecked")
    public final void testToFindWithInvalidId() throws Exception {
        new DBAccess<T>() {
            @Override
            protected T doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                T t1 = create(System.currentTimeMillis(), "findInvalid", 0);

                con.beginTransaction();
                T t = dao.find(con, (PK) t1.getPrimaryKey());
                con.commit();

                Assert.assertNull(t);
                return null;
            }
        }.execute();
    }

    @Test
    public final void testToIterate() throws Exception {
        new DBAccess<T>() {
            @Override
            protected T doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                Set<T> created = new HashSet<T>();
                for (int i = 0; i < 3; ++i) {
                    T t = create(System.currentTimeMillis(), String.valueOf(i), i);
                    created.add(t);

                    con.beginTransaction();
                    dao.put(con, t);
                    con.commit();
                }

                int count = 0;
                DataIterator<T> it = dao.getIterator(con);
                try {
                    while (it.hasNext()) {
                        T t = it.next();
                        if (t == null) { continue; }
                        ++count;
                        Assert.assertTrue(created.contains(t));
                    }
                } finally {
                    it.close();
                }
                Assert.assertEquals(3, count);
                return null;
            }
        }.execute();
    }

    @Test
    public final void testToCount() throws Exception {
        new DBAccess<T>() {
            @Override
            protected T doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                Set<T> created = new HashSet<T>();
                for (int i = 0; i < 10; ++i) {
                    T t = create(System.currentTimeMillis(), String.valueOf(i), i);
                    created.add(t);

                    con.beginTransaction();
                    dao.put(con, t);
                    con.commit();
                }

                assertThat(dao.count(con), is(10));
                return null;
            }
        }.execute();
    }


    @Test
    @SuppressWarnings("unchecked")
    public final void testToExist() throws Exception {
        new DBAccess<T>() {
            @Override
            protected T doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                T t1 = create(System.currentTimeMillis(), "exist", 0);
                T t2 = create(System.currentTimeMillis(), "not-exist", 0);

                con.beginTransaction();
                dao.put(con, t1);
                con.commit();

                assertThat(dao.exists(con, (PK) t1.getPrimaryKey()), is(true));
                assertThat(dao.exists(con, (PK) t2.getPrimaryKey()), is(false));

                return null;
            }
        }.execute();
    }
}
