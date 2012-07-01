package in.partake.model.dao.mock;

import java.util.HashSet;
import java.util.Set;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeConnectionPool;

public class MockConnectionPool extends PartakeConnectionPool {
    private Set<PartakeConnection> connections;
    private MockConnection preparedConnection;

    public MockConnectionPool() {
        connections = new HashSet<PartakeConnection>();
    }

    @Override
    protected PartakeConnection getConnectionImpl(String name) throws DAOException {
        final PartakeConnection con;
        if (this.preparedConnection != null) {
            con = preparedConnection;
            preparedConnection = null;
        } else {
            con = new MockConnection(this);
        }
        connections.add(con);
        return con;
    }
    
    @Override
    protected void releaseConnectionImpl(PartakeConnection connection) {
        connections.remove(connection);
    }
    
    public boolean areAllConnectionsReleased() {
        return connections.isEmpty();
    }

    /**
     * pre-set MockConnection for injecting.
     * @param MockConnection to prepare
     */
    public void prepareConnection(MockConnection prepared) {
        if (this.preparedConnection != null) {
            throw new IllegalStateException();
        }
        this.preparedConnection = prepared;
    }

    @Override
    public void willDestroy() {
    }
}
