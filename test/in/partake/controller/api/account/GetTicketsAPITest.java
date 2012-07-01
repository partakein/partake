package in.partake.controller.api.account;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.base.DateTime;
import in.partake.base.Pair;
import in.partake.base.TimeUtil;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class GetTicketsAPITest extends APIControllerTest {

    @Test
    public void testGetEnrollments() throws Exception {
        List<Pair<UUID, String>> ids = prepareEvents(20);
        prepareEnrollment(DEFAULT_USER_ID, ids);

        ActionProxy proxy = getActionProxy(GET, "/api/account/tickets");
        loginAs(proxy, DEFAULT_USER_ID);
        addParameter(proxy, "limit", "10");

        proxy.execute();
        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);
        assertThat(obj.getInt("totalTicketCount"), is(20));

        JSONArray array = obj.getJSONArray("ticketStatuses");
        assertThat(array.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            assertThat(array.getJSONObject(i).getJSONObject("ticket").getString("id"), is(ids.get(i).getFirst().toString()));
            assertThat(array.getJSONObject(i).getString("status"), is("enrolled"));
        }
    }

    private List<Pair<UUID, String>> prepareEvents(int n) throws Exception {
        List<Pair<UUID, String>> ids = new ArrayList<Pair<UUID, String>>();

        DateTime now = TimeUtil.getCurrentDateTime();
        DateTime late = new DateTime(now.getTime() + 1000 * 3600);
        String category = EventCategory.getCategories().get(0).getKey();

        for (int i = 0; i < n; ++i) {
            boolean isPrivate = i % 2 == 1;
            Event event = new Event(null, "title", "summary", category, late, late,
                    "url", "place",
                    "address", "description", "#hashTag", EVENT_OWNER_ID,
                    EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, isPrivate ? "passcode" : null, false,
                    Collections.singletonList(EVENT_EDITOR_TWITTER_SCREENNAME), new ArrayList<String>(), null,
                    now, now, -1);

            String eventId = storeEvent(event);

            UUID uuid = UUID.randomUUID();
            EventTicket ticket = EventTicket.createDefaultTicket(uuid, eventId);
            storeEventTicket(ticket);

            ids.add(new Pair<UUID, String>(uuid, eventId));
        }

        return ids;
    }

    private List<String> prepareEnrollment(String userId, List<Pair<UUID, String>> ids) throws Exception {
        List<String> userTicketIds = new ArrayList<String>();
        for (int i = 0; i < ids.size(); ++i) {
            UUID ticketId = ids.get(i).getFirst();
            String eventId = ids.get(i).getSecond();
            ParticipationStatus status = ParticipationStatus.ENROLLED;
            ModificationStatus modificationStatus = ModificationStatus.CHANGED;
            AttendanceStatus attendanceStatus = AttendanceStatus.UNKNOWN;
            DateTime enrolledAt = new DateTime(TimeUtil.getCurrentTime() + (ids.size() - i) * 1000);
            UserTicket enrollment = new UserTicket(null, userId, ticketId, eventId, "comment", status, modificationStatus, attendanceStatus, null, enrolledAt, enrolledAt, enrolledAt);
            userTicketIds.add(storeEnrollment(enrollment));
        }
        return userTicketIds;
    }
}
