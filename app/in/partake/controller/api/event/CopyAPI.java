package in.partake.controller.api.event;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.resource.UserErrorCode;

public class CopyAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new CopyAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();

        String eventId = getValidEventIdParameter();
        String newEventId = new CopyTransaction(user, eventId).execute();

        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("eventId", newEventId);
        return renderOK(obj);
    }
}

class CopyTransaction extends Transaction<String> {
    private UserEx user;
    private String eventId;

    public CopyTransaction(UserEx user, String eventId) {
        this.user = user;
        this.eventId = eventId;
    }

    @Override
    protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        Event event = daos.getEventAccess().find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        if (!EventEditPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_COPY);

        return EventDAOFacade.copy(con, daos, user, event);
    }
}
