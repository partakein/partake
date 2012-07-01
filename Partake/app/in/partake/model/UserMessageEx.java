package in.partake.model;

import net.sf.json.JSONObject;
import in.partake.base.SafeJSONable;
import in.partake.model.dto.Event;
import in.partake.model.dto.Message;
import in.partake.model.dto.UserReceivedMessage;

public class UserMessageEx extends UserReceivedMessage implements SafeJSONable {
    private UserEx sender;
    private Event event;
    private Message message;

    public UserMessageEx(UserReceivedMessage userMessage, UserEx sender, Event event, Message message) {
        super(userMessage);
        this.sender = sender;
        this.event = event;
        this.message = message;
    }

    public UserEx getSender() {
        return sender;
    }

    public Event getEvent() {
        return event;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public JSONObject toSafeJSON() {
        JSONObject obj = super.toSafeJSON();

        // TODO: We don't need the detailed information of the event.
        obj.put("event", event.toSafeJSON());
        obj.put("sender", sender.toSafeJSON());
        obj.put("message", message.toSafeJSON());

        return obj;
    }
}
