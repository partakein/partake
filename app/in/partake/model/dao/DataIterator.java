package in.partake.model.dao;

/**
 * Iterator to iterate objects gotten from DAO.
 * @author shinyak
 *
 * @param <T>
 */
public abstract class DataIterator<T> {

    /**
     *     
     * @return true if there are more elements.
     * @throws DAOException
     */
    public abstract boolean hasNext() throws DAOException;

    /**
     * 
     * @return next element.
     * @throws DAOException
     */
    public abstract T next() throws DAOException;

    /**
     * Closes data iterator.
     */
    public abstract void close();

    /**
     * removes current element. This may throw UnsupportedOperationException.
     * @throws DAOException
     * @throws UnsupportedOperationException
     */
    public abstract void remove() throws DAOException, UnsupportedOperationException;

    /**
     * update the current element with <code>t</code>. This may throw UnsupportedOperationException.
     * @param t
     * @throws DAOException
     * @throws UnsupportedOperationException
     */
    public abstract void update(T t) throws DAOException, UnsupportedOperationException;
}
