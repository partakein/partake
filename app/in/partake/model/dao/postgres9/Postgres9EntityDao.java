package in.partake.model.dao.postgres9;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.DataMapper;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.UUID;

/**
 *
 * @author shinyak
 *
 */
public class Postgres9EntityDao extends Postgres9Dao {
    private final String tableName;

    public Postgres9EntityDao(String tableName) {
        this.tableName = tableName;
    }

    public void initialize(Postgres9Connection con) throws DAOException {
        makeSureExistEntitiesTable(con);
    }

    private void makeSureExistEntitiesTable(Postgres9Connection con) throws DAOException {
        try {
            if (existsTable(con, tableName))
                return;

            createEntitiesTable(con.getConnection());
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private void createEntitiesTable(Connection con) throws SQLException {
        // NOTE: Postgres9.1 has 'CREATE TABLE IF NOT EXISTS' though postgres9.0 does not have it.
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(
                    "CREATE TABLE " + tableName + "(" +
                    "    id         UUID        PRIMARY KEY," +
                    "    version    INTEGER     NOT NULL," +
                    "    body       BYTEA       NOT NULL," +
                    "    opt        BYTEA," +
                    "    updatedAt  TIMESTAMP   NOT NULL" +
                    ")");
             ps.execute();
        } finally {
            close(ps);
        }
    }

    public String getFreshId(Postgres9Connection con) throws DAOException {
        for (int i = 0; i < 5; ++i) {
            UUID uuid = UUID.randomUUID();
            if (!exists(con, uuid.toString()))
                return uuid.toString();
        }

        return null;
    }


    public void insert(Postgres9Connection pcon, Postgres9Entity entity) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO " + tableName + "(id, version, body, opt, updatedAt) VALUES(?, ?, ?, ?, ?)");
            ps.setObject(1, entity.getId(), Types.OTHER);
            ps.setInt(2, entity.getVersion());
            ps.setBinaryStream(3, new ByteArrayInputStream(entity.getBody()), entity.getBodyLength());
            if (entity.getOpt() != null)
                ps.setBinaryStream(4, new ByteArrayInputStream(entity.getOpt()), entity.getOptLength());
            else
                ps.setNull(4, Types.NULL);
            ps.setTimestamp(5, new Timestamp(entity.getUpdatedAt().getTime()));

            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }
    }

    public void update(Postgres9Connection pcon, Postgres9Entity entity) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("UPDATE " + tableName + " SET version = ?, body = ?, opt = ?, updatedAt = ? WHERE id = ?");
            ps.setInt(1, entity.getVersion());
            ps.setBinaryStream(2, new ByteArrayInputStream(entity.getBody()), entity.getBodyLength());
            if (entity.getOpt() != null)
                ps.setBinaryStream(3, new ByteArrayInputStream(entity.getOpt()), entity.getOptLength());
            else
                ps.setNull(3, Types.NULL);
            ps.setTimestamp(4, new Timestamp(TimeUtil.getCurrentTime()));
            ps.setObject(5, entity.getId(), Types.OTHER);

            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }
    }

    public boolean exists(Postgres9Connection pcon, UUID id) throws DAOException {
        return exists(pcon, id.toString());
    }

    public boolean exists(Postgres9Connection pcon, String id) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT 1 FROM " + tableName + " WHERE id = ?");
            ps.setObject(1, id, Types.OTHER);

            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(rs);
            close(ps);
        }
    }

    public Postgres9Entity find(Postgres9Connection pcon, String id) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT version, body, opt, updatedAt FROM " + tableName + " WHERE id = ?");
            ps.setObject(1, id, Types.OTHER);

            rs = ps.executeQuery();
            if (rs.next()) {
                int version = rs.getInt(1);
                byte[] body = rs.getBytes(2);
                byte[] opt = rs.getBytes(3);
                Timestamp updatedAt = rs.getTimestamp(4);
                return new Postgres9Entity(id, version, body, opt, updatedAt != null ? new DateTime(updatedAt.getTime()) : null);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(rs);
            close(ps);
        }
    }

    public Postgres9Entity find(Postgres9Connection pcon, UUID id) throws DAOException {
        return find(pcon, id.toString());
    }

    /** Removes */
    public void remove(Postgres9Connection pcon, String id) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?");
            ps.setObject(1, id, Types.OTHER);

            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }
    }

    public void remove(Postgres9Connection pcon, UUID id) throws DAOException {
        remove(pcon, id.toString());
    }

    /** Removes all entities. All data might be lost. You should call this very carefully. */
    public void truncate(Postgres9Connection pcon) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("DELETE FROM " + tableName);
            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }
    }

    public DataIterator<Postgres9Entity> getIterator(Postgres9Connection pcon) throws DAOException {
        final String sql = "SELECT id, version, body, opt, updatedAt FROM " + tableName;

        Connection con = pcon.getConnection();

        boolean shouldClose = true;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            shouldClose = false;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            if (shouldClose) {
                close(rs);
                close(ps);
                return null;
            }
        }

        DataMapper<ResultSet, Postgres9Entity> mapper = new DataMapper<ResultSet, Postgres9Entity>() {
            @Override
            public Postgres9Entity map(ResultSet rs) throws DAOException {
                try {
                    String id = rs.getString(1);
                    int version = rs.getInt(2);
                    byte[] body = rs.getBytes(3);
                    byte[] opt = rs.getBytes(4);
                    Timestamp updatedAt = rs.getTimestamp(5);
                    return new Postgres9Entity(id, version, body, opt, updatedAt != null ? new DateTime(updatedAt.getTime()) : null);
                } catch (SQLException e) {
                    throw new DAOException(e);
                }
            }

            @Override
            public ResultSet unmap(Postgres9Entity t) throws DAOException {
                throw new UnsupportedOperationException();
            }
        };

        Postgres9StatementAndResultSet sars = new Postgres9StatementAndResultSet(ps, rs);
        return new Postgres9EntityIterator(mapper, this, pcon, sars);
    }

    public int count(Postgres9Connection pcon) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT count(*) FROM " + tableName);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            else
                return 0;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(rs);
            close(ps);
        }
    }
}
