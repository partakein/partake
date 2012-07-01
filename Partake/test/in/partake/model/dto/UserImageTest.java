package in.partake.model.dto;

import in.partake.app.PartakeTestApp;
import in.partake.base.TimeUtil;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserImageTest extends AbstractPartakeModelTest<UserImage> {
    @Override
    protected UserImage copy(UserImage t) {
        return new UserImage(t);
    }

    @Override
    protected TestDataProvider<UserImage> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getImageProvider();
    }

    private UserImage[] samples;

    @Before
    public void createSampleData() {
        samples = new UserImage[] {
                new UserImage(),
                new UserImage("id1", "userId1", "something", new byte[] { -1, 0, 1 }, TimeUtil.getCurrentDateTime()),
                new UserImage("id2", "userId2", "somewhere", new byte[] { 0, 1, 2, 3, 4 }, TimeUtil.getCurrentDateTime()),
        };
    }

    @Test
    public void testCopyConstructor() {
        for (UserImage source : samples) {
            Assert.assertEquals(source, new UserImage(source));
        }

        for (UserImage lhs : samples) {
            for (UserImage rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }

}
