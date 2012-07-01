package in.partake.model.dao;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.access.IAccess;
import in.partake.model.dao.access.IUserCalendarLinkageAccess;
import in.partake.model.dao.access.IEventCommentAccess;
import in.partake.model.dao.access.IUserTicketAccess;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dao.access.IEventMessageAccess;
import in.partake.model.dao.access.IEventTicketAccess;
import in.partake.model.dao.access.IEventTicketNotificationAccess;
import in.partake.model.dao.access.IUserImageAccess;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dao.access.IMessageEnvelopeAccess;
import in.partake.model.dao.access.IUserOpenIDLinkAccess;
import in.partake.model.dao.access.IUserThumbnailAccess;
import in.partake.model.dao.access.IUserTwitterLinkAccess;
import in.partake.model.dao.access.ITwitterMessageAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.access.IUserNotificationAccess;
import in.partake.model.dao.access.IUserPreferenceAccess;
import in.partake.model.dao.access.IUserReceivedMessageAccess;
import in.partake.model.dao.access.IUserSentMessageAccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PartakeDAOFactory implements IPartakeDAOs {
    private final IUserCalendarLinkageAccess calendarLinkageAccess;
    private final IEventCommentAccess commentAccess;
    private final IUserTicketAccess enrollmentAccess;
    private final IEventAccess eventAccess;
    private final IEventFeedAccess eventFeedAccess;
    private final IEventActivityAccess eventActivityAccess;
    private final IEventTicketAccess eventTicketAccess;
    private final IUserImageAccess imageAccess;
    private final IUserOpenIDLinkAccess openIDLinkageAccess;
    private final IUserThumbnailAccess thumbnailAccess;
    private final IUserTwitterLinkAccess twitterLinkageAccess;
    private final IUserAccess userAccess;
    private final IUserPreferenceAccess userPreferenceAccess;
    private final IEventMessageAccess eventMessageAccess;
    private final IUserReceivedMessageAccess userMessageAccess;
    private final IEventTicketNotificationAccess eventNotificationMessageAccess;
    private final IMessageAccess messageAccess;
    private final IMessageEnvelopeAccess messageEnvelopeAccess;
    private final ITwitterMessageAccess twitterMessageAccess;

    private final IUserNotificationAccess userNotificationAccess;
    private final IUserSentMessageAccess userSentMessageAccess;

    private final List<IAccess<?, ?>> daos;

    protected PartakeDAOFactory() {
        daos = new ArrayList<IAccess<?, ?>>();

        addDao(calendarLinkageAccess = createCalendarLinkageAccess());
        addDao(commentAccess         = createCommentAccess());
        addDao(enrollmentAccess      = createEnrollmentAccess());
        addDao(eventAccess           = createEventAccess());
        addDao(eventFeedAccess       = createEventFeedAccess());
        addDao(eventActivityAccess   = createEventActivityAccess());
        addDao(eventTicketAccess     = createEventTicketAccess());
        addDao(imageAccess           = createImageAccess());
        addDao(openIDLinkageAccess   = createOpenIDLinkageAccess());
        addDao(thumbnailAccess       = createThumbnailAccess());
        addDao(twitterLinkageAccess  = createTwitterLinkageAccess());
        addDao(userAccess            = creataeUserAccess());
        addDao(userPreferenceAccess  = createUserPreferenceAccess());
        addDao(eventMessageAccess = createEventMessageAccess());
        addDao(userMessageAccess = createUserReceivedMessageAccess());
        addDao(eventNotificationMessageAccess = createEventNotificationAccess());
        addDao(messageAccess = createMessageAccess());
        addDao(twitterMessageAccess = createTwitterMessageAccess());
        addDao(messageEnvelopeAccess = createMessageEnvelopeAccess());
        addDao(userNotificationAccess = createUserNotificationAccess());
        addDao(userSentMessageAccess = createUserSentMessageAccess());
    }

    public void initialize(PartakeConnection con) throws DAOException {
        for (IAccess<?, ?> dao : daos) {
            dao.initialize(con);
        }
    }

    // ----------------------------------------------------------------------
    //

    private void addDao(IAccess<?, ?> t) {
        if (t != null)
            daos.add(t);
    }

    public List<IAccess<?, ?>> getDaos() {
        return Collections.unmodifiableList(daos);
    }


    // ----------------------------------------------------------------------
    // accessors

    public final IUserCalendarLinkageAccess getCalendarAccess() {
        return calendarLinkageAccess;
    }

    public final IEventCommentAccess getCommentAccess() {
        return commentAccess;
    }

    public final IUserTicketAccess getEnrollmentAccess() {
        return enrollmentAccess;
    }

    public final IEventAccess getEventAccess() {
        return eventAccess;
    }

    public final IEventFeedAccess getEventFeedAccess() {
        return eventFeedAccess;
    }

    public final IEventActivityAccess getEventActivityAccess() {
        return eventActivityAccess;
    }

    public final IUserImageAccess getImageAccess() {
        return imageAccess;
    }

    public final IUserOpenIDLinkAccess getOpenIDLinkageAccess() {
        return openIDLinkageAccess;
    }

    public final IUserThumbnailAccess getThumbnailAccess() {
        return thumbnailAccess;
    }

    public final IUserTwitterLinkAccess getTwitterLinkageAccess() {
        return twitterLinkageAccess;
    }

    public final IUserAccess getUserAccess() {
        return userAccess;
    }

    public final IUserPreferenceAccess getUserPreferenceAccess() {
        return userPreferenceAccess;
    }

    public final IEventMessageAccess getEventMessageAccess() {
        return eventMessageAccess;
    }

    public final IEventTicketAccess getEventTicketAccess() {
        return eventTicketAccess;
    }

    public final IUserCalendarLinkageAccess getCalendarLinkageAccess() {
        return calendarLinkageAccess;
    }

    public final IUserReceivedMessageAccess getUserReceivedMessageAccess() {
        return userMessageAccess;
    }

    public final IEventTicketNotificationAccess getEventNotificationAccess() {
        return eventNotificationMessageAccess;
    }

    public final IMessageAccess getMessageAccess() {
        return messageAccess;
    }

    @Override
    public final IMessageEnvelopeAccess getMessageEnvelopeAccess() {
        return this.messageEnvelopeAccess;
    }

    @Override
    public final ITwitterMessageAccess getTwitterMessageAccess() {
        return this.twitterMessageAccess;
    }

    @Override
    public IUserNotificationAccess getUserNotificationAccess() {
        return this.userNotificationAccess;
    }

    @Override
    public IUserSentMessageAccess getUserSentMessageAccess() {
        return this.userSentMessageAccess;
    }

    protected abstract IUserCalendarLinkageAccess createCalendarLinkageAccess();
    protected abstract IEventCommentAccess createCommentAccess();
    protected abstract IUserTicketAccess createEnrollmentAccess();
    protected abstract IEventAccess createEventAccess();
    protected abstract IEventTicketAccess createEventTicketAccess();
    protected abstract IEventFeedAccess createEventFeedAccess();
    protected abstract IEventActivityAccess createEventActivityAccess();
    protected abstract IUserOpenIDLinkAccess createOpenIDLinkageAccess();
    protected abstract IUserImageAccess createImageAccess();
    protected abstract IUserThumbnailAccess createThumbnailAccess();
    protected abstract IUserTwitterLinkAccess createTwitterLinkageAccess();
    protected abstract IUserAccess creataeUserAccess();
    protected abstract IUserPreferenceAccess createUserPreferenceAccess();
    protected abstract IEventMessageAccess createEventMessageAccess();
    protected abstract IUserReceivedMessageAccess createUserReceivedMessageAccess();
    protected abstract IEventTicketNotificationAccess createEventNotificationAccess();
    protected abstract IMessageAccess createMessageAccess();
    protected abstract ITwitterMessageAccess createTwitterMessageAccess();
    protected abstract IMessageEnvelopeAccess createMessageEnvelopeAccess();
    protected abstract IUserNotificationAccess createUserNotificationAccess();
    protected abstract IUserSentMessageAccess createUserSentMessageAccess();
}
