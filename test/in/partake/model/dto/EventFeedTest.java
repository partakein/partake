package in.partake.model.dto;

import in.partake.app.PartakeTestApp;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventFeedTest extends AbstractPartakeModelTest<EventFeed> {
    @Override
    protected EventFeed copy(EventFeed t) {
        return new EventFeed(t);
    }

    @Override
    protected TestDataProvider<EventFeed> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getEventFeedProvider();
    }

    private EventFeed[] samples;

    @Before
    public void createSamples() {
        samples = new EventFeed[] {
            new EventFeed(),
            new EventFeed("id1", "hoge"),
            new EventFeed("id2", "fuga"),
        };
    }

    @Test
    public void testCopyConstructor() {
        for (EventFeed source : samples) {
            Assert.assertEquals(source, new EventFeed(source));
        }

        for (EventFeed lhs : samples) {
            for (EventFeed rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }
}
