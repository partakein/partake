package in.partake.service.impl;

import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeConnectionPool;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.postgres9.Postgres9ConnectionPool;
import in.partake.model.dao.postgres9.Postgres9DAOFactory;
import in.partake.service.IDBService;

public class Postgres9DBService implements IDBService {
    private PartakeConnectionPool pool;
    private PartakeDAOFactory factory;

    public Postgres9DBService() {
        pool = new Postgres9ConnectionPool();
        factory = new Postgres9DAOFactory();
    }

    @Override
    public PartakeConnection getConnection() throws DAOException {
        return pool.getConnection();
    }

    @Override
    public IPartakeDAOs getDAOs() {
        return factory;
    }

    @Override
    public void initialize() throws DAOException, PartakeException {
        new Transaction<Void>() {
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException ,in.partake.base.PartakeException {
                factory.initialize(con);
                return null;
            }
        }.execute();
    }

    @Override
    public PartakeConnectionPool getPool() {
        return pool;
    }
}
