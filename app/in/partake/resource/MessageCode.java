package in.partake.resource;

/**
 * UserAlertCode
 * @author shinyak
 *
 */
public enum MessageCode {
    // Note: Each message id should have "message.<type>.<reason> format.
    MESSAGE_UNKNOWN("message.unknown"),

    MESSAGE_AUTH_LOGIN("message.auth.login"),
    MESSAGE_AUTH_LOGOUT("message.auth.logout"),

    MESSAGE_PASSCODE_INVALID("message.event.passcode.invalid", MessageLevel.WARNING),
    MESSAGE_EVENT_INDEX_RECREATED("message.event.index.recreated"),

    MESSAGE_OPENID_LOGIN_FAILURE("message.openid.login.failure", MessageLevel.WARNING),
    MESSAGE_OPENID_LOGIN_NOLINKAGE("message.openid.login.nolinkage", MessageLevel.WARNING),
    MESSAGE_OPENID_CONNECTION_SUCCESS("message.openid.connection.success");

    // ----------------------------------------------------------------------

    public static MessageCode safeValueOf(String id) {
        if (id == null)
            return null;

        MessageCode errorCode = null;
        try {
            errorCode = MessageCode.valueOf(id);
        } catch (IllegalArgumentException ignore) {
        }

        if (errorCode != null)
            return errorCode;

        for (MessageCode ec : MessageCode.values()) {
            if (ec.messageDescriptionId.equalsIgnoreCase(id))
                return ec;
            if (ec.toString().equalsIgnoreCase(id))
                return ec;
        }

        return null;
    }

    // ----------------------------------------------------------------------
    private final String messageDescriptionId;
    private final MessageLevel level;

    private MessageCode(String id) {
        this.messageDescriptionId = id;
        this.level = MessageLevel.INFO;
    }

    private MessageCode(String id, MessageLevel level) {
        this.messageDescriptionId = id;
        this.level = level;
    }

    // TODO(mayah): Why getErrorCode()? Why not getDescriptionId() or something like that?
    public String getErrorCode() {
        return messageDescriptionId;
    }

    public String getMessage() {
        return I18n.t(messageDescriptionId);
    }

    public MessageLevel getLevel() {
        return level;
    }
}
