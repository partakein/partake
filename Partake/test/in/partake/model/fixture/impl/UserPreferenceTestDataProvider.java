package in.partake.model.fixture.impl;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserPreferenceAccess;
import in.partake.model.dto.UserPreference;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserPreferenceTestDataProvider extends TestDataProvider<UserPreference> {

    @Override
    public UserPreference create(long pkNumber, String pkSalt, int objNumber) {
        UUID id = new UUID(pkNumber, pkSalt.hashCode());

        boolean profilePublic = (objNumber & 0x1) != 0;
        boolean receivingTwitterMessage = (objNumber & 0x10) != 0;
        boolean tweetingAttendanceAutomatically = (objNumber & 0x100) != 0;
        return new UserPreference(id.toString(), profilePublic, receivingTwitterMessage, tweetingAttendanceAutomatically);
    }

    @Override
    public List<UserPreference> createSamples() {
        List<UserPreference> array = new ArrayList<UserPreference>();
        array.add(new UserPreference("userId", false, false, false));
        array.add(new UserPreference("userId1", false, false, false));
        array.add(new UserPreference("userId", true, false, false));
        array.add(new UserPreference("userId", false, true, false));
        array.add(new UserPreference("userId", false, false, true));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IUserPreferenceAccess dao = daos.getUserPreferenceAccess();
        dao.truncate(con);

        dao.put(con, new UserPreference(DEFAULT_USER_ID, true, true, false));
        dao.put(con, new UserPreference(USER_WITH_PRIVATE_PREF_ID, false, true, false));
    }
}
