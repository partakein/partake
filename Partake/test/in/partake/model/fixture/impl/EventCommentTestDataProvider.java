package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventCommentAccess;
import in.partake.model.dto.EventComment;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventCommentTestDataProvider extends TestDataProvider<EventComment> {

    @Override
    public EventComment create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("comment" + pkSalt).hashCode());
        String eventId = new UUID(pkNumber, ("comment" + pkSalt).hashCode()).toString();
        String userId  = new UUID(objNumber, "user".hashCode()).toString();
        String comment = "";
        boolean isHTML = false;
        DateTime createdAt = new DateTime(objNumber);
        return new EventComment(uuid.toString(), eventId, userId, comment, isHTML, createdAt);
    }

    @Override
    public List<EventComment> createSamples() {
        List<EventComment> list = new ArrayList<EventComment>();

        DateTime now = new DateTime(0);
        list.add(new EventComment("id", "eventId", "userId", "comment", false, now));
        list.add(new EventComment("id1", "eventId1", "userId", "comment", false, now));
        list.add(new EventComment("id", "eventId1", "userId", "comment", false, now));
        list.add(new EventComment("id", "eventId", "userId1", "comment", false, now));
        list.add(new EventComment("id", "eventId", "userId", "comment1", false, now));
        list.add(new EventComment("id", "eventId", "userId", "comment", true, now));
        list.add(new EventComment("id", "eventId", "userId", "comment", false, new DateTime(now.getTime() + 1)));

        return list;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IEventCommentAccess dao = daos.getCommentAccess();
        dao.truncate(con);

        DateTime now = TimeUtil.getCurrentDateTime();

        dao.put(con, new EventComment(OWNER_COMMENT_ID, DEFAULT_EVENT_ID, EVENT_OWNER_ID, "comment", false, now));
        dao.put(con, new EventComment(EDITOR_COMMENT_ID, DEFAULT_EVENT_ID, EVENT_EDITOR_ID, "comment", false, now));
        dao.put(con, new EventComment(COMMENTOR_COMMENT_ID, DEFAULT_EVENT_ID, EVENT_COMMENTOR_ID, "comment", false, now));
        dao.put(con, new EventComment(UNRELATED_USER_COMMENT_ID, DEFAULT_EVENT_ID, EVENT_UNRELATED_USER_ID, "comment", false, now));
    }

}
