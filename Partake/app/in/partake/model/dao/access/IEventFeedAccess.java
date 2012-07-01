package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventFeed;

public interface IEventFeedAccess extends IAccess<EventFeed, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
	public String findByEventId(PartakeConnection con, String eventId) throws DAOException;
}