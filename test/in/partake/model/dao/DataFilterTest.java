package in.partake.model.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Predicates;

public class DataFilterTest {
    DataIterator<Integer> filtered;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @SuppressWarnings("unchecked")
    @Before
    public void createFiltered() throws DAOException {
        filtered = mock(DataIterator.class);
        doAnswer(new Answer<Boolean>(){
            private int time;
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                ++time;
                return time < 3;
            }
        }).when(filtered).hasNext();
        doAnswer(new Answer<Object>(){
            private int time;
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ++time;
                if (time >= 3) {
                    throw new NoSuchElementException();
                }
                return Integer.valueOf(time);
            }
        }).when(filtered).next();
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
