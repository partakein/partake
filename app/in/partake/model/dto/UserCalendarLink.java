package in.partake.model.dto;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

public class UserCalendarLink extends PartakeModel<UserCalendarLink> {
    private String id;
    private String userId;

    public UserCalendarLink() {
        this(null, null);
    }

    public UserCalendarLink(String userId) {
        this(null, userId);
    }

    public UserCalendarLink(String id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    public UserCalendarLink(UserCalendarLink src) {
        this.id = src.id;
        this.userId = src.userId;
    }

    public UserCalendarLink(JSONObject obj) {
        this.id = obj.getString("id");
        this.userId = obj.getString("userId");
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("userId", userId);
        return obj;
    }

    // ----------------------------------------------------------------------
    // equals methods

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserCalendarLink)) { return false; }

        UserCalendarLink lhs = this;
        UserCalendarLink rhs = (UserCalendarLink) obj;

        return ObjectUtils.equals(lhs.id, rhs.id) && ObjectUtils.equals(lhs.userId, rhs.userId);
    }

    @Override
    public int hashCode() {
        int x = id == null ? 0 : id.hashCode();
        int y = userId == null ? 0 : id.hashCode();

        return x * 37 + y;
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }
}
