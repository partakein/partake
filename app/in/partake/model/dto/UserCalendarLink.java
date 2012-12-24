package in.partake.model.dto;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

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

    public UserCalendarLink(ObjectNode obj) {
        this.id = obj.get("id").asText();
        this.userId = obj.get("userId").asText();
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    public ObjectNode toJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
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
