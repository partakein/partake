package in.partake.model.dao.access;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.model.dao.access.ITwitterMessageAccess;
import in.partake.model.dto.TwitterMessage;

import org.junit.Before;

public class TwitterMessageAccessTest extends AbstractDaoTestCaseBase<ITwitterMessageAccess, TwitterMessage, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getTwitterMessageAccess());
    }

    @Override
    protected TwitterMessage create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getTwitterMessageProvider().create(pkNumber, pkSalt, objNumber);
    }
}
