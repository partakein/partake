package in.partake.model;

import in.partake.model.dto.EventMessage;
import in.partake.model.dto.Message;

public class EventMessageEx extends EventMessage {
    private UserEx sender;
    private Message message;

    public EventMessageEx(EventMessage eventMessage, UserEx sender, Message message) {
        super(eventMessage);
        this.sender = sender;
        this.message = message;
    }

    public UserEx getSender() {
        return sender;
    }

    public Message getMessage() {
        return message;
    }
}
