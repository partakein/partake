package in.partake.controller.action.event;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;

public class EventPrivacyEditAction extends AbstractEventEditAction {
    private String eventId;

    public static Result get(String eventId) throws DAOException, PartakeException {
        EventPrivacyEditAction action = new EventPrivacyEditAction();
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

        return render(views.html.events.edit_privacy.render(context(), event));
    }
}


