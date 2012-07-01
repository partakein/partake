package in.partake.model.fixture.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserCalendarLinkageAccess;
import in.partake.model.dto.UserCalendarLink;
import in.partake.model.fixture.TestDataProvider;

/**
 *
 * @author shinyak
 *
 */
public class UserCalendarLinkTestDataProvider extends TestDataProvider<UserCalendarLink> {
    @Override
    public UserCalendarLink create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new UserCalendarLink(uuid.toString(), "" + objNumber);
    }

    @Override
    public List<UserCalendarLink> createSamples() {
        List<UserCalendarLink> list = new ArrayList<UserCalendarLink>();

        list.add(new UserCalendarLink("id", "userId"));
        list.add(new UserCalendarLink("id1", "userId"));
        list.add(new UserCalendarLink("id", "userId2"));

        return list;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IUserCalendarLinkageAccess dao = daos.getCalendarAccess();
        dao.truncate(con);

        dao.put(con, new UserCalendarLink(DEFAULT_CALENDAR_ID, DEFAULT_USER_ID));
        dao.put(con, new UserCalendarLink(ENROLLED_USER_CALENDAR_ID, EVENT_ENROLLED_USER_ID));
    }
}
