package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventTicketNotification;
import in.partake.model.dto.auxiliary.NotificationType;

import java.util.List;
import java.util.UUID;

public interface IEventTicketNotificationAccess extends IAccess<EventTicketNotification, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;

    public List<EventTicketNotification> findByTicketId(PartakeConnection con, UUID ticketId, int offset, int limit) throws DAOException;
    public EventTicketNotification findLastNotification(PartakeConnection con, UUID ticketId, NotificationType type) throws DAOException;

    public List<EventTicketNotification> findByEventId(PartakeConnection con, String eventId, int offset, int limit) throws DAOException;
}
