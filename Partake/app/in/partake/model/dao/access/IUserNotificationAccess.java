package in.partake.model.dao.access;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserNotification;

public interface IUserNotificationAccess extends IAccess<UserNotification, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;

    List<UserNotification> findByUserId(PartakeConnection con, String userId, int offset, int limit) throws DAOException;
}
