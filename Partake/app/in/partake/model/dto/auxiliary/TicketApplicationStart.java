package in.partake.model.dto.auxiliary;

public enum TicketApplicationStart {
    ANYTIME,
    FROM_NTH_DAY_BEFORE,
    FROM_CUSTOM_DAY;

    public String toString() {
        return super.toString().toLowerCase();
    }

    public static TicketApplicationStart safeValueOf(String value) {
        for (TicketApplicationStart start : values()) {
            if (start.toString().equalsIgnoreCase(value))
                return start;
        }

        return ANYTIME;
    }
}
