package in.partake.model.dao.access;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTwitterLinkAccess;
import in.partake.model.dto.UserTwitterLink;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class UserTwitterLinkAccessTest extends AbstractDaoTestCaseBase<IUserTwitterLinkAccess, UserTwitterLink, UUID> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getTwitterLinkageAccess());
    }

    @Override
    protected UserTwitterLink create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new UserTwitterLink(uuid, objNumber, "userId" + objNumber, "screenName", "name", "accessToken", "accessTokenSecret", "profileImageURL");
    }

    @Test
    public void testToFindByTwitterId() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                UserTwitterLink link1 = create(0, "hoge", 1);
                link1.setTwitterId(0);
                UserTwitterLink link2 = create(1, "hoge", 2);
                link2.setTwitterId(1);

                dao.put(con, link1);
                dao.put(con, link2);


                assertThat(dao.findByTwitterId(con, 0).getId(), is(link1.getId()));
                assertThat(dao.findByTwitterId(con, 1).getId(), is(link2.getId()));
                return null;
            }
        }.execute();
    }

    @Test
    public void testToFindByUserId() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                UserTwitterLink link1 = create(0, "hoge", 1);
                link1.setUserId("userId1");
                UserTwitterLink link2 = create(1, "hoge", 2);
                link2.setUserId("userId2");

                dao.put(con, link1);
                dao.put(con, link2);

                assertThat(dao.findByUserId(con, "userId1").getId(), is(link1.getId()));
                assertThat(dao.findByUserId(con, "userId2").getId(), is(link2.getId()));
                return null;
            }
        }.execute();
    }

    @Test
    public void testToFindByScreenNamePrefix() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                String[] screenNames = new String[] {
                        "hoge", "hoge1", "hoge2", "hoge3", "hoge4",
                        "fuga", "fuga1", "fuga2", "fuga3", "fuga4",
                };
                UserTwitterLink[] links = new UserTwitterLink[10];
                for (int i = 0; i < 10; ++i) {
                    links[i] = create(i, "hoge", i);
                    links[i].setScreenName(screenNames[i]);
                    dao.put(con, links[i]);
                }

                List<UserTwitterLink> found = dao.findByScreenNamePrefix(con, "hoge", 10);
                assertThat(found.size(), is(5));
                assertThat(found, hasItem(is(links[0])));

                return null;
            }
        }.execute();
    }

    @Test
    public void testToFindByScreenNamePrefixWithEscapeCharacters() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                String[] screenNames = new String[] {
                        "hoge",  "hoge1",  "hoge2",  "hoge3",  "hoge4",
                        "hoge%", "hoge%1", "hoge%2", "hoge%3", "hoge%4",
                        "hoge_", "hoge_1", "hoge_2", "hoge_3", "hoge_4",
                };
                UserTwitterLink[] links = new UserTwitterLink[15];
                for (int i = 0; i < 10; ++i) {
                    links[i] = create(i, "hoge", i);
                    links[i].setScreenName(screenNames[i]);
                    dao.put(con, links[i]);
                }

                List<UserTwitterLink> found = dao.findByScreenNamePrefix(con, "hoge%", 10);
                assertThat(found.size(), is(5));
                assertThat(found, hasItem(is(links[5])));

                return null;
            }
        }.execute();
    }
}
