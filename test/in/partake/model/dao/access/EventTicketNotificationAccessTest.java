package in.partake.model.dao.access;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.model.dao.access.IEventTicketNotificationAccess;
import in.partake.model.dto.EventTicketNotification;

import org.junit.Before;

public class EventTicketNotificationAccessTest extends AbstractDaoTestCaseBase<IEventTicketNotificationAccess, EventTicketNotification, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEventNotificationAccess());
    }

    @Override
    protected EventTicketNotification create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getEventTicketNotificationProvider().create(pkNumber, pkSalt, objNumber);
    }
}
