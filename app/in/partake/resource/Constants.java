package in.partake.resource;

import java.util.UUID;

@SuppressWarnings("nls")
public final class Constants {
    // twitter settings
    public static final String TWITTER_APPLICATION_NAME  = "Partake";
    public static final String TWITTER_REQUEST_TOKEN_URL = "http://twitter.com/oauth/request_token";
    public static final String TWITTER_ACCESS_TOKEN_URL  = "http://twitter.com/oauth/access_token";
    public static final String TWITTER_AUTHORIZE_URL 	 = "http://twitter.com/oauth/authorize";

    public static final String ANTISAMY_POLICY_FILE_RELATIVE_LOCATION = "/antisamy-partake-from-myspace-1.4.1.xml";

    public static final UUID DEMO_ID = UUID.fromString("ff24c3ad-98b6-4fe3-b2be-68d220d6a866");

    // Since session is just a cookie, we should reduce the whole quantity bytes of session value.
    public interface Session {
        // For login user
        public static final String USER_ID_KEY = "userId";
        // For CSRF prevention token
        public static final String TOKEN_KEY = "sessionToken";
        // For Session rare data.
        public static final String ID_KEY = "sessionId";
    }

    // Flash is also just a cookie, we should reduce the whole quantify bytes of flash value.
    public interface Flash {
        public static final String MESSAGE_ID = "messageId";
    }

    // Used for HTTP request parameter key.
    public interface Parameter {
        public static final String SESSION_TOKEN = "sessionToken";

        // SessionToken used for test. (Currently this value is only for Scala controllers)
        public static final String VALID_SESSION_TOKEN_FOR_TEST = "valid-session-token-for-test";
    }

    // Used for Cache.
    public interface Cache {
        public static final String TWITTER_LOGIN_KEY_PREFIX = "twitterLogin:";
        public static final String OPENID_LOGIN_KEY_PREFIX = "openID:";
    }


    // These deprecated value should be removed once they are not used.
    // Session Attribute
    @Deprecated
    public static final String ATTR_USER = "user";          // User (logged in user)
    @Deprecated
    public static final String ATTR_ACTION = "actionModel"; //
    @Deprecated
    public static final String ATTR_PARTAKE_SESSION = "sessionToken";
    @Deprecated
    public static final String ATTR_REDIRECTURL = "redirectURL";
    @Deprecated
    public static final String ATTR_CURRENT_URL = "currentURL";
    @Deprecated
    public static final String ATTR_NO_HEADER_MESSAGES = "NO_HEADER_MESSAGES";
    @Deprecated
    public static final String ATTR_PARTAKE_API_SESSION_TOKEN = ATTR_PARTAKE_SESSION;
    @Deprecated
    public static final String JSON_DATE_FORMAT = "yyyy/MM/dd HH:mm";

    public static final String READABLE_DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private Constants() {
        // Prevents from instantiation.
    }
}
