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
import in.partake.resource.PartakeProperties;
import in.partake.service.IEventSearchService;
import in.partake.service.ITestService;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;

public class TestService implements ITestService {
    private static PartakeTestDataProviderSet testDataProviderSet;

    public void initialize() {
        testDataProviderSet = new PartakeTestDataProviderSet();
//        try {
//            initializeDataSource();
//        } catch (NameAlreadyBoundException e) {
//            // Maybe already DataSource is created.
//        } catch (NamingException e) {
//            throw new RuntimeException(e);
//        }
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

    private static void initializeDataSource() throws NamingException {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");

        InitialContext ic = new InitialContext();
        ic.createSubcontext("java:");
        ic.createSubcontext("java:/comp");
        ic.createSubcontext("java:/comp/env");
        ic.createSubcontext("java:/comp/env/jdbc");

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(PartakeProperties.get().getString("comp.env.jdbc.postgres.driver"));
        ds.setUrl(PartakeProperties.get().getString("comp.env.jdbc.postgres.url"));
        ds.setUsername(PartakeProperties.get().getString("comp.env.jdbc.postgres.user"));
        ds.setPassword(PartakeProperties.get().getString("comp.env.jdbc.postgres.password"));

        ic.bind("java:/comp/env/jdbc/postgres", ds);
    }

}
