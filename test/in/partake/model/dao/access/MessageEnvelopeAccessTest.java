package in.partake.model.dao.access;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.model.dao.access.IMessageEnvelopeAccess;
import in.partake.model.dto.MessageEnvelope;

import org.junit.Before;

public class MessageEnvelopeAccessTest extends AbstractDaoTestCaseBase<IMessageEnvelopeAccess, MessageEnvelope, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getMessageEnvelopeAccess());
    }

    @Override
    protected MessageEnvelope create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getMessageEnvelopeProvider().create(pkNumber, pkSalt, objNumber);
    }
}
