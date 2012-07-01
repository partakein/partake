package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventNotificationListPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicketNotification;
import in.partake.resource.UserErrorCode;

import java.util.List;

import play.mvc.Result;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetNotificationsAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new GetNotificationsAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        String eventId = getValidEventIdParameter();

        int offset = optIntegerParameter("offset", 0);
        offset = Util.ensureRange(offset, 0, Integer.MAX_VALUE);

        int limit = optIntegerParameter("limit", 10);
        limit = Util.ensureRange(limit, 1, 100);

        List<EventTicketNotification> notifications = new GetNotificationsAccess(user, eventId, offset, limit).execute();
        JSONArray array = Util.toJSONArray(notifications);

        JSONObject obj = new JSONObject();
        obj.put("notifications", array);
        return renderOK(obj);
    }
}

class GetNotificationsAccess extends Transaction<List<EventTicketNotification>> {
    private UserEx user;
    private String eventId;
    private int offset;
    private int limit;

    public GetNotificationsAccess(UserEx user, String eventId, int offset, int limit) {
        this.user = user;
        this.eventId = eventId;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    protected List<EventTicketNotification> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        Event event = daos.getEventAccess().find(con, eventId);

        if (!EventNotificationListPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_SHOW_NOTIFICATION);

        return daos.getEventNotificationAccess().findByEventId(con, eventId, offset, limit);
    }
}
