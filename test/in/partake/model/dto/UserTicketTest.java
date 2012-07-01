package in.partake.model.dto;

import in.partake.app.PartakeTestApp;
import in.partake.model.fixture.TestDataProvider;

public final class UserTicketTest extends AbstractPartakeModelTest<UserTicket> {
    @Override
    protected UserTicket copy(UserTicket t) {
        return new UserTicket(t);
    }

    @Override
    protected TestDataProvider<UserTicket> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getEnrollmentProvider();
    }
}
