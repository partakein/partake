package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.PrivateEventShowPermission;
import in.partake.model.EventEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.resource.UserErrorCode;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import play.mvc.Result;

public class GetAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new GetAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        String eventId = getValidEventIdParameter();
        UserEx user = getLoginUser();
        String passcode = getParameter("passcode");
        if (passcode == null)
            passcode = session().get("event:" + eventId);

        EventEx event = new GetTransaction(user, eventId, passcode).execute();

        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("event", event.toSafeJSON());
        return renderOK(obj);
    }
}

class GetTransaction extends DBAccess<EventEx> {
    private String eventId;
    private UserEx user;
    private String passcode;

    public GetTransaction(UserEx user, String eventId, String passcode) {
        this.user = user;
        this.eventId = eventId;
        this.passcode = passcode;
    }

    @Override
    protected EventEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        EventEx event = EventDAOFacade.getEventEx(con, daos, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        if (!StringUtils.isBlank(event.getPasscode())) {
            // owner および manager は見ることが出来る。
            if (user != null && PrivateEventShowPermission.check(event, user)) {
                // OK. You have the right to show this event.
            } else if (StringUtils.equals(event.getPasscode(), passcode)) {
                // OK. The same passcode.
            } else {
                // public でなければ、passcode を入れなければ見ることが出来ない
                throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_SHOW);
            }
        }

        return event;
    }
}
