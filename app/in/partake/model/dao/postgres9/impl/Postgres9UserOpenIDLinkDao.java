package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserOpenIDLinkAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9DataIterator;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IdMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.dto.UserOpenIDLink;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

class EntityOpenIDLinkageMapper extends Postgres9EntityDataMapper<UserOpenIDLink> {
    @Override
    public UserOpenIDLink map(JSONObject obj) {
        return new UserOpenIDLink(obj).freeze();
    }
}

public class Postgres9UserOpenIDLinkDao extends Postgres9Dao implements IUserOpenIDLinkAccess {
    static final String ENTITY_TABLE_NAME = "UserOpenIDLinkEntities";
    static final String INDEX_TABLE_NAME = "UserOpenIdLinkIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityOpenIDLinkageMapper mapper;

    public Postgres9UserOpenIDLinkDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityOpenIDLinkageMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        entityDao.initialize((Postgres9Connection) con);
        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, identifier TEXT NOT NULL, userId TEXT NOT NULL)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "UserId" + " ON " + INDEX_TABLE_NAME + "(userId)");
            indexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + INDEX_TABLE_NAME + "OpenId" + " ON " + INDEX_TABLE_NAME + "(identifier)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
        indexDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, UserOpenIDLink linkage) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        Postgres9Entity entity = new Postgres9Entity(linkage.getId(), CURRENT_VERSION, linkage.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());

        if (entityDao.exists(pcon, linkage.getId())) {
            entityDao.update(pcon, entity);
        } else {
            entityDao.insert(pcon, entity);
        }
        indexDao.put((Postgres9Connection) con, new String[] { "id", "identifier", "userId" }, new String[] { linkage.getId().toString(), linkage.getIdentifier(), linkage.getUserId() });
    }

    @Override
    public UserOpenIDLink find(PartakeConnection con, UUID id) throws DAOException {
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
        indexDao.remove((Postgres9Connection) con, "id", id);
    }

    @Override
    public DataIterator<UserOpenIDLink> getIterator(PartakeConnection con) throws DAOException {
        DataIterator<Postgres9Entity> iterator = entityDao.getIterator((Postgres9Connection) con);
        return new MapperDataIterator<Postgres9Entity, UserOpenIDLink>(mapper, iterator);
    }

    // TODO: Why not DataIterator?
    // TODO: Why not List<OpenIdLinkage>?
    @Override
    public List<UserOpenIDLink> findByUserId(PartakeConnection con, String userId) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE userId = ?",
                new Object[] { userId });

        Postgres9IdMapper<UserOpenIDLink> idMapper = new Postgres9IdMapper<UserOpenIDLink>((Postgres9Connection) con, mapper, entityDao);

        DataIterator<UserOpenIDLink> it = new Postgres9DataIterator<UserOpenIDLink>(idMapper, psars);
        try {
            ArrayList<UserOpenIDLink> results = new ArrayList<UserOpenIDLink>();
            while (it.hasNext()) {
                UserOpenIDLink t = it.next();
                if (t == null)
                    continue;

                results.add(t.freeze());
            }

            return results;
        } finally {
            it.close();
        }

    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }

    @Override
    public UserOpenIDLink findByOpenId(PartakeConnection con, String identifier) throws DAOException {
        String id = indexDao.find((Postgres9Connection) con, "id", "identifier", identifier);
        if (id == null)
            return null;

        return find((Postgres9Connection) con, UUID.fromString(id));
    }

    @Override
    public UUID getFreshId(PartakeConnection con) throws DAOException {
        return UUID.fromString(entityDao.getFreshId((Postgres9Connection) con));
    }
}
