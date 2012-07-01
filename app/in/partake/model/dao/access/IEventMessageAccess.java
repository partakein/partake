package in.partake.model.dao.access;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventMessage;

public interface IEventMessageAccess extends IAccess<EventMessage, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;

    // Returns event messages order by createdAt DESC.
    public List<EventMessage> findByEventId(PartakeConnection con, String eventId, int offset, int limit) throws DAOException;

}
