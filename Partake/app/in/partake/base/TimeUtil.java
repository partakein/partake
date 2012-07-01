package in.partake.base;

import in.partake.resource.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

/**
 * Time utility functions.
 *
 * @author shinyak
 */
public final class TimeUtil {
    private static DateTime currentDateTime;

    private TimeUtil() {
        // Prevents from instantiation.
    }

    /**
     * Resets the current date.
     */
    public static void resetCurrentDate() {
        TimeUtil.currentDateTime = null;
    }

    /**
     * 現在時刻を返す。もし、現在時刻が陽に設定されていれば、その時刻を返す。
     * そうでなければ、OS から現在時刻を取得して返す。
     * @return
     */
    public static DateTime getCurrentDateTime() {
        if (currentDateTime != null)
            return new DateTime(currentDateTime.getTime());
        else
            return new DateTime(System.currentTimeMillis());
    }

    /**
     * 現在時刻をミリ秒単位で返す。現在時刻が陽に設定されていれば、その現在時刻を返す。
     * そうでなければ、new Date().getTime() と同じ。
     * @return
     */
    public static long getCurrentTime() {
        if (currentDateTime != null)
            return currentDateTime.getTime();
        else
            return new Date().getTime();
    }

    public static void setCurrentDateTime(DateTime dt) {
        currentDateTime = dt;
    }

    public static void setCurrentTime(long time) {
        currentDateTime = new DateTime(time);
    }

    /**
     * Waits for a while.
     */
    public static void waitForTick() {
        if (currentDateTime != null) {
            setCurrentTime(currentDateTime.getTime() + 20);
            return;
        }

        long now = new Date().getTime();
        do {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // ignore.
            }
        } while (now == TimeUtil.getCurrentTime());
    }

    public static DateTime create(int year, int month, int date, int hour, int min, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("JST"));

        return new DateTime(calendar.getTime());
    }


    public static Date create(int year, int month, int date, int hour, int min, int sec, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(timeZone);

        return calendar.getTime();
    }

    public static Calendar calendar(Date date) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String formatForEvent(DateTime date) {
        DateFormat dateFormatForEvent = new SimpleDateFormat(Constants.READABLE_DATE_FORMAT);
        return dateFormatForEvent.format(date.toDate());
    }

    public static DateTime parseForEvent(String dateStr) {
        DateFormat dateFormatForEvent = new SimpleDateFormat(Constants.READABLE_DATE_FORMAT);
        try {
            return new DateTime(dateFormatForEvent.parse(dateStr).getTime());
        } catch (ParseException e) {
            // DO NOTHING.
        }

        try {
            long time = Long.valueOf(dateStr);
            return new DateTime(time);
        } catch (NumberFormatException e) {
            // DO NOTHING
        }

        return null;
    }

    public static DateTime dateTimeFromTimeString(String timeString) {
        try {
            return new DateTime(Long.parseLong(timeString));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String getTimeString(DateTime date) {
        return getTimeString(date.getTime());
    }

    public static String getTimeString(long time) {
        return new Formatter().format("%020d", time).toString();
    }
}
