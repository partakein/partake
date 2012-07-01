package in.partake.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import in.partake.model.dto.UserTicket;

/**
 * enrollment with related data.
 * @author shinyak
 *
 */
public class UserTicketEx extends UserTicket {
    private UserEx user;
    private List<String> relatedEventIds;

    // priority, modifiedAt 順に並べる comparator
    public static Comparator<UserTicketEx> getPriorityBasedComparator() {
        return new Comparator<UserTicketEx>() {
            @Override
            public int compare(UserTicketEx lhs, UserTicketEx rhs) {
                if (lhs == rhs) { return 0; }
                if (lhs == null) { return -1; }
                if (rhs == null) { return 1; }

                int x = lhs.getAppliedAt().compareTo(rhs.getAppliedAt());
                if (x != 0)
                    return x;

                // If application time is the same, we use userId to compare it.
                return lhs.getUserId().compareTo(rhs.getUserId());
            }
        };
    }


    public UserTicketEx(UserTicket enrollment, UserEx user) {
        super(enrollment);
        this.user = user;
        this.relatedEventIds = new ArrayList<String>();
    }

    public UserEx getUser() {
        return this.user;
    }

    public List<String> getRelatedEventIds() {
        return relatedEventIds;
    }

    public void addRelatedEventId(String eventId) {
        checkFrozen();
        relatedEventIds.add(eventId);
    }
}
