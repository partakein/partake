package in.partake.model.dao;


import java.util.NoSuchElementException;

public class MapperDataIterator<S, T> extends DataIterator<T> {
    private DataMapper<S, T> mapper;
    private DataIterator<S> iterator;

    public MapperDataIterator(DataMapper<S, T> mapper, DataIterator<S> iterator) {
        this.mapper = mapper;
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() throws DAOException {
        return iterator.hasNext();
    }

    @Override
    public T next() throws DAOException {
        if (hasNext())
            return mapper.map(iterator.next());

        throw new NoSuchElementException();
    }

    @Override
    public void close() {
        iterator.close();
    }

    @Override
    public void remove() throws DAOException, UnsupportedOperationException {
        iterator.remove();
    }

    @Override
    public void update(T t) throws DAOException, UnsupportedOperationException {
        iterator.update(mapper.unmap(t));
    }
}
