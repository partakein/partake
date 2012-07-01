package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.DataMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class Postgres9DataIterator<T> extends DataIterator<T> {
    private Postgres9StatementAndResultSet sars;
    private DataMapper<ResultSet, T> mapper;
    private T next;
    private T current;

    public Postgres9DataIterator(DataMapper<ResultSet, T> mapper, Postgres9StatementAndResultSet sars) {
        this.mapper = mapper;
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
    public T next() throws DAOException {
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(T t) throws DAOException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
