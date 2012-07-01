package in.partake.controller.action.feed;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;

public class FeedListPageAction extends AbstractPartakeAction {

    public static Result get() throws DAOException, PartakeException {
        return new FeedListPageAction().execute();
    }

    @Override
    protected Result doExecute() throws PartakeException, DAOException {
        return render(views.html.feedlist.render(context()));
    }
}
