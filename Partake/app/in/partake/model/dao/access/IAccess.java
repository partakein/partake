package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.PartakeModel;

/**
 * 全ての DAO が兼ね備えるべき、put, find, remove を提供する層。
 *
 * @author shinyak
 *
 * @param <T> Data type
 * @param <PK> primary key type. usually String, but it may differ.
 */
public interface IAccess<T extends PartakeModel<T>, PK> {
    /** Will be called once after DAOs are created. If there are multiple DAOs,
     *  the order of call is not determined.
     */
    public abstract void initialize(PartakeConnection con) throws DAOException;

    /**
     * Removes all data. NEVER use unless in unittest.
     */
    public abstract void truncate(PartakeConnection con) throws DAOException;

    /**
     * Persist the data.
     */
    public abstract void put(PartakeConnection con, T t) throws DAOException;

    /**
     * Find data from a primary key.
     */
    public abstract T find(PartakeConnection con, PK key) throws DAOException;

    /**
     * @return true if exists.
     */
    public abstract boolean exists(PartakeConnection con, PK key) throws DAOException;

    /**
     * Remove the data.
     */
    public abstract void remove(PartakeConnection con, PK key) throws DAOException;
    public abstract DataIterator<T> getIterator(PartakeConnection con) throws DAOException;

    public abstract int count(PartakeConnection con) throws DAOException;
}
