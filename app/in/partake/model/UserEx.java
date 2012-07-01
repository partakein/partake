package in.partake.model;

import java.util.Set;

import net.sf.json.JSONObject;
import in.partake.base.SafeJSONable;
import in.partake.model.dto.UserTwitterLink;
import in.partake.model.dto.User;
import in.partake.resource.PartakeProperties;

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
        Set<String> adminScreenNames = PartakeProperties.get().getTwitterAdminNames();
        return adminScreenNames.contains(screenName);
    }


    public JSONObject toSafeJSON() {
        JSONObject obj = super.toSafeJSON();

        if (twitterLinkage != null)
            obj.put("twitter", twitterLinkage.toSafeJSON());

        return obj;
    }
}
