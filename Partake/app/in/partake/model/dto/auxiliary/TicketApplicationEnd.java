package in.partake.model.dto.auxiliary;

public enum TicketApplicationEnd {
    TILL_TIME_BEFORE_EVENT,
    TILL_TIME_AFTER_EVENT,
    TILL_NDAY_BEFORE,
    TILL_CUSTOM_DAY;

    public String toString() {
        return super.toString().toLowerCase();
    }

    public static TicketApplicationEnd safeValueOf(String value) {
        for (TicketApplicationEnd start : values()) {
            if (start.toString().equalsIgnoreCase(value))
                return start;
        }

        return TILL_TIME_BEFORE_EVENT;
    }
}
