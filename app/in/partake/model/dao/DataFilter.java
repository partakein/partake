package in.partake.model.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

public class DataFilter<T> extends DataIterator<T> {
    private final DataIterator<T> unfiltered;
    private final Predicate<? super T> predicate;
    private Optional<T> next;
    private boolean searchedNext;

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
            T nextValue = unfiltered.next();
            if (predicate.apply(nextValue)) {
                next = Optional.of(nextValue);
                return true;
            }
        }
        next = null;
        return false;
    }

    @Override
    public T next() throws DAOException {
        if ((searchedNext && next == null) || (!searchedNext && !hasNext())){
            throw new NoSuchElementException();
        }
        searchedNext = false;
        return next.get();
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
