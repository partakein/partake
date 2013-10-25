package in.partake.model.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;

import com.google.common.base.Predicate;

public class DataFilter<T> extends DataIterator<T> {
    private final DataIterator<T> unfiltered;
    private final Predicate<? super T> predicate;
    private T next;

    public DataFilter(@Nonnull DataIterator<T> unfiltered, @Nonnull Predicate<? super T> predicate) {
        this.unfiltered = checkNotNull(unfiltered);
        this.predicate = checkNotNull(predicate);
    }

    @Override
    public boolean hasNext() throws DAOException {
        while (unfiltered.hasNext()) {
            next = unfiltered.next();
            if (predicate.apply(next)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public T next() throws DAOException {
        return next;
    }

    @Override
    public void close() {
        unfiltered.close();
    }

    @Override
    public void remove() throws DAOException, UnsupportedOperationException {
        unfiltered.remove();
    }

    @Override
    public void update(T t) throws DAOException, UnsupportedOperationException {
        unfiltered.update(t);
    }

}
