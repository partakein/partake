package in.partake.controller.action.user;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.UserPreference;
import in.partake.resource.UserErrorCode;
import play.mvc.Result;

public class ShowAction extends AbstractPartakeAction {
    private String userId;
    private UserEx user;

    public static Result get(String userId) throws DAOException, PartakeException {
        ShowAction action = new ShowAction();
        action.userId = userId;
        return action.execute();
    }

    public Result doExecute() throws DAOException, PartakeException {
    	checkIdParameterIsValid(userId,UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        ShowActionTransaction transaction = new ShowActionTransaction(userId);
        transaction.execute();

        if (!transaction.getPreference().isProfilePublic())
        	return render(views.html.users.showPrivate.render(context()));

        user = transaction.getUser();
        return render(views.html.users.show.render(context(), user));
    }

    public UserEx getUser() {
        return user;
    }
}

class ShowActionTransaction extends DBAccess<UserEx> {
    private String userId;
    private UserEx user;
    private UserPreference pref;

    public ShowActionTransaction(String userId) {
        this.userId = userId;
    }

    @Override
    protected UserEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        user = UserDAOFacade.getUserEx(con, daos, userId);
        if (user == null)
            throw new PartakeException(UserErrorCode.INVALID_NOTFOUND);
        pref = daos.getUserPreferenceAccess().find(con, userId);
        if (pref == null)
            pref = UserPreference.getDefaultPreference(userId);
        return user;
    }

    public UserEx getUser() {
        return user;
    }

    public UserPreference getPreference() {
        return pref;
    }
}
