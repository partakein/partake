package in.partake.session;

import org.openid4java.discovery.DiscoveryInformation;

public class OpenIDLoginInformation {
    private String loginPurpose;
    private DiscoveryInformation discoveryInformation;
    
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
