package in.partake.model.dao.access;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.model.dao.access.IUserThumbnailAccess;
import in.partake.model.dto.UserThumbnail;

import org.junit.Before;

public class UserThumbnailAccessTest extends AbstractDaoTestCaseBase<IUserThumbnailAccess, UserThumbnail, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getThumbnailAccess());
    }

    @Override
    protected UserThumbnail create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getThumbnailProvider().create(pkNumber, pkSalt, objNumber);
    }
}
