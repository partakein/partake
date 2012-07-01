package in.partake.controller.action.errorpage;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import play.mvc.Result;

public class StaticNotFoundPageAction extends AbstractPartakeAction {

    public static Result get() throws DAOException, PartakeException {
        return new StaticNotFoundPageAction().execute();
    }

    public Result doExecute() throws DAOException {
        return render(views.html.error.notfound.render(context()));
    }
}
