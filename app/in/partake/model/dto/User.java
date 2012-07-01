package in.partake.model.dto;

import in.partake.base.DateTime;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

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

    public User(JSONObject obj) {
        this.id = obj.getString("id");
        this.screenName = obj.getString("screenName");
        this.profileImageURL = obj.getString("profileImageURL");
        this.createdAt = new DateTime(obj.getLong("createdAt"));
        if (obj.containsKey("modifiedAt"))
            this.modifiedAt = new DateTime(obj.getLong("modifiedAt"));
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
    public JSONObject toSafeJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("screenName", screenName);
        obj.put("profileImageURL", profileImageURL);
        return obj;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
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
