package in.partake.controller.action.feed;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.EventEx;
import in.partake.model.dto.EventActivity;
import in.partake.view.util.Helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

public abstract class AbstractFeedPageAction extends AbstractPartakeAction{
    protected InputStream createFeed(SyndFeed feed, List<EventEx> events) throws IOException, FeedException {
        List<SyndEntry> entries = new ArrayList<SyndEntry>();

        for (EventEx event : events) {
            if (event == null) { continue; }
            if (!event.isSearchable()) continue;

            SyndContent content = new SyndContentImpl();
            content.setType("text/html");
            content.setValue(Helper.cleanupHTML(event.getDescription()));

            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(event.getTitle());
            entry.setLink(event.getEventURL());
            entry.setPublishedDate(event.getCreatedAt().toDate());

            entry.setAuthor(event.getOwner().getTwitterScreenName());
            entry.setDescription(content);

            entries.add(entry);
        }

        feed.setEntries(entries);
        return outputSyndFeed(feed);
    }

    protected InputStream createEventFeed(SyndFeed feed, List<EventActivity> activities) throws IOException, FeedException {
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        for (EventActivity activity : activities) {
            SyndContent content = new SyndContentImpl();
            content.setType("text/html");
            content.setValue(Helper.cleanupHTML(activity.getContent()));

            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(Helper.h(activity.getTitle()));
            entry.setDescription(content);

            entries.add(entry);
        }

        feed.setEntries(entries);
        return outputSyndFeed(feed);
    }


    protected InputStream outputSyndFeed(SyndFeed feed) throws IOException, FeedException, UnsupportedEncodingException {
        SyndFeedOutput output = new SyndFeedOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.output(feed, new OutputStreamWriter(baos, "utf-8"));
        baos.flush();
        baos.close();

        return new ByteArrayInputStream(baos.toByteArray());
    }
}
