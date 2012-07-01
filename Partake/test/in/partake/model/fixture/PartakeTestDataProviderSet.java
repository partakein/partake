package in.partake.model.fixture;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.fixture.impl.UserCalendarLinkTestDataProvider;
import in.partake.model.fixture.impl.EventCommentTestDataProvider;
import in.partake.model.fixture.impl.UserTicketTestDataProvider;
import in.partake.model.fixture.impl.EventActivityTestDataProvider;
import in.partake.model.fixture.impl.EventFeedTestDataProvider;
import in.partake.model.fixture.impl.EventMessageTestDataProvider;
import in.partake.model.fixture.impl.EventTestDataProvider;
import in.partake.model.fixture.impl.EventTicketNotificationTestDataProvider;
import in.partake.model.fixture.impl.EventTicketTestDataProvider;
import in.partake.model.fixture.impl.UserImageTestDataProvider;
import in.partake.model.fixture.impl.MessageEnvelopeTestDataProvider;
import in.partake.model.fixture.impl.MessageTestDataProvider;
import in.partake.model.fixture.impl.UserOpenIDLinkTestDataProvider;
import in.partake.model.fixture.impl.UserThumbnailTestDataProvider;
import in.partake.model.fixture.impl.UserTwitterLinkTestDataProvider;
import in.partake.model.fixture.impl.TwitterMessageTestDataProvider;
import in.partake.model.fixture.impl.UserNotificationTestDataProvider;
import in.partake.model.fixture.impl.UserPreferenceTestDataProvider;
import in.partake.model.fixture.impl.UserReceivedMessageTestDataProvider;
import in.partake.model.fixture.impl.UserSentMessageTestDataProvider;
import in.partake.model.fixture.impl.UserTestDataProvider;

import java.util.ArrayList;

/**
 * A set of test data providers.
 * @author shinyak
 *
 */
public class PartakeTestDataProviderSet {
    private ArrayList<TestDataProvider<?>> providers;

    private UserCalendarLinkTestDataProvider calendarDataProvider;
    private EventCommentTestDataProvider commentDataprovider;
    private UserTicketTestDataProvider enrollmentProvider;
    private EventTestDataProvider eventProvider;
    private UserImageTestDataProvider imageProvider;
    private UserOpenIDLinkTestDataProvider openIDLinkageProvider;
    private UserTwitterLinkTestDataProvider twitterLinkageProvider;
    private UserTestDataProvider userProvider;
    private UserReceivedMessageTestDataProvider userMessageProvider;
    private UserPreferenceTestDataProvider userPreferenceProvider;
    private UserSentMessageTestDataProvider userSentMessageProvider;
    private UserNotificationTestDataProvider userNotificationProvider;
    private EventActivityTestDataProvider eventActivityProvider;
    private EventFeedTestDataProvider eventFeedProvider;
    private EventMessageTestDataProvider eventMessageProvider;
    private EventTicketNotificationTestDataProvider eventTicketNotificationProvider;
    private EventTicketTestDataProvider eventTicketProvider;
    private MessageTestDataProvider messageProvider;
    private MessageEnvelopeTestDataProvider messageEnvelopeProvider;
    private UserThumbnailTestDataProvider thumbnailProvider;
    private TwitterMessageTestDataProvider twitterMessageProvider;

    public PartakeTestDataProviderSet() {
        this.providers = new ArrayList<TestDataProvider<?>>();

        providers.add(calendarDataProvider = createCalendarLinkageTestDataProvider());
        providers.add(commentDataprovider = createCommentTestDataProvider());
        providers.add(enrollmentProvider = createEnrollmentTestDataProvider());
        providers.add(eventProvider = createEventTestDataProvider());
        providers.add(imageProvider = createImageTestDataProvider());
        providers.add(openIDLinkageProvider = createOpenIDLinkageTestDataProvider());
        providers.add(twitterLinkageProvider = createTwitterLinkageTestDataProvider());
        providers.add(userProvider = createUserTestDataProvider());
        providers.add(userPreferenceProvider = createUserPreferenceTestDataProvider());
        providers.add(eventActivityProvider = createEventActivityTestDataProvider());
        providers.add(eventFeedProvider = createEventFeedTestDataProvider());
        providers.add(eventMessageProvider = createEventMessageTestDataProvider());
        providers.add(eventTicketNotificationProvider = createEventNotificationTestDataProvider());
        providers.add(eventTicketProvider = createEventTicketTestDataProvider());
        providers.add(messageProvider = createMessageTestDataProvider());
        providers.add(messageEnvelopeProvider = createMessageEnvelopeTestDataProvider());
        providers.add(thumbnailProvider = createThumbnailTestDataProvider());
        providers.add(twitterMessageProvider = createTwitterMessageTestDataProvider());
        providers.add(userMessageProvider = createUserReceivedMessageTestDataProvider());
        providers.add(userSentMessageProvider = createUserSentMessageTestDataProvider());
        providers.add(userNotificationProvider = createUserNotificationTestDataProvider());
    }

    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        for (TestDataProvider<?> provider : providers) {
            provider.createFixtures(con, daos);
        }
    }

    public UserCalendarLinkTestDataProvider getCalendarTestDataProvider() {
        return calendarDataProvider;
    }

    public EventCommentTestDataProvider getCommentDataProvider() {
        return commentDataprovider;
    }

    public UserTicketTestDataProvider getEnrollmentProvider() {
        return enrollmentProvider;
    }

    public EventTestDataProvider getEventProvider() {
        return eventProvider;
    }

    public EventTicketTestDataProvider getEventTicketProvider() {
        return eventTicketProvider;
    }

    public UserImageTestDataProvider getImageProvider() {
        return imageProvider;
    }

    public UserOpenIDLinkTestDataProvider getOpenIDLinkageProvider() {
        return openIDLinkageProvider;
    }

    public UserTwitterLinkTestDataProvider getTwitterLinkageProvider() {
        return twitterLinkageProvider;
    }

    public UserTestDataProvider getUserProvider() {
        return userProvider;
    }

    public UserPreferenceTestDataProvider getUserPreferenceProvider() {
        return userPreferenceProvider;
    }

    public EventActivityTestDataProvider getEventActivityProvider() {
        return eventActivityProvider;
    }

    public EventFeedTestDataProvider getEventFeedProvider() {
        return eventFeedProvider;
    }

    public EventMessageTestDataProvider getEventMessageProvider() {
        return eventMessageProvider;
    }

    public EventTicketNotificationTestDataProvider getEventTicketNotificationProvider() {
        return eventTicketNotificationProvider;
    }

    public MessageTestDataProvider getMessageProvider() {
        return messageProvider;
    }

    public MessageEnvelopeTestDataProvider getMessageEnvelopeProvider() {
        return messageEnvelopeProvider;
    }

    public UserThumbnailTestDataProvider getThumbnailProvider() {
        return thumbnailProvider;
    }

    public TwitterMessageTestDataProvider getTwitterMessageProvider() {
        return twitterMessageProvider;
    }

    public UserReceivedMessageTestDataProvider getUserReceivedMessageProvider() {
        return userMessageProvider;
    }

    public UserSentMessageTestDataProvider getUserSentMessageProvider() {
        return userSentMessageProvider;
    }

    public UserNotificationTestDataProvider getUserNotificationProvider() {
        return userNotificationProvider;
    }

    private UserCalendarLinkTestDataProvider createCalendarLinkageTestDataProvider() {
        return new UserCalendarLinkTestDataProvider();
    }

    private EventCommentTestDataProvider createCommentTestDataProvider() {
        return new EventCommentTestDataProvider();
    }

    private UserTicketTestDataProvider createEnrollmentTestDataProvider() {
        return new UserTicketTestDataProvider();
    }

    private EventTestDataProvider createEventTestDataProvider() {
        return new EventTestDataProvider();
    }

    private UserImageTestDataProvider createImageTestDataProvider() {
        return new UserImageTestDataProvider();
    }

    private UserOpenIDLinkTestDataProvider createOpenIDLinkageTestDataProvider() {
        return new UserOpenIDLinkTestDataProvider();
    }

    private UserTwitterLinkTestDataProvider createTwitterLinkageTestDataProvider() {
        return new UserTwitterLinkTestDataProvider();
    }

    private UserTestDataProvider createUserTestDataProvider() {
        return new UserTestDataProvider();
    }

    private UserPreferenceTestDataProvider createUserPreferenceTestDataProvider() {
        return new UserPreferenceTestDataProvider();
    }

    private EventActivityTestDataProvider createEventActivityTestDataProvider() {
        return new EventActivityTestDataProvider();
    }

    private EventFeedTestDataProvider createEventFeedTestDataProvider() {
        return new EventFeedTestDataProvider();
    }

    private EventMessageTestDataProvider createEventMessageTestDataProvider() {
        return new EventMessageTestDataProvider();
    }

    private EventTicketNotificationTestDataProvider createEventNotificationTestDataProvider() {
        return new EventTicketNotificationTestDataProvider();
    }

    private MessageTestDataProvider createMessageTestDataProvider() {
        return new MessageTestDataProvider();
    }

    private MessageEnvelopeTestDataProvider createMessageEnvelopeTestDataProvider() {
        return new MessageEnvelopeTestDataProvider();
    }

    private TwitterMessageTestDataProvider createTwitterMessageTestDataProvider() {
        return new TwitterMessageTestDataProvider();
    }

    private UserThumbnailTestDataProvider createThumbnailTestDataProvider() {
        return new UserThumbnailTestDataProvider();
    }

    private UserReceivedMessageTestDataProvider createUserReceivedMessageTestDataProvider() {
        return new UserReceivedMessageTestDataProvider();
    }

    private UserSentMessageTestDataProvider createUserSentMessageTestDataProvider() {
        return new UserSentMessageTestDataProvider();
    }

    private UserNotificationTestDataProvider createUserNotificationTestDataProvider() {
        return new UserNotificationTestDataProvider();
    }

    private EventTicketTestDataProvider createEventTicketTestDataProvider() {
        return new EventTicketTestDataProvider();
    }
}
