package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserCalendarLinkageAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dto.UserCalendarLink;
import net.sf.json.JSONObject;

class EntityCalendarLinkageMapper extends Postgres9EntityDataMapper<UserCalendarLink> {
    public UserCalendarLink map(JSONObject obj) {
        return new UserCalendarLink(obj).freeze();
    }
}

public class Postgres9UserCalendarLinkDao extends Postgres9Dao implements IUserCalendarLinkageAccess {
    static final String ENTITY_TABLE_NAME = "UserCalendarLinkEntities";
    static final String INDEX_TABLE_NAME = "UserCalendarLinkIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityCalendarLinkageMapper mapper;

    public Postgres9UserCalendarLinkDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityCalendarLinkageMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        entityDao.initialize((Postgres9Connection) con);

        Postgres9Connection pcon = (Postgres9Connection) con;
        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, userId TEXT NOT NULL UNIQUE)");
            indexDao.createIndex(pcon, "CREATE INDEX "+ INDEX_TABLE_NAME + "UserId ON " + INDEX_TABLE_NAME + "(userId)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
        indexDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, UserCalendarLink linkage) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        Postgres9Entity entity = new Postgres9Entity(linkage.getId(), CURRENT_VERSION, linkage.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());
        if (entityDao.exists(pcon, linkage.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon, new String[] { "id", "userId" }, new String[] { linkage.getId(), linkage.getUserId() });
    }

    @Override
    public UserCalendarLink find(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public boolean exists(PartakeConnection con, String id) throws DAOException {
        return entityDao.exists((Postgres9Connection) con, id);
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
        indexDao.remove((Postgres9Connection) con, "id", id);
    }


    @Override
    public DataIterator<UserCalendarLink> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, UserCalendarLink>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public UserCalendarLink findByUserId(PartakeConnection con, String userId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", "userId", userId);
        if (id == null)
            return null;

        return mapper.map(entityDao.find(pcon, id));
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }

    @Override
    public void removeByUserId(PartakeConnection con, String userId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", "userId", userId);

        entityDao.remove(pcon, id);
        indexDao.remove(pcon, "id", id);
    }
}
