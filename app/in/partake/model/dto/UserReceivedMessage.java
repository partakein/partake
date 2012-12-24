package in.partake.model.dto;

import in.partake.base.DateTime;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.resource.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

/**
 * @author shinyak
 *
 */
public class UserReceivedMessage extends PartakeModel<UserReceivedMessage> {
    private UUID id;
    private String senderId;
    private String receiverId;
    private String eventId;
    private String messageId;

    private boolean opened;
    private MessageDelivery delivery;
    private DateTime openedAt;
    private DateTime deliveredAt;
    private DateTime createdAt;
    private DateTime modifiedAt;

    public UserReceivedMessage(UserReceivedMessage message) {
        this(message.id, message.senderId, message.receiverId, message.eventId, message.messageId,
                message.opened, message.delivery, message.openedAt, message.deliveredAt,
                message.createdAt, message.modifiedAt);
    }

    public UserReceivedMessage(UUID id, String senderId, String receiverId, String eventId, String messageId,
            boolean opened, MessageDelivery delivery, DateTime openedAt, DateTime deliveredAt,
            DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.eventId = eventId;
        this.messageId = messageId;
        this.opened = opened;
        this.delivery = delivery;
        this.openedAt = openedAt;
        this.deliveredAt = deliveredAt;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public UserReceivedMessage(ObjectNode obj) {
        this.id = UUID.fromString(obj.get("id").asText());
        this.senderId = obj.get("senderId").asText();
        this.receiverId = obj.get("receiverId").asText();
        this.eventId = obj.get("eventId").asText();
        this.messageId = obj.get("messageId").asText();
        this.opened = obj.get("opened").asBoolean();
        this.delivery = MessageDelivery.safeValueOf(obj.get("delivery").asText());

        if (obj.has("openedAt"))
            this.openedAt = new DateTime(obj.get("openedAt").asLong());
        if (obj.has("deliveredAt"))
            this.deliveredAt = new DateTime(obj.get("deliveredAt").asLong());

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
        obj.put("receiverId", receiverId);
        if (eventId != null)
            obj.put("eventId", eventId);
        obj.put("messageId", messageId);
        obj.put("opened", opened);
        obj.put("delivery", delivery.toString());

        if (openedAt != null)
            obj.put("openedAt", openedAt.getTime());
        if (deliveredAt != null)
            obj.put("deliveredAt", deliveredAt.getTime());

        obj.put("createdAt", createdAt.getTime());
        if (modifiedAt != null)
            obj.put("modifiedAt", modifiedAt.getTime());
        return obj;
    }

    public ObjectNode toSafeJSON() {
        DateFormat format = new SimpleDateFormat(Constants.READABLE_DATE_FORMAT, Locale.getDefault());

        ObjectNode obj = toJSON();
        if (this.deliveredAt != null) {
            obj.put("deliveredAtText", format.format(deliveredAt.toDate()));
            obj.put("deliveredAtTime", format.format(deliveredAt.getTime()));
        }

        return obj;
    }

    // ----------------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserReceivedMessage)) { return false; }

        UserReceivedMessage lhs = this;
        UserReceivedMessage rhs = (UserReceivedMessage) obj;

        if (!(ObjectUtils.equals(lhs.id,          rhs.id)))          { return false; }
        if (!(ObjectUtils.equals(lhs.senderId,    rhs.senderId)))    { return false; }
        if (!(ObjectUtils.equals(lhs.receiverId,  rhs.receiverId)))  { return false; }
        if (!(ObjectUtils.equals(lhs.eventId,     rhs.eventId)))     { return false; }
        if (!(ObjectUtils.equals(lhs.messageId,   rhs.messageId)))   { return false; }
        if (!(ObjectUtils.equals(lhs.delivery,    rhs.delivery)))    { return false; }
        if (!(ObjectUtils.equals(lhs.opened,      rhs.opened)))      { return false; }
        if (!(ObjectUtils.equals(lhs.openedAt,    rhs.openedAt)))    { return false; }
        if (!(ObjectUtils.equals(lhs.deliveredAt, rhs.deliveredAt))) { return false; }
        if (!(ObjectUtils.equals(lhs.createdAt,   rhs.createdAt)))   { return false; }
        if (!(ObjectUtils.equals(lhs.modifiedAt,  rhs.modifiedAt)))  { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(senderId);
        code = code * 37 + ObjectUtils.hashCode(receiverId);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(messageId);
        code = code * 37 + ObjectUtils.hashCode(delivery);
        code = code * 37 + ObjectUtils.hashCode(opened);
        code = code * 37 + ObjectUtils.hashCode(openedAt);
        code = code * 37 + ObjectUtils.hashCode(deliveredAt);
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

    public String getReceiverId() {
        return receiverId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getMessageId() {
        return messageId;
    }

    public boolean isOpened() {
        return opened;
    }

    public MessageDelivery getDelivery() {
        return delivery;
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

    public void setReceiverId(String receiverId) {
        checkFrozen();
        this.receiverId = receiverId;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setMessageId(String messageId) {
        checkFrozen();
        this.messageId = messageId;
    }

    public void setOpened(boolean opened) {
        checkFrozen();
        this.opened = opened;
    }

    public void setDelivery(MessageDelivery delivery) {
        checkFrozen();
        this.delivery = delivery;
    }

    public void setOpenedAt(DateTime openedAt) {
        checkFrozen();
        this.openedAt = openedAt;
    }

    public void setDeliveredAt(DateTime deliveredAt) {
        checkFrozen();
        this.deliveredAt = deliveredAt;
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

