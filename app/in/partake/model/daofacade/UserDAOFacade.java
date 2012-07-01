package in.partake.model.daofacade;

import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.access.IUserTwitterLinkAccess;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.UserTwitterLink;

public class UserDAOFacade extends AbstractPartakeDAOFacade {
    public static UserPreference getPreference(PartakeConnection con, IPartakeDAOs daos, String userId) throws DAOException {
        UserPreference pref = daos.getUserPreferenceAccess().find(con, userId);
        if (pref == null)
            pref = UserPreference.getDefaultPreference(userId);
        return pref;
    }

    public static UserEx getUserEx(PartakeConnection con, IPartakeDAOs daos, String userId) throws DAOException {
        IUserAccess userAccess = daos.getUserAccess();
        IUserTwitterLinkAccess twitterDAO = daos.getTwitterLinkageAccess();

        User user = userAccess.find(con, userId);
        if (user == null) { return null; }

        UserTwitterLink linkage = twitterDAO.findByUserId(con, userId);
        return new UserEx(user, linkage);
    }

}
