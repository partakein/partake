package in.partake.model.dao.postgres9.impl;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IMessageEnvelopeAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dto.MessageEnvelope;

class EntityMessageEnvelopeMapper extends Postgres9EntityDataMapper<MessageEnvelope> {
    public MessageEnvelope map(ObjectNode obj) {
        return new MessageEnvelope(obj).freeze();
    }

    @Override
    public Postgres9Entity unmap(MessageEnvelope t) throws DAOException {
        return new Postgres9Entity(t.getId(), Postgres9MessageEnvelopeDao.CURRENT_VERSION, t.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());
    }
}

public class Postgres9MessageEnvelopeDao extends Postgres9Dao implements IMessageEnvelopeAccess {
    static final String ENTITY_TABLE_NAME = "MessageEnvelopeEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final EntityMessageEnvelopeMapper mapper;

    public Postgres9MessageEnvelopeDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.mapper = new EntityMessageEnvelopeMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        entityDao.initialize((Postgres9Connection) con);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, MessageEnvelope envelope) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Entity should have getId() instead of getEnvelopeId().
        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(envelope.getId(), CURRENT_VERSION, envelope.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());
        if (entityDao.exists(pcon, envelope.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
    }

    @Override
    public MessageEnvelope find(PartakeConnection con, String id) throws DAOException {
        Postgres9Entity entity = entityDao.find((Postgres9Connection) con, id);
        if (entity == null)
            return null;


        ObjectNode json;
        try {
            json = new ObjectMapper().readValue(new String(entity.getBody(), UTF8), ObjectNode.class);
        } catch (JsonParseException e) {
            throw new IllegalArgumentException(e);
        } catch (JsonMappingException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new DAOException(e);
        }
        return new MessageEnvelope(json).freeze();
    }

    @Override
    public boolean exists(PartakeConnection con, String id) throws DAOException {
        return entityDao.exists((Postgres9Connection) con, id);
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
    }

    @Override
    public DataIterator<MessageEnvelope> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, MessageEnvelope>(mapper, entityDao.getIterator((Postgres9Connection) con));
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
