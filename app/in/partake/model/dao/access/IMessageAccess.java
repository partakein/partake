package in.partake.model.dao.access;

import java.util.UUID;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Message;

public interface IMessageAccess extends IAccess<Message, UUID> {
    public UUID getFreshId(PartakeConnection con) throws DAOException;
}
