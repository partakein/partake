package in.partake.controller.action.auth;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeConfiguration;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.User;
import in.partake.model.dto.UserTwitterLink;
import in.partake.resource.Constants;
import in.partake.resource.MessageCode;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.service.ITwitterService;
import in.partake.session.TwitterLoginInformation;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import play.mvc.Result;
import twitter4j.TwitterException;

public class VerifyForTwitterAction extends AbstractPartakeAction {

    public static Result get() throws DAOException, PartakeException {
        return new VerifyForTwitterAction().execute();
    }

    public Result doExecute() throws DAOException, PartakeException {
        String verifier = getParameter("oauth_verifier");
        if (StringUtils.isBlank(verifier))
            return renderInvalid(UserErrorCode.INVALID_OAUTH_VERIFIER);

        String sessionId = session().get(Constants.Session.ID_KEY);
        assert sessionId != null;
        TwitterLoginInformation loginInformation =
                (TwitterLoginInformation) Cache.get(Constants.Cache.TWITTER_LOGIN_KEY_PREFIX + sessionId);
        if (loginInformation == null)
            return renderInvalid(UserErrorCode.UNEXPECTED_REQUEST);
        Cache.set(Constants.Cache.TWITTER_LOGIN_KEY_PREFIX + sessionId, null);

        MessageCode messageCode = null;
        try {
            ITwitterService twitterService = PartakeApp.getTwitterService();
            UserTwitterLink linkage = twitterService.createTwitterLinkageFromLoginInformation(loginInformation, verifier);

            UserEx user = new VerifyForTwitterActionTransaction(linkage).execute();
            session().put(Constants.Session.USER_ID_KEY, user.getId());

            messageCode = MessageCode.MESSAGE_AUTH_LOGIN;
        } catch (TwitterException e) {
            return renderError(ServerErrorCode.TWITTER_OAUTH_ERROR, e);
        }

        String redirectURL = loginInformation.getRedirectURL();
        if (StringUtils.isEmpty(redirectURL))
            return renderRedirect("/", messageCode);

        // If the redirect page is the error page, we do not want to show it. Showing the top page is better.
        // TODO(mayah): We should not put these values here.
        String errorPagePaths[] = new String[] { "/error", "/notfound", "/invalid", "/prohibited", "/forbidden", "/loginRequired" };
        for (String errorPagePath : errorPagePaths) {
            String errorPageURL = PartakeConfiguration.toppath() + errorPagePath;
            if (redirectURL.startsWith(errorPageURL))
                return renderRedirect("/", messageCode);
        }

        return renderRedirect(redirectURL, messageCode);
    }
}

class VerifyForTwitterActionTransaction extends Transaction<UserEx> {
    private UserTwitterLink twitterLinkageEmbryo;

    public VerifyForTwitterActionTransaction(UserTwitterLink linkage) {
        this.twitterLinkageEmbryo = linkage;
    }

    @Override
    protected UserEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        // Twitter Linkage から User を引いてくる。
        // 対応する user がいない場合は、user を作成して Twitter Linkage を付与する

        try {
            // 1. まず TwitterLinkage をアップデート
            UserTwitterLink twitterLinkage = updateTwitterLinkage(con, daos, twitterLinkageEmbryo);
            // 2. 対応するユーザーを生成
            UserEx user = getUserFromTwitterLinkage(con, daos, twitterLinkage);
            return user;
        } catch (TwitterException e) {
            throw new PartakeException(ServerErrorCode.TWITTER_OAUTH_ERROR, e);
        }
    }

    private UserTwitterLink updateTwitterLinkage(PartakeConnection con, IPartakeDAOs daos, UserTwitterLink twitterLinkageEmbryo) throws DAOException, TwitterException {
        UserTwitterLink twitterLinkage = daos.getTwitterLinkageAccess().findByTwitterId(con, twitterLinkageEmbryo.getTwitterId());

        if (twitterLinkage == null || twitterLinkage.getUserId() == null) {
            String userId = daos.getUserAccess().getFreshId(con);
            UUID id = daos.getTwitterLinkageAccess().getFreshId(con);
            twitterLinkageEmbryo.setId(id);
            twitterLinkageEmbryo.setUserId(userId);
        } else {
            twitterLinkageEmbryo.setId(twitterLinkage.getId());
            twitterLinkageEmbryo.setUserId(twitterLinkage.getUserId());
        }

        daos.getTwitterLinkageAccess().put(con, twitterLinkageEmbryo);
        return twitterLinkageEmbryo;
    }

    private UserEx getUserFromTwitterLinkage(PartakeConnection con, IPartakeDAOs daos, UserTwitterLink twitterLinkage) throws DAOException, TwitterException {
        String userId = twitterLinkage.getUserId();
        User user = daos.getUserAccess().find(con, userId);
        if (user != null) {
            if (verifyUserProfiles(user, twitterLinkage)) {
                return new UserEx(user, twitterLinkage);
            } else {
                // to update user data, the user is once removed.
                daos.getUserAccess().remove(con, userId);
            }
        }

        // If no user was associated to UserTwitterLink, we create a new user.
        User newUser = new User(userId, twitterLinkage.getScreenName(), twitterLinkage.getProfileImageURL(), TimeUtil.getCurrentDateTime(), null);
        daos.getUserAccess().put(con, newUser);
        newUser.freeze();

        return new UserEx(newUser, twitterLinkage);
    }

	/**
	 * Verifies whether the screenName & profileImageURL of the user are the same<br>
	 *  with the original twitterLinkage ones.
	 * @param user
	 * @param twitterLinkage
	 * @return true when the data are the same.
	 */
	private boolean verifyUserProfiles(User user, UserTwitterLink twitterLinkage) {
    return
        user.getScreenName().equals(twitterLinkage.getScreenName())
        &&
        user.getProfileImageURL().equals(twitterLinkage.getProfileImageURL());
	}

}
