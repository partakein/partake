package in.partake.controller.action.toppage;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.resource.Constants;

public class ToppageAction extends AbstractPartakeAction {

    public static Result get() throws DAOException, PartakeException {
        return new ToppageAction().execute();
    }

    @Override
    protected Result doExecute() throws PartakeException, DAOException {
        return render(views.html.index.render(context()));
    }
}

