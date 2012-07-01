package in.partake.controller.action.errorpage;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;
import play.mvc.Result;

public class StaticInvalidPageAction extends AbstractPartakeAction {
    private UserErrorCode errorCode;

    public static Result get() throws DAOException, PartakeException {
        return new StaticInvalidPageAction().execute();
    }

    public Result doExecute() throws DAOException {
        errorCode = UserErrorCode.safeValueOf(getParameter("errorCode"));
        return render(views.html.error.invalid.render(context(), errorCode));
    }

    public UserErrorCode getUserErrorCode() {
        return errorCode;
    }
}
