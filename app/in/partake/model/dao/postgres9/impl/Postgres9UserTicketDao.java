package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTicketAccess;
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
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

class EntityEnrollmentMapper extends Postgres9EntityDataMapper<UserTicket> {
    public UserTicket map(JSONObject obj) {
        return new UserTicket(obj).freeze();
    }
}

public class Postgres9UserTicketDao extends Postgres9Dao implements IUserTicketAccess {
    static final String ENTITY_TABLE_NAME = "UserTicketEntities";
    static final String INDEX_TABLE_NAME = "UserTicketIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityEnrollmentMapper mapper;

    public Postgres9UserTicketDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityEnrollmentMapper();
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, userId TEXT NOT NULL, ticketId TEXT NOT NULL, eventId TEXT NOT NULL, status TEXT NOT NULL, appliedAt TIMESTAMP NOT NULL)");
            indexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + INDEX_TABLE_NAME + "UserIdTicketId" + " ON " + INDEX_TABLE_NAME + "(userId, ticketId)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "TicketId" + " ON " + INDEX_TABLE_NAME + "(ticketId, appliedAt)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "TicketIdStatus" + " ON " + INDEX_TABLE_NAME + "(ticketId, status, appliedAt)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "EventId" + " ON " + INDEX_TABLE_NAME + "(eventId, appliedAt)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "EventIdStatus" + " ON " + INDEX_TABLE_NAME + "(ticketId, status, appliedAt)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "UserId" + " ON " + INDEX_TABLE_NAME + "(userId, appliedAt)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        entityDao.truncate(pcon);
        indexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, UserTicket t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        Postgres9Entity entity = new Postgres9Entity(t.getId(), CURRENT_VERSION, t.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());

        if (entityDao.exists(pcon, t.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon,
                new String[] { "id", "userId", "ticketId", "eventId", "status", "appliedAt" },
                new Object[] { t.getId(), t.getUserId(), t.getTicketId().toString(), t.getEventId(), t.getStatus().toString(), t.getAppliedAt() });
    }

    @Override
    public UserTicket find(PartakeConnection con, String id) throws DAOException {
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
    public DataIterator<UserTicket> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, UserTicket>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public UserTicket findByTicketIdAndUserId(PartakeConnection con, UUID ticketId, String userId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", new String[] { "userId", "ticketId" }, new Object[] { userId, ticketId.toString() });
        if (id == null)
            return null;

        return find(con, id);
    }

    @Override
    public void removeByEventTicketIdAndUserId(PartakeConnection con, UUID eventTicketId, String userId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", new String[] { "userId", "ticketId" }, new Object[] { userId, eventTicketId.toString() });
        if (id == null)
            return;

        remove(con, id);
    }

    @Override
    public List<UserTicket> findByTicketId(PartakeConnection con, UUID eventTicketId, int offset, int limit) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE ticketId = ? ORDER BY appliedAt DESC OFFSET ? LIMIT ?",
                new Object[] { eventTicketId.toString(), offset, limit });

        Postgres9IdMapper<UserTicket> idMapper = new Postgres9IdMapper<UserTicket>((Postgres9Connection) con, mapper, entityDao);
        DataIterator<UserTicket> it = new Postgres9DataIterator<UserTicket>(idMapper, psars);
        return DAOUtil.freeze(DAOUtil.convertToList(it));
    }

    @Override
    public List<UserTicket> findByEventId(PartakeConnection con, String eventId, int offset, int limit) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE eventId = ? ORDER BY appliedAt DESC OFFSET ? LIMIT ?",
                new Object[] { eventId, offset, limit });

        Postgres9IdMapper<UserTicket> idMapper = new Postgres9IdMapper<UserTicket>((Postgres9Connection) con, mapper, entityDao);
        DataIterator<UserTicket> it = new Postgres9DataIterator<UserTicket>(idMapper, psars);
        return DAOUtil.freeze(DAOUtil.convertToList(it));
    }


    @Override
    public List<UserTicket> findByUserId(PartakeConnection con, String userId, int offset, int limit) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE userId = ? ORDER BY appliedAt DESC OFFSET ? LIMIT ?",
                new Object[] { userId, offset, limit });

        Postgres9IdMapper<UserTicket> idMapper = new Postgres9IdMapper<UserTicket>((Postgres9Connection) con, mapper, entityDao);
        DataIterator<UserTicket> it = new Postgres9DataIterator<UserTicket>(idMapper, psars);
        return DAOUtil.freeze(DAOUtil.convertToList(it));
    }

    @Override
    public int countByUserId(PartakeConnection con, String userId, ParticipationStatus status) throws DAOException {
        return indexDao.count((Postgres9Connection) con,
                new String[] { "userId", "status" },
                new String[] { userId, status.toString() });
    }

    @Override
    public int countByUserId(PartakeConnection con, String userId) throws DAOException {
        return indexDao.count((Postgres9Connection) con, "userId", userId);
    }

    @Override
    public int countByTicketId(PartakeConnection con, UUID eventTicketId, ParticipationStatus status) throws DAOException {
        return indexDao.count((Postgres9Connection) con,
                new String[] { "ticketId", "status" },
                new Object[] { eventTicketId.toString(), status.toString() });
    }

    @Override
    public int countByEventId(PartakeConnection con, String eventId, ParticipationStatus status) throws DAOException {
        return indexDao.count((Postgres9Connection) con,
                new String[] { "eventId", "status" },
                new Object[] { eventId.toString(), status.toString() });
    }


    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
}
