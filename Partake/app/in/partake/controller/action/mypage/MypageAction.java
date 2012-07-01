package in.partake.controller.action.mypage;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.UserCalendarLink;
import in.partake.model.dto.UserOpenIDLink;
import in.partake.model.dto.UserPreference;

import java.util.List;

import play.mvc.Result;

public class MypageAction extends AbstractPartakeAction {
    private UserPreference preference;
    private List<UserOpenIDLink> openIds;
    private UserCalendarLink calendarLink;

    public static Result get() throws DAOException, PartakeException {
        return new MypageAction().execute();
    }

    public Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        MypageActionTransaction transaction = new MypageActionTransaction(user.getId());
        transaction.execute();

        preference = transaction.getPreference();
        openIds = transaction.getOpenIds();
        calendarLink = transaction.getCalendarLink();

        return render(views.html.mypage.show.render(context(), user, preference, openIds, calendarLink));
    }

    // ----------------------------------------------------------------------

    public UserPreference getPreference() {
        return preference;
    }

    public UserCalendarLink getCalendarLink() {
        return calendarLink;
    }

    public List<UserOpenIDLink> getOpenIds() {
        return openIds;
    }
}

class MypageActionTransaction extends DBAccess<Void> {
    private String userId;
    private UserPreference preference;
    private UserCalendarLink calendarLink;
    private List<UserOpenIDLink> openIds;

    public MypageActionTransaction(String userId) {
        this.userId = userId;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        preference = UserDAOFacade.getPreference(con, daos, userId);
        calendarLink = daos.getCalendarAccess().findByUserId(con, userId);
        openIds = daos.getOpenIDLinkageAccess().findByUserId(con, userId);
        return null;
    }

    public UserPreference getPreference() {
        return preference;
    }

    public UserCalendarLink getCalendarLink() {
        return calendarLink;
    }

    public List<UserOpenIDLink> getOpenIds() {
        return openIds;
    }
}
