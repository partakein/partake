package in.partake.model;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class EventParticipationList {
    private List<EventParticipation> participations;
    private int numEvents;

    public EventParticipationList(List<EventParticipation> participations, int numEvents) {
        this.participations = participations;
        this.numEvents = numEvents;
    }

    public JSONObject toSafeJSON() {
        JSONArray events = new JSONArray();
        for (EventParticipation participation : participations)
            events.add(participation.toSafeJSON());

        JSONObject obj = new JSONObject();
        obj.put("numEvents", numEvents);
        obj.put("participations", events);

        return obj;
    }
}
