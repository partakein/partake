package in.partake.controller.action.errorpage;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;

public class StaticErrorPageAction extends AbstractPartakeAction {
    private ServerErrorCode errorCode;

    public static Result get() throws DAOException, PartakeException {
        return new StaticErrorPageAction().execute();
    }

    @Override
    public Result doExecute() throws DAOException {
        errorCode = ServerErrorCode.safeValueOf(getParameter("errorCode"));
        return render(views.html.error.error.render(context(), errorCode));
    }

    public ServerErrorCode getServerErrorCode() {
        return errorCode;
    }
}
