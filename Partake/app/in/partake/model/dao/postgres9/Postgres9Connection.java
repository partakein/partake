package in.partake.model.dao.postgres9;

import java.sql.Connection;
import java.sql.SQLException;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;

public class Postgres9Connection extends PartakeConnection {
    private Connection connection;
    private boolean inTransation;
    
    public Postgres9Connection(String name, Connection con, Postgres9ConnectionPool pool, long acquiredTime) {
        super(name, pool, acquiredTime);
        this.connection = con;
        this.inTransation = false;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public void close() throws DAOException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void beginTransaction() throws DAOException {
        try {
            connection.setAutoCommit(false);
            inTransation = true;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void commit() throws DAOException {
        if (!inTransation)
            throw new IllegalStateException("Transaction is not acquired.");
        
        try {
            connection.commit();
            inTransation = false;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void rollback() throws DAOException {
        if (!inTransation)
            throw new IllegalStateException("Transaction is not acquired.");

        try {
            connection.rollback();
            inTransation = false;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean isInTransaction() throws DAOException {
        return inTransation;
    }
}
