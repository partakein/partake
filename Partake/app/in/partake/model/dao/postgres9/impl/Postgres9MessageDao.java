package in.partake.model.dao.postgres9.impl;

import java.util.UUID;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dto.Message;
import net.sf.json.JSONObject;

class EntityMessageMapper extends Postgres9EntityDataMapper<Message> {
    public Message map(JSONObject obj) {
        return new Message(obj).freeze();
    }
}

public class Postgres9MessageDao extends Postgres9Dao implements IMessageAccess {
    static final String ENTITY_TABLE_NAME = "MessageEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final EntityMessageMapper mapper;

    public Postgres9MessageDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.mapper = new EntityMessageMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, Message t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(t.getId(), CURRENT_VERSION, t.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());
        if (entityDao.exists(pcon, t.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
    }

    @Override
    public Message find(PartakeConnection con, UUID id) throws DAOException {
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
    }

    @Override
    public DataIterator<Message> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, Message>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public UUID getFreshId(PartakeConnection con) throws DAOException {
        return UUID.fromString(entityDao.getFreshId((Postgres9Connection) con));
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
}
