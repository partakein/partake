package in.partake.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.AbstractPartakeTestWithApplication;
import in.partake.app.PartakeTestApp;
import in.partake.base.PartakeException;
import in.partake.base.PartakeParamNamesConstants;
import in.partake.base.TimeUtil;
import in.partake.model.EventEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.daoutil.DAOUtil;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.EventTicketNotification;
import in.partake.model.dto.MessageEnvelope;
import in.partake.model.dto.TwitterMessage;
import in.partake.model.dto.UserCalendarLink;
import in.partake.model.dto.UserImage;
import in.partake.model.dto.UserNotification;
import in.partake.model.dto.UserOpenIDLink;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.UserReceivedMessage;
import in.partake.model.dto.UserThumbnail;
import in.partake.model.dto.UserTicket;
import in.partake.model.fixture.TestDataProviderConstants;
import in.partake.resource.Constants;
import in.partake.resource.ServerErrorCode;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Before;

import play.test.Helpers;

public abstract class AbstractPartakeControllerTest
    extends AbstractPartakeTestWithApplication
    implements TestDataProviderConstants, PartakeActionURLConstants, PartakeParamNamesConstants {

    enum HttpMethod {
        GET,
        POST
    }

    protected final HttpMethod GET = HttpMethod.GET;
    protected final HttpMethod POST = HttpMethod.POST;

    // Make setUp called before each test.
    @Before
    public void setUp() throws Exception {
        PartakeTestApp.getTestService().setDefaultFixtures();
        TimeUtil.resetCurrentDate();
    }

    protected ActionProxy getActionProxy(HttpMethod method, String url) {
        switch (method) {
        case GET:
            return ActionProxy.get(url);
        case POST:
            return ActionProxy.post(url);
        }

        throw new RuntimeException("method should not be null");
    }

    /** log in した状態にする */
    protected void loginAs(ActionProxy proxy, String userId) throws DAOException, PartakeException {
        proxy.addSession(Constants.Session.USER_ID_KEY, userId);
    }

    /** logout する */
    protected void logout(ActionProxy proxy) throws DAOException {
        proxy.addSession(Constants.Session.USER_ID_KEY, null);
    }

    protected void addFormParameter(ActionProxy proxy, String key, String value) {
        proxy.addFormParameter(key, value);
    }

    protected void addFormParameter(ActionProxy proxy, String key, String[] values) {
        for (String value : values)
            addFormParameter(proxy, key, value);
    }

    @Deprecated
    protected void addParameter(ActionProxy proxy, String key, Object obj) {
        if (obj instanceof String)
            addFormParameter(proxy, key, (String) obj);
        else if (obj instanceof String[])
            addFormParameter(proxy, key, (String[]) obj);
        else
            throw new RuntimeException("ASSERT_NOT_REACHED");
    }

    protected void addValidSessionTokenToParameter(ActionProxy proxy) {
        String validToken = proxy.session(Constants.Session.TOKEN_KEY);
        if (StringUtils.isEmpty(validToken)) {
            validToken = UUID.randomUUID().toString();
            proxy.addSession(Constants.Session.TOKEN_KEY, validToken);
        }

        addFormParameter(proxy, Constants.Session.TOKEN_KEY, validToken);
    }

    protected void addInvalidSessionTokenToParameter(ActionProxy proxy) {
        String validToken = proxy.session(Constants.Session.TOKEN_KEY);
        if (StringUtils.isEmpty(validToken)) {
            validToken = UUID.randomUUID().toString();
            proxy.addSession(Constants.Session.TOKEN_KEY, validToken);
        }

        String invalidToken = validToken + "-invalid";
        addFormParameter(proxy, Constants.Session.TOKEN_KEY, invalidToken);
    }

    // ----------------------------------------------------------------------

    protected void assertLoggedOut(ActionProxy proxy) {
        assertThat(Helpers.session(proxy.getResult()).get(Constants.Session.USER_ID_KEY), nullValue());
    }

    protected void assertResultLoginRequired(ActionProxy proxy) throws Exception {
        assertThat(Helpers.redirectLocation(proxy.getResult()), Matchers.startsWith("/loginRequired"));
    }

    protected void assertResultRedirect(ActionProxy proxy, String url) throws Exception {
        assertThat(Helpers.status(proxy.getResult()), is(303));

        if (url != null)
            assertThat(Helpers.redirectLocation(proxy.getResult()), is(url));
    }

    protected void assertResultForbidden(ActionProxy proxy) throws Exception {
        assertThat(Helpers.redirectLocation(proxy.getResult()), Matchers.startsWith("/forbidden"));
    }

    protected void assertResultNotFound(ActionProxy proxy) throws Exception {
        assertThat(Helpers.redirectLocation(proxy.getResult()), Matchers.startsWith("/notfound"));
    }

    protected void assertResultError(ActionProxy proxy) throws Exception {
        assertThat(Helpers.redirectLocation(proxy.getResult()), Matchers.startsWith("/error"));
    }

    protected void assertResultError(ActionProxy proxy, ServerErrorCode errorCode) throws Exception {
        assertResultError(proxy);
        assertThat(Helpers.redirectLocation(proxy.getResult()), is("/error?errorCode=" + errorCode.getErrorCode()));
    }

    // ----------------------------------------------------------------------
    // DB Accessors

    protected List<MessageEnvelope> loadMessageEnvelopes() throws DAOException, PartakeException {
        return new DBAccess<List<MessageEnvelope>>() {
            @Override
            protected List<MessageEnvelope> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return DAOUtil.convertToList(daos.getMessageEnvelopeAccess().getIterator(con));
            }
        }.execute();
    }

    protected UserEx loadUserEx(final String userId) throws DAOException, PartakeException {
        return new DBAccess<UserEx>() {
            @Override
            protected UserEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return UserDAOFacade.getUserEx(con, daos, userId);
            }
        }.execute();
    }

    protected UserPreference loadUserPreference(final String userId) throws DAOException, PartakeException {
        return new DBAccess<UserPreference>() {
            @Override
            protected UserPreference doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getUserPreferenceAccess().find(con, userId);
            }
        }.execute();
    }

    protected Event loadEvent(final String eventId) throws DAOException, PartakeException {
        return new DBAccess<Event>() {
            @Override
            protected Event doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getEventAccess().find(con, eventId);
            }
        }.execute();
    }

    protected EventEx loadEventEx(final String eventId) throws DAOException, PartakeException {
        return new DBAccess<EventEx>() {
            @Override
            protected EventEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return EventDAOFacade.getEventEx(con, daos, eventId);
            }
        }.execute();
    }

    protected String storeEvent(final Event event) throws DAOException, PartakeException {
        return new Transaction<String>() {
            protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException ,PartakeException {
                if (event.getId() == null) {
                    String eventId = daos.getEventAccess().getFreshId(con);
                    event.setId(eventId);
                }
                daos.getEventAccess().put(con, event);
                return event.getId();
            };
        }.execute();
    }

    protected List<EventTicket> loadEventTickets(final String eventId) throws DAOException, PartakeException {
        return new Transaction<List<EventTicket>>() {
            protected List<EventTicket> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException ,PartakeException {
                return daos.getEventTicketAccess().findEventTicketsByEventId(con, eventId);
            };
        }.execute();
    }

    protected EventTicket loadEventTicket(final UUID eventTicketId) throws DAOException, PartakeException {
        return new Transaction<EventTicket>() {
            protected EventTicket doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getEventTicketAccess().find(con, eventTicketId);
            };
        }.execute();
    }

    protected void storeEventTicket(final EventTicket ticket) throws DAOException, PartakeException {
        new Transaction<Void>() {
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException ,PartakeException {
                daos.getEventTicketAccess().put(con, ticket);
                return null;
            };
        }.execute();
    }

    protected List<UserOpenIDLink> loadOpenIDIdentifiers(final String userId) throws DAOException, PartakeException {
        return new DBAccess<List<UserOpenIDLink>>() {
            @Override
            protected List<UserOpenIDLink> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getOpenIDLinkageAccess().findByUserId(con, userId);
            }
        }.execute();
    }

    protected MessageEnvelope loadEnvelope(final String id) throws DAOException, PartakeException {
        return new DBAccess<MessageEnvelope>() {
            @Override
            protected MessageEnvelope doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getMessageEnvelopeAccess().find(con, id);
            }
        }.execute();
    }


    protected List<MessageEnvelope> loadEnvelopes() throws DAOException, PartakeException {
        return new DBAccess<List<MessageEnvelope>>() {
            @Override
            protected List<MessageEnvelope> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return DAOUtil.convertToList(daos.getMessageEnvelopeAccess().getIterator(con));
            }
        }.execute();
    }

    protected String loadCalendarIdFromUser(final String userId) throws DAOException, PartakeException {
        return new DBAccess<String>() {
            @Override
            protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                UserCalendarLink linkage = daos.getCalendarAccess().findByUserId(con, userId);
                if (linkage == null)
                    return null;
                return linkage.getId();
            }
        }.execute();
    }

    protected UserTicket loadEnrollment(final String enrollmentId) throws DAOException, PartakeException {
        return new DBAccess<UserTicket>() {
            @Override
            protected UserTicket doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getEnrollmentAccess().find(con, enrollmentId);
            }
        }.execute();
    }

    protected UserTicket loadEnrollment(final String userId, final UUID ticketId) throws DAOException, PartakeException {
        return new DBAccess<UserTicket>() {
            @Override
            protected UserTicket doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getEnrollmentAccess().findByTicketIdAndUserId(con, ticketId, userId);
            }
        }.execute();
    }

    protected List<EventTicketNotification> loadEventTicketNotificationsByEventId(final UUID ticketId) throws Exception {
        return new DBAccess<List<EventTicketNotification>>() {
            @Override
            protected List<EventTicketNotification> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getEventNotificationAccess().findByTicketId(con, ticketId, 0, Integer.MAX_VALUE);
            }
        }.execute();
    }

    protected String storeEnrollment(final UserTicket enrollment) throws DAOException, PartakeException {
        return new Transaction<String>() {
            @Override
            protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                if (enrollment.getId() == null) {
                    String enrollmentId = daos.getEnrollmentAccess().getFreshId(con);
                    enrollment.setId(enrollmentId);
                }
                daos.getEnrollmentAccess().put(con, enrollment);
                return enrollment.getId();
            }
        }.execute();
    }

    protected UserImage loadImage(final String imageId) throws DAOException, PartakeException {
        return new DBAccess<UserImage>() {
            @Override
            protected UserImage doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getImageAccess().find(con, imageId);
            }
        }.execute();
    }

    protected UserThumbnail loadThumbnail(final String imageId) throws DAOException, PartakeException {
        return new DBAccess<UserThumbnail>() {
            @Override
            protected UserThumbnail doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getThumbnailAccess().find(con, imageId);
            }
        }.execute();
    }

    protected TwitterMessage loadTwitterMessage(final String twitterMessageId) throws DAOException, PartakeException {
        return new DBAccess<TwitterMessage>() {
            @Override
            protected TwitterMessage doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getTwitterMessageAccess().find(con, twitterMessageId);
            }
        }.execute();
    }

    protected UserNotification loadUserNotification(final String userNotificationId) throws DAOException, PartakeException {
        return new DBAccess<UserNotification>() {
            @Override
            protected UserNotification doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getUserNotificationAccess().find(con, userNotificationId);
            }
        }.execute();
    }

    protected List<UserNotification> loadUserNotificationsByUserId(final String userId) throws Exception {
        return new DBAccess<List<UserNotification>>() {
            @Override
            protected List<UserNotification> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getUserNotificationAccess().findByUserId(con, userId, 0, Integer.MAX_VALUE);
            }
        }.execute();
    }

    protected UserReceivedMessage loadUserReceivedMessage(final UUID userReceivedMessageId) throws DAOException, PartakeException {
        return new DBAccess<UserReceivedMessage>() {
            @Override
            protected UserReceivedMessage doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getUserReceivedMessageAccess().find(con, userReceivedMessageId);
            }
        }.execute();
    }

}
