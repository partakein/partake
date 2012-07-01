package in.partake.controller.action.auth;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.resource.MessageCode;
import play.mvc.Result;

public class LogoutAction extends AbstractPartakeAction {

    public static Result get() throws DAOException, PartakeException {
        return new LogoutAction().execute();
    }

    public Result doExecute() throws DAOException {
        session().clear();

        return renderRedirect("/", MessageCode.MESSAGE_AUTH_LOGOUT);
    }
}
