package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserOpenIDLink;

import java.util.List;
import java.util.UUID;


public interface IUserOpenIDLinkAccess extends IAccess<UserOpenIDLink, UUID> {
    public UUID getFreshId(PartakeConnection con) throws DAOException;
    public List<UserOpenIDLink> findByUserId(PartakeConnection con, String userId) throws DAOException;
    public abstract UserOpenIDLink findByOpenId(PartakeConnection con, String identifier) throws DAOException;
}
