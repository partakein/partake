package in.partake.controller.action.feed;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import in.partake.controller.action.ActionControllerTest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import play.test.Helpers;

import in.partake.controller.ActionProxy;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

public class FeedRecentEventsActionTest extends ActionControllerTest {
    @Test
    public void testFeedRecentEvents() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/feed/all");
        proxy.execute();

        assertThat(Helpers.contentType(proxy.getResult()), is("application/rss+xml"));
        assertThat(Helpers.charset(proxy.getResult()), is("utf-8"));
        assertThat(Helpers.header("Content-Disposition", proxy.getResult()), is("inline"));

        InputStream is = new ByteArrayInputStream(Helpers.contentAsBytes(proxy.getResult()));
        Reader reader = new InputStreamReader(is, Charset.forName("utf-8")); // TODO: Charset should be constant.
        SyndFeed feed = new SyndFeedInput().build(reader);

        List<String> links = new ArrayList<String>();
        @SuppressWarnings("unchecked")
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry entry : entries)
            links.add(entry.getLink());

        assertThat(links, hasItem(loadEvent(DEFAULT_EVENT_ID).getEventURL()));
        assertThat(links, not(hasItem(loadEvent(PRIVATE_EVENT_ID).getEventURL())));
    }
}
