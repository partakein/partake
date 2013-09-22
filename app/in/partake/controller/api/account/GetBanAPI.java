package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dto.User;
import in.partake.resource.UserErrorCode;
import play.Logger;
import play.mvc.Result;

public class GetBanAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new GetBanAPI().execute();
    } 

    @Override
    public Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureAdmin();
        String targetUserId = getQueryStringParameter("targetUserId");

        GetBanAPITransaction transaction = new GetBanAPITransaction(user.getId(), targetUserId);
        User targetUser = transaction.execute();
        if (targetUser == null) {
            return renderInvalid(UserErrorCode.INVALID_ARGUMENT);
        } else {
            // Admin専用APIなのでtoSafeJSONの代わりにtoJSONを呼ぶ
            return renderOK(targetUser.toJSON());
        }
    }
}

class GetBanAPITransaction extends DBAccess<User> {
    private final String userId;
    private final String targetUserId;

    public GetBanAPITransaction(String userId, String targetUserId) {
        this.userId = userId;
        this.targetUserId = targetUserId;
    }

    @Override
    protected User doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IUserAccess access = daos.getUserAccess();
        User targetUser = access.find(con, targetUserId);
        if (targetUser == null) {
            Logger.info("No user has specified ID: " + targetUserId);
            return null;
        }

        Logger.info("Administrator(" + userId + ") researched that specified user (" + targetUserId + ") is banned or not.");
        return targetUser;
    }
}
