package in.partake.model.dao.postgres9;

import in.partake.base.PartakeRuntimeException;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeConnectionPool;
import in.partake.resource.ServerErrorCode;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import play.db.DB;

public class Postgres9ConnectionPool extends PartakeConnectionPool {
    private static final Logger logger = Logger.getLogger(Postgres9ConnectionPool.class);
    private final DataSource dataSource;

    public Postgres9ConnectionPool() {
        super();
        dataSource = DB.getDataSource();
    }

    @Override
    protected PartakeConnection getConnectionImpl(String name) throws DAOException {
        if (dataSource == null)
            throw new PartakeRuntimeException(ServerErrorCode.DB_CONNECTION_POOL_INITIALIZATION_FAILURE);

        long now = new Date().getTime();
        try {
            Connection con = dataSource.getConnection();
            con.setAutoCommit(false);
            return new Postgres9Connection(name, con, this, now);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    protected void releaseConnectionImpl(PartakeConnection connection) {
        assert (connection instanceof Postgres9Connection);
        if (!(connection instanceof Postgres9Connection)) {
            logger.warn("connection is not Postgres9Connection.");
            return;
        }

        Postgres9Connection con = (Postgres9Connection) connection;
        try {
            con.close();
        } catch (DAOException e) {
            logger.warn("Connection cannot be released.", e);
        }
    }

    @Override
    public void willDestroy() {
        // DO NOTHING.
    }
}
