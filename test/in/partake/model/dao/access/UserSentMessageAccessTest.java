package in.partake.model.dao.access;

import java.util.UUID;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.model.dao.access.IUserSentMessageAccess;
import in.partake.model.dto.UserSentMessage;

import org.junit.Before;

public class UserSentMessageAccessTest extends AbstractDaoTestCaseBase<IUserSentMessageAccess, UserSentMessage, UUID> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getUserSentMessageAccess());
    }

    @Override
    protected UserSentMessage create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getUserSentMessageProvider().create(pkNumber, pkSalt, objNumber);
    }
}
