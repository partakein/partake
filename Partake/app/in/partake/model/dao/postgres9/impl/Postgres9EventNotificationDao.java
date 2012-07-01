package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventTicketNotificationAccess;
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
import in.partake.model.dto.EventTicketNotification;
import in.partake.model.dto.auxiliary.NotificationType;

import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

class EntityEventNotificationMapper extends Postgres9EntityDataMapper<EventTicketNotification> {
    public EventTicketNotification map(JSONObject obj) {
        return new EventTicketNotification(obj).freeze();
    }
}

public class Postgres9EventNotificationDao extends Postgres9Dao implements IEventTicketNotificationAccess {
    static final String ENTITY_TABLE_NAME = "EventNotificationEntities";
    static final String INDEX_TABLE_NAME = "EventNotificationIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityEventNotificationMapper mapper;

    public Postgres9EventNotificationDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityEventNotificationMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            // event id may be NULL if system message.
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, ticketId TEXT NOT NULL, eventId TEXT NOT NULL, notificationType TEXT NOT NULL, createdAt TIMESTAMP NOT NULL)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "TicketId" + " ON " + INDEX_TABLE_NAME + "(ticketId, createdAt)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "EventId" + " ON " + INDEX_TABLE_NAME + "(eventId, createdAt)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "TicketIdNotificationType" + " ON " + INDEX_TABLE_NAME + "(ticketId, notificationType, createdAt)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.truncate(pcon);
        indexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, EventTicketNotification t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(t.getId(), CURRENT_VERSION, t.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());
        if (entityDao.exists(pcon, t.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);

        indexDao.put(pcon,
                new String[] { "id", "ticketId", "eventId", "notificationType", "createdAt" },
                new Object[] { t.getId(), t.getTicketId().toString(), t.getEventId(), t.getNotificationType().toString(), t.getCreatedAt() } );
    }

    @Override
    public EventTicketNotification find(PartakeConnection con, String id) throws DAOException {
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
    public DataIterator<EventTicketNotification> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, EventTicketNotification>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public List<EventTicketNotification> findByTicketId(PartakeConnection con, UUID ticketId, int offset, int limit) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE ticketId = ? ORDER BY createdAt DESC OFFSET ? LIMIT ?",
                new Object[] { ticketId.toString(), offset, limit });

        Postgres9IdMapper<EventTicketNotification> idMapper = new Postgres9IdMapper<EventTicketNotification>((Postgres9Connection) con, mapper, entityDao);
        return DAOUtil.convertToList(new Postgres9DataIterator<EventTicketNotification>(idMapper, psars));
    }

    @Override
    public List<EventTicketNotification> findByEventId(PartakeConnection con, String eventId, int offset, int limit) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE eventId = ? ORDER BY createdAt DESC OFFSET ? LIMIT ?",
                new Object[] { eventId, offset, limit });

        Postgres9IdMapper<EventTicketNotification> idMapper = new Postgres9IdMapper<EventTicketNotification>((Postgres9Connection) con, mapper, entityDao);
        return DAOUtil.convertToList(new Postgres9DataIterator<EventTicketNotification>(idMapper, psars));
    }

    @Override
    public EventTicketNotification findLastNotification(PartakeConnection con, UUID ticketId, NotificationType type) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE ticketId = ? AND notificationType = ? ORDER BY createdAt DESC LIMIT 1",
                new Object[] { ticketId.toString(), type.toString() });

        Postgres9IdMapper<EventTicketNotification> idMapper = new Postgres9IdMapper<EventTicketNotification>((Postgres9Connection) con, mapper, entityDao);
        List<EventTicketNotification> list = DAOUtil.convertToList(new Postgres9DataIterator<EventTicketNotification>(idMapper, psars));

        if (list.isEmpty())
            return null;
        else
            return list.get(0);
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
}
