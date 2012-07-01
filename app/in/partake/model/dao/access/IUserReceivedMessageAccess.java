package in.partake.model.dao.access;

import java.util.List;
import java.util.UUID;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserReceivedMessage;

public interface IUserReceivedMessageAccess extends IAccess<UserReceivedMessage, UUID> {
    public UUID getFreshId(PartakeConnection con) throws DAOException;

    int countByReceiverId(PartakeConnection con, String receiverId) throws DAOException;
    List<UserReceivedMessage> findByReceiverId(PartakeConnection con, String receiverId, int offset, int limit) throws DAOException;

}
