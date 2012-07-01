package in.partake.controller.api.event;

import play.mvc.Result;
import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventRemovePermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dto.Event;
import in.partake.resource.UserErrorCode;

public class RemoveAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new RemoveAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();

        new RemoveEventTransaction(user, eventId).execute();
        PartakeApp.getEventSearchService().remove(eventId);

        return renderOK();
    }
}

class RemoveEventTransaction extends Transaction<Void> {
    private UserEx user;
    private String eventId;

    public RemoveEventTransaction(UserEx user, String eventId) {
        this.user = user;
        this.eventId = eventId;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IEventAccess dao = daos.getEventAccess();

        Event event = dao.find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        if (!EventRemovePermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        dao.remove(con, event.getId());
        return null;
    }
}
