package in.partake.model.dto;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;


// TODO: Should be merged into Event...
public class EventFeed extends PartakeModel<EventFeed> {
    private String id;
    private String eventId;

    public EventFeed() {
        // default constructor.
    }

    public EventFeed(String id, String eventId) {
        this.id = id;
        this.eventId = eventId;
    }

    public EventFeed(EventFeed linkage) {
        this.id = linkage.id;
        this.eventId = linkage.eventId;
    }

    public EventFeed(JSONObject obj) {
        this.id = obj.getString("id");
        this.eventId = obj.getString("eventId");
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
        return obj;
    }

    // ----------------------------------------------------------------------
    // equal methods

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventFeed)) { return false; }

        EventFeed lhs = this;
        EventFeed rhs = (EventFeed) obj;

        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.eventId, rhs.eventId)) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(id);
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }
}
