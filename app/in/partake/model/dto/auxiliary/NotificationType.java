package in.partake.model.dto.auxiliary;


public enum NotificationType {
    UNKNOWN_TYPE,
    EVENT_ONEDAY_BEFORE_REMINDER,
    ONE_DAY_BEFORE_REMINDER_FOR_RESERVATION,
    HALF_DAY_BEFORE_REMINDER_FOR_RESERVATION,
    BECAME_TO_BE_ENROLLED, //　繰り上がり
    BECAME_TO_BE_CANCELLED, // 繰り下がり
    ;

    public static NotificationType safeValueOf(String value) {
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            // TODO: should warn.
            return UNKNOWN_TYPE;
        }
    }
}
