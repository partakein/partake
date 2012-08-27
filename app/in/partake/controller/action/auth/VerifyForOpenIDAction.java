package in.partake.controller.action.auth;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeConfiguration;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.openid4java.OpenIDException;
import org.openid4java.discovery.DiscoveryInformation;

import play.cache.Cache;
import play.mvc.Result;

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

        String receivingURL = receivingURL();
        Map<String, Object> params = new HashMap<String, Object>();
        for (Map.Entry<String, String[]> entry : request().queryString().entrySet())
            params.put(entry.getKey(), entry.getValue());

        OpenIDLoginInformation info = (OpenIDLoginInformation) Cache.get(Constants.Cache.OPENID_LOGIN_KEY_PREFIX + sessionId);
        String purpose = info.takeLoginPurpose();
        try {
            if ("login".equals(purpose))
                return verifyOpenIDForLogin(receivingURL, params, info.getDiscoveryInformation());
            if ("connect".equals(purpose))
                return verifyOpenIDForConnection(receivingURL, params, info.getDiscoveryInformation());

            return renderInvalid(UserErrorCode.INVALID_OPENID_PURPOSE);
        } catch (OpenIDException e) {
            return renderError(ServerErrorCode.OPENID_ERROR, e);
        }
    }

    private Result verifyOpenIDForLogin(String receivingURL, Map<String, Object> params, DiscoveryInformation discoveryInformation) throws DAOException, OpenIDException, PartakeException {
        String identity = PartakeApp.getOpenIDService().getIdentifier(receivingURL, params, discoveryInformation);
        if (identity == null)
            return renderRedirect("/", MessageCode.MESSAGE_OPENID_LOGIN_FAILURE);

        // TODO: UserEx が identifier から取れるべき
        UserEx user = new GetUserFromOpenIDIdentifierTransaction(identity).execute();
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

    private Result verifyOpenIDForConnection(String receivingURL, Map<String, Object> params, DiscoveryInformation discoveryInformation) throws DAOException, PartakeException, OpenIDException {
        User user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        String identity = PartakeApp.getOpenIDService().getIdentifier(receivingURL, params, discoveryInformation);
        if (identity == null)
            return renderInvalid(UserErrorCode.INVALID_OPENID_IDENTIFIER);

        new AddOpenIDTransaction(user.getId(), identity).execute();

        return renderRedirect("/mypage#account", MessageCode.MESSAGE_OPENID_CONNECTION_SUCCESS);
    }

    private String receivingURL() {
        return PartakeConfiguration.toppath() + request().uri();
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
