package in.partake.model.dto;

import in.partake.base.TimeUtil;
import in.partake.model.fixture.TestDataProvider;
import in.partake.model.fixture.impl.UserTestDataProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserTest extends AbstractPartakeModelTest<User> {
    private User[] samples;
    UserTestDataProvider provider = new UserTestDataProvider();

    @Before
    public void createSampleData() {
        samples = new User[] {
                new User("id1", "screenName1", "http://www.example.com/1", TimeUtil.getCurrentDateTime(), null),
                new User("id2", "screenName2", "http://www.example.com/2", TimeUtil.getCurrentDateTime(), null),
                new User("id3", "screenName3", "http://www.example.com/3", TimeUtil.getCurrentDateTime(), null),
        };
    }

    @Test
    public void testCopyConstructor() {
        for (User source : samples) {
            Assert.assertEquals(source, new User(source));
        }

        for (User lhs : samples) {
            for (User rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }

    @Test
    public void testToJSONFromJSON() {
        User user = new User("id1", "screenName1", "http://www.example.com/1", TimeUtil.getCurrentDateTime(), null);
        Assert.assertEquals(user, new User(user.toJSON()));
    }

    @Override
    protected TestDataProvider<User> getTestDataProvider() {
        return provider;
    }

    @Override
    protected User copy(User t) {
        return new User(t);
    }
}
