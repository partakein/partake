package in.partake.model.dto;

import in.partake.base.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Strings;

/**
 * @author shinyak
 *
 */
public class UserSentMessage extends PartakeModel<UserSentMessage> {
    private UUID id;
    private String senderId;
    private List<String> receiverIds;
    private String eventId;
    private String messageId;
    private DateTime createdAt;
    private DateTime modifiedAt;

    public UserSentMessage(UserSentMessage message) {
        this(message.id, message.senderId, message.receiverIds,
                message.eventId, message.messageId,
                message.createdAt, message.modifiedAt);
    }

    public UserSentMessage(UUID id, String senderId, List<String> receiverIds, String eventId, String messageId,
            DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverIds = new ArrayList<String>(receiverIds);
        this.eventId = eventId;
        this.messageId = messageId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public UserSentMessage(ObjectNode obj) {
        this.id = UUID.fromString(obj.get("id").asText());
        this.senderId = obj.get("senderId").asText();
        this.receiverIds = new ArrayList<String>();
        JsonNode array = obj.get("receiverIds");
        if (array != null) {
            for (int i = 0; i < array.size(); ++i)
                this.receiverIds.add(array.get(i).asText());
        }
        this.eventId = Strings.emptyToNull(obj.path("eventId").asText());
        this.messageId = obj.get("messageId").asText();

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
        obj.put("senderId", senderId);

        ArrayNode array = obj.putArray("receiverIds");
        for (String receiverId : receiverIds)
            array.add(receiverId);

        if (eventId != null)
            obj.put("eventId", eventId);
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
        if (!(obj instanceof UserSentMessage)) { return false; }

        UserSentMessage lhs = this;
        UserSentMessage rhs = (UserSentMessage) obj;

        if (!(ObjectUtils.equals(lhs.id,          rhs.id)))          { return false; }
        if (!(ObjectUtils.equals(lhs.senderId,    rhs.senderId)))    { return false; }
        if (!(ObjectUtils.equals(lhs.receiverIds, rhs.receiverIds))) { return false; }
        if (!(ObjectUtils.equals(lhs.eventId,     rhs.eventId)))     { return false; }
        if (!(ObjectUtils.equals(lhs.messageId,   rhs.messageId)))   { return false; }
        if (!(ObjectUtils.equals(lhs.createdAt,   rhs.createdAt)))   { return false; }
        if (!(ObjectUtils.equals(lhs.modifiedAt,  rhs.modifiedAt)))  { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(senderId);
        code = code * 37 + ObjectUtils.hashCode(receiverIds);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(messageId);
        code = code * 37 + ObjectUtils.hashCode(createdAt);
        code = code * 37 + ObjectUtils.hashCode(modifiedAt);

        return code;
    }

    // ----------------------------------------------------------------------
    // accessors

    public UUID getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public List<String> getReceiverIds() {
        return receiverIds;
    }

    public String getEventId() {
        return eventId;
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

    public void setId(UUID id) {
        checkFrozen();
        this.id = id;
    }

    public void setSenderId(String senderId) {
        checkFrozen();
        this.senderId = senderId;
    }

    public void setReceiverIds(List<String> receiverIds) {
        checkFrozen();
        if (receiverIds == null)
            this.receiverIds = null;
        else
            this.receiverIds = new ArrayList<String>(receiverIds);
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setMessageId(String messageId) {
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

