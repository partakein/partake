package in.partake.model.dao.auxiliary;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import in.partake.model.dto.Event;

public class EventStatus {
    public Event event;
    public boolean isAmountInfinite;
    public int amount;
    public boolean isBeforeDeadline;
    public int numEnrolledUsers;
    public int numReservedUsers;
    public int numCancelledUsers;

    public EventStatus(Event event, boolean isAmountInfinite, int amount,
            boolean isBeforeDeadline, int numEnrolledUsers, int numReservedUsers, int numCancelledUsers) {
        this.event = event;
        this.isAmountInfinite = isAmountInfinite;
        this.amount = amount;
        this.isBeforeDeadline = isBeforeDeadline;
        this.numEnrolledUsers = numEnrolledUsers;
        this.numReservedUsers = numReservedUsers;
        this.numCancelledUsers = numCancelledUsers;
    }

    public ObjectNode toSafeJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("event", event.toSafeJSON());
        obj.put("isAmountInfinite", isAmountInfinite);
        obj.put("amount", amount);
        obj.put("isBeforeDeadline", isBeforeDeadline);
        obj.put("numEnrolledUsers", numEnrolledUsers);
        obj.put("numReservedUsers", numReservedUsers);
        obj.put("numCancelledUsers", numCancelledUsers);
        return obj;
    }
}
