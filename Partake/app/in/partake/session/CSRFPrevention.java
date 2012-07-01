package in.partake.session;


import java.util.UUID;

/**
 * To prevent CSRF, we issue sessionToken, which is a unique value for each session.
 * When a use submits something, he must submit sessionToken also. If the session token is missing or
 * not equal to one we have, the submission is considered as invalid.
 *
 * @author shinyak
 */
@Deprecated
public final class CSRFPrevention {
    private String sessionToken;

    public CSRFPrevention() {
        // Note that UUID.randomUUID uses secure random.
        sessionToken = UUID.randomUUID().toString();
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public boolean isValidSessionToken(String token) {
        return sessionToken.equals(token);
    }
}
