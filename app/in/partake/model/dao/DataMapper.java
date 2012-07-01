package in.partake.model.dao;

public interface DataMapper<S, T> {
    public T map(S s) throws DAOException;
    public S unmap(T t) throws DAOException;
}
