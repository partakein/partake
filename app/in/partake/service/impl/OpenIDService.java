package in.partake.service.impl;

import in.partake.service.IOpenIDService;

import java.util.List;
import java.util.Map;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ParameterList;

public class OpenIDService implements IOpenIDService {
    private ConsumerManager consumerManager;

    public OpenIDService() {
        try {
            consumerManager = new ConsumerManager();
        } catch (ConsumerException e) {
            throw new RuntimeException(e);
        }
    }

    public DiscoveryInformation discover(String identifier) throws OpenIDException {
        String userSuppliedString = identifier;
        List<?> discoveries = consumerManager.discover(userSuppliedString);
        DiscoveryInformation discovered = consumerManager.associate(discoveries);

        return discovered;
    }

    public String getURLToAuthenticate(DiscoveryInformation discoveryInformation, String callbackURL) throws OpenIDException {
        AuthRequest authReq = consumerManager.authenticate(discoveryInformation, callbackURL);
        return authReq.getDestinationUrl(true);
    }

    public String getIdentifier(String receivingURL, Map<String, Object> params, DiscoveryInformation discoveryInformation) throws OpenIDException {
        VerificationResult verification = consumerManager.verify(receivingURL, new ParameterList(params), discoveryInformation);

        Identifier verified = verification.getVerifiedId();
        if (verified != null)
            return verified.getIdentifier();

        return null;
    }
}
