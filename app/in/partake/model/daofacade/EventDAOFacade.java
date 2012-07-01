package in.partake.model.daofacade;

import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.model.EventCommentEx;
import in.partake.model.EventEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.model.dto.EventComment;
import in.partake.model.dto.EventFeed;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.MessageEnvelope;
import in.partake.model.dto.TwitterMessage;
import in.partake.model.dto.User;
import in.partake.model.dto.UserTwitterLink;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.resource.PartakeProperties;
import in.partake.service.EventSearchServiceException;
import in.partake.service.IEventSearchService;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class EventDAOFacade {
    private static final Logger logger = Logger.getLogger(EventDAOFacade.class);

    public static EventEx getEventEx(PartakeConnection con, IPartakeDAOs daos, String eventId) throws DAOException {
        Event event = daos.getEventAccess().find(con, eventId);
        if (event == null) { return null; }
        UserEx owner = UserDAOFacade.getUserEx(con, daos, event.getOwnerId());
        if (owner == null) { return null; }

        String feedId = daos.getEventFeedAccess().findByEventId(con, eventId);

        List<EventTicket> tickets = daos.getEventTicketAccess().findEventTicketsByEventId(con, eventId);

        List<User> editors = new ArrayList<User>();
        if (event.getEditorIds() != null) {
            for (String editorId : event.getEditorIds()) {
                User editor = daos.getUserAccess().find(con, editorId);
                if (editor != null)
                    editors.add(editor);
            }
        }

        List<Event> relatedEvents = new ArrayList<Event>();
        if (event.getRelatedEventIds() != null) {
            for (String relatedEventId : event.getRelatedEventIds()) {
                Event relatedEvent = daos.getEventAccess().find(con, relatedEventId);
                if (relatedEvent != null)
                    relatedEvents.add(relatedEvent);
            }
        }

        return new EventEx(event, owner, feedId, tickets, editors, relatedEvents);
    }


    /**
     * event をデータベースに保持します。
     * @return event id
     */
    public static String create(PartakeConnection con, IPartakeDAOs daos, Event eventEmbryo) throws DAOException {
        String eventId = daos.getEventAccess().getFreshId(con);
        eventEmbryo.setId(eventId);
        daos.getEventAccess().put(con, eventEmbryo);

        // Feed Dao にも挿入。
        String feedId = daos.getEventFeedAccess().findByEventId(con, eventId);
        if (feedId == null) {
            feedId = daos.getEventFeedAccess().getFreshId(con);
            daos.getEventFeedAccess().put(con, new EventFeed(feedId, eventId));
        }

        // Event Activity にも挿入
        {
            IEventActivityAccess eaa = daos.getEventActivityAccess();
            EventActivity activity = new EventActivity(eaa.getFreshId(con), eventEmbryo.getId(), "イベントが登録されました : " + eventEmbryo.getTitle(), eventEmbryo.getDescription(), eventEmbryo.getCreatedAt());
            eaa.put(con, activity);
        }

        // さらに、twitter bot がつぶやく (private の場合はつぶやかない)
        if (eventEmbryo.isSearchable())
            tweetNewEventArrival(con, daos, eventEmbryo);

        return eventEmbryo.getId();
    }

    public static String copy(PartakeConnection con, IPartakeDAOs daos, UserEx user, Event event) throws DAOException {
        // --- copy event.
        Event newEvent = new Event(event);
        newEvent.setId(null);
        newEvent.setTitle(Util.shorten("コピー -- " + event.getTitle(), 100));
        newEvent.setDraft(true);
        newEvent.setOwnerId(user.getId());
        String newEventId = EventDAOFacade.create(con, daos, newEvent);
        newEvent.setId(newEventId);

        // --- copy ticket.
        List<EventTicket> tickets = daos.getEventTicketAccess().findEventTicketsByEventId(con, event.getId());
        for (EventTicket ticket : tickets) {
            EventTicket newTicket = new EventTicket(ticket);
            newTicket.setId(daos.getEventTicketAccess().getFreshId(con));
            newTicket.setEventId(newEventId);
            daos.getEventTicketAccess().put(con, newTicket);
        }

        return newEventId;
    }

    public static void modify(PartakeConnection con, IPartakeDAOs daos, Event eventEmbryo) throws DAOException {
        assert eventEmbryo != null;
        assert eventEmbryo.getId() != null;

        // master を update
        daos.getEventAccess().put(con, eventEmbryo);

        // Event Activity にも挿入
        {
            IEventActivityAccess eaa = daos.getEventActivityAccess();
            EventActivity activity = new EventActivity(eaa.getFreshId(con), eventEmbryo.getId(), "イベントが更新されました : " + eventEmbryo.getTitle(), eventEmbryo.getDescription(), eventEmbryo.getCreatedAt());
            eaa.put(con, activity);
        }

        // TODO: twitter bot が更新をつぶやいてもいいような気がする。
    }

    public static void recreateEventIndex(PartakeConnection con, IPartakeDAOs daos, IEventSearchService searchService) throws DAOException, EventSearchServiceException {
        searchService.truncate();
        DataIterator<Event> it = daos.getEventAccess().getIterator(con);
        try {
            while (it.hasNext()) {
                Event event = it.next();
                if (event == null) { continue; }

                List<EventTicket> tickets = daos.getEventTicketAccess().findEventTicketsByEventId(con, event.getId());

                if (!event.isSearchable())
                    searchService.remove(event.getId());
                else if (searchService.hasIndexed(event.getId()))
                    searchService.update(event, tickets);
                else
                    searchService.create(event, tickets);
            }
        } finally {
            it.close();
        }
    }

    // ----------------------------------------------------------------------
    // Comments

    public static EventCommentEx getCommentEx(PartakeConnection con, IPartakeDAOs daos, String commentId) throws DAOException {
        EventComment comment = daos.getCommentAccess().find(con, commentId);
        if (comment == null) { return null; }
        UserEx user = UserDAOFacade.getUserEx(con, daos, comment.getUserId());
        if (user == null) { return null; }
        return new EventCommentEx(comment, user);
    }

    public static List<EventCommentEx> getCommentsExByEvent(PartakeConnection con, IPartakeDAOs daos, String eventId) throws DAOException {
        List<EventCommentEx> result = new ArrayList<EventCommentEx>();

        con.beginTransaction();
        DataIterator<EventComment> iterator = daos.getCommentAccess().getCommentsByEvent(con, eventId);
        try {
            if (iterator == null) { return result; }

            while (iterator.hasNext()) {
                EventComment comment = iterator.next();
                if (comment == null) { continue; }
                String commentId = comment.getId();
                if (commentId == null) { continue; }
                EventCommentEx commentEx = getCommentEx(con, daos, commentId);
                if (commentEx == null) { continue; }
                result.add(commentEx);
            }
        } finally {
            iterator.close();
        }

        return result;
    }

    private static void tweetNewEventArrival(PartakeConnection con, IPartakeDAOs daos, Event event) throws DAOException {
        String hashTag = event.getHashTag() != null ? event.getHashTag() : "";
        String messagePrefix = "[PARTAKE] 新しいイベントが追加されました :";
        String eventURL = event.getUrl(); // always 20
        int length = (messagePrefix.length() + 1) + (20 + 1) + (hashTag.length() + 1);
        String title = Util.shorten(event.getTitle(), 140 - length);

        String message = messagePrefix + " " + title + " " + eventURL + " " + hashTag;
        long twitterId = PartakeProperties.get().getTwitterBotTwitterId();
        if (twitterId < 0) {
            logger.info("No bot id.");
            return;
        }
        UserTwitterLink linkage = daos.getTwitterLinkageAccess().findByTwitterId(con, twitterId);
        if (linkage == null) {
            logger.info("twitter bot does have partake user id. Login using the account once to create the user id.");
            return;
        }
        String userId = linkage.getUserId();
        if (userId == null) {
            logger.info("twitter bot does have partake user id. Login using the account once to create the user id.");
            return;
        }

        String twitterMessageId = daos.getTwitterMessageAccess().getFreshId(con);
        TwitterMessage twitterMessage = new TwitterMessage(twitterMessageId, userId, twitterMessageId, MessageDelivery.INQUEUE, TimeUtil.getCurrentDateTime(), null);
        daos.getTwitterMessageAccess().put(con, twitterMessage);

        String envelopeId = daos.getMessageEnvelopeAccess().getFreshId(con);
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId, twitterMessageId, null);
        daos.getMessageEnvelopeAccess().put(con, envelope);

        logger.info("bot will tweet: " + message);
    }
}
