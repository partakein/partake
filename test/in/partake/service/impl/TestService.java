package in.partake.service.impl;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.fixture.PartakeTestDataProviderSet;
import in.partake.model.fixture.impl.EventTestDataProvider;
import in.partake.model.fixture.impl.UserOpenIDLinkTestDataProvider;
import in.partake.model.fixture.impl.UserTestDataProvider;
import in.partake.model.fixture.impl.UserTicketTestDataProvider;
import in.partake.model.fixture.impl.UserTwitterLinkTestDataProvider;
import in.partake.service.IEventSearchService;
import in.partake.service.ITestService;

public class TestService implements ITestService {
    private static PartakeTestDataProviderSet testDataProviderSet;

    public void initialize() {
        testDataProviderSet = new PartakeTestDataProviderSet();
    }

    public PartakeTestDataProviderSet getTestDataProviderSet() {
        return testDataProviderSet;
    }

    /**
     * <p>test用のデータがDatastoreに存在することを保証する。作成されるデータは各Fixtureを参照。
     * @throws PartakeException
     * @see CacheTestDataProvider
     * @see UserTestDataProvider
     * @see UserTwitterLinkTestDataProvider
     * @see UserOpenIDLinkTestDataProvider
     * @see EventTestDataProvider
     * @see UserTicketTestDataProvider
     */
    public void setDefaultFixtures() throws DAOException, PartakeException {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                IEventSearchService searchService = PartakeApp.getEventSearchService();
                testDataProviderSet.createFixtures(con, daos);
                EventDAOFacade.recreateEventIndex(con, daos, searchService);
                return null;
            }
        }.execute();
    }
}
