package in.partake.resource;
//TODO: move this package into in.partake.base.resource

/**
 * ServerErrorCode describes the reason why the server returned an error.
 * @author shinyak
 */
public enum ServerErrorCode {
    // TODO: sort this

    UNKNOWN_ERROR("error.unknown"),
    runUNKNOWN_RUNTIME_ERROR("error.runtime.error"),
    INTENTIONAL_ERROR("error.intentional"),
    LOGIC_ERROR("error.logic_error"), // some logic error

    // TODO: DB_ERROR は後で名前かえるべき直す (or id をかえる)
    DB_ERROR("in.partake.database_error"),
    DB_CONNECTION_POOL_INITIALIZATION_FAILURE("error.db.pool.initialization_error"),

    ERROR_IO("error.io"),

    FEED_CREATION("error.feed.creation"),

    DAO_INITIALIZATION_ERROR("error.dao.initialization_error"),

    CALENDAR_CREATION_FAILURE("error.calendar.creation_failure"),
    CALENDAR_INVALID_FORMAT("error.calendar.invalid_format"),

    NO_CSRF_PREVENTION("error.no_csrf_prevention"),
    NO_CREATED_SESSION_TOKEN("error.no_created_session_token"),

    TWITTER_OAUTH_ERROR("error.twitter.oauth"),

    PARTICIPATIONS_RETRIEVAL_ERROR("error.participations.retrieval"),

    USER_PREFERENCE_NOTFOUND("error.user.preference.notfound"),

    BITLY_ERROR("error.bitly"),

    OPENID_ERROR("error.opneid"),

    EVENT_SEARCH_SERVICE_ERROR("error.service.event_search"),

    LUCENE_INITIALIZATION_FAILURE("error.lucene.initialization_failure");

    // ----------------------------------------------------------------------

    public static ServerErrorCode safeValueOf(String id) {
        if (id == null)
            return null;

        ServerErrorCode errorCode = null;
        try {
            errorCode = ServerErrorCode.valueOf(id);
        } catch (IllegalArgumentException ignore) {
        }
        if (errorCode != null)
            return errorCode;

        for (ServerErrorCode ec : ServerErrorCode.values()) {
            if (ec.errorDescriptionId.equalsIgnoreCase(id))
                return ec;
            if (ec.toString().equalsIgnoreCase(id))
                return ec;
        }

        return null;
    }

    // ----------------------------------------------------------------------
    private final String errorDescriptionId;
    private int statusCode;

    private ServerErrorCode(String errorReasonId) {
        this(errorReasonId, 500);
    }

    private ServerErrorCode(String errorReasonId, int statusCode) {
        this.errorDescriptionId = errorReasonId;
        this.statusCode = statusCode;
    }

    public String getReasonString() {
        return I18n.t(errorDescriptionId);
    }

    public String getErrorCode() {
        return errorDescriptionId;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
