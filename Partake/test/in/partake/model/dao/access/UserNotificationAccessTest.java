package in.partake.model.dao.access;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.model.dao.access.IUserNotificationAccess;
import in.partake.model.dto.UserNotification;

import org.junit.Before;

public class UserNotificationAccessTest extends AbstractDaoTestCaseBase<IUserNotificationAccess, UserNotification, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getUserNotificationAccess());
    }

    @Override
    protected UserNotification create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getUserNotificationProvider().create(pkNumber, pkSalt, objNumber);
    }
}
