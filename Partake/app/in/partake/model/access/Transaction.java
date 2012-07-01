package in.partake.model.access;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.service.IDBService;

public abstract class Transaction<T> {
    public final T execute() throws DAOException, PartakeException {
        IDBService dbService = PartakeApp.getDBService();

        PartakeConnection con = dbService.getConnection();
        try {
            con.beginTransaction();
            T result = doExecute(con, dbService.getDAOs());

            if (con.isInTransaction()) {
                con.commit();
            }

            return result;
        } finally {
        	try {
	        	if (con.isInTransaction())
	        		con.rollback();
        	} finally {
        		con.invalidate();
        	}
        }
    }

    protected abstract T doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException;
}
