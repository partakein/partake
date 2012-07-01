package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.EventEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.resource.UserErrorCode;
import play.mvc.Result;

public class EventEditAction extends AbstractEventEditAction {
    private String eventId;

    public static Result get(String eventId) throws DAOException, PartakeException {
        EventEditAction action = new EventEditAction();
        action.eventId = eventId;
        return action.execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        checkIdParameterIsValid(eventId, UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        event = new EventEditTransaction(eventId).execute();
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_NOTFOUND);

        if (!EventEditPermission.check(event, user))
            return renderForbidden(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        return render(views.html.events.edit_basic.render(context(), event, user));
    }
}

class EventEditTransaction extends DBAccess<EventEx> {
    private String eventId;

    public EventEditTransaction(String eventId) {
        this.eventId = eventId;
    }

    @Override
    protected EventEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        return EventDAOFacade.getEventEx(con, daos, eventId);
    }
}
