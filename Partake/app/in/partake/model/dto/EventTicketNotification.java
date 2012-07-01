package in.partake.model.dto;

import in.partake.base.DateTime;
import in.partake.base.JSONable;
import in.partake.model.dto.auxiliary.NotificationType;

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
// TODO: Should use MessageCode to convert EventNotification to Text.
public class EventTicketNotification extends PartakeModel<EventTicketNotification> implements JSONable {
    private String id;
    private UUID ticketId;
    private String eventId;
    private List<String> userIds;
    private NotificationType notificationType;

    private DateTime createdAt;

    public EventTicketNotification(EventTicketNotification message) {
        this(message.id, message.ticketId, message.eventId, message.userIds, message.notificationType, message.createdAt);
    }

    public EventTicketNotification(String id, UUID ticketId, String eventId, List<String> userIds, NotificationType notificationType,  DateTime createdAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.eventId = eventId;
        if (userIds != null)
            this.userIds = new ArrayList<String>(userIds);
        this.notificationType = notificationType;
        this.createdAt = createdAt;
    }

    public EventTicketNotification(JSONObject obj) {
        this.id = obj.getString("id");
        this.ticketId = UUID.fromString(obj.getString("ticketId"));
        this.eventId = obj.getString("eventId");

        this.userIds = new ArrayList<String>();
        if (userIds != null) {
            JSONArray array = obj.getJSONArray("userIds");
            for (int i = 0; i < array.size(); ++i)
                userIds.add(array.getString(i));
        }

        this.notificationType = NotificationType.safeValueOf(obj.getString("notificationType"));
        this.createdAt = new DateTime(obj.getLong("createdAt"));
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
        obj.put("eventId", eventId);

        JSONArray array = new JSONArray();
        for (String userId : userIds)
            array.add(userId);
        obj.put("userIds", userIds);

        obj.put("notificationType", notificationType.toString());
        if (createdAt != null)
            obj.put("createdAt", createdAt.getTime());
        return obj;
    }

    // ----------------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventTicketNotification)) { return false; }

        EventTicketNotification lhs = this;
        EventTicketNotification rhs = (EventTicketNotification) obj;

        if (!(ObjectUtils.equals(lhs.id,         rhs.id)))         { return false; }
        if (!(ObjectUtils.equals(lhs.ticketId,   rhs.ticketId)))   { return false; }
        if (!(ObjectUtils.equals(lhs.eventId,    rhs.eventId)))   { return false; }
        if (!(ObjectUtils.equals(lhs.userIds,    rhs.userIds)))    { return false; }
        if (!(ObjectUtils.equals(lhs.notificationType,   rhs.notificationType)))   { return false; }
        if (!(ObjectUtils.equals(lhs.createdAt,  rhs.createdAt)))  { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(ticketId);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(userIds);
        code = code * 37 + ObjectUtils.hashCode(notificationType);
        code = code * 37 + ObjectUtils.hashCode(createdAt);

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

    public String getEventId() {
        return eventId;
    }

    public List<String> getUserIds() {
        return this.userIds;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setTicketId(UUID ticketId) {
        checkFrozen();
        this.ticketId = ticketId;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setUserIds(List<String> userIds) {
        checkFrozen();
        if (userIds == null)
            this.userIds = null;
        else
            this.userIds = new ArrayList<String>(userIds);
    }

    public void setNotificationType(NotificationType notificationType) {
        checkFrozen();
        this.notificationType = notificationType;
    }

    public void setCreatedAt(DateTime createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
    }
}

