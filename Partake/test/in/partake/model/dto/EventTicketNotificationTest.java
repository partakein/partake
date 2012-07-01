package in.partake.model.dto;

import in.partake.app.PartakeTestApp;
import in.partake.model.fixture.TestDataProvider;


public class EventTicketNotificationTest extends AbstractPartakeModelTest<EventTicketNotification> {

    @Override
    protected TestDataProvider<EventTicketNotification> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getEventTicketNotificationProvider();
    }

    @Override
    protected EventTicketNotification copy(EventTicketNotification t) {
        return new EventTicketNotification(t);
    }
}
