package in.partake.view;

import in.partake.base.DateTime;
import in.partake.view.util.Helper;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Assert;

import org.junit.Test;

public final class HelperTest {

    // -----------------------------------------
    // readableDate
    @Test
    public void testReadableDateAtMorning() {
        DateTime date = createDate(2010, 1, 1, 9, 0);
        Assert.assertEquals("2010年1月1日(金) 09:00", Helper.readableDate(date));
    }

    @Test
    public void testReadableDateAtAfternoon() {
        DateTime date = createDate(2010, 1, 1, 15, 0);
        Assert.assertEquals("2010年1月1日(金) 15:00", Helper.readableDate(date));
    }

    @Test
    public void testReadableDateAtMidnight() {
        DateTime lastMin  = createDate(2010, 1, 1, 23, 59);
        Assert.assertEquals("2010年1月1日(金) 23:59", Helper.readableDate(lastMin));

        DateTime midnight = createDate(2010, 1, 1, 24, 0);
        Assert.assertEquals("2010年1月2日(土) 00:00", Helper.readableDate(midnight));
    }

    @Test
    public void testZeroSuppressed() {
        DateTime saturday  = createDate(2010, 01, 01, 11, 22);
        Assert.assertEquals("2010年1月1日(金) 11:22", Helper.readableDate(saturday));
    }

    @Test
    public void testZeropaddinged() {
        DateTime saturday  = createDate(0001, 12, 23, 00, 00);
        Assert.assertEquals("0001年12月23日(金) 00:00", Helper.readableDate(saturday));
    }

    @Test
    public void testReadableDateAllDaysOfWeek() {
        DateTime sunday    = createDate(2010, 12, 26, 00, 00);
        Assert.assertEquals("2010年12月26日(日) 00:00", Helper.readableDate(sunday));

        DateTime monday    = createDate(2010, 12, 27, 00, 00);
        Assert.assertEquals("2010年12月27日(月) 00:00", Helper.readableDate(monday));

        DateTime tuesday   = createDate(2010, 12, 28, 00, 00);
        Assert.assertEquals("2010年12月28日(火) 00:00", Helper.readableDate(tuesday));

        DateTime wednesday = createDate(2010, 12, 29, 00, 00);
        Assert.assertEquals("2010年12月29日(水) 00:00", Helper.readableDate(wednesday));

        DateTime thursday  = createDate(2010, 12, 30, 00, 00);
        Assert.assertEquals("2010年12月30日(木) 00:00", Helper.readableDate(thursday));

        DateTime friday    = createDate(2010, 12, 31, 00, 00);
        Assert.assertEquals("2010年12月31日(金) 00:00", Helper.readableDate(friday));

        DateTime saturday  = createDate(2011, 01, 01, 00, 00);
        Assert.assertEquals("2011年1月1日(土) 00:00", Helper.readableDate(saturday));
    }

    // -----------------------------------------
    // readableDuration
    @Test
    public void testReadableDuration1Day() {
        DateTime beginDate = createDate(2010, 1, 1,  9, 0);
        DateTime endDate   = createDate(2010, 1, 1, 14, 0);
        Assert.assertEquals("2010年1月1日(金) 09:00 - 14:00", Helper.readableDuration(beginDate, endDate));
    }

    @Test
    public void testReadableDuration2Day() {
        DateTime beginDate = createDate(2010, 1, 1, 20, 0);
        DateTime endDate   = createDate(2010, 1, 2, 05, 0);
        Assert.assertEquals("2010年1月1日(金) 20:00 - 2010年1月2日(土) 05:00", Helper.readableDuration(beginDate, endDate));
    }

    @Test
    public void testReadableDuration1Month() {
        DateTime beginDate = createDate(2010, 1, 1, 20, 0);
        DateTime endDate   = createDate(2010, 2, 1, 20, 0);
        Assert.assertEquals("2010年1月1日(金) 20:00 - 2010年2月1日(月) 20:00", Helper.readableDuration(beginDate, endDate));
    }

    @Test
    public void testReadableDuration1Year() {
        DateTime beginDate = createDate(2010, 1, 1, 20, 0);
        DateTime endDate   = createDate(2011, 1, 1, 20, 0);
        Assert.assertEquals("2010年1月1日(金) 20:00 - 2011年1月1日(土) 20:00", Helper.readableDuration(beginDate, endDate));
    }

    private DateTime createDate(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("JST"), Locale.JAPANESE);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return new DateTime(calendar.getTime().getTime());
    }
}
