package in.partake.controller.action.toppage;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;

public class StaticTermOfUsePageAction extends AbstractPartakeAction {
    public static Result get() throws DAOException, PartakeException {
        return new StaticTermOfUsePageAction().execute();
    }

    @Override
    protected Result doExecute() throws PartakeException, DAOException {
        return render(views.html.termofuse.render(context()));
    }
}
