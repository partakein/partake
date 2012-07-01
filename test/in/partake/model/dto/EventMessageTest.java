package in.partake.model.dto;

import in.partake.app.PartakeTestApp;
import in.partake.model.fixture.TestDataProvider;


public class EventMessageTest extends AbstractPartakeModelTest<EventMessage> {

    @Override
    protected TestDataProvider<EventMessage> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getEventMessageProvider();
    }

    @Override
    protected EventMessage copy(EventMessage t) {
        return new EventMessage(t);
    }
}
