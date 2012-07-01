package in.partake.model.dto;

import java.util.UUID;

import in.partake.base.DateTime;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.model.dto.auxiliary.NotificationType;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

/**
 * @author shinyak
 *
 */
public class UserNotification extends PartakeModel<UserNotification> {
    private String id;
    private UUID ticketId;
    private String userId;
    private NotificationType notificationType;
    private MessageDelivery delivery;
    private DateTime createdAt;
    private DateTime modifiedAt;

    public UserNotification() {
        // do nothing
    }

    public UserNotification(UserNotification message) {
        this(message.id, message.ticketId, message.userId, message.notificationType, message.delivery, message.createdAt, message.modifiedAt);
    }

    public UserNotification(String id, UUID ticketId, String userId, NotificationType notificationType, MessageDelivery delivery, DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.userId = userId;
        this.notificationType = notificationType;
        this.delivery = delivery;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public UserNotification(JSONObject obj) {
        this.id = obj.getString("id");
        this.ticketId = UUID.fromString(obj.getString("ticketId"));
        this.userId = obj.getString("userId");
        this.notificationType = NotificationType.safeValueOf(obj.getString("notificationType"));
        this.delivery = MessageDelivery.safeValueOf(obj.getString("delivery"));
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
        obj.put("ticketId", ticketId.toString());
        obj.put("userId", userId);
        obj.put("notificationType", notificationType.toString());
        obj.put("delivery", delivery.toString());
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
        if (!(obj instanceof UserNotification)) { return false; }

        UserNotification lhs = this;
        UserNotification rhs = (UserNotification) obj;

        if (!(ObjectUtils.equals(lhs.id,         rhs.id)))         { return false; }
        if (!(ObjectUtils.equals(lhs.ticketId,   rhs.ticketId)))    { return false; }
        if (!(ObjectUtils.equals(lhs.userId,     rhs.userId)))     { return false; }
        if (!(ObjectUtils.equals(lhs.notificationType,   rhs.notificationType)))   { return false; }
        if (!(ObjectUtils.equals(lhs.delivery,   rhs.delivery)))   { return false; }
        if (!(ObjectUtils.equals(lhs.createdAt,  rhs.createdAt)))  { return false; }
        if (!(ObjectUtils.equals(lhs.modifiedAt, rhs.modifiedAt))) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(ticketId);
        code = code * 37 + ObjectUtils.hashCode(userId);
        code = code * 37 + ObjectUtils.hashCode(notificationType);
        code = code * 37 + ObjectUtils.hashCode(delivery);
        code = code * 37 + ObjectUtils.hashCode(createdAt);
        code = code * 37 + ObjectUtils.hashCode(modifiedAt);

        return code;
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
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

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setTicketId(UUID ticketId) {
        checkFrozen();
        this.ticketId = ticketId;
    }

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }

    public void setNotificationType(NotificationType notificationType) {
        checkFrozen();
        this.notificationType = notificationType;
    }

    public void setDelivery(MessageDelivery delivery) {
        checkFrozen();
        this.delivery = delivery;
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

