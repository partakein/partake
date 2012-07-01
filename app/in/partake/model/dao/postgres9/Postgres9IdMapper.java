package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataMapper;
import in.partake.model.dto.PartakeModel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Postgres9IdMapper<T extends PartakeModel<T>> implements DataMapper<ResultSet, T> {
    private Postgres9Connection con;
    private Postgres9EntityDataMapper<T> mapper;
    private Postgres9EntityDao entityDao;
    
    public Postgres9IdMapper(Postgres9Connection con, Postgres9EntityDataMapper<T> mapper, Postgres9EntityDao entityDao) {
        this.con = con;
        this.mapper = mapper;
        this.entityDao = entityDao;
    }
    
    @Override
    public T map(ResultSet rs) throws DAOException {
        try {
            String id = rs.getString("id");
            if (id == null)
                return null;
            
            return mapper.map(entityDao.find(con, id));
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public ResultSet unmap(T t) throws DAOException {
        throw new UnsupportedOperationException();
    }
}
