package in.partake.session;

import java.io.Serializable;

import org.openid4java.discovery.DiscoveryInformation;

public class OpenIDLoginInformation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String loginPurpose;
    private DiscoveryInformation discoveryInformation;

    public OpenIDLoginInformation(String loginPurpose, DiscoveryInformation discoveryInformation) {
        this.loginPurpose = loginPurpose;
        this.discoveryInformation = discoveryInformation;
    }

    public synchronized void setLoginPurpose(String purpose) {
        this.loginPurpose = purpose;
    }

    public synchronized String takeLoginPurpose() {
        String purpose = loginPurpose;
        loginPurpose = null;
        return purpose;
    }

    public synchronized void setDiscoveryInformation(DiscoveryInformation discoveryInformation) {
        this.discoveryInformation = discoveryInformation;
    }

    public synchronized DiscoveryInformation getDiscoveryInformation() {
        return this.discoveryInformation;
    }
}
