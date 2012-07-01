package in.partake.controller.action.auth;

import in.partake.base.PartakeException;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;

import org.openid4java.OpenIDException;

import play.mvc.Result;

public class ConnectWithOpenIDAction extends AbstractOpenIDAction {

    public static Result get() throws DAOException, PartakeException {
        return new ConnectWithOpenIDAction().execute();
    }

    public Result doExecute() throws DAOException, PartakeException {
        try {
            ensureValidSessionToken();
            return doAuthenticate("connect");
        } catch (OpenIDException e) {
            return renderError(ServerErrorCode.OPENID_ERROR, e);
        }
    }
}
