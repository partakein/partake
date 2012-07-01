package in.partake.model.dao.access;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventActivity;

public interface IEventActivityAccess extends IAccess<EventActivity, String> {

    public String getFreshId(PartakeConnection con) throws DAOException;
    
    /**
     *  eventActivity を取得。created at で降順に sort される。即ち、新しいのが一番前にくる。
     */
    public List<EventActivity> findByEventId(PartakeConnection con, String eventId, int length) throws DAOException;
    
}
