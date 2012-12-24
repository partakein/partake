package in.partake.model.dto;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Strings;

/**
 * Message を、「他に人に送る」ことを表現するクラス。
 *
 * @author shinyak
 *
 */

public class MessageEnvelope extends PartakeModel<MessageEnvelope> {
    private String id;

    // Either of these messageId is set.
    private String userMessageId;
    private String twitterMessageId;
    private String userNotificationId;

    private int numTried;

    private DateTime lastTriedAt;
    private DateTime invalidAfter;
    private DateTime tryAfter;

    private DateTime createdAt;
    private DateTime modifiedAt;

    public static MessageEnvelope createForUserMessage(String id, String userMessageId, DateTime invalidAfter) {
        MessageEnvelope envelope = new MessageEnvelope();
        envelope.id = id;
        envelope.userMessageId = userMessageId;
        envelope.numTried = 0;
        envelope.invalidAfter = invalidAfter;
        envelope.createdAt = TimeUtil.getCurrentDateTime();

        return envelope;
    }

    public static MessageEnvelope createForTwitterMessage(String id, String twitterMessageId, DateTime invalidAfter) {
        MessageEnvelope envelope = new MessageEnvelope();
        envelope.id = id;
        envelope.twitterMessageId = twitterMessageId;
        envelope.numTried = 0;
        envelope.invalidAfter = invalidAfter;
        envelope.createdAt = TimeUtil.getCurrentDateTime();

        return envelope;
    }

    public static MessageEnvelope createForUserNotification(String id, String userNotificationId, DateTime invalidAfter) {
        MessageEnvelope envelope = new MessageEnvelope();
        envelope.id = id;
        envelope.userNotificationId = userNotificationId;
        envelope.numTried = 0;
        envelope.invalidAfter = invalidAfter;
        envelope.createdAt = TimeUtil.getCurrentDateTime();

        return envelope;
    }

    private MessageEnvelope() {
    }

    public MessageEnvelope(String id, String userMessageId, String twitterMessageId, String userNotificationId,
            int numTried, DateTime lastTriedAt, DateTime invalidAfter, DateTime tryAfter, DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.userMessageId = userMessageId;
        this.twitterMessageId = twitterMessageId;
        this.userNotificationId = userNotificationId;
        this.numTried = numTried;
        this.lastTriedAt = lastTriedAt;
        this.invalidAfter = invalidAfter;
        this.tryAfter = tryAfter;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public MessageEnvelope(MessageEnvelope envelope) {
        this(envelope.id, envelope.userMessageId, envelope.twitterMessageId, envelope.userNotificationId,
                envelope.numTried, envelope.lastTriedAt, envelope.invalidAfter, envelope.tryAfter, envelope.createdAt, envelope.modifiedAt);
    }

    public MessageEnvelope(ObjectNode json) {
        this.id = json.get("id").asText();

        this.userMessageId = Strings.emptyToNull(json.path("userMessageId").asText());
        this.twitterMessageId = Strings.emptyToNull(json.path("twitterMessageId").asText());
        this.userNotificationId = Strings.emptyToNull(json.path("userNotificationId").asText());

        this.numTried = json.get("numTried").asInt();

        if (json.has("lastTriedAt"))
            this.lastTriedAt = new DateTime(json.get("lastTriedAt").asLong());
        if (json.has("invalidAfter"))
            this.invalidAfter = new DateTime(json.get("invalidAfter").asLong());
        if (json.has("tryAfter"))
            this.tryAfter = new DateTime(json.get("tryAfter").asLong());
        if (json.has("createdAt"))
            this.createdAt = new DateTime(json.get("createdAt").asLong());
        if (json.has("modifiedAt"))
            this.modifiedAt = new DateTime(json.get("modifiedAt").asLong());
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public ObjectNode toJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("id", id);

        if (userMessageId != null)
            obj.put("userMessageId", userMessageId);
        if (twitterMessageId != null)
            obj.put("twitterMessageId", twitterMessageId);
        if (userNotificationId != null)
            obj.put("userNotificationId", userNotificationId);

        obj.put("numTried", numTried);
        if (lastTriedAt != null)
            obj.put("lastTriedAt", lastTriedAt.getTime());
        if (invalidAfter != null)
            obj.put("invalidAfter", invalidAfter.getTime());
        if (tryAfter != null)
            obj.put("tryAfter", tryAfter.getTime());

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
        if (!(obj instanceof MessageEnvelope)) { return false; }

        MessageEnvelope lhs = this;
        MessageEnvelope rhs = (MessageEnvelope) obj;

        if (!ObjectUtils.equals(lhs.id,  rhs.id))  { return false; }

        if (!ObjectUtils.equals(lhs.userMessageId,   rhs.userMessageId))   { return false; }
        if (!ObjectUtils.equals(lhs.twitterMessageId,   rhs.twitterMessageId))   { return false; }
        if (!ObjectUtils.equals(lhs.userNotificationId,   rhs.userNotificationId))   { return false; }

        if (!ObjectUtils.equals(lhs.numTried,    rhs.numTried))    { return false; }

        if (!ObjectUtils.equals(lhs.lastTriedAt, rhs.lastTriedAt)) { return false; }
        if (!ObjectUtils.equals(lhs.invalidAfter,    rhs.invalidAfter))    { return false; }
        if (!ObjectUtils.equals(lhs.tryAfter,    rhs.tryAfter))    { return false; }

        if (!ObjectUtils.equals(lhs.createdAt,   rhs.createdAt))   { return false; }
        if (!ObjectUtils.equals(lhs.modifiedAt,   rhs.modifiedAt))   { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);

        code = code * 37 + ObjectUtils.hashCode(userMessageId);
        code = code * 37 + ObjectUtils.hashCode(twitterMessageId);
        code = code * 37 + ObjectUtils.hashCode(userNotificationId);

        code = code * 37 + ObjectUtils.hashCode(numTried);

        code = code * 37 + ObjectUtils.hashCode(lastTriedAt);
        code = code * 37 + ObjectUtils.hashCode(invalidAfter);
        code = code * 37 + ObjectUtils.hashCode(tryAfter);

        code = code * 37 + ObjectUtils.hashCode(createdAt);
        code = code * 37 + ObjectUtils.hashCode(modifiedAt);

        return code;
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() { return id; }

    public String getUserMessageId()  { return userMessageId; }
    public String getTwitterMessageId() { return twitterMessageId; }
    public String getUserNotificationId() { return userNotificationId; }

    public int getNumTried() { return numTried; }

    public DateTime getLastTriedAt() { return lastTriedAt; }
    public DateTime getInvalidAfter() { return invalidAfter; }
    public DateTime getTryAfter() { return tryAfter; }

    public DateTime getCreatedAt() { return createdAt; }
    public DateTime getModifiedAt() { return modifiedAt; }

    public void updateForSendingFailure(DateTime retryAfter) {
        checkFrozen();
        this.numTried += 1;
        this.lastTriedAt = TimeUtil.getCurrentDateTime();
        this.tryAfter = retryAfter;
    }
}
