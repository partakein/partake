package in.partake.model.dto;

import in.partake.base.DateTime;
import in.partake.model.dto.auxiliary.MessageDelivery;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

public class TwitterMessage extends PartakeModel<TwitterMessage> {
    private String id;
    private String userId;
    private String message;
    private MessageDelivery delivery;
    private DateTime createdAt;
    private DateTime modifiedAt;

    public TwitterMessage(TwitterMessage message) {
        this(message.id, message.userId, message.message, message.delivery, message.createdAt, message.modifiedAt);
    }

    public TwitterMessage(String id, String userId, String message, MessageDelivery delivery, DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.delivery = delivery;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public TwitterMessage(JSONObject obj) {
        this.id = obj.getString("id");
        this.userId = obj.optString("userId", null);
        this.message = obj.getString("message");
        this.delivery = MessageDelivery.safeValueOf(obj.getString("delivery"));
        if (obj.containsKey("createdAt"))
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
        obj.put("userId", userId);
        obj.put("message", message);
        obj.put("delivery", delivery);
        obj.put("createdAt", createdAt.getTime());
        if (modifiedAt != null)
            obj.put("modifiedAt", modifiedAt.getTime());
        return obj;
    }

    // ----------------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TwitterMessage)) { return false; }

        TwitterMessage lhs = this;
        TwitterMessage rhs = (TwitterMessage) obj;

        if (!(ObjectUtils.equals(lhs.id,        rhs.id)))        { return false; }
        if (!(ObjectUtils.equals(lhs.userId,    rhs.userId)))    { return false; }
        if (!(ObjectUtils.equals(lhs.message,   rhs.message)))   { return false; }
        if (!(ObjectUtils.equals(lhs.delivery,  rhs.delivery)))  { return false; }
        if (!(ObjectUtils.equals(lhs.createdAt, rhs.createdAt))) { return false; }
        if (!(ObjectUtils.equals(lhs.modifiedAt, rhs.modifiedAt))) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(userId);
        code = code * 37 + ObjectUtils.hashCode(message);
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

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
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

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }

    public void setMessage(String message) {
        checkFrozen();
        this.message = message;
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

