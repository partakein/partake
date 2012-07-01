package in.partake.model.dto;

import in.partake.app.PartakeTestApp;
import in.partake.model.fixture.TestDataProvider;

public class UserTwitterLinkTest extends AbstractPartakeModelTest<UserTwitterLink> {
    @Override
    protected UserTwitterLink copy(UserTwitterLink t) {
        return new UserTwitterLink(t);
    }

    @Override
    protected TestDataProvider<UserTwitterLink> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getTwitterLinkageProvider();
    }
}
