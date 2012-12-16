package in.partake.controller.api.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.ActionProxy;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.fixture.impl.EventTestDataProvider;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;


public class SearchAPITest extends APIControllerTest {
    private static final String SEARCH_QUERY = "あ";

    @Test
    public void testSearchEventAllCategory() throws Exception {
        storeEventBeforeDeadline();
        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        addFormParameter(proxy, "query", SEARCH_QUERY);
        addFormParameter(proxy, "category", "all");
        addFormParameter(proxy, "beforeDeadlineOnly", "true");
        addFormParameter(proxy, "sortOrder", "score");
        addFormParameter(proxy, "maxNum", "10");
        proxy.execute();

        assertResultOK(proxy);
        ObjectNode json = getJSON(proxy);
        assertPublicEventsAreFound(json);
        assertThat(json.has("reason"), equalTo(false));
    }

    @Test
    public void testSearchJapaneseQuery() throws Exception {
        // http://code.google.com/p/partakein/issues/detail?id=233
        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        addFormParameter(proxy, "query", EventTestDataProvider.JAPANESE_IDENTIFIER);
        addFormParameter(proxy, "category", "all");
        addFormParameter(proxy, "beforeDeadlineOnly", "true");
        addFormParameter(proxy, "sortOrder", "score");
        addFormParameter(proxy, "maxNum", "10");
        proxy.execute();

        assertResultOK(proxy);
        ObjectNode json = getJSON(proxy);
        assertPublicEventsAreFound(json);
        assertThat(json.has("reason"), equalTo(false));
    }

    @Test
    public void testSearchJapaneseQueryWithBeforeDeadlineOnly() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        addFormParameter(proxy, "query", EventTestDataProvider.JAPANESE_IDENTIFIER);
        addFormParameter(proxy, "category", "all");
        addFormParameter(proxy, "beforeDeadlineOnly", "false");
        addFormParameter(proxy, "sortOrder", "score");
        addFormParameter(proxy, "maxNum", "10");
        proxy.execute();

        assertResultOK(proxy);
        ObjectNode json = getJSON(proxy);
        assertPublicEventsAreFound(json);
        assertThat(json.has("reason"), equalTo(false));

    }

    @Test
    public void testSearchEventWithoutQuery() throws Exception {
        storeEventBeforeDeadline();
        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        // addFormParameter(proxy, "query", null);
        addFormParameter(proxy, "category", "all");
        addFormParameter(proxy, "beforeDeadlineOnly", "true");
        addFormParameter(proxy, "sortOrder", "score");
        addFormParameter(proxy, "maxNum", "10");
        proxy.execute();

        assertResultOK(proxy);
        ObjectNode json = getJSON(proxy);
        assertThat(json.has("reason"), equalTo(false));
        assertPublicEventsAreFound(json);
    }

    // =========================================================================
    // beforeDeadlineOnly
    @Test
    public void testSearchEventBeforeDeadlineOnly() throws Exception {
        storeEventAfterDeadline();
        storeEventBeforeDeadline();

        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        addFormParameter(proxy, "query", SEARCH_QUERY);
        addFormParameter(proxy, "category", "all");
        // addFormParameter(proxy, "beforeDeadlineOnly", null);
        addFormParameter(proxy, "sortOrder", "score");
        addFormParameter(proxy, "maxNum", "10");
        proxy.execute();

        assertResultOK(proxy);
        ObjectNode json = getJSON(proxy);
        assertOnlyBeforeDeadlineAreFound(json);
    }

    @Test
    public void testSearchEventIncludeAfterDeadline() throws Exception {
        storeEventAfterDeadline();
        storeEventBeforeDeadline();

        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        addFormParameter(proxy, "query", SEARCH_QUERY);
        addFormParameter(proxy, "category", "all");
        addFormParameter(proxy, "beforeDeadlineOnly", "false");
        addFormParameter(proxy, "sortOrder", "score");
        addFormParameter(proxy, "maxNum", "10");
        proxy.execute();

        assertResultOK(proxy);
        ObjectNode json = getJSON(proxy);
        DateTime now = TimeUtil.getCurrentDateTime();
        boolean findEventWhichIsAfterDeadline = false;
        boolean findEventWhichIsBeforeDeadline = false;
        for (JsonNode eventJSON : json.get("events")) {
            Event event = loadEvent(eventJSON.get("id").asText());
            List<EventTicket> tickets = loadEventTickets(event.getId());
            DateTime deadline = event.acceptsSomeTicketsTill(tickets);

            if (deadline.isBefore(now)) {
                findEventWhichIsAfterDeadline = true;
            } else {
                findEventWhichIsBeforeDeadline = true;
            }
        }
        assertTrue("見つかるはずのイベントが見つかりませんでした", findEventWhichIsAfterDeadline);
        assertTrue("見つかるはずのイベントが見つかりませんでした", findEventWhichIsBeforeDeadline);
    }

    /**
     * beforeDeadlineOnlyを省略した場合はtrueであるとみなす
     */
    @Test
    public void testSearchEventBeforeDeadlineOnlyEmpty() throws Exception {
        storeEventAfterDeadline();
        storeEventBeforeDeadline();

        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        addFormParameter(proxy, "query", SEARCH_QUERY);
        addFormParameter(proxy, "category", "all");
        // addFormParameter(proxy, "beforeDeadlineOnly", null);
        addFormParameter(proxy, "sortOrder", "score");
        addFormParameter(proxy, "maxNum", "10");
        proxy.execute();

        assertResultOK(proxy);
        ObjectNode json = getJSON(proxy);
        assertOnlyBeforeDeadlineAreFound(json);
    }

    /**
     * beforeDeadlineOnlyにtrueでもfalseでもない値が渡されたら引数が異常としてエラー
     */
    @Test
    public void testSearchEventIllegalBeforeDeadlineOnly() throws Exception {
        storeEventAfterDeadline();
        storeEventBeforeDeadline();

        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        addFormParameter(proxy, "query", SEARCH_QUERY);
        addFormParameter(proxy, "category", "all");
        addFormParameter(proxy, "beforeDeadlineOnly", "(´・ω・`)");
        addFormParameter(proxy, "sortOrder", "score");
        addFormParameter(proxy, "maxNum", "10");
        proxy.execute();

        assertResultOK(proxy);
    }

    private Event createEvent() {
        Event event = PartakeTestApp.getTestService().getTestDataProviderSet().getEventProvider().create();
        event.setTitle(SEARCH_QUERY);
        event.setSummary(SEARCH_QUERY);
        event.setDescription(SEARCH_QUERY);
        event.setBeginDate(new DateTime(0L));
        event.setCategory("neta");
        event.setCreatedAt(new DateTime(0L));
        event.setDraft(false);
        event.setPasscode(null); // privateイベントは検索の対象にならないので公開イベントとして作成

        assert event.isSearchable();
        return event;
    }

    private Event storeEventAfterDeadline() throws DAOException, PartakeException {
        final Event event = createEvent();
        event.setBeginDate(TimeUtil.getCurrentDateTime().nDayBefore(1));
        String eventId = new Transaction<String>() {
            @Override
            protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return EventDAOFacade.create(con, daos, event);
            }
        }.execute();

        UUID ticketId = UUID.randomUUID();
        EventTicket ticket = EventTicket.createDefaultTicket(ticketId, eventId);
        storeEventTicket(ticket);

        PartakeApp.getEventSearchService().create(event, Collections.singletonList(ticket));
        return event;
    }

    private Event storeEventBeforeDeadline() throws DAOException, PartakeException {
        final Event event = createEvent();
        event.setBeginDate(TimeUtil.getCurrentDateTime().nDayAfter(1));
        String eventId = new Transaction<String>() {
            @Override
            protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return EventDAOFacade.create(con, daos, event);
            }
        }.execute();

        UUID ticketId = UUID.randomUUID();
        EventTicket ticket = EventTicket.createDefaultTicket(ticketId, eventId);
        storeEventTicket(ticket);

        PartakeApp.getEventSearchService().create(event, Collections.singletonList(ticket));
        return event;
    }

    // =========================================================================
    // maxNum
    @Test
    public void testSearchEventTooLargeMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        addFormParameter(proxy, "query", SEARCH_QUERY);
        addFormParameter(proxy, "category", "all");
        addFormParameter(proxy, "beforeDeadlineOnly", "true");
        addFormParameter(proxy, "sortOrder", "score");
        addFormParameter(proxy, "maxNum", "10000");
        proxy.execute();

        assertResultOK(proxy);
    }

    /**
     * maxNum省略時はデフォルト（10）と解釈する
     * @throws Exception
     */
    @Test
    public void testSearchEventMissingMaxNum() throws Exception {
        for (int i = 1; i <= 11; ++i) {
            storeEventBeforeDeadline();
        }
        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        addFormParameter(proxy, "query", SEARCH_QUERY);
        addFormParameter(proxy, "category", "all");
        addFormParameter(proxy, "beforeDeadlineOnly", "true");
        addFormParameter(proxy, "sortOrder", "score");
        // addFormParameter(proxy, "maxNum", null);
        proxy.execute();

        assertResultOK(proxy);
        ObjectNode json = getJSON(proxy);
        assertThat(json.has("reason"), equalTo(false));
        assertThat(json.get("events").size(), equalTo(10));
    }

    @Test
    public void testSearchEventEmptyMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        addFormParameter(proxy, "query", SEARCH_QUERY);
        addFormParameter(proxy, "category", "all");
        addFormParameter(proxy, "beforeDeadlineOnly", "true");
        addFormParameter(proxy, "sortOrder", "score");
        addFormParameter(proxy, "maxNum", "");
        proxy.execute();

        assertResultOK(proxy);
    }

    @Test
    public void testSearchEventNotNumberMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        addFormParameter(proxy, "query", SEARCH_QUERY);
        addFormParameter(proxy, "category", "all");
        addFormParameter(proxy, "beforeDeadlineOnly", "true");
        addFormParameter(proxy, "sortOrder", "score");
        addFormParameter(proxy, "maxNum", "m(_ _)m");
        proxy.execute();

        assertResultOK(proxy);
    }

    /**
     * 大文字小文字間違えて指定した場合はデフォルト値が有効になる
     */
    @Test
    public void testSearchEventNotLowerCamelMaxNum() throws Exception {
        for (int i = 1; i <= 11; ++i) {
            storeEventBeforeDeadline();
        }

        ActionProxy proxy = getActionProxy(POST, "/api/event/search");
        addFormParameter(proxy, "query", SEARCH_QUERY);
        addFormParameter(proxy, "category", "all");
        addFormParameter(proxy, "beforeDeadlineOnly", "true");
        addFormParameter(proxy, "sortOrder", "score");
        // addFormParameter(proxy, "maxNum", null);
        addFormParameter(proxy, "maxnum", "10");
        proxy.execute();

        assertResultOK(proxy);
        ObjectNode json = getJSON(proxy);
        assertThat(json.has("reason"), equalTo(false));
        assertThat(json.get("events").size(), equalTo(10));
    }

    // =========================================================================
    // utility

    private void assertPublicEventsAreFound(ObjectNode json) {
        assertTrue(json.get("events").isArray());
        assertThat(json.get("events").size(), is(greaterThan(0)));
    }

    private void assertOnlyBeforeDeadlineAreFound(ObjectNode json) throws PartakeException, DAOException {
        DateTime now = TimeUtil.getCurrentDateTime();

        boolean findEvents = false;
        for (JsonNode node : json.get("events")) {
            Event event = loadEvent(node.get("id").asText());
            List<EventTicket> tickets = loadEventTickets(event.getId());
            DateTime deadline = event.acceptsSomeTicketsTill(tickets);
            assertThat("締め切り後のイベントが見つかってしまいました", deadline.getTime(), is(greaterThan(now.getTime())));
            findEvents = true;
        }
        assertTrue("見つかるはずのイベントが見つかりませんでした", findEvents);
    }
}
