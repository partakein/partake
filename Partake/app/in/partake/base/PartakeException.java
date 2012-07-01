package in.partake.base;

import java.util.Collections;
import java.util.Map;

import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

/**
 * An Exception which contains ServerErrorCode or UserErrorCode.
 * Either ServerErrorCode or UserErrorCode should be included.
 *
 * @author shinyak
 */
public class PartakeException extends Exception {
    private static final long serialVersionUID = 1L;

    private ServerErrorCode serverErrorCode;
    private UserErrorCode userErrorCode;
    private Map<String, String> additionalInfo;

    public PartakeException(ServerErrorCode ec) {
        super(ec.getReasonString());
        this.serverErrorCode = ec;
    }

    public PartakeException(ServerErrorCode ec, Throwable t) {
        super(ec.getReasonString(), t);
        this.serverErrorCode = ec;
    }

    public PartakeException(ServerErrorCode ec, String key, String value) {
        super(ec.getReasonString());
        this.serverErrorCode = ec;
        this.additionalInfo = Collections.singletonMap(key, value);
    }

    public PartakeException(ServerErrorCode ec, Map<String, String> additionalInfo) {
        super(ec.getReasonString());
        this.serverErrorCode = ec;
        this.additionalInfo = additionalInfo;
    }

    public PartakeException(UserErrorCode ec) {
        super(ec.getReasonString());
        this.userErrorCode = ec;
    }

    public PartakeException(UserErrorCode ec, String key, String value) {
        super(ec.getReasonString());
        this.userErrorCode = ec;
        this.additionalInfo = Collections.singletonMap(key, value);
    }

    public PartakeException(UserErrorCode ec, Throwable t) {
        super(ec.getReasonString(), t);
        this.userErrorCode = ec;
    }

    public PartakeException(UserErrorCode ec, Map<String, String> additionalInfo) {
        super(ec.getReasonString());
        this.userErrorCode = ec;
        this.additionalInfo = additionalInfo;
    }

    public boolean isServerError() {
        return serverErrorCode != null;
    }

    public ServerErrorCode getServerErrorCode() {
        return serverErrorCode;
    }

    public boolean isUserError() {
        return userErrorCode != null;
    }

    public UserErrorCode getUserErrorCode() {
        return userErrorCode;
    }

    public int getStatusCode() {
        if (serverErrorCode != null)
            return serverErrorCode.getStatusCode();
        else
            return userErrorCode.getStatusCode();
    }

    public Map<String, String> getAdditionalInfo() {
        return this.additionalInfo;
    }
}
