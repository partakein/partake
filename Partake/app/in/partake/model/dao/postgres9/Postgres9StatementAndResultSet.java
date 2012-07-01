package in.partake.model.dao.postgres9;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Postgres9StatementAndResultSet {
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    
    public Postgres9StatementAndResultSet(PreparedStatement ps, ResultSet rs) {
        this.preparedStatement = ps;
        this.resultSet = rs;
    }
    
    public ResultSet getResultSet() {
        return resultSet;
    }
    
    public void close() {
        try {
            resultSet.close();
        } catch (SQLException e) {
            // squash!
        }

        try {
            preparedStatement.close();
        } catch (SQLException e) {
            // squash!
        }
    }
}

