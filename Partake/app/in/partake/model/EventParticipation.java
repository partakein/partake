package in.partake.model;

import in.partake.model.dto.Event;
import net.sf.json.JSONObject;

public class EventParticipation {
    private Event event;
    private int numEnrolledUsers;
    private int numReservedUsers;
    
    public EventParticipation(Event event, int numEnrolledUsers, int numReservedUsers) {
        this.event = event;
        this.numEnrolledUsers = numEnrolledUsers;
        this.numReservedUsers = numReservedUsers;
    }
    
    public JSONObject toSafeJSON() {
        JSONObject obj = new JSONObject();
        obj.put("event", event.toSafeJSON());
        obj.put("numEnrolledUsers", numEnrolledUsers);
        obj.put("numReservedUsers", numReservedUsers);
        return obj;
    }
}
