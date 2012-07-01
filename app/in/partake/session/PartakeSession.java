package in.partake.session;

import in.partake.resource.MessageCode;


/**
 * PartakeSession is a type safe session object.
 * @author shinyak
 *
 */
@Deprecated
public class PartakeSession {
    private CSRFPrevention csrfPrevention;

    private OpenIDLoginInformation openIDLoginInfomation;
    private TwitterLoginInformation twitterLoginInformation;

    private MessageCode messageCode;

    private PartakeSession(CSRFPrevention prevention) {
        this.csrfPrevention = prevention;
    }

    public static PartakeSession createInitialPartakeSession() {
        return new PartakeSession(new CSRFPrevention());
    }

    public CSRFPrevention getCSRFPrevention() {
        return this.csrfPrevention;
    }

    public synchronized OpenIDLoginInformation ensureOpenIDLoginInformation() {
        if (openIDLoginInfomation == null)
            openIDLoginInfomation = new OpenIDLoginInformation();

        return openIDLoginInfomation;
    }

    public synchronized void setTwitterLoginInformation(TwitterLoginInformation information) {
        twitterLoginInformation = information;
    }

    public synchronized TwitterLoginInformation takeTwitterLoginInformation() {
        TwitterLoginInformation result = twitterLoginInformation;
        twitterLoginInformation = null;
        return result;
    }

    public synchronized void setMessageCode(MessageCode messageCode) {
        if (messageCode != null)
            this.messageCode = messageCode;
    }

    public synchronized MessageCode takeMessageCode() {
        MessageCode code = this.messageCode;
        this.messageCode = null;
        return code;
    }
}
