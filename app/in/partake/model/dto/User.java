package in.partake.model.dto;

import in.partake.base.DateTime;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class User extends PartakeModel<User> {
    private String id;
    private String screenName;
    private String profileImageURL;
    private DateTime createdAt;
    private DateTime modifiedAt;

    public User(String id, String screenName, String profileImageURL, DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.screenName = screenName;
        this.profileImageURL = profileImageURL;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public User(User user) {
        this(user.id, user.screenName, user.profileImageURL, user.createdAt, user.modifiedAt);
    }

    public User(ObjectNode obj) {
        this.id = obj.get("id").asText();
        this.screenName = obj.get("screenName").asText();
        this.profileImageURL = obj.get("profileImageURL").asText();
        this.createdAt = new DateTime(obj.get("createdAt").asLong());
        if (obj.has("modifiedAt"))
            this.modifiedAt = new DateTime(obj.get("modifiedAt").asLong());
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    /**
     * sensitive な情報を含まないような user を取得します。
     *
     * @return
     */
    public ObjectNode toSafeJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("id", id);
        obj.put("screenName", screenName);
        obj.put("profileImageURL", profileImageURL);
        return obj;
    }

    public ObjectNode toJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("id", id);
        obj.put("screenName", screenName);
        obj.put("profileImageURL", profileImageURL);
        obj.put("createdAt", createdAt.getTime());
        if (modifiedAt != null)
            obj.put("modifiedAt", modifiedAt.getTime());
        return obj;
    }

    // ----------------------------------------------------------------------
    // equal methods

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) { return false; }

        User lhs = this;
        User rhs = (User) obj;

        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.screenName, rhs.screenName)) { return false; }
        if (!ObjectUtils.equals(lhs.profileImageURL, rhs.profileImageURL)) { return false; }
        if (!ObjectUtils.equals(lhs.createdAt, rhs.createdAt)) { return false; }
        if (!ObjectUtils.equals(lhs.modifiedAt, rhs.modifiedAt)) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(id);
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setScreenName(String screenName) {
        checkFrozen();
        this.screenName = screenName;
    }

    public void setProfileImageURL(String profileImageURL) {
        checkFrozen();
        this.profileImageURL = profileImageURL;
    }

    public void setCreatedAt(DateTime createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
    }

    public void setModifiedAt(DateTime modifiedAt) {
        checkFrozen();
        this.modifiedAt = modifiedAt;
    }
}
