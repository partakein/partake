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

public class BanAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new BanAPI().execute();
    } 

    @Override
    public Result doExecute() throws DAOException, PartakeException {
        ensureValidSessionToken();
        UserEx user = ensureAdmin();

        String targetUserId = getFormParameter("targetUserId");
        boolean state = getBooleanParameter("targetState"); // true if administrator wants to ban

        BanAPITransaction transaction = new BanAPITransaction(user.getId(), targetUserId, state);
        User bannedUser = transaction.execute();
        if (bannedUser == null) {
            return renderInvalid(UserErrorCode.INVALID_ARGUMENT);
        } else {
            // Admin専用APIなのでtoSafeJSONの代わりにtoJSONを呼ぶ
            return renderOK(bannedUser.toJSON());
        }
    }
}

class BanAPITransaction extends DBAccess<User> {
    private final String userId;
    private final String targetUserId;
    private final boolean targetState;

    public BanAPITransaction(String userId, String targetUserId, boolean state) {
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.targetState = state;
    }

    @Override
    protected User doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IUserAccess access = daos.getUserAccess();
        User targetUser = access.find(con, targetUserId);
        if (targetUser == null) {
            Logger.info("No user has specified ID: " + targetUserId);
            return null;
        } else if (targetState == targetUser.isBanned()) {
            if (targetState) {
                Logger.info(String.format(
                        "No need to ban: specified user (%s) is already banned",
                        targetUser.getId()));
            } else {
                Logger.info(String.format(
                        "No need to recover: specified user (%s) is not banned yet",
                        targetUser.getId()));
            }
            return null;
        }

        targetUser = new User(targetUser);
        targetUser.setBanned(targetState);
        access.put(con, targetUser);
        Logger.info(String.format(
                "Administrator (%s) changed state of user (%s) to %s",
                userId,
                targetUser.getId(),
                targetState ? "BANNED" : "NOT BANNED"));

        return targetUser;
    }
}
