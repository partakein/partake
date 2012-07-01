package in.partake.resource;


/**
 * UserErrorCode describes why the user request is invalid.
 * @author shinyak
 *
 */
public enum UserErrorCode {
    // TODO: 表記を揃える。invalid.<type>.<reason> に固定すること。
    // TODO: invalid から始まることがわかっているのだから、ここではとってしまってもいいかもしれない。resources_ja.properties では残す。

    //
    UNKNOWN_USER_ERROR("invalid.unknown"),
    INTENTIONAL_USER_ERROR("invalid.intentional"),
    UNEXPECTED_REQUEST("invalid.request.unexpected"),

    // BASE
    INVALID_ARGUMENT("invalid.argument"),
    INVALID_PARAMETERS("invalid.parameters"),
    INVALID_LOGIN_REQUIRED("invalid.login", 401),
    INVALID_PROHIBITED("invalid.prohibited", 403),
    INVALID_NOTFOUND("invalid.notfound", 404),
    INVALID_NONMULTIPART_REQUEST("invalid.request.nonmultipart"),

    // EVENT
    INVALID_EVENT_ID("invalid.event.id"),
    MISSING_EVENT_ID("invalid.event.id.missing"),

    FORBIDDEN_EVENT_SHOW("invalid.event.show.forbidden", 403),
    FORBIDDEN_EVENT_EDIT("invalid.event.edit.forbidden", 403),
    FORBIDDEN_EVENT_COPY("invalid.event.copy.forbidden", 403),
    FORBIDDEN_EVENT_ATTENDANT_EDIT("invalid.event.attendant.edit.forbidden", 403),

    INVALID_ENROLL_TIMEOVER("invalid.event.enroll.timeover"),
    INVALID_ENROLL_STATUS("invalid.event.enroll.status"),
    INVALID_ENROLL_REQUIRED("invalid.event.enroll.required"),

    INVALID_ATTENDANT_EDIT("invalid.attendant.edit"),
    EVENT_ALREADY_PUBLISHED("invalid.event.publish.already"),

    // TICKET
    INVALID_TICKET_ID("invalid.ticket.id"),
    MISSING_TICKET_ID("invalid.ticket.id.missing"),
    INVALID_TICKET_DUPLICATE_ID("invalid.ticket.id.duplicate"),

    // TICKET APPLICATION
    INVALID_APPLICATION_RESERVATION_TIMEOVER("invalid.application.reservation.timeover"),

    // ENQUETE
    MISSING_ENQUETE_QUESTION("invalid.enquete.question.missing"),
    MISSING_ENQUETE_TYPE("invalid.enquete.type.missing"),
    MISSING_ENQUETE_OPTION("invalid.enquete.option.missing"),
    INVALID_ENQUETE_PARAMS("invalid.enquete.params"),
    INVALID_ENQUETE_ANSWERS("invalid.enquete.answers"),

    // USER
    INVALID_USER_ID("invalid.invalid_userid"),
    MISSING_USER_ID("invalid.missing_userid"),
    INVALID_USER_PRIVATE("invalid.user.profile.private", 403),
    MISSING_USERNAME("invalid.user.username.missing"),

    // OPEN_ID
    INVALID_OPENID("invalid.invalid_openid"),
    MISSING_OPENID("invalid.missing_openid"),
    INVALID_OPENID_PURPOSE("invalid.openid.purpose"),
    INVALID_OPENID_IDENTIFIER("invalid.openid.identifier"),

    // IMAGE
    INVALID_IMAGEID("invalid.image.id"),
    MISSING_IMAGEID("invalid.image.id.missing"),
    INVALID_NOIMAGE("invalid.image.noimage"),
    INVALID_IMAGE_CONTENTTYPE("invalid.image.contenttype"),
    INVALID_IMAGE_OWNER("invalid.image.owner"),

    // CALENDAR
    INVALID_CALENDAR_ID("invalid.calendar.id"),
    MISSING_CALENDAR_ID("invalid.calendar.id.missing"),

    // SESSION
    INVALID_SESSION("invalid.invalid_session"),
    MISSING_SESSION("invalid.missing_session"),

    // SECURITY
    INVALID_SECURITY_CSRF("invalid.security.csrf"),

    // ATTENDANCE
    INVALID_ATTENDANCE_STATUS("invalid.invalid_attendance_status"),
    MISSING_ATTENDANCE_STATUS("invalid.missing_attendance_status"),

    // SEARCH
    INVALID_SEARCH_QUERY("invalid.invalid_search_query"),
    MISSING_SEARCH_QUERY("invalid.missing_search_query"),
    INVALID_SEARCH_CATEGORY("invalid.invalid_search_category"),
    MISSING_SEARCH_CATEGORY("invalid.missing_search_category"),
    INVALID_SEARCH_DEADLINE("invalid.invalid_search_deadline"),
    MISSING_SEARCH_DEADLINE("invalid.missing_search_deadline"),
    INVALID_SEARCH_ORDER("invalid.invalid_search_order"),
    MISSING_SEARCH_ORDER("invalid.missing_search_order"),
    INVALID_SEARCH_MAXNUM("invalid.invalid_search_max_num"),
    MISSING_SEARCH_MAXNUM("invalid.missing_search_max_num"),

    // COMMENT
    INVALID_COMMENT_ID("invalid.comment.id"),
    MISSING_COMMENT_ID("invalid.comment.id.missing"),
    MISSING_COMMENT("invalid.comment.missing"),
    INVALID_COMMENT_TOOLONG("invalid.comment.toolong"),
    COMMENT_REMOVAL_FORBIDDEN("invalid.comment.removal.forbidden", 403),

    // MESSAGE
    MISSING_MESSAGE("invalid.message.missing"),
    MISSING_MESSAGE_SUBJECT("invalid.message.subject"),
    INVALID_MESSAGE_SUBJECT_TOOLONG("invalid.message.subject.toolong"),
    INVALID_MESSAGE_TOOMUCH("invalid.message.toomuch"),
    INVALID_MESSAGE_TOOLONG("invalid.message.toolong"),
    FORBIDDEN_MESSAGE_SHOW("invalid.message.show.forbidden", 403),

    // ENROLLMENT
    INVALID_MISSING_VIP("invalid.enrollment.vip.missing"),

    // NOTIFICATION
    FORBIDDEN_SHOW_NOTIFICATION("invalid.notification.show.forbidden"),

    // OAUTH
    INVALID_OAUTH_VERIFIER("invalid.oauth.verifier");

    // ----------------------------------------------------------------------

    public static UserErrorCode safeValueOf(String id) {
        if (id == null)
            return null;

        UserErrorCode errorCode = null;
        try {
            errorCode = UserErrorCode.valueOf(id);
        } catch (IllegalArgumentException ignore) {
        }

        if (errorCode != null)
            return errorCode;

        for (UserErrorCode ec : UserErrorCode.values()) {
            if (ec.errorDescriptionId.equalsIgnoreCase(id))
                return ec;
            if (ec.toString().equalsIgnoreCase(id))
                return ec;
        }

        return null;
    }

    // ----------------------------------------------------------------------
    private final String errorDescriptionId;
    private final int statusCode;

    private UserErrorCode(String errorReason) {
        this(errorReason, 400);
    }

    private UserErrorCode(String errorReason, int statusCode) {
        this.errorDescriptionId = errorReason;
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorDescriptionId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonString() {
        return I18n.t(errorDescriptionId);
    }

}
