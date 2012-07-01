package in.partake.model.dao.access;

import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventTicketAccess;
import in.partake.model.dto.EventTicket;
import in.partake.model.fixture.impl.EventTicketTestDataProvider;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class EventTicketAccessTest extends AbstractDaoTestCaseBase<IEventTicketAccess, EventTicket, UUID> {
    private EventTicketTestDataProvider provider;

    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEventTicketAccess());
        provider = PartakeTestApp.getTestService().getTestDataProviderSet().getEventTicketProvider();
    }

    @Override
    protected EventTicket create(long pkNumber, String pkSalt, int objNumber) {
        return provider.create(pkNumber, pkSalt, objNumber);
    }

    @Test
    public void testFindByEventId() throws Exception {
        final String eventId1 = UUID.randomUUID().toString();
        final String eventId2 = UUID.randomUUID().toString();
        final UUID[] ids = new UUID[] { UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID() };
        final String[] eventIds = new String[] { eventId1, eventId2, eventId2 };

        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                for (int i = 0; i < ids.length; ++i) {
                    dao.put(con, EventTicket.createDefaultTicket(ids[i], eventIds[i]));
                }

                con.commit();
                con.beginTransaction();

                List<EventTicket> tickets = dao.findEventTicketsByEventId(con, eventId1);
                assertThat(tickets.size(), is(1));
                assertThat(tickets.get(0).getId(), is(ids[0]));

                return null;
            }
        }.execute();


    }
}
