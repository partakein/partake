package in.partake.model.dao.mock;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;

public class MockConnection extends PartakeConnection {
    private boolean isInTransaction;
    
    public MockConnection(MockConnectionPool pool) {
        super("<mock connection>", pool, TimeUtil.getCurrentTime());
    }

    @Override
    public void beginTransaction() throws DAOException {
        isInTransaction = true;
    }

    @Override
    public void commit() throws DAOException {
        isInTransaction = false;
    }

    @Override
    public void rollback() throws DAOException {
        isInTransaction = false;
    }

    @Override
    public boolean isInTransaction() throws DAOException {
        return isInTransaction;
    }
}
