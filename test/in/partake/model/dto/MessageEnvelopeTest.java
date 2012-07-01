package in.partake.model.dto;

import in.partake.app.PartakeTestApp;
import in.partake.model.fixture.TestDataProvider;


public class MessageEnvelopeTest extends AbstractPartakeModelTest<MessageEnvelope> {
    @Override
    protected MessageEnvelope copy(MessageEnvelope t) {
        return new MessageEnvelope(t);
    }

    @Override
    protected TestDataProvider<MessageEnvelope> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getMessageEnvelopeProvider();
    }
}
