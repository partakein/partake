package in.partake.controller.api.account;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserPreference;

public class SetPreferenceAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new SetPreferenceAPI().execute();
    }

    @Override
    public Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();

        new SetPreferenceAPITransaction(
                user,
                getBooleanParameter("profilePublic"),
                getBooleanParameter("receivingTwitterMessage"),
                getBooleanParameter("tweetingAttendanceAutomatically")
        ).execute();

        return renderOK();
    }
}

class SetPreferenceAPITransaction extends Transaction<Void> {
    UserEx user;
    Boolean profilePublic;
    Boolean receivingTwitterMessage;
    Boolean tweetingAttendanceAutomatically;

    public SetPreferenceAPITransaction(UserEx user, Boolean profilePublic, Boolean receivingTwitterMessage, Boolean tweetingAttendanceAutomatically) {
        this.user = user;
        this.profilePublic = profilePublic;
        this.receivingTwitterMessage = receivingTwitterMessage;
        this.tweetingAttendanceAutomatically = tweetingAttendanceAutomatically;
    }

    /**
     * Updates UserPreference. Null arguments won't be updated.
     */
    public Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        final UserPreference pref = daos.getUserPreferenceAccess().find(con, user.getId());
        UserPreference newPref = new UserPreference(pref != null ? pref : UserPreference.getDefaultPreference(user.getId()));

        if (profilePublic != null)
            newPref.setProfilePublic(profilePublic);
        if (receivingTwitterMessage != null)
            newPref.setReceivingTwitterMessage(receivingTwitterMessage);
        if (tweetingAttendanceAutomatically != null)
            newPref.setTweetingAttendanceAutomatically(tweetingAttendanceAutomatically);

        daos.getUserPreferenceAccess().put(con, newPref);
        return null;
    }

}


