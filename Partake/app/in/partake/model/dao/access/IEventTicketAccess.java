package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventTicket;

import java.util.List;
import java.util.UUID;

public interface IEventTicketAccess extends IAccess<EventTicket, UUID> {
    public UUID getFreshId(PartakeConnection con) throws DAOException;
    public List<EventTicket> findEventTicketsByEventId(PartakeConnection con, String eventId) throws DAOException;
    public void removeByEventId(PartakeConnection con, String eventId) throws DAOException;
}
