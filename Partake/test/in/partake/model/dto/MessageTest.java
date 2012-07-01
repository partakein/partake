package in.partake.model.dto;

import java.util.UUID;

import in.partake.app.PartakeTestApp;
import in.partake.base.DateTime;
import in.partake.model.fixture.TestDataProvider;
import junit.framework.Assert;

import org.junit.Test;

public class MessageTest extends AbstractPartakeModelTest<Message> {

    @Override
    protected Message copy(Message t) {
        return new Message(t);
    }

    @Override
    protected TestDataProvider<Message> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getMessageProvider();
    }

    @Test
    public void testToCopy() {
        DateTime dt = new DateTime(0);
        UUID id = UUID.randomUUID();
        Message message = new Message(id, "title", "body", dt, dt);
        Message copied = new Message(message);
        Assert.assertEquals(message, copied);
        Assert.assertEquals(message, new Message(id, "title", "body", dt, dt));

        // Ensures NullPointerException won't happen.
        new Message(new Message(id, "title", "message", dt, null));
    }
}
