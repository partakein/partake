package in.partake.controller.api.admin;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.service.IEventSearchService;
import play.mvc.Result;

public class AdminEventIndexRecreationAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new AdminEventIndexRecreationAPI().execute();
    }

    public Result doExecute() throws DAOException, PartakeException {
        ensureAdmin();
        ensureValidSessionToken();

        new EventIndexRecreationTransaction().execute();

        return renderOK();
    }
}

class EventIndexRecreationTransaction extends Transaction<Void> {

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IEventSearchService searchService = PartakeApp.getEventSearchService();
        EventDAOFacade.recreateEventIndex(con, daos, searchService);
        return null;
    }
}
