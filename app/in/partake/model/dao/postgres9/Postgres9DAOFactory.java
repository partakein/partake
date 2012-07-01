package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
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
import in.partake.model.dao.postgres9.impl.Postgres9UserCalendarLinkDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventCommentDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserTicketDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventActivityDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventFeedDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventMessageDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventNotificationDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventTicketDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserImageDao;
import in.partake.model.dao.postgres9.impl.Postgres9MessageDao;
import in.partake.model.dao.postgres9.impl.Postgres9MessageEnvelopeDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserOpenIDLinkDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserThumbnailDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserTwitterLinkDao;
import in.partake.model.dao.postgres9.impl.Postgres9TwitterMessageDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserNotificationDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserPreferenceDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserReceivedMessageDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserSentMessageDao;

public class Postgres9DAOFactory extends PartakeDAOFactory {
    public Postgres9DAOFactory() {
        super();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        super.initialize(con);
    }

    @Override
    protected IUserCalendarLinkageAccess createCalendarLinkageAccess() {
        return new Postgres9UserCalendarLinkDao();
    }

    @Override
    protected IEventCommentAccess createCommentAccess() {
        return new Postgres9EventCommentDao();
    }

    @Override
    protected IUserTicketAccess createEnrollmentAccess() {
        return new Postgres9UserTicketDao();
    }

    @Override
    protected IEventAccess createEventAccess() {
        return new Postgres9EventDao();
    }

    @Override
    protected IEventFeedAccess createEventFeedAccess() {
        return new Postgres9EventFeedDao();
    }

    @Override
    protected IEventActivityAccess createEventActivityAccess() {
        return new Postgres9EventActivityDao();
    }

    @Override
    public IEventTicketAccess createEventTicketAccess() {
        return new Postgres9EventTicketDao();
    }

    @Override
    protected IUserOpenIDLinkAccess createOpenIDLinkageAccess() {
        return new Postgres9UserOpenIDLinkDao();
    }

    @Override
    protected IUserImageAccess createImageAccess() {
        return new Postgres9UserImageDao();
    }

    @Override
    protected IUserThumbnailAccess createThumbnailAccess() {
        return new Postgres9UserThumbnailDao();
    }

    @Override
    protected IUserTwitterLinkAccess createTwitterLinkageAccess() {
        return new Postgres9UserTwitterLinkDao();
    }

    @Override
    protected IMessageAccess createMessageAccess() {
        return new Postgres9MessageDao();
    }

    @Override
    protected IUserAccess creataeUserAccess() {
        return new Postgres9UserDao();
    }

    @Override
    protected IUserPreferenceAccess createUserPreferenceAccess() {
        return new Postgres9UserPreferenceDao();
    }

    @Override
    protected IEventMessageAccess createEventMessageAccess() {
        return new Postgres9EventMessageDao();
    }

    @Override
    protected IUserReceivedMessageAccess createUserReceivedMessageAccess() {
        return new Postgres9UserReceivedMessageDao();
    }

    @Override
    protected IEventTicketNotificationAccess createEventNotificationAccess() {
        return new Postgres9EventNotificationDao();
    }

    @Override
    protected IMessageEnvelopeAccess createMessageEnvelopeAccess() {
        return new Postgres9MessageEnvelopeDao();
    }

    @Override
    protected ITwitterMessageAccess createTwitterMessageAccess() {
        return new Postgres9TwitterMessageDao();
    }

    @Override
    protected IUserNotificationAccess createUserNotificationAccess() {
        return new Postgres9UserNotificationDao();
    }

    @Override
    protected IUserSentMessageAccess createUserSentMessageAccess() {
        return new Postgres9UserSentMessageDao();
    }
}
