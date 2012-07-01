package in.partake.model.dto;

import in.partake.base.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the comparator which created by {@link Event#getComparatorBeginDateAsc()}.
 * It has to use beginDate and ID.
 *
 * @see Event#getComparatorBeginDateAsc()
 * @author skypencil (@eller86)
 */
public final class EventComparatorBeginDateAscTest {
    private Comparator<Event> comparator;

    @Before
    public void createComparator() {
        comparator = Event.getComparatorBeginDateAsc();
        Assert.assertNotNull(comparator);
    }

    @Test
    public void sortEmptyList() {
        List<Event> list = Collections.emptyList();
        Collections.sort(list, comparator);
    }

    @Test
    public void sortAscSortedValues() {
        List<Event> list = Arrays.asList(new Event[] {
                createEvent("ID", 2010, 12, 1, 11, 0),
                createEvent("ID", 2010, 12, 1, 11, 5)
        });
        Collections.sort(list, comparator);
        Assert.assertFalse(list.get(0).getBeginDate().isAfter(list.get(1).getBeginDate()));
    }

    @Test
    public void sortDescSortedValues() {
        List<Event> list = Arrays.asList(new Event[] {
                createEvent("ID", 2010, 12, 1, 11, 0),
                createEvent("ID", 2010, 12, 1, 10, 55)
        });
        Collections.sort(list, comparator);
        Assert.assertFalse(list.get(0).getBeginDate().isAfter(list.get(1).getBeginDate()));
    }

    @Test
    public void sortNullValues() {
        List<Event> list = Arrays.asList(new Event[] {
                null,
                null
        });
        Collections.sort(list, comparator);
        Assert.assertNull(list.get(0));
        Assert.assertNull(list.get(1));
    }

    @Test
    public void sortEventAndNull() {
        List<Event> list = Arrays.asList(new Event[] {
                createEvent("ID", 2010, 12, 1, 12, 0),
                null
        });
        Collections.sort(list, comparator);
        Assert.assertNull(list.get(0));
        Assert.assertNotNull(list.get(1));
    }

    @Test
    public void sortEqualValues() {
        List<Event> list = Arrays.asList(new Event[] {
                createEvent("ID", 2010, 12, 1, 12, 0),
                createEvent("ID", 2010, 12, 1, 12, 0)
        });
        Collections.sort(list, comparator);
        Assert.assertEquals(list.get(0).getBeginDate(), list.get(1).getBeginDate());
        Assert.assertEquals(list.get(0).getId(), list.get(1).getId());
    }

    @Test
    public void sortEqualDateAscSortedIds() {
        List<Event> list = Arrays.asList(new Event[] {
                createEvent("1", 2010, 12, 1, 12, 0),
                createEvent("2", 2010, 12, 1, 12, 0)
        });
        Collections.sort(list, comparator);
        Assert.assertEquals(list.get(0).getBeginDate(), list.get(1).getBeginDate());
        Assert.assertTrue(list.get(0).getId().compareTo(list.get(1).getId()) < 0);
    }

    @Test
    public void sortEqualDateDescSortedIds() {
        List<Event> list = Arrays.asList(new Event[] {
                createEvent("2", 2010, 12, 1, 12, 0),
                createEvent("1", 2010, 12, 1, 12, 0)
        });
        Collections.sort(list, comparator);
        Assert.assertEquals(list.get(0).getBeginDate(), list.get(1).getBeginDate());
        Assert.assertTrue(list.get(0).getId().compareTo(list.get(1).getId()) < 0);
    }

    // throwing NullPointerException is needed? really?
    @Test(expected = NullPointerException.class)
    public void sortNullId() {
        List<Event> list = Arrays.asList(new Event[] {
                createEvent("ID", 2010, 12, 1, 12, 0),
                createEvent(null, 2010, 12, 1, 12, 0)
        });
        Collections.sort(list, comparator);
    }

    private Event createEvent(String id, int beginYear, int beginMonth, int beginDay, int beginHour, int beginMin) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("JST"), Locale.JAPANESE);
        DateTime createdAt = new DateTime(calendar.getTimeInMillis());

        calendar.clear();
        calendar.set(Calendar.YEAR, beginYear);
        calendar.set(Calendar.MONTH, beginMonth - 1);
        calendar.set(Calendar.DAY_OF_MONTH, beginDay);
        calendar.set(Calendar.HOUR_OF_DAY, beginHour);
        calendar.set(Calendar.MINUTE, beginMin);

        DateTime beginDate = new DateTime(calendar.getTimeInMillis());

        Event event = new Event(id, "title", "summary", "category", beginDate, null,
                "url", "place", "address", "description", "hashTag", "ownerId", null, null,
                "passcode", false, null, new ArrayList<String>(), null, createdAt, null, 0);
        return event;
    }
}
