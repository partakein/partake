package in.partake.service;

import java.util.Map;

import org.openid4java.OpenIDException;
import org.openid4java.discovery.DiscoveryInformation;

public interface IOpenIDService {

    public DiscoveryInformation discover(String identifier) throws OpenIDException;
    public String getURLToAuthenticate(DiscoveryInformation discoveryInformation, String callbackURL) throws OpenIDException;
    public String getIdentifier(String receivingURL, Map<String, Object> params, DiscoveryInformation discoveryInformation) throws OpenIDException;
}
