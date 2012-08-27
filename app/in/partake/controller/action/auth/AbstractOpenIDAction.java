package in.partake.controller.action.auth;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeConfiguration;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.resource.Constants;
import in.partake.resource.ServerErrorCode;
import in.partake.service.IOpenIDService;
import in.partake.session.OpenIDLoginInformation;

import org.openid4java.OpenIDException;
import org.openid4java.discovery.DiscoveryInformation;

import play.cache.Cache;
import play.mvc.Result;

public abstract class AbstractOpenIDAction extends AbstractPartakeAction {
    private static final int LOGIN_TIMEOUT_SEC = 300;
    private static final String CALLBACK_URL = PartakeConfiguration.toppath() + "/auth/verifyOpenID";

    protected Result doAuthenticate(String purpose) throws OpenIDException, PartakeException {
        String identifier = getParameter("openidIdentifier");

        String sessionId = session().get(Constants.Session.ID_KEY);
        assert sessionId != null;
        if (sessionId == null)
            throw new PartakeException(ServerErrorCode.SESSION_ID_KEY_NOTFOUND);

        IOpenIDService service = PartakeApp.getOpenIDService();
        DiscoveryInformation discoveryInformation = service.discover(identifier);
        OpenIDLoginInformation info = new OpenIDLoginInformation(purpose, discoveryInformation);
        Cache.set(Constants.Cache.OPENID_LOGIN_KEY_PREFIX + sessionId, info, LOGIN_TIMEOUT_SEC);

        String authURL = service.getURLToAuthenticate(discoveryInformation, CALLBACK_URL);
        return renderRedirect(authURL);
    }
}
