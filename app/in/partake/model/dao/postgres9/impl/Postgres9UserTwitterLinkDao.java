package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTwitterLinkAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9DataIterator;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IdMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.daoutil.DAOUtil;
import in.partake.model.dto.UserTwitterLink;

import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

class EntityUserTwitterLinkMapper extends Postgres9EntityDataMapper<UserTwitterLink> {
    public UserTwitterLink map(JSONObject obj) {
        return new UserTwitterLink(obj).freeze();
    }
}

public class Postgres9UserTwitterLinkDao extends Postgres9Dao implements IUserTwitterLinkAccess {
    static final String ENTITY_TABLE_NAME = "UserTwitterLinkEntities";
    static final String INDEX_TABLE_NAME = "UserTwitterLinkIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityUserTwitterLinkMapper mapper;

    public Postgres9UserTwitterLinkDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityUserTwitterLinkMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, twitterId TEXT NOT NULL, userId TEXT NOT NULL, screenName TEXT NOT NULL)");
            indexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + INDEX_TABLE_NAME + "TwitterId" + " ON " + INDEX_TABLE_NAME + "(twitterId)");
            indexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + INDEX_TABLE_NAME + "UserId" + " ON " + INDEX_TABLE_NAME + "(userId)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "ScreenName" + " ON " + INDEX_TABLE_NAME + "(screenName)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.truncate(pcon);
        indexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, UserTwitterLink linkage) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        Postgres9Entity entity = new Postgres9Entity(linkage.getId(), CURRENT_VERSION, linkage.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());

        if (entityDao.exists(pcon, linkage.getId())) {
            entityDao.update(pcon, entity);
        } else {
            entityDao.insert(pcon, entity);
        }

        indexDao.put(pcon,
                new String[] { "id", "twitterId", "userId", "screenName" },
                new Object[] { linkage.getId().toString(), String.valueOf(linkage.getTwitterId()), linkage.getUserId(), linkage.getScreenName() });
    }

    @Override
    public UserTwitterLink find(PartakeConnection con, UUID id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public boolean exists(PartakeConnection con, UUID id) throws DAOException {
        return entityDao.exists((Postgres9Connection) con, id);
    }

    @Override
    public void remove(PartakeConnection con, UUID id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.remove(pcon, id);
        indexDao.remove(pcon, "id", id);
    }

    @Override
    public DataIterator<UserTwitterLink> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, UserTwitterLink>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }

    @Override
    public UserTwitterLink findByTwitterId(PartakeConnection con, long twitterId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", "twitterId", String.valueOf(twitterId));
        if (id == null)
            return null;

        return find(pcon, UUID.fromString(id));
    }

    @Override
    public UserTwitterLink findByUserId(PartakeConnection con, String userId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", "userId", userId);
        if (id == null)
            return null;

        return find(pcon, UUID.fromString(id));
    }

    @Override
    public List<UserTwitterLink> findByScreenNamePrefix(PartakeConnection con, String screenNamePrefix, int limit) throws DAOException {
        // What happends if '%' is included in the screenNamePrefix?
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE screenName LIKE ? LIMIT ?",
                new Object[] { escapeForLike(screenNamePrefix) + "%", limit });

        try {
            Postgres9IdMapper<UserTwitterLink> idMapper = new Postgres9IdMapper<UserTwitterLink>((Postgres9Connection) con, mapper, entityDao);
            DataIterator<UserTwitterLink> it = new Postgres9DataIterator<UserTwitterLink>(idMapper, psars);
            return DAOUtil.convertToList(it);
        } finally {
            psars.close();
        }


    }

    @Override
    public UUID getFreshId(PartakeConnection con) throws DAOException {
        return UUID.fromString(entityDao.getFreshId((Postgres9Connection) con));
    }
}
