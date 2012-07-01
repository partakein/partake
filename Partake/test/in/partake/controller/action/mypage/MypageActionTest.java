package in.partake.controller.action.mypage;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.action.ActionControllerTest;
import in.partake.model.dto.UserOpenIDLink;
import in.partake.model.dto.UserPreference;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class MypageActionTest extends ActionControllerTest {
    @Test
    public void testToExecute() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/mypage");
        loginAs(proxy, DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        MypageAction action = (MypageAction) proxy.getAction();

        List<String> ids = new ArrayList<String>();
        for (UserOpenIDLink link : action.getOpenIds())
            ids.add(link.getIdentifier());

        assertThat(action.getPreference(), is(UserPreference.getDefaultPreference(DEFAULT_USER_ID)));
        assertThat(ids, hasItem(DEFAULT_USER_OPENID_IDENTIFIER));
        assertThat(ids, hasItem(DEFAULT_USER_OPENID_ALTERNATIVE_IDENTIFIER));
        assertThat(action.getCalendarLink().getId(), is(DEFAULT_CALENDAR_ID));
    }

    @Test
    public void testToExecuteWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/mypage");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }
}
