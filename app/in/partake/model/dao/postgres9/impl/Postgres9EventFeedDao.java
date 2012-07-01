package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dto.EventFeed;
import net.sf.json.JSONObject;

class EntityEventFeedMapper extends Postgres9EntityDataMapper<EventFeed> {
    public EventFeed map(JSONObject obj) {
        return new EventFeed(obj).freeze();
    }
}

public class Postgres9EventFeedDao extends Postgres9Dao implements IEventFeedAccess {
    static final String ENTITY_TABLE_NAME = "EventFeedEntities";
    static final String INDEX_TABLE_NAME = "EventFeedIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityEventFeedMapper mapper;

    public Postgres9EventFeedDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityEventFeedMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, eventId TEXT NOT NULL)");
            indexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + INDEX_TABLE_NAME + "EventId" + " ON " + INDEX_TABLE_NAME + "(eventId)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
        indexDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, EventFeed linkage) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        Postgres9Entity entity = new Postgres9Entity(linkage.getId(), CURRENT_VERSION, linkage.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());

        if (entityDao.exists(pcon, linkage.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon, new String[] { "id", "eventId" } , new Object[] { linkage.getId(), linkage.getEventId() });
    }

    @Override
    public EventFeed find(PartakeConnection con, String id) throws DAOException {
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
    public DataIterator<EventFeed> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, EventFeed>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public String findByEventId(PartakeConnection con, String eventId) throws DAOException {
        return indexDao.find((Postgres9Connection) con, "id", "eventId", eventId);
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
}
