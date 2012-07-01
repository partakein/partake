package in.partake.model.dao.access;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IUserCalendarLinkageAccess;
import in.partake.model.dto.UserCalendarLink;

import java.util.UUID;

import org.junit.Before;

public class UserCalendarLinkAccessTest extends AbstractDaoTestCaseBase<IUserCalendarLinkageAccess, UserCalendarLink, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getCalendarAccess());
    }

    @Override
    protected UserCalendarLink create(long pkNumber, String pkSalt, int objNumber) {
        UUID id = new UUID(pkNumber, ("calendarLinkage" + pkSalt).hashCode());
        return new UserCalendarLink(id.toString(), "calendarLinkage" + objNumber);
    }
}
