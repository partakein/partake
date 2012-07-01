package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.TwitterMessage;

public interface ITwitterMessageAccess extends IAccess<TwitterMessage, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
}
