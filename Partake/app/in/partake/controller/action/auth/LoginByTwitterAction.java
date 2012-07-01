package in.partake.controller.action.auth;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.resource.Constants;
import in.partake.resource.ServerErrorCode;
import in.partake.service.ITwitterService;
import in.partake.session.TwitterLoginInformation;
import play.cache.Cache;
import play.mvc.Result;
import twitter4j.TwitterException;

public class LoginByTwitterAction extends AbstractPartakeAction {
    private static final int LOGIN_TIMEOUT_SEC = 300;

    public static Result get() throws DAOException, PartakeException {
        return new LoginByTwitterAction().execute();
    }

    public Result doExecute() throws DAOException {
        try {
            ITwitterService twitterService = PartakeApp.getTwitterService();
            String redirectURL = getParameter("redirectURL");
            TwitterLoginInformation info = twitterService.createLoginInformation(redirectURL);

            String sessionId = session().get(Constants.Session.ID_KEY);
            assert sessionId != null;
            Cache.set(Constants.Cache.TWITTER_LOGIN_KEY_PREFIX + sessionId, info, LOGIN_TIMEOUT_SEC);

            return renderRedirect(info.getAuthenticationURL());
        } catch (TwitterException e) {
            return renderError(ServerErrorCode.TWITTER_OAUTH_ERROR, e);
        }

    }
}
