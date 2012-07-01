package in.partake.model.dto.auxiliary;

public enum TicketReservationEnd {
    TILL_TIME_BEFORE_APPLICATION,
    TILL_NHOUR_BEFORE,
    TILL_NONE,
    TILL_CUSTOM_DAY;

    public String toString() {
        return super.toString().toLowerCase();
    }

    public static TicketReservationEnd safeValueOf(String value) {
        for (TicketReservationEnd start : values()) {
            if (start.toString().equalsIgnoreCase(value))
                return start;
        }

        return TILL_NONE;
    }
}
