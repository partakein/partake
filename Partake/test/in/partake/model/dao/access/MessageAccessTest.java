package in.partake.model.dao.access;

import java.util.UUID;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dto.Message;

import org.junit.Before;

public class MessageAccessTest extends AbstractDaoTestCaseBase<IMessageAccess, Message, UUID> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getMessageAccess());
    }

    @Override
    protected Message create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getMessageProvider().create(pkNumber, pkSalt, objNumber);
    }
}
