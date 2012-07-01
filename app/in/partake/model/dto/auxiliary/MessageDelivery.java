package in.partake.model.dto.auxiliary;

public enum MessageDelivery {
    NOT_DELIVERED, // Not queued.
    INQUEUE,       // In queue
    SUCCESS,       // Succeeded sending.
    FAIL;          // Failed sending.

    public static MessageDelivery safeValueOf(String str) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException e) {
            return NOT_DELIVERED;
        }
    }
}
