package in.partake.model.dto;

import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class UserTwitterLink extends PartakeModel<UserTwitterLink> {
    private UUID id;
    private long twitterId;
    private String userId;
    private String screenName;
    private String name;
    private String accessToken;
    private String accessTokenSecret;
    private String profileImageURL;

    public UserTwitterLink(UUID id, long twitterId, String userId, String screenName, String name, String accessToken, String accessTokenSecret, String profileImageURL) {
        this.id = id;
        this.twitterId = twitterId;
        this.userId = userId;
        this.screenName = screenName;
        this.name = name;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.profileImageURL = profileImageURL;
    }

    public UserTwitterLink(UserTwitterLink linkage) {
        this.id = linkage.id;
        this.twitterId = linkage.twitterId;
        this.userId = linkage.userId;
        this.screenName = linkage.screenName;
        this.name = linkage.name;
        this.accessToken = linkage.accessToken;
        this.accessTokenSecret = linkage.accessTokenSecret;
        this.profileImageURL = linkage.profileImageURL;
    }

    public UserTwitterLink(ObjectNode obj) {
        this.id = UUID.fromString(obj.get("id").asText());
        this.twitterId = obj.get("twitterId").asLong();
        this.userId = obj.get("userId").asText();
        this.screenName = obj.get("screenName").asText();
        if (obj.has("name"))
            this.name = obj.get("name").asText();
        if (obj.has("accessToken") && !obj.get("accessToken").isNull())
            this.accessToken = obj.get("accessToken").asText();
        if (obj.has("accessTokenSecret") && !obj.get("accessTokenSecret").isNull())
            this.accessTokenSecret = obj.get("accessTokenSecret").asText();
        if (obj.has("profileImageURL"))
            this.profileImageURL = obj.get("profileImageURL").asText();
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    public ObjectNode toSafeJSON() {
    	ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);

        obj.put("twitterId", twitterId);
        obj.put("screenName", screenName);
        obj.put("name", name);
        obj.put("profileImageURL", profileImageURL);

        return obj;
    }

    @Override
    public ObjectNode toJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("id", id.toString());
        obj.put("twitterId", twitterId);
        obj.put("userId", userId);
        obj.put("screenName", screenName);
        obj.put("name", name);
        obj.put("accessToken", accessToken);
        obj.put("accessTokenSecret", accessTokenSecret);
        obj.put("profileImageURL", profileImageURL);
        return obj;
    }

    // ----------------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserTwitterLink)) { return false; }

        UserTwitterLink lhs = this;
        UserTwitterLink rhs = (UserTwitterLink) obj;

        if (!ObjectUtils.equals(lhs.id,                rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.twitterId,         rhs.twitterId)) { return false; }
        if (!ObjectUtils.equals(lhs.userId,            rhs.userId)) { return false; }
        if (!ObjectUtils.equals(lhs.screenName,        rhs.screenName)) { return false; }
        if (!ObjectUtils.equals(lhs.name,              rhs.name)) { return false; }
        if (!ObjectUtils.equals(lhs.accessToken,       rhs.accessToken)) { return false; }
        if (!ObjectUtils.equals(lhs.accessTokenSecret, rhs.accessTokenSecret)) { return false; }
        if (!ObjectUtils.equals(lhs.profileImageURL,   rhs.profileImageURL)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(twitterId);
        code = code * 37 + ObjectUtils.hashCode(userId);
        code = code * 37 + ObjectUtils.hashCode(screenName);
        code = code * 37 + ObjectUtils.hashCode(name);
        code = code * 37 + ObjectUtils.hashCode(accessToken);
        code = code * 37 + ObjectUtils.hashCode(accessTokenSecret);
        code = code * 37 + ObjectUtils.hashCode(profileImageURL);

        return code;
    }

    // ----------------------------------------------------------------------
    //

    public UUID getId() {
        return id;
    }

    public long getTwitterId() {
        return twitterId;
    }

    public String getUserId() {
        return userId;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getName() {
        return name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setId(UUID id) {
        checkFrozen();
        this.id = id;
    }

    public void setTwitterId(long twitterId) {
        checkFrozen();
        this.twitterId = twitterId;
    }

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }

    public void setScreenName(String screenName) {
        checkFrozen();
        this.screenName = screenName;
    }

    public void setName(String name) {
        checkFrozen();
        this.name = name;
    }

    public void setAccessToken(String accessToken) {
        checkFrozen();
        this.accessToken = accessToken;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        checkFrozen();
        this.accessTokenSecret = accessTokenSecret;
    }

    public void setProfileImageURL(String profileImageURL) {
        checkFrozen();
        this.profileImageURL = profileImageURL;
    }

    // ----------------------------------------------------------------------

    /**
     * mark this linkage as unauthorized one.
     * @see http://dev.twitter.com/pages/auth
     */
    public void markAsUnauthorized() {
        checkFrozen();
        this.accessToken = null;
        this.accessTokenSecret = null;
    }

    /**
     * @return true if this is authorized user.
     */
    public boolean isAuthorized() {
        return this.accessToken != null && this.accessTokenSecret != null;
    }
}
