package in.partake.model.dao.postgres9.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IConfigurationItemAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dto.ConfigurationItem;

public class Postgres9ConfigurationItemDao extends Postgres9Dao implements IConfigurationItemAccess {
    static final String TABLE_NAME = "ConfigurationItems";

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        removeAll(pcon, TABLE_NAME);
    }

    @Override
    public void put(PartakeConnection con, ConfigurationItem t) throws DAOException {
        if (exists(con, t.getPrimaryKey()))
            update(con, t);
        else
            insert(con, t);
    }

    public void insert(PartakeConnection con, ConfigurationItem t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        Connection cn = pcon.getConnection();

        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO " + TABLE_NAME + "(key, value) VALUES(?, ?)";
            ps = cn.prepareStatement(sql);
            ps.setString(1, t.key());
            ps.setString(2, t.value());
            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }
    }

    public void update(PartakeConnection con, ConfigurationItem t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        Connection cn = pcon.getConnection();

        PreparedStatement ps = null;
        try {
            String sql = "UPDATE " + TABLE_NAME + " SET value = ? WHERE key = ?";
            ps = cn.prepareStatement(sql);
            ps.setString(1, t.value());
            ps.setString(2, t.key());
            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }
    }

    @Override
    public ConfigurationItem find(PartakeConnection con, String key) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        Connection cn = pcon.getConnection();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT value FROM " + TABLE_NAME + " WHERE key = ?";
            ps = cn.prepareStatement(sql);
            ps.setString(1, key);
            rs = ps.executeQuery();
            if (rs.next()) {
                String value = rs.getString(1);
                return new ConfigurationItem(key, value);
            } else
                return null;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(rs);
            close(ps);
        }
    }

    @Override
    public boolean exists(PartakeConnection con, String key) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        Connection cn = pcon.getConnection();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT 1 FROM " + TABLE_NAME + " WHERE key = ?";
            ps = cn.prepareStatement(sql);
            ps.setString(1, key);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(rs);
            close(ps);
        }
    }

    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        Connection cn = pcon.getConnection();

        PreparedStatement ps = null;
        try {
            String sql = "DELETE FROM " + TABLE_NAME + " WHERE key = ?";
            ps = cn.prepareStatement(sql);
            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }
    }

    @Override
    public DataIterator<ConfigurationItem> getIterator(PartakeConnection con) throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }
}
