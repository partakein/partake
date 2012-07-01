package in.partake.controller.action.errorpage;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;

public class StaticForbiddenPageAction extends AbstractPartakeAction {

    public static Result get() throws DAOException, PartakeException {
        return new StaticForbiddenPageAction().execute();
    }

    public Result doExecute() throws DAOException {
        return render(views.html.error.forbidden.render(context()));
    }
}
