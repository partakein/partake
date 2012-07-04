package in.partake.controller.api.event;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class CreateAPITest extends APIControllerTest {

    @Test
    public void testToCreate() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/create");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        addValidSessionTokenToParameter(proxy);
        addNecessaryEventParameters(proxy);

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testToCreateWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/create");
        addValidSessionTokenToParameter(proxy);
        addNecessaryEventParameters(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToCreateWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/create");
        addInvalidSessionTokenToParameter(proxy);
        addNecessaryEventParameters(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    private void addNecessaryEventParameters(ActionProxy proxy) {
        addFormParameter(proxy, "title", "Title");
        addFormParameter(proxy, "beginDate", "2013-01-01 12:00");
        addFormParameter(proxy, "category", EventCategory.getCategories().get(0).getKey());
    }
}
