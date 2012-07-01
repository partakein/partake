package in.partake.model.dto;

import in.partake.app.PartakeTestApp;
import in.partake.model.fixture.TestDataProvider;


public class UserReceivedMessageTest extends AbstractPartakeModelTest<UserReceivedMessage> {
    @Override
    protected UserReceivedMessage copy(UserReceivedMessage t) {
        return new UserReceivedMessage(t);
    }

    @Override
    protected TestDataProvider<UserReceivedMessage> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getUserReceivedMessageProvider();
    }
}
