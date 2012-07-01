package in.partake.model.dao.access;

import in.partake.app.PartakeApp;
import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserImageAccess;
import in.partake.model.dto.UserImage;
import in.partake.model.fixture.TestDataProvider;
import in.partake.model.fixture.impl.UserImageTestDataProvider;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserImageAccessTest extends AbstractDaoTestCaseBase<IUserImageAccess, UserImage, String> {
    private UserImageTestDataProvider provider = new UserImageTestDataProvider();

    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getImageAccess());
    }

    @Override
    protected UserImage create(long pkNumber, String pkSalt, int objNumber) {
        return provider.create(pkNumber, pkSalt, objNumber);
    }

    @Test
    public void testToFindIdsByUserId() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                // Create test data.
                UserImage[] data = new UserImage[10];
                for (int i = 0; i < 10; ++i) {
                    data[i] = create(i, "findIds", i);
                    data[i].setCreatedAt(new DateTime(10 - i));
                }

                con.beginTransaction();
                for (int i = 0; i < 10; ++i)
                    dao.put(con, data[i]);
                con.commit();

                // Do test
                List<String> result = dao.findIdsByUserId(con, TestDataProvider.DEFAULT_USER_ID, 0, 10);
                Assert.assertEquals(10, result.size());
                for (int i = 0; i < 10; ++i)
                    Assert.assertEquals(data[i].getId(), result.get(i));

                result = dao.findIdsByUserId(con, TestDataProvider.DEFAULT_USER_ID, 0, 5);
                Assert.assertEquals(5, result.size());
                for (int i = 0; i < 5; ++i)
                    Assert.assertEquals(data[i].getId(), result.get(i));

                result = dao.findIdsByUserId(con, TestDataProvider.DEFAULT_USER_ID, 5, 10);
                Assert.assertEquals(5, result.size());
                for (int i = 0; i < 5; ++i)
                    Assert.assertEquals(data[i + 5].getId(), result.get(i));

                return null;
            }
        }.execute();
    }
}
