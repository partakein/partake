package in.partake.base;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TimeUtilTest {
    @Before
    public void setUp() {
        TimeUtil.resetCurrentDate();
    }

    @Test
    public void testCurrentDate1() {
        Date now = new Date();
        assertThat(TimeUtil.getCurrentTime(), is(greaterThanOrEqualTo(now.getTime())));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(greaterThanOrEqualTo(now.getTime())));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(greaterThanOrEqualTo(now.getTime())));

        TimeUtil.setCurrentDateTime(new DateTime(0L));
        assertThat(TimeUtil.getCurrentTime(), is(0L));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(0L));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(0L));

        TimeUtil.setCurrentTime(0L);
        assertThat(TimeUtil.getCurrentTime(), is(0L));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(0L));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(0L));

        TimeUtil.setCurrentDateTime(new DateTime(0L));
        assertThat(TimeUtil.getCurrentTime(), is(0L));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(0L));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(0L));

        TimeUtil.resetCurrentDate();
        assertThat(TimeUtil.getCurrentTime(), is(not(0L)));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(not(0L)));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(not(0L)));
    }

    @Test
    public void testCurrentDate2() {
        long d1 = TimeUtil.getCurrentTime();
        long d2 = new Date().getTime();
        long d3 = TimeUtil.getCurrentTime();
        long d4 = new Date().getTime();

        assertThat(d1, lessThanOrEqualTo(d2));
        assertThat(d2, lessThanOrEqualTo(d3));
        assertThat(d3, lessThanOrEqualTo(d4));
    }

    @Test
    public void testToCreate() {
        DateTime dt = TimeUtil.create(1970, 1, 1, 9, 0, 0);
        assertThat(dt.getTime(), is(0L));
    }

    @Test
    public void dateConverterTest() {
        DateTime date1 = TimeUtil.getCurrentDateTime();
        DateTime date2 = TimeUtil.dateTimeFromTimeString(TimeUtil.getTimeString(date1));
        Assert.assertEquals(date1, date2);
    }

    @Test
    public void dateConverterCornerTest1() {
        DateTime date1 = new DateTime(Long.MAX_VALUE);
        DateTime date2 = TimeUtil.dateTimeFromTimeString(TimeUtil.getTimeString(date1));
        Assert.assertEquals(date1, date2);
    }

    @Test
    public void dateConverterCornerTest2() {
        DateTime date1 = new DateTime(0);
        DateTime date2 = TimeUtil.dateTimeFromTimeString(TimeUtil.getTimeString(date1));
        Assert.assertEquals(date1, date2);
    }
}
