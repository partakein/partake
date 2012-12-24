package in.partake.controller.api.user;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.controller.ActionProxy;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dto.Event;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

public class GetEventsAPITest extends APIControllerTest {
    private static final int N = 20;
    private List<String> ids = new ArrayList<String>();

    @Before
    public void setUp() throws Exception {
        super.setUp();

        while (ids.size() < N)
            ids.add(UUID.randomUUID().toString());

        // Create 20 events here.
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                IEventAccess dao = daos.getEventAccess();
                dao.truncate(con);

                for (int i = 0; i < N; ++i) {
                    boolean isPrivate = i % 8 == 0;
                    boolean draft = i % 8 == 1;
                    dao.put(con, new Event(ids.get(i), "title", "summary", "category",
                            new DateTime(i), null, "url", "place",
                            "address", "description", "#hashTag", TestDataProvider.EVENT_OWNER_ID,
                            null, null, isPrivate ? "passcode" : null, draft,
                            Collections.singletonList(TestDataProvider.EVENT_EDITOR_ID), new ArrayList<String>(), null,
                            new DateTime(i), new DateTime(i), -1));
                }

                return null;
            }
        }.execute();
    }

    @Test
    public void testToGetEventsForOwner() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/user/events?userId=" + EVENT_OWNER_ID + "&queryType=owner");
        proxy.execute();
        assertResultOK(proxy);

        ObjectNode obj = getJSON(proxy);
        assertThat(obj.get("totalEventCount").asInt(), is(N - 6));
        assertThat(obj.path("eventStatuses"), is(not(nullValue())));
        JsonNode array = obj.get("eventStatuses");
        assertThat(array.size(), is(10));

        int pos = N - 1;
        for (int i = 0; i < array.size(); ++i) {
            while (pos % 8 == 0 || pos % 8 == 1)
                pos -= 1;

            JsonNode eventStatus = array.get(i);
            JsonNode event = eventStatus.get("event");
            assertThat(event.get("id").asText(), is(ids.get(pos)));

            pos -= 1;
        }
    }

    @Test
    public void testToGetEventsForEditor() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/user/events?userId=" + EVENT_EDITOR_ID + "&queryType=editor");
        proxy.execute();
        assertResultOK(proxy);

        ObjectNode obj = getJSON(proxy);
        assertThat(obj.get("totalEventCount").asInt(), is(N - 6));
        assertThat(obj.path("eventStatuses"), is(not(nullValue())));
        JsonNode array = obj.get("eventStatuses");
        assertThat(array.size(), is(10));

        int pos = N - 1;
        for (int i = 0; i < array.size(); ++i) {
            while (pos % 8 == 0 || pos % 8 == 1)
                pos -= 1;

            JsonNode eventStatus = array.get(i);
            JsonNode event = eventStatus.get("event");
            assertThat(event.get("id").asText(), is(ids.get(pos)));

            pos -= 1;
        }
    }

    @Test
    public void testToGetInvalidUserEvent() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/user/events?userId=" + INVALID_USER_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_USER_ID);
    }
}
