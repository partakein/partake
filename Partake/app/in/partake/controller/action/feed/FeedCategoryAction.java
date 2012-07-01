package in.partake.controller.action.feed;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.model.EventEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.resource.PartakeProperties;
import in.partake.resource.ServerErrorCode;
import in.partake.service.IEventSearchService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import play.mvc.Result;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;

public class FeedCategoryAction extends AbstractFeedPageAction {
    private String catgory;

    public static Result get(String catgory) throws DAOException, PartakeException {
        FeedCategoryAction action = new FeedCategoryAction();
        action.catgory = catgory;
        return action.execute();
    }

    @Override
    public Result doExecute() throws DAOException, PartakeException {
        // TODO: CACHE!

        String category = getParameter("category");

        // check category is correct.
        if (!EventCategory.isValidCategoryName(category))
            return renderNotFound();

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setEncoding("utf-8");

        feed.setTitle("Recent 100 events - " + EventCategory.getReadableCategoryName(category) + " - [PARTAKE]");
        feed.setLink(PartakeProperties.get().getTopPath() + "/");
        feed.setDescription("最近登録されたイベントを(最大100)フィードします。");

        try {
            IEventSearchService searchService = PartakeApp.getEventSearchService();
            List<String> eventIds = searchService.getRecentByCategory(category, 100);

            List<EventEx> events = new FeedCategoryTransaction(eventIds).execute();
            InputStream is = createFeed(feed, events);

            return renderInlineStream(is, "application/rss+xml");
        } catch (IOException e) {
            return renderError(ServerErrorCode.ERROR_IO, e);
        } catch (FeedException e) {
            return renderError(ServerErrorCode.FEED_CREATION, e);
        }
    }
}

class FeedCategoryTransaction extends DBAccess<List<EventEx>> {
    private List<String> eventIds;

    public FeedCategoryTransaction(List<String> eventIds) {
        this.eventIds = eventIds;
    }

    @Override
    protected List<EventEx> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        List<EventEx> events = new ArrayList<EventEx>();
        for (String eventId : eventIds) {
            EventEx event = EventDAOFacade.getEventEx(con, daos, eventId);
            if (event != null)
                events.add(event);
        }

        return events;
    }
}
