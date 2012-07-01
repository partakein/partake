package in.partake.model.dto;

import in.partake.base.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

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

    public UserSentMessage(JSONObject obj) {
        this.id = UUID.fromString(obj.getString("id"));
        this.senderId = obj.getString("senderId");
        this.receiverIds = new ArrayList<String>();
        JSONArray array = obj.getJSONArray("receiverIds");
        if (array != null) {
            for (int i = 0; i < array.size(); ++i)
                this.receiverIds.add(array.getString(i));
        }
        this.eventId = obj.optString("eventId", null);
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
        obj.put("id", id.toString());
        obj.put("senderId", senderId);

        JSONArray array = new JSONArray();
        for (String receiverId : receiverIds)
            array.add(receiverId);
        obj.put("receiverIds", array);

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

