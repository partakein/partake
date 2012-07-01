package in.partake.controller.action.errorpage;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;

public class StaticLoginRequiredPageAction extends AbstractPartakeAction {

    public static Result get() throws DAOException, PartakeException {
        return new StaticLoginRequiredPageAction().execute();
    }

    public Result doExecute() throws DAOException {
        // If a user already has logged in, redirect to the top page.
        UserEx user = getLoginUser();
        if (user != null)
            return renderRedirect("/");

        return render(views.html.error.loginRequired.render(context()));
    }
}
