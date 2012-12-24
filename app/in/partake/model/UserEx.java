package in.partake.model;

import in.partake.app.PartakeConfiguration;
import in.partake.base.SafeJSONable;
import in.partake.model.dto.User;
import in.partake.model.dto.UserTwitterLink;

import java.util.Set;

import org.codehaus.jackson.node.ObjectNode;

/**
 * user with related data.
 * @author shinyak
 *
 */
public class UserEx extends User implements SafeJSONable {
    private UserTwitterLink twitterLinkage;

    public UserEx(User user, UserTwitterLink twitterLinkage) {
        super(user);
        this.twitterLinkage = twitterLinkage;
    }

    public UserTwitterLink getTwitterLinkage() {
        return twitterLinkage;
    }

    public String getTwitterScreenName() {
        return twitterLinkage.getScreenName();
    }

    public String getTwitterProfileImageURL() {
        return twitterLinkage.getProfileImageURL();
    }

    public boolean isAdministrator() {
        String screenName = twitterLinkage.getScreenName();
        Set<String> adminScreenNames = PartakeConfiguration.administratorScreenNames();
        return adminScreenNames.contains(screenName);
    }


    public ObjectNode toSafeJSON() {
        ObjectNode obj = super.toSafeJSON();

        if (twitterLinkage != null)
            obj.put("twitter", twitterLinkage.toSafeJSON());

        return obj;
    }
}
