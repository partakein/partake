package in.partake.model.dto;

import in.partake.base.DateTime;

import java.util.Arrays;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Strings;

public class UserImage extends PartakeModel<UserImage> {
    private String id;
    private String userId;
    private String type;
    private byte[] data;
    private DateTime createdAt;

    public UserImage() {
        this(null, null, null, null, null);
    }

    public UserImage(String id, String userId, String type, byte[] data, DateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.data = data;
        this.createdAt = createdAt;
    }

    public UserImage(UserImage src) {
        this.id = src.id;
        this.userId = src.userId;
        this.type = src.type;
        this.data = src.data;
        this.createdAt = src.createdAt;
    }

    public UserImage(ObjectNode obj) {
        this.id = obj.get("id").asText();
        this.userId = Strings.emptyToNull(obj.path("userId").asText());
        this.type = obj.get("type").asText();
        if (obj.has("createdAt"))
            this.createdAt = new DateTime(obj.get("createdAt").asLong());

        // We don't create data from JSONObject.
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    // ----------------------------------------------------------------------
    // equals / hashCode

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserImage)) { return false; }

        UserImage lhs = this;
        UserImage rhs = (UserImage) obj;

        if (!ObjectUtils.equals(lhs.id, rhs.id))         { return false; }
        if (!ObjectUtils.equals(lhs.userId, rhs.userId)) { return false; }
        if (!ObjectUtils.equals(lhs.type, rhs.type))     { return false; }
        if (!Arrays.equals(lhs.data, rhs.data))          { return false; }
        if (!ObjectUtils.equals(lhs.createdAt, rhs.createdAt)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;
        code = code * 37 + ObjectUtils.hashCode(id);
        return code;
    }

    @Override
    public ObjectNode toJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("id", id);
        obj.put("userId", userId);
        obj.put("type", type);
        if (createdAt != null)
            obj.put("createdAt", createdAt.getTime());

        return obj;
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }

    public void setType(String type) {
        checkFrozen();
        this.type = type;
    }

    public void setData(byte[] data) {
        checkFrozen();
        this.data = data;
    }

    public void setCreatedAt(DateTime createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
    }
}
