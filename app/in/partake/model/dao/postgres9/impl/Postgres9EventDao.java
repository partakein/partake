package in.partake.model.dao.postgres9.impl;

import in.partake.base.PartakeRuntimeException;
import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.aux.EventFilterCondition;
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
import in.partake.model.dto.Event;
import in.partake.resource.ServerErrorCode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

class EntityEventMapper extends Postgres9EntityDataMapper<Event> {
    public Event map(JSONObject obj) {
        return new Event(obj).freeze();
    }
}

public class Postgres9EventDao extends Postgres9Dao implements IEventAccess {
    static final String ENTITY_TABLE_NAME = "EventEntities";
    static final String INDEX_TABLE_NAME = "EventIndex";
    static final String EDITOR_INDEX_TABLE_NAME = "EventIndexForEditor";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final Postgres9IndexDao editorIndexDao;
    private final EntityEventMapper mapper;

    public Postgres9EventDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.editorIndexDao = new Postgres9IndexDao(EDITOR_INDEX_TABLE_NAME);
        this.mapper = new EntityEventMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME +
                    "(id TEXT PRIMARY KEY, ownerId TEXT NOT NULL, draft BOOL NOT NULL, isPrivate BOOL NOT NULL, beginDate TIMESTAMP NOT NULL)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "OwnerId"     + " ON " + INDEX_TABLE_NAME + "(ownerId, draft, beginDate)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "Draft"       + " ON " + INDEX_TABLE_NAME + "(draft)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "isPrivate"   + " ON " + INDEX_TABLE_NAME + "(isPrivate)");
        }

        if (!existsTable(pcon, EDITOR_INDEX_TABLE_NAME)) {
            editorIndexDao.createIndexTable(pcon, "CREATE TABLE " + EDITOR_INDEX_TABLE_NAME +
                    "(id TEXT NOT NULL, editorId TEXT NOT NULL, draft BOOL NOT NULL, isPrivate BOOL NOT NULL, beginDate TIMESTAMP NOT NULL)");
            editorIndexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + EDITOR_INDEX_TABLE_NAME + "Unique" + " ON " + EDITOR_INDEX_TABLE_NAME + "(id, editorId)");
            editorIndexDao.createIndex(pcon, "CREATE INDEX " + EDITOR_INDEX_TABLE_NAME + "EditorId"    + " ON " + EDITOR_INDEX_TABLE_NAME + "(editorId, draft, beginDate)");
            editorIndexDao.createIndex(pcon, "CREATE INDEX " + EDITOR_INDEX_TABLE_NAME + "Draft"       + " ON " + EDITOR_INDEX_TABLE_NAME + "(draft)");
            editorIndexDao.createIndex(pcon, "CREATE INDEX " + EDITOR_INDEX_TABLE_NAME + "isPrivate"   + " ON " + EDITOR_INDEX_TABLE_NAME + "(isPrivate)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.truncate(pcon);
        indexDao.truncate(pcon);
        editorIndexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, Event event) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        Postgres9Entity entity = new Postgres9Entity(event.getId(), CURRENT_VERSION, event.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());
        if (entityDao.exists(pcon, event.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);

        indexDao.put(pcon,
                new String[] {"id", "ownerId", "draft", "isPrivate", "beginDate" },
                new Object[] { event.getId(), event.getOwnerId(), event.isDraft(), !StringUtils.isEmpty(event.getPasscode()), event.getBeginDate() });

        editorIndexDao.remove(pcon, "id", event.getId());
        if (event.getEditorIds() != null) {
            for (String editorId : event.getEditorIds()) {
                editorIndexDao.put(pcon,
                        new String[] { "id", "editorId", "draft", "isPrivate", "beginDate" },
                        new Object[] { event.getId(), editorId, event.isDraft(), !StringUtils.isEmpty(event.getPasscode()), event.getBeginDate() });
            }
        }
    }

    @Override
    public Event find(PartakeConnection con, String id) throws DAOException {
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
        editorIndexDao.remove(pcon, "id", id);
    }

    @Override
    public DataIterator<Event> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, Event>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public boolean isRemoved(PartakeConnection con, String eventId) throws DAOException {
        // TODO: should be implemented.
        return false;
    }

    @Override
    public List<Event> findByOwnerId(PartakeConnection con, String userId, EventFilterCondition criteria, int offset, int limit) throws DAOException {
        String draftSql = conditionClauseForCriteria(criteria);

        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE ownerId = ? " + draftSql + " ORDER BY beginDate DESC OFFSET ? LIMIT ?",
                new Object[] { userId, offset, limit });

        Postgres9IdMapper<Event> idMapper = new Postgres9IdMapper<Event>((Postgres9Connection) con, mapper, entityDao);

        try {
            ArrayList<Event> events = new ArrayList<Event>();
            DataIterator<Event> it = new Postgres9DataIterator<Event>(idMapper, psars);
            while (it.hasNext()) {
                Event event = it.next();
                if (event == null)
                    continue;
                events.add(event);
            }

            return events;
        } finally {
            psars.close();
        }
    }

    @Override
    public DataIterator<Event> getIterator(PartakeConnection con, EventFilterCondition condition) throws DAOException {
        String draftSql = conditionClauseForCriteria(condition);
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE 1 = 1 " + draftSql + " ORDER BY beginDate DESC",
                new Object[] {});

        Postgres9IdMapper<Event> idMapper = new Postgres9IdMapper<Event>((Postgres9Connection) con, mapper, entityDao);
        return new Postgres9DataIterator<Event>(idMapper, psars);
    }

    @Override
    public List<Event> findByEditorUserId(PartakeConnection con, String editorUserId, EventFilterCondition criteria, int offset, int limit) throws DAOException {
        String condition = conditionClauseForCriteria(criteria);

        Postgres9StatementAndResultSet psars = editorIndexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + EDITOR_INDEX_TABLE_NAME + " WHERE editorId = ? " + condition + " ORDER BY beginDate DESC OFFSET ? LIMIT ?",
                new Object[] { editorUserId, offset, limit });

        Postgres9IdMapper<Event> idMapper = new Postgres9IdMapper<Event>((Postgres9Connection) con, mapper, entityDao);

        try {
            DataIterator<Event> it = new Postgres9DataIterator<Event>(idMapper, psars);
            return DAOUtil.convertToList(it);
        } finally {
            psars.close();
        }
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }

    @Override
    public int count(PartakeConnection con, EventFilterCondition condition) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        switch (condition) {
        case ALL_EVENTS:
            return entityDao.count(pcon);
        case DRAFT_EVENT_ONLY:
            return indexDao.count(pcon, new String[] { "draft" }, new Object[] { true });
        case PRIVATE_EVENT_ONLY:
            return indexDao.count(pcon, new String[] { "isPrivate" }, new Object[] { true });
        case PUBLIC_EVENT_ONLY:
            return indexDao.count(pcon, new String[] { "isPrivate" }, new Object[] { false });
        case PUBLISHED_EVENT_ONLY:
            return indexDao.count(pcon, new String[] { "draft" }, new Object[] { false });
        case PUBLISHED_PUBLIC_EVENT_ONLY:
            return indexDao.count(pcon, new String[] { "draft", "isPrivate" }, new Object[] { false, false });
        }

        assert false;
        throw new PartakeRuntimeException(ServerErrorCode.LOGIC_ERROR);
    }

    @Override
    public int countEventsByOwnerId(PartakeConnection con, String userId, EventFilterCondition criteria) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        switch (criteria) {
        case ALL_EVENTS:
            return indexDao.count(pcon, "ownerId", userId);
        case DRAFT_EVENT_ONLY:
            return indexDao.count(pcon, new String[] { "ownerId", "draft" }, new Object[] { userId, true });
        case PRIVATE_EVENT_ONLY:
            return indexDao.count(pcon, new String[] { "ownerId", "isPrivate" }, new Object[] { userId, true });
        case PUBLISHED_EVENT_ONLY:
            return indexDao.count(pcon, new String[] { "ownerId", "draft" }, new Object[] { userId, false });
        case PUBLIC_EVENT_ONLY:
            return indexDao.count(pcon, new String[] { "ownerId", "isPrivate" }, new Object[] { userId, false });
        case PUBLISHED_PUBLIC_EVENT_ONLY:
            return indexDao.count(pcon, new String[] { "ownerId", "draft", "isPrivate" }, new Object[] { userId, false, false });
        }

        assert false;
        throw new PartakeRuntimeException(ServerErrorCode.LOGIC_ERROR);
    }

    @Override
    public int countByEditorUserId(PartakeConnection con, String editorId, EventFilterCondition criteria) throws DAOException {
        String condition = conditionClauseForCriteria(criteria);
        Postgres9StatementAndResultSet psars = editorIndexDao.select((Postgres9Connection) con,
                "SELECT count(1) FROM " + EDITOR_INDEX_TABLE_NAME + " WHERE editorId = ? " + condition,
                new Object[] { editorId });

        try {
            ResultSet rs = psars.getResultSet();
            if (rs.next())
                return rs.getInt(1);
            else
                return 0;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            psars.close();
        }
    }

    @Override
    public int countEventsByOwnerIdAndEditorId(PartakeConnection con, String userId, EventFilterCondition criteria) throws DAOException {
        String condition = conditionClauseForCriteria(criteria);
        Postgres9StatementAndResultSet psars = editorIndexDao.select((Postgres9Connection) con,
                "SELECT count(1) FROM (" +
                    "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE ownerId = ? " + condition +
                    " UNION " +
                    "SELECT id FROM " + EDITOR_INDEX_TABLE_NAME + " WHERE editorId = ? " + condition +
                ") as a",
                new Object[] { userId, userId });
        try {
            ResultSet rs = psars.getResultSet();
            if (rs.next())
                return rs.getInt(1);
            else
                return 0;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            psars.close();
        }
    }

    @Override
    public List<Event> findByOwnerIdAndEditorId(PartakeConnection con, String userId, EventFilterCondition criteria) throws DAOException {
        String condition = conditionClauseForCriteria(criteria);
        Postgres9StatementAndResultSet psars = editorIndexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE ownerId = ? " + condition +
                " UNION " +
                "SELECT id FROM " + EDITOR_INDEX_TABLE_NAME + " WHERE editorId = ? " + condition,
                new Object[] { userId, userId });

        Postgres9IdMapper<Event> idMapper = new Postgres9IdMapper<Event>((Postgres9Connection) con, mapper, entityDao);

        try {
            DataIterator<Event> it = new Postgres9DataIterator<Event>(idMapper, psars);
            return DAOUtil.convertToList(it);
        } finally {
            psars.close();
        }
    }

    private String conditionClauseForCriteria(EventFilterCondition criteria) {
        switch (criteria) {
        case ALL_EVENTS:
            return "";
        case DRAFT_EVENT_ONLY:
            return " AND draft = true";
        case PUBLISHED_EVENT_ONLY:
            return " AND draft = false";
        case PRIVATE_EVENT_ONLY:
            return " AND isPrivate = true";
        case PUBLIC_EVENT_ONLY:
            return " AND isPrivate = false";
        case PUBLISHED_PUBLIC_EVENT_ONLY:
            return " AND draft = false AND isPrivate = false";
        case UPCOMING_EVENT_ONLY:
            return " AND beginDate >= CURRENT_TIMESTAMP";
        }

        return "";
    }
}
