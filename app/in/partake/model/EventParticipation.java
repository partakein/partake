package in.partake.model;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import in.partake.model.dto.Event;

public class EventParticipation {
    private Event event;
    private int numEnrolledUsers;
    private int numReservedUsers;

    public EventParticipation(Event event, int numEnrolledUsers, int numReservedUsers) {
        this.event = event;
        this.numEnrolledUsers = numEnrolledUsers;
        this.numReservedUsers = numReservedUsers;
    }

    public ObjectNode toSafeJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("event", event.toSafeJSON());
        obj.put("numEnrolledUsers", numEnrolledUsers);
        obj.put("numReservedUsers", numReservedUsers);
        return obj;
    }
}
