package in.partake.model.dto;

import in.partake.base.DateTime;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;


public class EventActivity extends PartakeModel<EventActivity> {
    private String id;
    private String eventId;
    private String title;
    private String content;
    private DateTime createdAt;

    public EventActivity() {
    }

    public EventActivity(String id, String eventId, String title, String content, DateTime createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public EventActivity(EventActivity eventActivity) {
        this(eventActivity.id, eventActivity.eventId, eventActivity.title, eventActivity.content, eventActivity.createdAt);
    }

    public EventActivity(JSONObject obj) {
        this.id = obj.getString("id");
        this.eventId = obj.getString("eventId");
        this.title = obj.getString("title");
        this.content = obj.getString("content");
        if (obj.containsKey("createdAt"))
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
        obj.put("eventId", eventId);
        obj.put("title", title);
        obj.put("content", content);
        if (createdAt != null)
            obj.put("createdAt", createdAt.getTime());
        return obj;
    }

    // ----------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventActivity)) { return false; }

        EventActivity lhs = this;
        EventActivity rhs = (EventActivity) obj;

        if (!ObjectUtils.equals(lhs.id,        rhs.id))        { return false; }
        if (!ObjectUtils.equals(lhs.eventId,   rhs.eventId))   { return false; }
        if (!ObjectUtils.equals(lhs.title,     rhs.title))     { return false; }
        if (!ObjectUtils.equals(lhs.content,   rhs.content))   { return false; }
        if (!ObjectUtils.equals(lhs.createdAt, rhs.createdAt)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(title);
        code = code * 37 + ObjectUtils.hashCode(content);
        code = code * 37 + ObjectUtils.hashCode(createdAt);

        return code;
    }

    // ----------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setTitle(String title) {
        checkFrozen();
        this.title = title;
    }

    public void setContent(String content) {
        checkFrozen();
        this.content = content;
    }

    public void setCreatedAt(DateTime createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
    }
}
