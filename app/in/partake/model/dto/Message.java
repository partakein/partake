package in.partake.model.dto;

import in.partake.base.DateTime;

import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;


public class Message extends PartakeModel<Message> {
    private UUID id;

    private String subject;
    private String body;

    private DateTime createdAt;
    private DateTime modifiedAt;

    public Message(UUID id, String subject, String body, DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.subject = subject;
        this.body = body;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public Message(Message message) {
        this(message.id, message.subject, message.body, message.createdAt, message.modifiedAt);
    }

    public Message(ObjectNode obj) {
        this.id = UUID.fromString(obj.get("id").asText());
        this.subject = obj.get("subject").asText();
        this.body = obj.get("body").asText();
        this.createdAt = new DateTime(obj.get("createdAt").asLong());
        if (obj.has("modifiedAt"))
            this.modifiedAt = new DateTime(obj.get("modifiedAt").asLong());
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public ObjectNode toJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("id", id.toString());
        obj.put("subject", subject);
        obj.put("body", body);
        if (createdAt != null)
            obj.put("createdAt", createdAt.getTime());
        if (modifiedAt != null)
            obj.put("modifiedAt", modifiedAt.getTime());
        return obj;
    }

    public ObjectNode toSafeJSON() {
        // Safe to use JSON for now.
        return toJSON();
    }

    // ----------------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Message)) { return false; }

        Message lhs = this;
        Message rhs = (Message) obj;

        if (!(ObjectUtils.equals(lhs.id,         rhs.id)))         { return false; }
        if (!(ObjectUtils.equals(lhs.subject,    rhs.subject)))    { return false; }
        if (!(ObjectUtils.equals(lhs.body,       rhs.body)))       { return false; }
        if (!(ObjectUtils.equals(lhs.createdAt,  rhs.createdAt)))  { return false; }
        if (!(ObjectUtils.equals(lhs.modifiedAt, rhs.modifiedAt))) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(subject);
        code = code * 37 + ObjectUtils.hashCode(body);
        code = code * 37 + ObjectUtils.hashCode(createdAt);
        code = code * 37 + ObjectUtils.hashCode(modifiedAt);

        return code;
    }

    // ----------------------------------------------------------------------
    // accessors

    public UUID getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setId(UUID id) {
        checkFrozen();
        this.id = id;
    }

    public void setTitle(String title) {
        checkFrozen();
        this.subject = title;
    }

    public void setBody(String body) {
        checkFrozen();
        this.body = body;
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

