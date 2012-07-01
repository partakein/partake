package in.partake.model.dao.mock;

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

import org.mockito.Mockito;

public class MockDaoFactory extends PartakeDAOFactory {

    public MockDaoFactory() {
    }

    @Override
    protected IUserCalendarLinkageAccess createCalendarLinkageAccess() {
        return Mockito.mock(IUserCalendarLinkageAccess.class);
    }

    @Override
    protected IEventCommentAccess createCommentAccess() {
        return Mockito.mock(IEventCommentAccess.class);
    }

    @Override
    protected IUserTicketAccess createEnrollmentAccess() {
        return Mockito.mock(IUserTicketAccess.class);
    }

    @Override
    protected IEventAccess createEventAccess() {
        return Mockito.mock(IEventAccess.class);
    }

    @Override
    protected IEventTicketAccess createEventTicketAccess() {
        return Mockito.mock(IEventTicketAccess.class);
    }

    @Override
    public IEventMessageAccess createEventMessageAccess() {
        return Mockito.mock(IEventMessageAccess.class);
    }

    @Override
    public IEventTicketNotificationAccess createEventNotificationAccess() {
        return Mockito.mock(IEventTicketNotificationAccess.class);
    }

    @Override
    protected IEventFeedAccess createEventFeedAccess() {
        return Mockito.mock(IEventFeedAccess.class);
    }

    @Override
    protected IEventActivityAccess createEventActivityAccess() {
        return Mockito.mock(IEventActivityAccess.class);
    }

    @Override
    protected IUserOpenIDLinkAccess createOpenIDLinkageAccess() {
        return Mockito.mock(IUserOpenIDLinkAccess.class);
    }

    @Override
    protected IUserThumbnailAccess createThumbnailAccess() {
        return Mockito.mock(IUserThumbnailAccess.class);
    }

    @Override
    protected IUserTwitterLinkAccess createTwitterLinkageAccess() {
        return Mockito.mock(IUserTwitterLinkAccess.class);
    }

    @Override
    protected IUserAccess creataeUserAccess() {
        return Mockito.mock(IUserAccess.class);
    }

    @Override
    protected IUserPreferenceAccess createUserPreferenceAccess() {
        return Mockito.mock(IUserPreferenceAccess.class);
    }

    @Override
    protected IUserImageAccess createImageAccess() {
        return Mockito.mock(IUserImageAccess.class);
    }

    @Override
    public IUserReceivedMessageAccess createUserReceivedMessageAccess() {
        return Mockito.mock(IUserReceivedMessageAccess.class);
    }

    @Override
    public IMessageAccess createMessageAccess() {
        return Mockito.mock(IMessageAccess.class);
    }

    public IMessageEnvelopeAccess createMessageEnvelopeAccess() {
        return Mockito.mock(IMessageEnvelopeAccess.class);
    }

    @Override
    public ITwitterMessageAccess createTwitterMessageAccess() {
        return Mockito.mock(ITwitterMessageAccess.class);
    }

    @Override
    protected IUserNotificationAccess createUserNotificationAccess() {
        return Mockito.mock(IUserNotificationAccess.class);
    }

    @Override
    protected IUserSentMessageAccess createUserSentMessageAccess() {
        return Mockito.mock(IUserSentMessageAccess.class);
    }
}
