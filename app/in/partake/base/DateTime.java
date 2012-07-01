package in.partake.base;

import in.partake.resource.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Since java.util.Date is mutable, We prefer to use DateTime instead.
// Keep DateTime immutable object.
public final class DateTime implements Comparable<DateTime> {
    private long millis;

    public DateTime(long millis) {
        this.millis = millis;
    }

    public DateTime(Date date) {
        this.millis = date.getTime();
    }


    public long getTime() {
        return millis;
    }

    public Date toDate() {
        return new Date(millis);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DateTime))
            return false;

        DateTime lhs = this;
        DateTime rhs = (DateTime) obj;

        return lhs.millis == rhs.millis;
    }

    @Override
    public int hashCode() {
        return (int) millis;
    }

    @Override
    public String toString() {
        return millis + "[ms]";
    }

    @Override
    public int compareTo(DateTime rhs) {
        DateTime lhs = this;
        if (lhs.millis < rhs.millis)
            return -1;
        else if (lhs.millis == rhs.millis)
            return 0;
        else
            return 1;
    }

    public boolean isBefore(DateTime dt) {
        return millis < dt.millis;
    }

    public boolean isAfter(DateTime dt) {
        return dt.millis < millis;
    }

    public DateTime nDayBefore(int n) {
        return new DateTime(Util.ensureMin(getTime() - 1000L * 3600 * 24 * n, 0L));
    }

    public DateTime nDayAfter(int n) {
        return new DateTime(getTime() + 1000L * 3600 * 24 * n);
    }

    public DateTime nHourBefore(int n) {
        return new DateTime(Util.ensureMin(getTime() - 1000L * 3600 * n, 0L));
    }

    public DateTime nHourAfter(int n) {
        return new DateTime(getTime() + 1000L * 3600 * n);
    }

    public DateTime nSecAfter(int n) {
        return new DateTime(getTime() + 1000L * n);
    }

    public DateTime nSecBefore(int n) {
        return new DateTime(getTime() - 1000L * n);
    }

    public String toHumanReadableFormat() {
        DateFormat format = new SimpleDateFormat(Constants.READABLE_DATE_FORMAT, Locale.getDefault());
        return format.format(this.toDate());
    }
}
