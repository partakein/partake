package in.partake.model.dto.auxiliary;

public enum TicketAmountType {
    UNLIMITED,
    LIMITED;

    public String toString() {
        return super.toString().toLowerCase();
    }

    public static TicketAmountType safeValueOf(String key) {
        if ("unlimited".equalsIgnoreCase(key))
            return UNLIMITED;
        if ("limited".equalsIgnoreCase(key))
            return LIMITED;
        return UNLIMITED;
    }
}
