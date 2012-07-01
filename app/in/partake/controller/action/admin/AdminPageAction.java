package in.partake.controller.action.admin;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.aux.EventFilterCondition;
import play.mvc.Result;

public class AdminPageAction extends AbstractPartakeAction {
    public static Result get() throws DAOException, PartakeException {
        return new AdminPageAction().execute();
    }

    public Result doExecute() throws DAOException, PartakeException {
        ensureAdmin();

        AdminCountAccess transaction = new AdminCountAccess();
        transaction.execute();

        int countUser = transaction.getCountUser();
        int countEvent = transaction.getCountEvent();
        int countPublicEvent = transaction.getCountPublicEvent();
        int countPrivateEvent = transaction.getCountPrivateEvent();
        int countDraftEvent = transaction.getCountDraftEvent();
        int countPublishedEvent = transaction.getCountPublishedEvent();

        return render(views.html.admin.index.render(context(), countUser, countEvent, countPublicEvent, countPrivateEvent, countPublishedEvent, countDraftEvent));
    }
}

class AdminCountAccess extends DBAccess<Void> {
    private int countUser;

    private int countEvent;
    private int countPublicEvent;
    private int countPrivateEvent;
    private int countDraftEvent;
    private int countPublishedEvent;

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IUserAccess userAccess = daos.getUserAccess();
        countUser = userAccess.count(con);

        IEventAccess eventAccess = daos.getEventAccess();
        countEvent = eventAccess.count(con);
        countPublicEvent = eventAccess.count(con, EventFilterCondition.PUBLIC_EVENT_ONLY);
        countPrivateEvent = eventAccess.count(con, EventFilterCondition.PRIVATE_EVENT_ONLY);
        countDraftEvent = eventAccess.count(con, EventFilterCondition.DRAFT_EVENT_ONLY);
        countPublishedEvent = eventAccess.count(con, EventFilterCondition.PUBLISHED_EVENT_ONLY);
        return null;
    }

    public int getCountUser() {
        return countUser;
    }

    public int getCountEvent() {
        return countEvent;
    }

    public int getCountPublicEvent() {
        return countPublicEvent;
    }

    public int getCountPrivateEvent() {
        return countPrivateEvent;
    }

    public int getCountDraftEvent() {
        return countDraftEvent;
    }

    public int getCountPublishedEvent() {
        return countPublishedEvent;
    }
}
