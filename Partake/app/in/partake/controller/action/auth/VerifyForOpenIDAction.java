package in.partake.controller.action.auth;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.User;
import in.partake.model.dto.UserOpenIDLink;
import in.partake.resource.Constants;
import in.partake.resource.MessageCode;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.session.OpenIDLoginInformation;

import java.util.Map;
import java.util.UUID;

import org.openid4java.OpenIDException;
import org.openid4java.discovery.DiscoveryInformation;

import play.cache.Cache;
import play.libs.OpenID;
import play.mvc.Result;
import scala.collection.immutable.Stream.Cons;

public class VerifyForOpenIDAction extends AbstractOpenIDAction {

    public static Result get() throws DAOException, PartakeException {
        return new VerifyForOpenIDAction().execute();
    }

    // なんでかしらないけど、login と connect の openID の URL を一緒にしないと残念なことになる。
    public Result doExecute() throws DAOException, PartakeException {
        String sessionId = session().get(Constants.Session.ID_KEY);
        assert sessionId != null;
        if (sessionId == null)
        	return renderInvalid(UserErrorCode.INVALID_OPENID_PURPOSE);

    	String purpose = (String) Cache.get(Constants.Cache.OPENID_LOGIN_KEY_PREFIX + sessionId);

    	OpenID.UserInfo info = OpenID.verifiedId().get();
    	if (info == null)
    		return renderRedirect("/", MessageCode.MESSAGE_OPENID_LOGIN_FAILURE);

        try {
            if ("login".equals(purpose))
                return verifyOpenIDForLogin(info);
            if ("connect".equals(purpose))
                return verifyOpenIDForConnection(info);

            return renderInvalid(UserErrorCode.INVALID_OPENID_PURPOSE);
        } catch (OpenIDException e) {
            return renderError(ServerErrorCode.OPENID_ERROR, e);
        }
    }

    private Result verifyOpenIDForLogin(OpenID.UserInfo userInfo) throws DAOException, OpenIDException, PartakeException {
        String identifier = userInfo.id;
        if (identifier == null)
            return renderRedirect("/", MessageCode.MESSAGE_OPENID_LOGIN_FAILURE);

        // TODO: UserEx が identifier から取れるべき
        UserEx user = new GetUserFromOpenIDIdentifierTransaction(identifier).execute();
        if (user != null) {
            session().put(Constants.Session.USER_ID_KEY, user.getId());
            if (getRedirectURL() == null)
                return renderRedirect("/");
            else
                return renderRedirect(getRedirectURL());
        } else {
            return renderRedirect("/", MessageCode.MESSAGE_OPENID_LOGIN_NOLINKAGE);
        }
    }

    private Result verifyOpenIDForConnection(OpenID.UserInfo userInfo) throws DAOException, PartakeException, OpenIDException {
        User user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        String identity = userInfo.id;
        if (identity == null)
            return renderInvalid(UserErrorCode.INVALID_OPENID_IDENTIFIER);

        new AddOpenIDTransaction(user.getId(), identity).execute();

        return renderRedirect("/mypage#account", MessageCode.MESSAGE_OPENID_CONNECTION_SUCCESS);
    }
}

class GetUserFromOpenIDIdentifierTransaction extends DBAccess<UserEx> {
    private String identifier;

    GetUserFromOpenIDIdentifierTransaction(String identifier) {
        this.identifier = identifier;
    }

    @Override
    protected UserEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        UserOpenIDLink linkage = daos.getOpenIDLinkageAccess().findByOpenId(con, identifier);
        if (linkage == null)
            return null;

        return UserDAOFacade.getUserEx(con, daos, linkage.getUserId());
    }
}

class AddOpenIDTransaction extends Transaction<Void> {
    private String userId;
    private String identifier;

    public AddOpenIDTransaction(String userId, String identifier) {
        this.userId = userId;
        this.identifier = identifier;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        UUID id = daos.getOpenIDLinkageAccess().getFreshId(con);
        daos.getOpenIDLinkageAccess().put(con, new UserOpenIDLink(id, userId, identifier));
        return null;
    }
}
