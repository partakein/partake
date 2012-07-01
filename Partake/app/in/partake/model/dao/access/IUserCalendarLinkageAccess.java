package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserCalendarLink;


/**
 * @author shinyak
 */
public interface IUserCalendarLinkageAccess extends IAccess<UserCalendarLink, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
    public UserCalendarLink findByUserId(PartakeConnection con, String userId) throws DAOException;
    public void removeByUserId(PartakeConnection con, String userId) throws DAOException;
}
