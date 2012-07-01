package in.partake.model.dao.access;

import java.util.List;
import java.util.UUID;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserTwitterLink;

public interface IUserTwitterLinkAccess extends IAccess<UserTwitterLink, UUID> {
    // screen name は、同じものが複数いるかもしれない。
    // TODO: あとで実装する必要がある
    // public List<TwitterLinkage> findByScreenName(PartakeConnection con, String screenName) throws DAOException;

    public UUID getFreshId(PartakeConnection con) throws DAOException;
    public UserTwitterLink findByTwitterId(PartakeConnection con, long twitterId) throws DAOException;
    public UserTwitterLink findByUserId(PartakeConnection con, String userId) throws DAOException;

    public List<UserTwitterLink> findByScreenNamePrefix(PartakeConnection con, String screenNamePrefix, int limit) throws DAOException;
}
