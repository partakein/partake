package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.ITwitterMessageAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dto.TwitterMessage;
import net.sf.json.JSONObject;

class EntityTwitterMessageMapper extends Postgres9EntityDataMapper<TwitterMessage> {
    public TwitterMessage map(JSONObject obj) {
        return new TwitterMessage(obj).freeze();
    }
}

public class Postgres9TwitterMessageDao extends Postgres9Dao implements ITwitterMessageAccess {
    static final String ENTITY_TABLE_NAME = "TwitterMessageEntities";
    static final String INDEX_TABLE_NAME = "TwitterMessageIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityTwitterMessageMapper mapper;

    public Postgres9TwitterMessageDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityTwitterMessageMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            // event id may be NULL if system message.
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, userId TEXT NOT NULL, createdAt TIMESTAMP NOT NULL)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "UserId" + " ON " + INDEX_TABLE_NAME + "(userId, createdAt)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.truncate(pcon);
        indexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, TwitterMessage t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        Postgres9Entity entity = new Postgres9Entity(t.getId(), CURRENT_VERSION, t.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());
        if (entityDao.exists(pcon, t.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon, new String[] { "id", "userId", "createdAt" }, new Object[] { t.getId(), t.getUserId(), t.getCreatedAt() } );
    }

    @Override
    public TwitterMessage find(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public boolean exists(PartakeConnection con, String id) throws DAOException {
        return entityDao.exists((Postgres9Connection) con, id);
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.remove(pcon, id);
        indexDao.remove(pcon, "id", id);
    }

    @Override
    public DataIterator<TwitterMessage> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, TwitterMessage>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
}
