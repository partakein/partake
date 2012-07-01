package in.partake.controller.api.account;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.UserOpenIDLink;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class RemoveOpenIDAPITest extends APIControllerTest {
    @Test
    public void testToRemoveOpenID() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/account/removeOpenID");

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        addParameter(proxy, "identifier", TestDataProvider.DEFAULT_USER_OPENID_IDENTIFIER);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        // Check the OpenID has been really removed.
        List<UserOpenIDLink> links = loadOpenIDIdentifiers(DEFAULT_USER_ID);
        Assert.assertNotNull(links);

        List<UUID> ids = new ArrayList<UUID>();
        for (UserOpenIDLink link : links)
            ids.add(link.getId());
        Assert.assertFalse(ids.contains(DEFAULT_USER_OPENID_ID));
    }

    @Test
    public void testToRemoveOpenIDWithoutIdentifier() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/account/removeOpenID");

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_OPENID);
    }

    @Test
    public void testToRemoveOpenIDWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/account/removeOpenID");

        // When not login, should fail.
        addParameter(proxy, "identifier", TestDataProvider.DEFAULT_USER_OPENID_IDENTIFIER);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToRemoveOpenIDWithInvalidLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/account/removeOpenID");

        loginAs(proxy, TestDataProvider.DEFAULT_ANOTHER_USER_ID);

        addParameter(proxy, "identifier", TestDataProvider.DEFAULT_USER_OPENID_IDENTIFIER);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_OPENID);
    }

    @Test
    public void testToRemoveOpenIDWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/account/removeOpenID");

        // Check CSRF prevention works.
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        addParameter(proxy, "identifier", TestDataProvider.DEFAULT_USER_OPENID_IDENTIFIER);
        addInvalidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }
}
