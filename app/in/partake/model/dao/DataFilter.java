package in.partake.model.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import com.google.common.base.Predicate;

public class DataFilter<T> extends DataIterator<T> {
    private final DataIterator<T> unfiltered;
    private final Predicate<? super T> predicate;
    private T next;
    private boolean searchedNext;
    private boolean foundNext;

    public DataFilter(@Nonnull DataIterator<T> unfiltered, @Nonnull Predicate<? super T> predicate) {
        this.unfiltered = checkNotNull(unfiltered);
        this.predicate = checkNotNull(predicate);
    }

    @Override
    public boolean hasNext() throws DAOException {
        if (searchedNext) {
            return next != null;
        }

        searchedNext = true;
        while (unfiltered.hasNext()) {
            next = unfiltered.next();
            if (predicate.apply(next)) {
                foundNext = true;
                return true;
            }
        }
        next = null;
        foundNext = true;
        return false;
    }

    @Override
    public T next() throws DAOException {
        if ((searchedNext && !foundNext) || (!searchedNext && !hasNext())){
            throw new NoSuchElementException();
        }
        searchedNext = foundNext = false;
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
