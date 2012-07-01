package in.partake.model.dto;

import in.partake.app.PartakeTestApp;
import in.partake.base.DateTime;
import in.partake.model.fixture.TestDataProvider;
import junit.framework.Assert;

import org.junit.Test;

public class EventActivityTest extends AbstractPartakeModelTest<EventActivity> {

    @Override
    protected TestDataProvider<EventActivity> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getEventActivityProvider();
    }

    @Override
    protected EventActivity copy(EventActivity t) {
        return new EventActivity(t);
    }

    @Test
    public void testToCopy() {
        DateTime date = new DateTime(0L);
        EventActivity activity = new EventActivity("id", "userId", "title", "content", date);
        EventActivity copied = new EventActivity(activity);
        Assert.assertEquals(activity, copied);
        Assert.assertEquals(activity, new EventActivity("id", "userId", "title", "content", date));

        // Ensures NullPointerException won't happen.
        new EventActivity(new EventActivity("id", "userId", "title", "content", null));
    }
}
