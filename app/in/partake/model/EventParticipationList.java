package in.partake.model;

import java.util.List;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class EventParticipationList {
    private List<EventParticipation> participations;
    private int numEvents;

    public EventParticipationList(List<EventParticipation> participations, int numEvents) {
        this.participations = participations;
        this.numEvents = numEvents;
    }

    public ObjectNode toSafeJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("numEvents", numEvents);
        ArrayNode events = obj.putArray("participations");

        for (EventParticipation participation : participations)
            events.add(participation.toSafeJSON());

        return obj;
    }
}
