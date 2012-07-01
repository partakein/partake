package in.partake.model.dto;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.app.PartakeTestApp;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserPreferenceTest extends AbstractPartakeModelTest<UserPreference> {
    @Override
    protected UserPreference copy(UserPreference t) {
        return new UserPreference(t);
    }

    @Override
    protected TestDataProvider<UserPreference> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getUserPreferenceProvider();
    }

    private UserPreference[] samples;

    @Before
    public void createSamples() {
        samples = new UserPreference[] {
            new UserPreference("id1", false, false, false),
            new UserPreference("id2", true, false, true),
            new UserPreference("id3", false, true, false),
            new UserPreference("id4", true, true, false),
            new UserPreference("id5", false, false, true),
            new UserPreference("id6", true, false, true),
            new UserPreference("id7", false, true, true),
            new UserPreference("id8", true, true, true),
        };
    }

    @Test
    public void testCopyConstructor() {
        for (UserPreference source : samples) {
            Assert.assertEquals(source, new UserPreference(source));
        }

        for (UserPreference lhs : samples) {
            for (UserPreference rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }

    @Test
    public void testEquals() {
        UserPreference pref = samples[0];
        assertThat(pref.equals(null), is(false));

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                assertThat(samples[i].equals(samples[j]), is(i == j));
            }
        }
    }

    @Test
    public void testSetUserId() {
        UserPreference pref = samples[0];
        pref.setUserId("modified");

        assertThat(pref.getUserId(), is("modified"));
    }
}
