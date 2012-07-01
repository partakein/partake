package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserReceivedMessageAccess;
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
import in.partake.model.dto.UserReceivedMessage;

import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

class EntityUserReceivedMessageMapper extends Postgres9EntityDataMapper<UserReceivedMessage> {
    public UserReceivedMessage map(JSONObject obj) {
        return new UserReceivedMessage(obj).freeze();
    }
}

public class Postgres9UserReceivedMessageDao extends Postgres9Dao implements IUserReceivedMessageAccess {
    static final String ENTITY_TABLE_NAME = "UserReceivedMessageEntities";
    static final String INDEX_TABLE_NAME = "UserReceivedMessageIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityUserReceivedMessageMapper mapper;

    public Postgres9UserReceivedMessageDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityUserReceivedMessageMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            // event id may be NULL if system message.
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, senderId TEXT NOT NULL, receiverId TEXT NOT NULL, opened BOOLEAN NOT NULL, createdAt TIMESTAMP NOT NULL)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "ReceiverId" + " ON " + INDEX_TABLE_NAME + "(receiverId, createdAt)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "Opened" + " ON " + INDEX_TABLE_NAME + "(receiverId, opened)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "SenderId" + " ON " + INDEX_TABLE_NAME + "(senderId, createdAt)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.truncate(pcon);
        indexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, UserReceivedMessage t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(t.getId(), CURRENT_VERSION, t.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());
        if (entityDao.exists(pcon, t.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon, new String[] { "id", "senderId", "receiverId", "opened", "createdAt" }, new Object[] { t.getId().toString(), t.getSenderId(), t.getReceiverId(), t.isOpened(), t.getCreatedAt() } );
    }

    @Override
    public UserReceivedMessage find(PartakeConnection con, UUID id) throws DAOException {
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
    public DataIterator<UserReceivedMessage> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, UserReceivedMessage>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public UUID getFreshId(PartakeConnection con) throws DAOException {
        return UUID.fromString(entityDao.getFreshId((Postgres9Connection) con));
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }

    @Override
    public int countByReceiverId(PartakeConnection con, String receiverId) throws DAOException {
        return indexDao.count((Postgres9Connection) con, "receiverId", receiverId);
    }

    @Override
    public List<UserReceivedMessage> findByReceiverId(PartakeConnection con, String receiverId, int offset, int limit) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE receiverId = ? ORDER BY createdAt DESC OFFSET ? LIMIT ?",
                new Object[] { receiverId, offset, limit });

        try {
            Postgres9IdMapper<UserReceivedMessage> idMapper = new Postgres9IdMapper<UserReceivedMessage>((Postgres9Connection) con, mapper, entityDao);
            DataIterator<UserReceivedMessage> it = new Postgres9DataIterator<UserReceivedMessage>(idMapper, psars);
            return DAOUtil.convertToList(it);
        } finally {
            psars.close();
        }
    }
}
