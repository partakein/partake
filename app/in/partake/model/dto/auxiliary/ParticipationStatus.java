package in.partake.model.dto.auxiliary;

public enum ParticipationStatus {
    ENROLLED() { // 参加します
        public boolean isEnrolled() { return true; }
        public String toHumanReadableString(boolean isInWaitingQueue) { return isInWaitingQueue ? "キャンセル待ち" : "参加"; }
    },
    RESERVED() { // 多分参加します(仮登録)
        public boolean isEnrolled() { return true; }
        public String toHumanReadableString(boolean isInWaitingQueue) { return isInWaitingQueue ? "キャンセル待ち(仮)" : "参加(仮)"; }
    },
    CANCELLED() { // 参加をキャンセルします
        public boolean isEnrolled() { return false; }
        public String toHumanReadableString(boolean isInWaitingQueue) { return "キャンセル済"; }
    },
    NOT_ENROLLED() { // そもそも参加をしていません
        public boolean isEnrolled() { return false; }
        public String toHumanReadableString(boolean isInWaitingQueue) { return "不参加"; }
    }
    ;

    private static ParticipationStatus SAFE_VALUE = NOT_ENROLLED;

    public static ParticipationStatus safeValueOf(String str) {
        if (str == null) { return SAFE_VALUE; }
        if ("".equals(str)) { return SAFE_VALUE; }

        try {
            return valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SAFE_VALUE;
        }
    }

    // ----------------------------------------------------------------------
    public abstract boolean isEnrolled();
    public abstract String toHumanReadableString(boolean isInWaitingQueue);
}


