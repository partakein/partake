package in.partake.model.dto;

import in.partake.base.DateTime;
import in.partake.base.Util;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

public class UserTicket extends PartakeModel<UserTicket> {
    private String id;
    private String userId;
    private UUID ticketId;
    private String eventId; // Note: this is a bit redundant, but this will reduce DB access a lot.
    private String comment;
    private ParticipationStatus status;
    private ModificationStatus modificationStatus;
    private AttendanceStatus attendanceStatus;
    private Map<UUID, List<String>> enqueteAnswers;
    private DateTime appliedAt;
    private DateTime createdAt;
    private DateTime modifiedAt;

    // ----------------------------------------------------------------------
    // constructors

    public UserTicket(String id, String userId, UUID ticketId, String eventId, String comment,
            ParticipationStatus status, ModificationStatus modificationStatus, AttendanceStatus attendanceStatus,
            Map<UUID, List<String>> enqueteAnswers,
            DateTime appliedAt, DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.userId = userId;
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.comment = comment;
        this.status = status;
        this.modificationStatus = modificationStatus;
        this.attendanceStatus = attendanceStatus;
        this.enqueteAnswers = enqueteAnswers;
        this.appliedAt = appliedAt;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public UserTicket(UserTicket p) {
        this(p.id, p.userId, p.ticketId, p.eventId, p.comment, p.status, p.modificationStatus, p.attendanceStatus, p.enqueteAnswers, p.appliedAt, p.createdAt, p.modifiedAt);
    }

    public UserTicket(JSONObject obj) {
        this.id = obj.getString("id");
        this.userId = obj.getString("userId");
        this.ticketId = UUID.fromString(obj.getString("ticketId"));
        this.eventId = obj.getString("eventId");
        this.comment = obj.getString("comment");
        this.status = ParticipationStatus.safeValueOf(obj.getString("status"));
        this.modificationStatus = ModificationStatus.safeValueOf(obj.getString("modificationStatus"));
        this.attendanceStatus = AttendanceStatus.safeValueOf(obj.getString("attendanceStatus"));
        if (obj.containsKey("enqueteAnswers")) {
            JSONObject map = obj.getJSONObject("enqueteAnswers");
            this.enqueteAnswers = Util.parseEnqueteAnswers(map);
        }
        this.appliedAt = new DateTime(obj.getLong("appliedAt"));
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
        obj.put("ticketId", ticketId.toString());
        obj.put("eventId", eventId);
        obj.put("comment", comment);
        obj.put("status", status.toString());
        obj.put("modificationStatus", modificationStatus.toString());
        obj.put("attendanceStatus", attendanceStatus.toString());
        if (enqueteAnswers != null && !enqueteAnswers.isEmpty()) {
            JSONObject enqueteAnswers = new JSONObject();
            for (Map.Entry<UUID, List<String>> entry : this.enqueteAnswers.entrySet())
                enqueteAnswers.put(entry.getKey().toString(), entry.getValue());
            obj.put("enqueteAnswers", enqueteAnswers);
        }
        obj.put("appliedAt", appliedAt.getTime());
        obj.put("createdAt", createdAt.getTime());
        if (modifiedAt != null)
            obj.put("modifiedAt", modifiedAt.getTime());
        return obj;
    }

    // ----------------------------------------------------------------------
    // equals method

    public boolean equals(Object obj) {
        if (!(obj instanceof UserTicket)) { return false; }

        UserTicket lhs = this;
        UserTicket rhs = (UserTicket) obj;

        if (!ObjectUtils.equals(lhs.id,                 rhs.id))                 { return false; }
        if (!ObjectUtils.equals(lhs.userId,             rhs.userId))             { return false; }
        if (!ObjectUtils.equals(lhs.ticketId,           rhs.ticketId))           { return false; }
        if (!ObjectUtils.equals(lhs.eventId,            rhs.eventId))            { return false; }
        if (!ObjectUtils.equals(lhs.comment,            rhs.comment))            { return false; }
        if (!ObjectUtils.equals(lhs.status,             rhs.status))             { return false; }
        if (!ObjectUtils.equals(lhs.modificationStatus, rhs.modificationStatus)) { return false; }
        if (!ObjectUtils.equals(lhs.attendanceStatus,   rhs.attendanceStatus))   { return false; }
        if (!ObjectUtils.equals(lhs.enqueteAnswers,     rhs.enqueteAnswers))     { return false; }
        if (!ObjectUtils.equals(lhs.appliedAt,          rhs.appliedAt))          { return false; }
        if (!ObjectUtils.equals(lhs.createdAt,          rhs.createdAt))          { return false; }
        if (!ObjectUtils.equals(lhs.modifiedAt,         rhs.modifiedAt))         { return false; }

        return true;
    }

    public int hashCode() {
        int hashCode = 0;

        hashCode = hashCode * 37 + ObjectUtils.hashCode(id);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(userId);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(ticketId);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(eventId);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(comment);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(status);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(modificationStatus);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(attendanceStatus);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(enqueteAnswers);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(appliedAt);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(createdAt);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(modifiedAt);

        return hashCode;
    }

    // ----------------------------------------------------------------------
    //

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getComment() {
        return comment;
    }

    public ParticipationStatus getStatus() {
        return status;
    }

    /**
     * 前回チェック時のステータス。ここは、 ENROLLED, NOT_ENROLLED のいずれかでなければならない。
     * 変更時に、この値が ENROLLED -> NOT_ENROLLED もしくあｈ NOT_ENROLLED -> ENROLLED になっていれば、
     * DM によって通知を出す。
     * @return
     */
    public ModificationStatus getModificationStatus() {
        return modificationStatus;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }

    public Map<UUID, List<String>> getEnqueteAnswers() {
        return enqueteAnswers;
    }

    public DateTime getAppliedAt() {
        return appliedAt;
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

    public void setTicketId(UUID ticketId) {
        checkFrozen();
        this.ticketId = ticketId;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setComment(String comment) {
        checkFrozen();
        this.comment = comment;
    }

    public void setStatus(ParticipationStatus status) {
        checkFrozen();
        this.status = status;
    }

    public void setModificationStatus(ModificationStatus lastStatus) {
        checkFrozen();
        this.modificationStatus = lastStatus;
    }

    public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
        checkFrozen();
        this.attendanceStatus = attendanceStatus;
    }

    public void setEnqueteAnswers(Map<UUID, List<String>> enqueteAnswers) {
        checkFrozen();
        this.enqueteAnswers = enqueteAnswers;
    }

    public void setAppliedAt(DateTime appliedAt) {
        checkFrozen();
        this.appliedAt = appliedAt;
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
