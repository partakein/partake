package in.partake.model.dao.postgres9;

import in.partake.base.DateTime;
import in.partake.base.PartakeRuntimeException;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

public class Postgres9IndexDao extends Postgres9Dao {
    private String indexTableName;

    public Postgres9IndexDao(String indexTableName) {
        this.indexTableName = indexTableName;
    }

    public void createIndexTable(Postgres9Connection con, String tableDeclaration) throws DAOException {
        executeSQL(con, tableDeclaration);
    }

    public void createIndex(Postgres9Connection con, String indexDeclaration) throws DAOException {
        executeSQL(con, indexDeclaration);
    }

    /** Be careful about using this. Do not use TAINTED columnName. */
    public String find(Postgres9Connection con, String columnForRetrieve, String columnForSearch, String value) throws DAOException {
        try {
            return find(con.getConnection(), columnForRetrieve, columnForSearch, value);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public String find(Postgres9Connection con, String columnForRetrieve, String[] columnsForSearch, Object[] values) throws DAOException {
        try {
            return find(con.getConnection(), columnForRetrieve, columnsForSearch, values);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public int count(Postgres9Connection con, String columnForSearch, String value) throws DAOException {
        try {
            return count(con.getConnection(), columnForSearch, value);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public int count(Postgres9Connection con, String[] columnsForSearch, Object[] values) throws DAOException {
        try {
            return count(con.getConnection(), columnsForSearch, values);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public int count(Postgres9Connection con, String whereClause, Object[] values) throws DAOException {
        try {
            return count(con.getConnection(), whereClause, values);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public Postgres9StatementAndResultSet select(Postgres9Connection con, String sql, Object[] values) throws DAOException {
        try {
            return select(con.getConnection(), sql, values);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /** We treat the first column as primary key. */
    public void put(Postgres9Connection con, String[] columns, Object values[]) throws DAOException {
        try {
            put(con.getConnection(), columns, values);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /** Removes all entries whose <code>column</code> has <code>value</code>. */
    public void remove(Postgres9Connection con, String column, String value) throws DAOException {
        try {
            remove(con.getConnection(), column, value);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public void remove(Postgres9Connection con, String column, UUID value) throws DAOException {
        remove(con, column, value.toString());
    }

    public void truncate(Postgres9Connection con) throws DAOException {
        try {
            truncate(con.getConnection());
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    // ----------------------------------------------------------------------

    private String find(Connection con, String columnForRetrieve, String columnForSearch, String value) throws SQLException {
        String sql = "SELECT " + columnForRetrieve + " FROM " + indexTableName + " WHERE " + columnForSearch + " = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, value);
            rs = ps.executeQuery();

            if (rs.next())
                return rs.getString(1);
            else
                return null;
        } finally {
            close(rs);
            close(ps);
        }
    }

    private String find(Connection con, String columnForRetrieve, String[] columnsForSearch, Object[] values) throws SQLException {
        String[] questions = new String[columnsForSearch.length];
        for (int i = 0; i < columnsForSearch.length; ++i)
            questions[i] = columnsForSearch[i] + " = ?";

        String sql = "SELECT " + columnForRetrieve + " FROM " + indexTableName + " WHERE " + StringUtils.join(questions, " AND ");

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            for (int i = 0; i < values.length; ++i)
                setObject(ps, i + 1, values[i]);
            rs = ps.executeQuery();

            if (rs.next())
                return rs.getString(1);
            else
                return null;
        } finally {
            close(rs);
            close(ps);
        }
    }

    private int count(Connection con, String columnForSearch, String value) throws SQLException {
        String sql = "SELECT count(1) FROM " + indexTableName + " WHERE " + columnForSearch + " = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, value);
            rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt(1);
            else
                return 0;
        } finally {
            close(rs);
            close(ps);
        }
    }

    private int count(Connection con, String[] columnsForSearch, Object[] values) throws SQLException {
        String[] questions = new String[columnsForSearch.length];
        for (int i = 0; i < columnsForSearch.length; ++i)
            questions[i] = columnsForSearch[i] + " = ?";

        String sql = "SELECT count(*) FROM " + indexTableName + " WHERE " + StringUtils.join(questions, " AND ");

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            for (int i = 0; i < values.length; ++i)
                setObject(ps, i + 1, values[i]);
            rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt(1);
            else
                return 0;
        } finally {
            close(rs);
            close(ps);
        }
    }

    private int count(Connection con, String whereClause, Object[] values) throws SQLException {
        String sql = "SELECT count(*) FROM " + indexTableName + " WHERE " + whereClause;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            for (int i = 0; i < values.length; ++i)
                setObject(ps, i + 1, values[i]);
            rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt(1);
            else
                return 0;
        } finally {
            close(rs);
            close(ps);
        }
    }

    private Postgres9StatementAndResultSet select(Connection con, String sql, Object[] values) throws SQLException {
        boolean shouldClose = true;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(sql);
            for (int i = 0; i < values.length; ++i)
                setObject(ps, i + 1, values[i]);

            rs = ps.executeQuery();
            shouldClose = false;
        } finally {
            if (shouldClose) {
                close(rs);
                close(ps);
                return null;
            }
        }

        return new Postgres9StatementAndResultSet(ps, rs);
    }


    private void put(Connection con, String[] columns, Object values[]) throws SQLException {
        if (exists(con, columns[0], (String) values[0]))
            update(con, columns, values);
        else
            insert(con, columns, values);
    }

    private void insert(Connection con, String[] columns, Object values[]) throws SQLException {
        String sqlColumns = StringUtils.join(columns, ",");
        String[] questions = new String[values.length];
        for (int i = 0; i < values.length; ++i)
            questions[i] = "?";
        String sqlQuestions = StringUtils.join(questions, ",");
        String sql = "INSERT INTO " + indexTableName + "(" + sqlColumns + ") VALUES(" + sqlQuestions + ")";

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            for (int i = 0; i < values.length; ++i)
                setObject(ps, i + 1, values[i]);
            ps.execute();
        } finally {
            close(ps);
        }
    }

    private void update(Connection con, String[] columns, Object values[]) throws SQLException {
        String[] questions = new String[columns.length - 1];
        for (int i = 1; i < columns.length; ++i)
            questions[i - 1] = columns[i] + " = ?";

        String sql = "UPDATE " + indexTableName + " SET " + StringUtils.join(questions, ",") + " WHERE " + columns[0] + " = ?";

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            for (int i = 1; i < columns.length; ++i)
                setObject(ps, i, values[i]);

            ps.setString(columns.length, (String) values[0]);
            ps.execute();
        } finally {
            close(ps);
        }
    }

    private void remove(Connection con, String column, String value) throws SQLException {
        String sql = "DELETE FROM " + indexTableName + " WHERE " + column + " = ?";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, value);

            ps.execute();
        } finally {
            close(ps);
        }
    }

    private boolean exists(Connection con, String columnName, String columnValue) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT 1 FROM " + indexTableName + " WHERE " + columnName + " = ?");
            ps.setString(1, columnValue);

            rs = ps.executeQuery();
            return rs.next();
        } finally {
            close(rs);
            close(ps);
        }
    }

    private void setObject(PreparedStatement ps, int nth, Object obj) throws SQLException {
        if (obj == null)
            ps.setNull(nth, Types.NULL);
        else if (obj instanceof String)
            ps.setString(nth, (String) obj);
        else if (obj instanceof Date)
            ps.setTimestamp(nth, new Timestamp(((Date) obj).getTime()));
        else if (obj instanceof DateTime)
            ps.setTimestamp(nth, new Timestamp(((DateTime) obj).getTime()));
        else if (obj instanceof Integer)
            ps.setInt(nth, (Integer) obj);
        else if (obj instanceof Boolean)
            ps.setBoolean(nth, (Boolean) obj);
        else
            throw new PartakeRuntimeException(ServerErrorCode.LOGIC_ERROR);
    }

    private void truncate(Connection con) throws SQLException {
        String sql = "DELETE from " + indexTableName;

        Statement st = null;
        try {
            st = con.createStatement();
            st.execute(sql);
        } finally {
            close(st);
        }
    }
}
