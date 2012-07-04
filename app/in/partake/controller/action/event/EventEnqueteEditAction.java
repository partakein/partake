package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;
import play.mvc.Result;

public class EventEnqueteEditAction extends AbstractEventEditAction {
    private String eventId;

    public static Result get(String eventId) throws DAOException, PartakeException {
        EventEnqueteEditAction action = new EventEnqueteEditAction();
        action.eventId = eventId;
        return action.execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        checkIdParameterIsValid(eventId, UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        event = new EventEditTransaction(eventId).execute();
        if (event == null)
            return renderNotFound();

        if (!EventEditPermission.check(event, user))
            return renderForbidden(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        return render(views.html.events.edit_enquete.render(context(), event));
    }
}


