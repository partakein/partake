package in.partake.model.dao.access;

import java.util.UUID;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.model.dao.access.IUserReceivedMessageAccess;
import in.partake.model.dto.UserReceivedMessage;

import org.junit.Before;

public class UserReceivedMessageAccessTest extends AbstractDaoTestCaseBase<IUserReceivedMessageAccess, UserReceivedMessage, UUID> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getUserReceivedMessageAccess());
    }

    @Override
    protected UserReceivedMessage create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getUserReceivedMessageProvider().create(pkNumber, pkSalt, objNumber);
    }
}
