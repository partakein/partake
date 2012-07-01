package in.partake.model;

import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.User;

import java.util.List;

/**
 * event with related data.
 * @author shinyak
 *
 */
public class EventEx extends Event {
    private UserEx owner;
    private String feedId;
    private List<EventTicket> tickets;
    private List<User> editors;
    private List<Event> relatedEvents;

    public EventEx(Event event, UserEx owner, String feedId,
            List<EventTicket> tickets, List<User> editors, List<Event> relatedEvents) {
        super(event);
        this.owner = owner;
        this.feedId = feedId;
        this.tickets = tickets;
        this.editors = editors;
        this.relatedEvents = relatedEvents;
    }

    public UserEx getOwner() {
        return owner;
    }

    public String getFeedId() {
        return feedId;
    }

    public List<EventTicket> getTickets() {
        return tickets;
    }

    public List<User> getEditors() {
        return editors;
    }

    public List<Event> getRelatedEvents() {
        return relatedEvents;
    }

    public String getDefaultTwitterPromotionMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(getTitle());
        builder.append(" ").append(getEventURL()).append(" ");
        if (getHashTag() != null && !"".equals(getHashTag())) {
            builder.append(" ").append(getHashTag());
        }

        return builder.toString();
    }
}
