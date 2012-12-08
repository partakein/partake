package in.partake.controller.base;

import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.PartakeActionContext;
import in.partake.controller.PartakeTestContext;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.resource.Constants;
import in.partake.resource.MessageCode;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

public abstract class AbstractPartakeController extends Controller {
    private PartakeActionContextImpl ctx;

    // ----------------------------------------------------------------------
    // Execute

    public final Result execute() {
        Result result = executeInternal();

        // For testing purpose, we would like to expose the action.
        PartakeTestContext.setAction(this);

        return result;
    }

    private final Result executeInternal() {
        long begin = System.currentTimeMillis();
        Logger.info("processing... " + request().uri());

        try {
            ensureContext();
            return doExecute();
        } catch (DAOException e) {
            return renderError(ServerErrorCode.DB_ERROR, null, e);
        } catch (PartakeException e) {
            return renderException(e);
        } catch (RuntimeException e) {
            return renderError(ServerErrorCode.UNKNOWN_ERROR, null, e);
        } catch (Exception e) {
            return renderError(ServerErrorCode.UNKNOWN_ERROR, null, e);
        } finally {
            long end = System.currentTimeMillis();
            Logger.info(request().uri() + " took "+ (end - begin) + "[msec] to process.");
        }
    }

    private void ensureContext() throws DAOException, PartakeException {
        PartakeActionContextImpl impl = new PartakeActionContextImpl();

        final String userId = session().get(Constants.Session.USER_ID_KEY);

        impl.setCurrentURL(request().uri());
        if (userId != null) {
            impl.loginUser = new DBAccess<UserEx>() {
                @Override
                protected UserEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                    return UserDAOFacade.getUserEx(con, daos, userId);
                }
            }.execute();
        }

        if (!session().containsKey(Constants.Session.ID_KEY))
            session().put(Constants.Session.ID_KEY, UUID.randomUUID().toString());
        if (!session().containsKey(Constants.Session.TOKEN_KEY))
            session().put(Constants.Session.TOKEN_KEY, UUID.randomUUID().toString());

        impl.messageCodes = new ArrayList<MessageCode>();
        if (flash().get(Constants.Flash.MESSAGE_ID) != null) {
            MessageCode code = MessageCode.safeValueOf(flash().get(Constants.Flash.MESSAGE_ID));
            impl.messageCodes.add(code);
        }

        impl.sessionToken = session().get(Constants.Session.TOKEN_KEY);
        this.ctx = impl;
    }

    protected PartakeActionContext context() {
        return this.ctx;
    }

    protected abstract Result doExecute() throws PartakeException, DAOException;

    // ----------------------------------------------------------------------
    // Render

    protected abstract Result renderInvalid(UserErrorCode ec, Map<String, String> additionalInfo, Throwable e);
    protected abstract Result renderError(ServerErrorCode ec, Map<String, String> additionalInfo, Throwable e);
    protected abstract Result renderLoginRequired();
    protected abstract Result renderForbidden();
    protected abstract Result renderNotFound();

    protected Result renderException(PartakeException e) {
        if (e.getStatusCode() == 401)
            return renderLoginRequired();
        if (e.getStatusCode() == 403)
            return renderForbidden();
        if (e.getStatusCode() == 404)
            return renderNotFound();

        if (e.isUserError())
            return renderInvalid(e.getUserErrorCode(), e.getAdditionalInfo(), e.getCause());

        assert e.isServerError();
        return renderError(e.getServerErrorCode(), e.getAdditionalInfo(), e.getCause());
    }

    protected Result renderInvalid(UserErrorCode ec) {
        return renderInvalid(ec, null, null);
    }

    protected Result renderInvalid(UserErrorCode ec, Throwable e) {
        return renderInvalid(ec, null, e);
    }

    protected Result renderInvalid(UserErrorCode ec, Map<String, String> additionalInfo) {
        return renderInvalid(ec, additionalInfo, null);
    }

    protected Result renderError(ServerErrorCode errorCode) {
        return renderError(errorCode, null, null);
    }

    protected Result renderError(ServerErrorCode errorCode, Throwable e) {
        return renderError(errorCode, null, e);
    }

    // ----------------------------------------------------------------------
    // Parameter

    protected String getFormParameter(String key) {
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        if (map == null)
            return null;

        String[] params = map.get(key);
        if (params == null || params.length <= 0)
            return null;

        return params[0];
    }

    protected String getQueryStringParameter(String key) {
        Map<String, String[]> map = request().queryString();
        if (map == null)
            return null;

        String[] params = map.get(key);
        if (params == null)
            return null;
        if (params.length <= 0)
            return null;
        return params[0];
    }

    protected Map<String, String[]> getFormParameters() {
        return request().body().asFormUrlEncoded();
    }

    @Deprecated
    protected String getParameter(String key) {
        String[] values = getParameters(key);
        if (values == null || values.length == 0)
            return null;

        return values[0];
    }

    /**
     * Gets boolean parameter. If parameter does not exist, null will be returned.
     * @param key
     * @param defaultValue
     * @return
     */
    protected Boolean getBooleanParameter(String key) {
        String value = getParameter(key);
        if (value == null)
            return null;

        return Util.parseBooleanParameter(value);
    }

    protected boolean optBooleanParameter(String key, boolean defaultValue) {
        Boolean value = getBooleanParameter(key);
        if (value != null)
            return value;
        return defaultValue;
    }

    protected Integer getIntegerParameter(String key) {
        String value = getParameter(key);
        if (value == null)
            return null;

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected int optIntegerParameter(String key, int defaultValue) {
        Integer value = getIntegerParameter(key);
        if (value != null)
            return value;
        return defaultValue;
    }

    protected Long getLongParameter(String key) {
        String value = getParameter(key);
        if (value == null)
            return null;

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected DateTime getDateTimeParameter(String key) {
        String value = getParameter(key);
        if (value == null)
            return null;

        DateTime date = TimeUtil.parseForEvent(value);
        if (date != null)
            return date;

        // Try parse it as long.
        try {
            long time = Long.valueOf(value);
            return new DateTime(time);
        } catch (NumberFormatException e) {
            // Do nothing.
        }

        return null;
    }

    protected UUID getValidUUIDParameter(String key, UserErrorCode missing, UserErrorCode invalid) throws PartakeException {
        String id = getParameter(key);
        if (id == null)
            throw new PartakeException(missing);
        if (!Util.isUUID(id))
            throw new PartakeException(invalid);

        return UUID.fromString(id);
    }
    protected String getValidIdParameter(String key, UserErrorCode missing, UserErrorCode invalid) throws PartakeException {
        String id = getParameter(key);
        checkIdParameterIsValid(id, missing, invalid);
        return id;
    }

    protected void checkIdParameterIsValid(String id, UserErrorCode missing, UserErrorCode invalid) throws PartakeException {
        if (id == null)
            throw new PartakeException(missing);
        if (!Util.isUUID(id))
            throw new PartakeException(invalid);
    }

    protected String optValidIdParameter(String key, UserErrorCode invalid, String defaultValue) throws PartakeException {
        String id = getParameter(key);
        if (id == null)
            return defaultValue;
        if (!Util.isUUID(id))
            throw new PartakeException(invalid);

        return id;
    }

    protected String getValidUserIdParameter(UserErrorCode missing, UserErrorCode invalid) throws PartakeException {
        return getValidIdParameter("userId", missing, invalid);
    }

    protected String getValidUserIdParameter() throws PartakeException {
        return getValidIdParameter("userId", UserErrorCode.MISSING_USER_ID, UserErrorCode.INVALID_USER_ID);
    }

    protected String getValidEventIdParameter() throws PartakeException {
        return getValidIdParameter("eventId", UserErrorCode.MISSING_EVENT_ID, UserErrorCode.INVALID_EVENT_ID);
    }

    protected UUID getValidTicketIdParameter() throws PartakeException {
        return getValidUUIDParameter("ticketId", UserErrorCode.MISSING_TICKET_ID, UserErrorCode.INVALID_TICKET_ID);
    }

    protected UUID getValidTicketIdParameter(UserErrorCode missing, UserErrorCode invalid) throws PartakeException {
        return getValidUUIDParameter("ticketId", missing, invalid);
    }

    protected String getValidEventIdParameter(UserErrorCode missing, UserErrorCode invalid) throws PartakeException {
        return getValidIdParameter("eventId", missing, invalid);
    }

    protected String getValidImageIdParameter() throws PartakeException {
        return getValidIdParameter("imageId", UserErrorCode.MISSING_IMAGEID, UserErrorCode.INVALID_IMAGEID);
    }

    protected String getValidCommentIdParameter() throws PartakeException {
        return getValidIdParameter("commentId", UserErrorCode.MISSING_COMMENT_ID, UserErrorCode.INVALID_COMMENT_ID);
    }

    protected void ensureValidSessionToken() throws PartakeException {
        if (!checkCSRFToken())
            throw new PartakeException(UserErrorCode.INVALID_SECURITY_CSRF);
    }

    protected String[] ensureParameters(String key, UserErrorCode ec) throws PartakeException {
        String[] params = getParameters(key);
        if (params == null)
            throw new PartakeException(ec);

        return params;
    }

    protected String[] ensureParameters(String key, int n, UserErrorCode ec) throws PartakeException {
        String[] params = getParameters(key);
        if (params == null) {
            if (n == 0)
                return new String[0];
            else
                throw new PartakeException(ec);
        }

        if (params.length != n)
            throw new PartakeException(ec);

        return params;
    }

    /**
     * take multiple parameters. If there is a single parameter, a new array will be created to return.
     * @param key
     * @return
     */
    @Deprecated
    protected String[] getParameters(String key) {
        String[] params = request().queryString().get(key);
        if (params != null)
            return params;

        if (request().body().asFormUrlEncoded() != null)
            return request().body().asFormUrlEncoded().get(key);

        if (request().body().asMultipartFormData() != null)
            return request().body().asMultipartFormData().asFormUrlEncoded().get(key);

        return null;
    }

    @Deprecated
    protected Map<String, String[]> getParameters() {
        return request().queryString();
    }

    public void setRedirectURL(String url) {
        ctx.redirectURL = url;
    }

    public String getRedirectURL() {
        return ctx.redirectURL;
    }

    public void setCurrentURL(String url) {
        ctx.setCurrentURL(url);
    }

    public String getCurrentURL() {
        return ctx.currentURL();
    }

    // ----------------------------------------------------------------------
    // Utility function

    /**
     * get logged in user. If a user is not logged in, null is returned.
     * @return the logged in user. null if a user is not logged in.
     */
    protected UserEx getLoginUser() {
        return ctx.loginUser;
    }

    protected UserEx ensureLogin() throws PartakeException {
        UserEx user = getLoginUser();
        if (user == null)
            throw new PartakeException(UserErrorCode.INVALID_LOGIN_REQUIRED);

        return user;
    }

    protected UserEx ensureAdmin() throws PartakeException {
        UserEx user = ensureLogin();
        if (!user.isAdministrator())
            throw new PartakeException(UserErrorCode.INVALID_PROHIBITED);

        return user;
    }

    // ----------------------------------------------------------------------
    // CSRF

    private boolean checkCSRFToken() {
        String sessionToken = getParameter(Constants.Parameter.SESSION_TOKEN);
        if (sessionToken == null)
            return false;

        String originalSessionToken = session().get(Constants.Session.TOKEN_KEY);
        if (originalSessionToken == null)
            return false;

        return StringUtils.equals(sessionToken, originalSessionToken);
    }
}

class PartakeActionContextImpl implements PartakeActionContext {
    UserEx loginUser;
    String sessionToken;
    String currentURL;
    String redirectURL;
    List<MessageCode> messageCodes = new ArrayList<MessageCode>();

    public void setLoginUser(UserEx loginUser) {
        this.loginUser = loginUser;
    }
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
    public void setCurrentURL(String currentURL) {
        this.currentURL = currentURL;
    }

    @Override
    public UserEx loginUser() {
        return loginUser;
    }

    @Override
    public String sessionToken() {
        return sessionToken;
    }

    @Override
    public String currentURL() {
        return currentURL;
    }

    @Override
    public String redirectURL() {
        if (Strings.isNullOrEmpty(redirectURL)) {
            return currentURL;
        } else {
            return redirectURL;
        }
    }

    @Override
    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    @Override
    public void addMessage(MessageCode mc) {
        messageCodes.add(mc);
    }

    @Override
    public List<MessageCode> messages() {
        return Collections.unmodifiableList(messageCodes);
    }
}
