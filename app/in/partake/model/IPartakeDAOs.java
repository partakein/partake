package in.partake.model;

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

public interface IPartakeDAOs {
    public IUserCalendarLinkageAccess getCalendarAccess();
    public IEventCommentAccess getCommentAccess();
    public IUserTicketAccess getEnrollmentAccess();
    public IEventAccess getEventAccess();
    public IEventActivityAccess getEventActivityAccess();
    public IEventFeedAccess getEventFeedAccess();
    public IEventMessageAccess getEventMessageAccess();
    public IEventTicketNotificationAccess getEventNotificationAccess();
    public IEventTicketAccess getEventTicketAccess();
    public IUserImageAccess getImageAccess();
    public IMessageAccess getMessageAccess();
    public IMessageEnvelopeAccess getMessageEnvelopeAccess();
    public IUserOpenIDLinkAccess getOpenIDLinkageAccess();
    public IUserThumbnailAccess getThumbnailAccess();
    public IUserTwitterLinkAccess getTwitterLinkageAccess();
    public ITwitterMessageAccess getTwitterMessageAccess();
    public IUserAccess getUserAccess();
    public IUserReceivedMessageAccess getUserReceivedMessageAccess();
    public IUserSentMessageAccess getUserSentMessageAccess();
    public IUserNotificationAccess getUserNotificationAccess();
    public IUserPreferenceAccess getUserPreferenceAccess();
}
