package in.partake.service;

import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeConnectionPool;

public interface IDBService {
    public void initialize() throws DAOException, PartakeException;
    public PartakeConnectionPool getPool();
    public PartakeConnection getConnection() throws DAOException;
    public IPartakeDAOs getDAOs();
}
