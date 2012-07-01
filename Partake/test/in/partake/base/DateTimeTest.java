package in.partake.base;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

public class DateTimeTest {
    private static final Calendar[] SUPPORTED_CALENDAR = {
        new GregorianCalendar(TimeZone.getTimeZone("JST"), Locale.JAPANESE)
    };

    @Test
    public void testEquals() {
        assertThat(new DateTime(0L).equals(null), is(false));
        assertThat(new DateTime(0L).equals(new String("0")), is(false));

        assertThat(new DateTime(0L).equals(new DateTime(0L)), is(true));
        assertThat(new DateTime(0L).equals(new DateTime(1L)), is(false));
    }

    @Test
    public void testIsBefore() {
        assertThat(new DateTime(500L).isBefore(new DateTime(499L)), is(false));
        assertThat(new DateTime(500L).isBefore(new DateTime(500L)), is(false));
        assertThat(new DateTime(500L).isBefore(new DateTime(501L)), is(true));

        assertThat(new DateTime(Long.MIN_VALUE).isBefore(new DateTime(0)), is(true));
        assertThat(new DateTime(0).isBefore(new DateTime(Long.MAX_VALUE)), is(true));
        assertThat(new DateTime(Long.MIN_VALUE).isBefore(new DateTime(Long.MAX_VALUE)), is(true));
    }

    @Test
    public void testIsAfter() {
        assertThat(new DateTime(500L).isAfter(new DateTime(499L)), is(true));
        assertThat(new DateTime(500L).isAfter(new DateTime(500L)), is(false));
        assertThat(new DateTime(500L).isAfter(new DateTime(501L)), is(false));

        assertThat(new DateTime(Long.MIN_VALUE).isAfter(new DateTime(0)), is(false));
        assertThat(new DateTime(0).isAfter(new DateTime(Long.MAX_VALUE)), is(false));
        assertThat(new DateTime(Long.MIN_VALUE).isAfter(new DateTime(Long.MAX_VALUE)), is(false));
    }

    @Test
    public void testOneDayBefore() {
        DateTime date = TimeUtil.getCurrentDateTime();
        DateTime before = date.nDayBefore(1);

        assertThat(before.getTime(), is(date.getTime() - 3600 * 24 * 1000));
    }

    @Test
    public void testHalfDayBefore() {
        DateTime date = TimeUtil.getCurrentDateTime();
        DateTime before = date.nHourBefore(12);

        assertThat(before.getTime(), is(date.getTime() - 3600 * 12 * 1000));
    }

    @Test
    public void testOneDayAfter() {
        DateTime date = TimeUtil.getCurrentDateTime();
        DateTime after = date.nDayAfter(1);

        assertThat(after.getTime(), is(date.getTime() + 3600 * 24 * 1000));
    }

    @Test
    public void oneDayBeforeTest() {
        for (Calendar calendar : SUPPORTED_CALENDAR) {
            calendar.clear();
            calendar.set(Calendar.YEAR, 2010);
            calendar.set(Calendar.MONTH, 0);

            for (int day = 2; day <= 31; ++day) {
                calendar.set(Calendar.DAY_OF_MONTH, day);
                DateTime now = new DateTime(calendar.getTime());
                calendar.set(Calendar.DAY_OF_MONTH, day - 1);
                DateTime yesterday = new DateTime(calendar.getTime());

                Assert.assertEquals(yesterday, now.nDayBefore(1));
            }
        }
    }

    @Test
    public void oneDayBeforeTestAt1stDay() {
        for (Calendar calendar : SUPPORTED_CALENDAR) {
            calendar.clear();
            calendar.set(Calendar.YEAR, 2010);
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            DateTime now = new DateTime(calendar.getTime());

            calendar.set(Calendar.YEAR, 2009);
            calendar.set(Calendar.MONTH, 11);
            calendar.set(Calendar.DAY_OF_MONTH, 31);
            DateTime yesterday = new DateTime(calendar.getTime());

            Assert.assertEquals(yesterday, now.nDayBefore(1));
        }
    }

    @Test
    public void oneDayBeforeTestAtNotLeapYear() {
        for (Calendar calendar : SUPPORTED_CALENDAR) {
            calendar.clear();
            calendar.set(Calendar.YEAR, 2011);  // is NOT leap year
            calendar.set(Calendar.MONTH, 2);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            DateTime now = new DateTime(calendar.getTime());

            calendar.set(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 28);
            DateTime yesterday = new DateTime(calendar.getTime());

            Assert.assertEquals(yesterday, now.nDayBefore(1));
        }
    }

    @Test
    public void oneDayBeforeTestAtLeapYear() {
        for (Calendar calendar : SUPPORTED_CALENDAR) {
            calendar.clear();
            calendar.set(Calendar.YEAR, 2012);  // is leap year
            calendar.set(Calendar.MONTH, 2);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            DateTime now = new DateTime(calendar.getTime());

            calendar.set(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 29);
            DateTime yesterday = new DateTime(calendar.getTime());

            Assert.assertEquals(yesterday, now.nDayBefore(1));
        }
    }

    @Test
    public void halfDayBeforeTest() {
        for (Calendar calendar : SUPPORTED_CALENDAR) {
            calendar.clear();
            calendar.set(Calendar.YEAR, 2010);
            calendar.set(Calendar.MONTH, 0);

            for (int day = 2; day <= 31; ++day) {
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                DateTime now = new DateTime(calendar.getTimeInMillis());

                calendar.set(Calendar.DAY_OF_MONTH, day - 1);
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                DateTime yesterday = new DateTime(calendar.getTimeInMillis());

                Assert.assertEquals(yesterday, now.nHourBefore(12));
            }
        }
    }

    @Test
    public void halfDayBeforeTestAt1stDay() {
        for (Calendar calendar : SUPPORTED_CALENDAR) {
            calendar.clear();
            calendar.set(Calendar.YEAR, 2010);
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            DateTime now = new DateTime(calendar.getTime());

            calendar.set(Calendar.YEAR, 2009);
            calendar.set(Calendar.MONTH, 11);
            calendar.set(Calendar.DAY_OF_MONTH, 31);
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            DateTime yesterday = new DateTime(calendar.getTime());

            Assert.assertEquals(yesterday, now.nHourBefore(12));
        }
    }

    @Test
    public void halfDayBeforeTestAtNotLeapYear() {
        for (Calendar calendar : SUPPORTED_CALENDAR) {
            calendar.clear();
            calendar.set(Calendar.YEAR, 2011);  // is NOT leap year
            calendar.set(Calendar.MONTH, 2);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            DateTime now = new DateTime(calendar.getTime());

            calendar.set(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 28);
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            DateTime yesterday = new DateTime(calendar.getTime());

            Assert.assertEquals(yesterday, now.nHourBefore(12));
        }
    }

    @Test
    public void halfDayBeforeTestAtLeapYear() {
        for (Calendar calendar : SUPPORTED_CALENDAR) {
            calendar.clear();
            calendar.set(Calendar.YEAR, 2012);  // is leap year
            calendar.set(Calendar.MONTH, 2);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            DateTime now = new DateTime(calendar.getTime());

            calendar.set(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 29);
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            DateTime yesterday = new DateTime(calendar.getTime());

            Assert.assertEquals(yesterday, now.nHourBefore(12));
        }
    }

    @Test
    public void testnSecBefore() {
        long msec = 100000000L;
        DateTime dt = new DateTime(msec);

        assertThat(dt.nSecBefore(1).getTime(), is(msec - 1000));
    }

    @Test
    public void testnSecAfter() {
        long msec = 100000000L;
        DateTime dt = new DateTime(msec);

        assertThat(dt.nSecAfter(1).getTime(), is(msec + 1000));
    }

    public void testToHumanReadableFormat() {
        for (Calendar calendar : SUPPORTED_CALENDAR) {
            calendar.clear();
            calendar.set(Calendar.YEAR, 2012);
            calendar.set(Calendar.MONTH, 2);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 10);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            DateTime dt = new DateTime(calendar.getTime());

            assertThat(dt.toHumanReadableFormat(), is("2012-02-01 08:10"));
        }
    }
}
