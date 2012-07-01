package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserSentMessage;

import java.util.UUID;

public interface IUserSentMessageAccess extends IAccess<UserSentMessage, UUID> {
    public UUID getFreshId(PartakeConnection con) throws DAOException;
}
