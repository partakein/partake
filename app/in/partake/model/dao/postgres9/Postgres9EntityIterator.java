package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.DataMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class Postgres9EntityIterator extends DataIterator<Postgres9Entity> {
    private Postgres9StatementAndResultSet sars;
    private Postgres9EntityDao entityDao;
    private Postgres9Connection pcon;
    private DataMapper<ResultSet, Postgres9Entity> mapper;
    private Postgres9Entity next;
    private Postgres9Entity current;

    public Postgres9EntityIterator(DataMapper<ResultSet, Postgres9Entity> mapper, Postgres9EntityDao entityDao, Postgres9Connection pcon, Postgres9StatementAndResultSet sars) {
        this.mapper = mapper;
        this.entityDao = entityDao;
        this.pcon = pcon;
        this.sars = sars;
    }

    @Override
    public boolean hasNext() throws DAOException {
        if (next != null)
            return true;

        try {
            if (!sars.getResultSet().next())
                return false;

            next = mapper.map(sars.getResultSet());
            return true;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Postgres9Entity next() throws DAOException {
        if (hasNext()) {
            current = next;
            next = null;
            return current;
        }

        assert next == null;
        current = null;
        throw new NoSuchElementException();
    }

    @Override
    public void close() {
        sars.close();
    }

    @Override
    public void remove() throws DAOException, UnsupportedOperationException {
        entityDao.remove(pcon, current.getId());
    }

    @Override
    public void update(Postgres9Entity entity) throws DAOException, UnsupportedOperationException {
        entityDao.update(pcon, entity);
    }
}
