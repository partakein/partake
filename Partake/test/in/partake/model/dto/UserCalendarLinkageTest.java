package in.partake.model.dto;

import in.partake.app.PartakeTestApp;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserCalendarLinkageTest extends AbstractPartakeModelTest<UserCalendarLink> {
    private UserCalendarLink[] samples;

    @Override
    protected UserCalendarLink copy(UserCalendarLink t) {
        return new UserCalendarLink(t);
    }

    @Override
    protected TestDataProvider<UserCalendarLink> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getCalendarTestDataProvider();
    }

    @Before
    public void createSampleData() {
        samples = new UserCalendarLink[] {
                new UserCalendarLink(),
                new UserCalendarLink("id1", "userId1"),
                new UserCalendarLink("id2", "userId2"),
        };
    }

    @Test
    public void testCopyConstructor() {
        for (UserCalendarLink source : samples) {
            Assert.assertEquals(source, new UserCalendarLink(source));
        }

        for (UserCalendarLink lhs : samples) {
            for (UserCalendarLink rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }

    @Test
    public void testToJSONFromJSON() {
        UserCalendarLink linkage = new UserCalendarLink("id", "userId");
        Assert.assertEquals(linkage, new UserCalendarLink(linkage.toJSON()));
    }
}
