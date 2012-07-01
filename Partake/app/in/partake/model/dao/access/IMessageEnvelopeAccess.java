package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.MessageEnvelope;

public interface IMessageEnvelopeAccess extends IAccess<MessageEnvelope, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
}
