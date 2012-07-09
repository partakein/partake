package in.partake.controller.action.auth;

import in.partake.app.PartakeConfiguration;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.resource.Constants;

import org.openid4java.OpenIDException;

import play.cache.Cache;
import play.libs.OpenID;
import play.mvc.Result;

public abstract class AbstractOpenIDAction extends AbstractPartakeAction {
    private static final int LOGIN_TIMEOUT_SEC = 300;
    private static final String CALLBACK_URL = PartakeConfiguration.toppath() + "/auth/verifyOpenID";

    protected Result doAuthenticate(String purpose) throws OpenIDException {
        String identifier = getParameter("openidIdentifier");

        String sessionId = session().get(Constants.Session.ID_KEY);
        assert sessionId != null;
        Cache.set(Constants.Cache.OPENID_LOGIN_KEY_PREFIX + sessionId, purpose, LOGIN_TIMEOUT_SEC);

        return renderRedirect(OpenID.redirectURL(identifier, CALLBACK_URL).get());
    }
}
