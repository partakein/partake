package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9DataIterator;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IdMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.dto.EventActivity;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

class EntityEventActivityMapper extends Postgres9EntityDataMapper<EventActivity> {
    public EventActivity map(JSONObject obj) {
        return new EventActivity(obj).freeze();
    }
}

public class Postgres9EventActivityDao extends Postgres9Dao implements IEventActivityAccess {
    static final String ENTITY_TABLE_NAME = "EventActivityEntities";
    static final String INDEX_TABLE_NAME = "EventActivityIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityEventActivityMapper mapper;

    public Postgres9EventActivityDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityEventActivityMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        entityDao.initialize(pcon);
        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, eventId TEXT NOT NULL, createdAt TIMESTAMP NOT NULL)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "EventId" + " ON " + INDEX_TABLE_NAME + "(eventId, createdAt)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        entityDao.truncate(pcon);
        indexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, EventActivity activity) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(activity.getId(), CURRENT_VERSION, activity.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());
        if (entityDao.exists(pcon, activity.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon, new String[] { "id", "eventId", "createdAt" }, new Object[] { activity.getId(), activity.getEventId(), activity.getCreatedAt() } );
    }

    @Override
    public EventActivity find(PartakeConnection con, String id) throws DAOException {
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
    public DataIterator<EventActivity> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, EventActivity>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public List<EventActivity> findByEventId(PartakeConnection con, String eventId, int length) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE eventId = ? ORDER BY createdAt DESC LIMIT ?",
                new Object[] { eventId, length });

        Postgres9IdMapper<EventActivity> idMapper = new Postgres9IdMapper<EventActivity>((Postgres9Connection) con, mapper, entityDao);

        DataIterator<EventActivity> it = new Postgres9DataIterator<EventActivity>(idMapper, psars);
        ArrayList<EventActivity> activities = new ArrayList<EventActivity>();
        try {
            while (it.hasNext()) {
                EventActivity activity = it.next();
                if (activity != null)
                    activities.add(activity);
            }
        } finally {
            it.close();
        }

        return activities;
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
}
