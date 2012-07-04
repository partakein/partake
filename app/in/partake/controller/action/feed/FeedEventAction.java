package in.partake.controller.action.feed;

import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.model.dto.EventFeed;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import play.mvc.Result;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;

public class FeedEventAction extends AbstractFeedPageAction {
    private String feedId;

    public static Result get(String feedId) throws DAOException, PartakeException {
        FeedEventAction action = new FeedEventAction();
        action.feedId = feedId;
        return action.execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        checkIdParameterIsValid(feedId, UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        FeedEventTransaction transaction = new FeedEventTransaction(feedId);
        transaction.execute();

        try {
            Event event = transaction.getEvent();
            if (event == null)
                return renderNotFound();

            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType("rss_2.0");
            feed.setEncoding("utf-8");

            feed.setTitle(event.getTitle() + " - [PARTAKE]");
            feed.setLink(event.getEventURL());
            feed.setDescription(event.getSummary());

            byte[] body = createEventFeed(feed, transaction.getActivities());
            if (body == null)
                return renderNotFound();
            return render(body, "application/rss+xml", "inline");
        } catch (IOException e) {
            throw new PartakeException(ServerErrorCode.ERROR_IO, e);
        } catch (FeedException e) {
            throw new PartakeException(ServerErrorCode.FEED_CREATION, e);
        }
    }
}

class FeedEventTransaction extends DBAccess<InputStream> {
    private String feedId;
    private Event event;
    private List<EventActivity> eventActivities;

    public FeedEventTransaction(String feedId) {
        this.feedId = feedId;
    }

    @Override
    protected InputStream doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IEventFeedAccess feedAccess = daos.getEventFeedAccess();
        EventFeed linkage = feedAccess.find(con, feedId);
        if (linkage == null)
            return null;

        event = EventDAOFacade.getEventEx(con, daos, linkage.getEventId());
        if (event == null)
            return null;

        eventActivities = daos.getEventActivityAccess().findByEventId(con, event.getId(), 100);
        return null;
    }

    public Event getEvent() {
        return event;
    }

    public List<EventActivity> getActivities() {
        return eventActivities;
    }
}
