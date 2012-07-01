package in.partake.model.fixture;

import java.util.List;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;

public abstract class TestDataProvider<T> implements TestDataProviderConstants {
    public final T create() {
        return create(0, "", 0);
    }

    public abstract T create(long pkNumber, String pkSalt, int objNumber);
    public abstract List<T> createSamples();

    public abstract void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException;
}
