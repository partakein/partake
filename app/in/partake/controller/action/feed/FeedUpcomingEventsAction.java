package in.partake.controller.action.feed;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.model.EventEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.resource.ServerErrorCode;
import in.partake.service.IEventSearchService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import play.mvc.Result;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;

public class FeedUpcomingEventsAction extends AbstractFeedPageAction {
    private String category;

    public static Result get(String category) throws DAOException, PartakeException {
        FeedUpcomingEventsAction action = new FeedUpcomingEventsAction();
        action.category = category;
        return action.execute();
    }

    @Override
    public Result doExecute() throws DAOException, PartakeException {
        // TODO: CACHE!

        String category = getParameter("category");
        if (!EventCategory.isValidCategoryName(category) && !category.equals(EventCategory.getAllEventCategory()))
            return renderNotFound();

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setEncoding("utf-8");

        if (category.equals(EventCategory.getAllEventCategory())) {
            feed.setTitle("Upcoming 100 events - [PARTAKE]");
        } else {
            feed.setTitle("Upcoming 100 events - " + EventCategory.getReadableCategoryName(category) + " - [PARTAKE]");
        }
        feed.setLink("http://partake.in/"); // TODO use in.partake.toppath from properties file
        feed.setDescription("近日開催されるイベントを(最大100)フィードします。");

        try {
            IEventSearchService searchService = PartakeApp.getEventSearchService();
            List<String> eventIds = searchService.getUpcomingByCategory(category, 100);

            List<EventEx> events = new GetEventsTransaction(eventIds).execute();
            InputStream is = createFeed(feed, events);

            return renderInlineStream(is, "application/rss+xml");
        } catch (IOException e) {
            return renderError(ServerErrorCode.ERROR_IO, e);
        } catch (FeedException e) {
            return renderError(ServerErrorCode.FEED_CREATION, e);
        }
    }
}
