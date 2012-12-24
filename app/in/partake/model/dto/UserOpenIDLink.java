package in.partake.model.dto;

import in.partake.base.JSONable;

import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class UserOpenIDLink extends PartakeModel<UserOpenIDLink> implements JSONable {
    private UUID id;
    private String userId;
    private String identifier;

    public UserOpenIDLink(UUID id, String userId, String identifier) {
        this.id = id;
        this.userId = userId;
        this.identifier = identifier;
    }

    public UserOpenIDLink(UserOpenIDLink linkage) {
        this(linkage.id, linkage.userId, linkage.identifier);
    }

    public UserOpenIDLink(ObjectNode obj) {
        this.id = UUID.fromString(obj.get("id").asText());
        this.userId = obj.get("userId").asText();
        this.identifier = obj.get("identifier").asText();
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public ObjectNode toJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("id", id.toString());
        obj.put("userId", userId);
        obj.put("identifier", identifier);

        return obj;
    }

    // -----------------------------------------------------------------------------
    //

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserOpenIDLink)) { return false; }

        UserOpenIDLink lhs = this;
        UserOpenIDLink rhs = (UserOpenIDLink) obj;

        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.userId, rhs.userId)) { return false; }
        if (!ObjectUtils.equals(lhs.identifier, rhs.identifier)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(userId);
        code = code * 37 + ObjectUtils.hashCode(identifier);

        return code;
    }

    // -----------------------------------------------------------------------------
    //

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setId(UUID id) {
        checkFrozen();
        this.id = id;
    }

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }

    public void setIdentifier(String identifier) {
        checkFrozen();
        this.identifier = identifier;
    }
}
