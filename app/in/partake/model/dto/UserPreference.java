package in.partake.model.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

@Entity(name = "UserPreferences")
public class UserPreference extends PartakeModel<UserPreference> {
    @Id
    private String  userId;
    @Column
    private boolean profilePublic;
    @Column
    private boolean receivingTwitterMessage;
    @Column
    private boolean tweetingAttendanceAutomatically;

    public static UserPreference getDefaultPreference(String userId) {
        return new UserPreference(userId, true, true, false);
    }

    public UserPreference(String userId, boolean profilePublic, boolean receivingTwitterMessage, boolean tweetingAttendanceAutomatically) {
        this.userId = userId;
        this.profilePublic = profilePublic;
        this.receivingTwitterMessage = receivingTwitterMessage;
        this.tweetingAttendanceAutomatically = tweetingAttendanceAutomatically;
    }

    public UserPreference(UserPreference pref) {
        this.userId = pref.userId;
        this.profilePublic = pref.profilePublic;
        this.receivingTwitterMessage = pref.receivingTwitterMessage;
        this.tweetingAttendanceAutomatically = pref.tweetingAttendanceAutomatically;
    }

    public UserPreference(ObjectNode obj) {
        this.userId = obj.get("id").asText();
        this.profilePublic = obj.get("profilePublic").asBoolean();
        this.receivingTwitterMessage = obj.get("receivingTwitterMessage").asBoolean();
        this.tweetingAttendanceAutomatically = obj.get("tweetingAttendanceAutomatically").asBoolean();
    }

    @Override
    public Object getPrimaryKey() {
        return userId;
    }

    public ObjectNode toSafeJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("profilePublic", profilePublic);
        obj.put("receivingTwitterMessage", receivingTwitterMessage);
        obj.put("tweetingAttendanceAutomatically", tweetingAttendanceAutomatically);

        return obj;
    }

    public ObjectNode toJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("id", userId);
        obj.put("profilePublic", profilePublic);
        obj.put("receivingTwitterMessage", receivingTwitterMessage);
        obj.put("tweetingAttendanceAutomatically", tweetingAttendanceAutomatically);
        return obj;
    }

    // ---------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserPreference)) { return false; }

        UserPreference lhs = this;
        UserPreference rhs = (UserPreference) obj;

        if (!ObjectUtils.equals(lhs.userId, rhs.userId)) { return false; }
        if (!ObjectUtils.equals(lhs.profilePublic, rhs.profilePublic)) { return false; }
        if (!ObjectUtils.equals(lhs.receivingTwitterMessage, rhs.receivingTwitterMessage)) { return false; }
        if (!ObjectUtils.equals(lhs.tweetingAttendanceAutomatically, rhs.tweetingAttendanceAutomatically)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(userId);
        code = code * 37 + ObjectUtils.hashCode(profilePublic);
        code = code * 37 + ObjectUtils.hashCode(receivingTwitterMessage);
        code = code * 37 + ObjectUtils.hashCode(tweetingAttendanceAutomatically);

        return code;
    }

    // ---------------------------------------------------------------
    // accessors

    public String getUserId() {
        return userId;
    }

    public boolean isProfilePublic() {
        return profilePublic;
    }

    public boolean isReceivingTwitterMessage() {
        return receivingTwitterMessage;
    }

    public boolean tweetsAttendanceAutomatically() {
        return tweetingAttendanceAutomatically;
    }

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }

    public void setTweetingAttendanceAutomatically(boolean tweetingAttendanceAutomatically) {
        checkFrozen();
        this.tweetingAttendanceAutomatically = tweetingAttendanceAutomatically;
    }

    public void setProfilePublic(boolean profilePublic) {
        checkFrozen();
        this.profilePublic = profilePublic;
    }

    public void setReceivingTwitterMessage(boolean receivingTwitterMessage) {
        checkFrozen();
        this.receivingTwitterMessage = receivingTwitterMessage;
    }




}
