package in.partake.model.dao.access;

import java.util.UUID;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IUserOpenIDLinkAccess;
import in.partake.model.dto.UserOpenIDLink;

import org.junit.Before;

public class UserOpenIDLinkAccessTest extends AbstractDaoTestCaseBase<IUserOpenIDLinkAccess, UserOpenIDLink, UUID> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getOpenIDLinkageAccess());
    }

    @Override
    protected UserOpenIDLink create(long pkNumber, String pkSalt, int objNumber) {
        return new UserOpenIDLink(new UUID(pkNumber, pkSalt.hashCode()), "id" + pkSalt + pkNumber, "userId" + objNumber);
    }
}
