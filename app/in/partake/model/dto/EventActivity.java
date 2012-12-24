package in.partake.model.dto;

import in.partake.base.DateTime;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;


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

    public EventActivity(ObjectNode obj) {
        this.id = obj.get("id").asText();
        this.eventId = obj.get("eventId").asText();
        this.title = obj.get("title").asText();
        this.content = obj.get("content").asText();
        if (obj.has("createdAt"))
            this.createdAt = new DateTime(obj.get("createdAt").asLong());
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public ObjectNode toJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
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
