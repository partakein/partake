package in.partake.model.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Predicates;

public class DataFilterTest {
    DataIterator<Integer> filtered;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @SuppressWarnings("unchecked")
    @Before
    public void createFiltered() throws DAOException {
        filtered = mock(DataIterator.class);
        when(filtered.hasNext()).thenReturn(true, true, false);
        when(filtered.next()).thenReturn(Integer.valueOf(1), Integer.valueOf(2)).thenThrow(new NoSuchElementException());
    }

    @Test
    public void testNormalCase() throws DAOException {
        DataFilter<Integer> filter = new DataFilter<Integer>(filtered, Predicates.alwaysTrue());
        int sum = 0;
        while (filter.hasNext()) {
            sum += filter.next();
        }

        assertThat(sum, is(1 + 2));
    }

    @Test
    public void testFilterAll() throws DAOException {
        DataFilter<Integer> filter = new DataFilter<Integer>(filtered, Predicates.alwaysFalse());

        assertThat(filter.hasNext(), is(false));
    }

    @Test
    public void testNoNeedToCallHasNext() throws DAOException {
        DataFilter<Integer> filter = new DataFilter<Integer>(filtered, Predicates.alwaysTrue());

        assertThat(filter.next(), is(Integer.valueOf(1)));
        assertThat(filter.next(), is(Integer.valueOf(2)));
        exception.expect(NoSuchElementException.class);
        filter.next();
    }
}
