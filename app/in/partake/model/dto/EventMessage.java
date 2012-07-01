package in.partake.model.dto;

import in.partake.base.DateTime;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

/**
 * Message corresponding to an event.
 * @author shinyak
 *
 */
public class EventMessage extends PartakeModel<EventMessage> {
    private String id;
    private String eventId;
    private String senderId;
    private String messageId;
    private DateTime createdAt;
    private DateTime modifiedAt;

    public EventMessage() {
        // do nothing
    }

    public EventMessage(EventMessage message) {
        this(message.id, message.eventId, message.senderId, message.messageId, message.createdAt, message.modifiedAt);
    }

    public EventMessage(String id, String eventId, String senderId, String messageId, DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.eventId = eventId;
        this.senderId = senderId;
        this.messageId = messageId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public EventMessage(JSONObject obj) {
        this.id = obj.getString("id");
        this.eventId = obj.getString("eventId");
        this.senderId = obj.getString("senderId");
        this.messageId = obj.getString("messageId");
        this.createdAt = new DateTime(obj.getLong("createdAt"));
        if (obj.containsKey("modifiedAt"))
            this.modifiedAt = new DateTime(obj.getLong("modifiedAt"));
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("eventId", eventId);
        obj.put("senderId", senderId);
        obj.put("messageId", messageId);
        if (createdAt != null)
            obj.put("createdAt", createdAt.getTime());
        if (modifiedAt != null)
            obj.put("modifiedAt", modifiedAt.getTime());
        return obj;
    }

    // ----------------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventMessage)) { return false; }

        EventMessage lhs = this;
        EventMessage rhs = (EventMessage) obj;

        if (!(ObjectUtils.equals(lhs.id,         rhs.id)))         { return false; }
        if (!(ObjectUtils.equals(lhs.eventId,    rhs.eventId)))    { return false; }
        if (!(ObjectUtils.equals(lhs.senderId,   rhs.senderId)))   { return false; }
        if (!(ObjectUtils.equals(lhs.messageId,  rhs.messageId)))  { return false; }
        if (!(ObjectUtils.equals(lhs.createdAt,  rhs.createdAt)))  { return false; }
        if (!(ObjectUtils.equals(lhs.modifiedAt, rhs.modifiedAt))) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(senderId);
        code = code * 37 + ObjectUtils.hashCode(messageId);
        code = code * 37 + ObjectUtils.hashCode(createdAt);
        code = code * 37 + ObjectUtils.hashCode(modifiedAt);

        return code;
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessageId() {
        return messageId;
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

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setSenderId(String senderId) {
        checkFrozen();
        this.senderId = senderId;
    }

    public void setBody(String messageId) {
        checkFrozen();
        this.messageId = messageId;
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

