package in.partake.model.dao.access;

import java.util.UUID;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dto.EventFeed;

import org.junit.Before;

public class EventFeedAccessTest extends AbstractDaoTestCaseBase<IEventFeedAccess, EventFeed, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEventFeedAccess());
    }

    @Override
    protected EventFeed create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("feed" + pkSalt).hashCode());
        return new EventFeed(uuid.toString(), "eventId" + objNumber);
    }
}
