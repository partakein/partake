package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserImage;

import java.util.List;

public interface IUserImageAccess extends IAccess<UserImage, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
    
    // Should return ids ORDER BY createdAt DESC
    public List<String> findIdsByUserId(PartakeConnection con, String userId, int offset, int limit) throws DAOException;
    public int countByUserId(PartakeConnection con, String userId) throws DAOException;
}
