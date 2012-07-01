package in.partake.model.dto;

import in.partake.base.DateTime;
import in.partake.model.UserTicketEx;
import in.partake.model.UserEx;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the comparator which created by {@link UserTicket#getPriorityBasedComparator()}.
 *
 * @see UserTicket#getPriorityBasedComparator()
 * @author skypencil (@eller86)
 */
public final class ParticipationComparatorTest {
    private Comparator<UserTicketEx> comparator;
    private UserEx user;

    @Before
    public void createComparator() {
        comparator = UserTicketEx.getPriorityBasedComparator();
        user = null;

        Assert.assertNotNull(comparator);
    }

    @Test
    public void sortEmptyList() {
        List<UserTicketEx> list = Collections.emptyList();
        Collections.sort(list, comparator);
    }

    @Test
    public void sortAllSameValues() {
        List<UserTicketEx> list = Arrays.asList(new UserTicketEx[] {
                new UserTicketEx(new UserTicket("id1", "userID", new UUID(0, 0), "eventId", "comment", null, null, null, null, new DateTime(0), new DateTime(0), null), user),
                new UserTicketEx(new UserTicket("id2", "userID", new UUID(0, 0), "eventId", "comment", null, null, null, null, new DateTime(0), new DateTime(0), null), user),
        });

        Collections.sort(list, comparator);
        Assert.assertTrue(list.get(0).getAppliedAt().compareTo(list.get(1).getAppliedAt()) == 0);
        Assert.assertTrue(list.get(0).getUserId().compareTo(list.get(1).getUserId()) == 0);
    }

    @Test
    public void sortNullValues() {
        List<UserTicketEx> list = Arrays.asList(new UserTicketEx[] {
                null,
                null
        });
        Collections.sort(list, comparator);
        Assert.assertNull(list.get(0));
        Assert.assertNull(list.get(1));
    }

    @Test
    public void sortParicipationAndNull() {
        List<UserTicketEx> list = Arrays.asList(new UserTicketEx[] {
                new UserTicketEx(new UserTicket("id", "userID", new UUID(0, 0), "eventId", "comment", null, null, null, null, new DateTime(0), new DateTime(0), new DateTime(0)), user),
                null
        });

        Collections.sort(list, comparator);
        Assert.assertNull(list.get(0));
        Assert.assertNotNull(list.get(1));
    }

    // throwing NullPointerException is needed? really?
    @Test(expected = NullPointerException.class)
    public void sortNullId() {
        List<UserTicketEx> list = Arrays.asList(new UserTicketEx[] {
                new UserTicketEx(new UserTicket(null, null, null, null, "comment", ParticipationStatus.CANCELLED, ModificationStatus.CHANGED, null, null, null, new DateTime(0), null), user),
                new UserTicketEx(new UserTicket(null, null, null, null, "comment", ParticipationStatus.CANCELLED, ModificationStatus.CHANGED, null, null, null, new DateTime(0), null), user),
        });
        Collections.sort(list, comparator);
    }
}
