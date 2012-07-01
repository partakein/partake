package in.partake.model.dao.access;

import in.partake.app.PartakeApp;
import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTicketAccess;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class UserTicketAccessTest extends AbstractDaoTestCaseBase<IUserTicketAccess, UserTicket, String> {
    @Rule
    public TestName name = new TestName();

    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEnrollmentAccess());
    }

    @Override
    protected UserTicket create(long pkNumber, String pkSalt, int objNumber) {
        return new UserTicket(
                new UUID(("id" + pkSalt).hashCode(), pkNumber).toString(),
                "userId" + pkSalt + pkNumber,
                new UUID(0, 0),
                "eventId" + pkSalt + pkNumber,
                "comment" + objNumber,
                ParticipationStatus.ENROLLED,
                ModificationStatus.CHANGED,
                AttendanceStatus.UNKNOWN,
                null,
                new DateTime(1L),
                new DateTime(1L),
                new DateTime(1L));
    }

    @Test
    public void testGetEmptyPaticipationList() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                UUID ticketId = UUID.randomUUID();
                List<UserTicket> list = dao.findByTicketId(con, ticketId, 0, 1);
                Assert.assertTrue(list.isEmpty());
                return null;
            }
        }.execute();
    }

    @Test
    public void testPutAndGetPaticipationList() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                ParticipationStatus status = ParticipationStatus.ENROLLED;

                String id = UUID.randomUUID().toString();
                String eventId = UUID.randomUUID().toString();
                UUID ticketId = UUID.randomUUID();
                String userId = UUID.randomUUID().toString();


                {
                    con.beginTransaction();
                    dao.put(con, new UserTicket(id, userId, ticketId, eventId, "", ParticipationStatus.ENROLLED, ModificationStatus.CHANGED, AttendanceStatus.UNKNOWN, null, TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime(), null));
                    con.commit();
                }

                List<UserTicket> list = dao.findByTicketId(con, ticketId, 0, Integer.MAX_VALUE);

                Assert.assertEquals(1, list.size());
                UserTicket storedParticipation = list.get(0);
                Assert.assertNotNull(storedParticipation);
                Assert.assertEquals(userId, storedParticipation.getUserId());
                Assert.assertEquals(ModificationStatus.CHANGED, storedParticipation.getModificationStatus());
                Assert.assertEquals(status, storedParticipation.getStatus());
                return null;
            }
        }.execute();
    }

    @Test
    public void testPutAndUpdatePaticipationList() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                String id = UUID.randomUUID().toString();
                String userId = UUID.randomUUID().toString();
                String eventId = UUID.randomUUID().toString();
                UUID ticketId = UUID.randomUUID();

                // create
                {
                    con.beginTransaction();
                    dao.put(con, new UserTicket(id,
                            userId, ticketId, eventId,
                            "comment", ParticipationStatus.ENROLLED, ModificationStatus.CHANGED, AttendanceStatus.UNKNOWN, null, TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime(), null));

                    con.commit();
                }

                // update
                {
                    con.beginTransaction();
                    List<UserTicket> storedList = dao.findByTicketId(con, ticketId, 0, Integer.MAX_VALUE);
                    UserTicket storedParticipation = storedList.get(0);
                    Assert.assertNotNull(storedParticipation);
                    ModificationStatus newStatus = ModificationStatus.NOT_ENROLLED;
                    Assert.assertFalse(newStatus.equals(storedParticipation.getModificationStatus()));
                    UserTicket newStoredParticipation = new UserTicket(storedParticipation);
                    newStoredParticipation.setModificationStatus(ModificationStatus.CHANGED);
                    dao.put(con, newStoredParticipation);
                    con.commit();
                }

                // get
                {
                    con.beginTransaction();
                    List<UserTicket> updatedList = dao.findByTicketId(con, ticketId, 0, Integer.MAX_VALUE);
                    Assert.assertEquals(1, updatedList.size());
                    UserTicket updatedParticipation = updatedList.get(0);
                    Assert.assertEquals(userId, updatedParticipation.getUserId());
                    Assert.assertEquals(ModificationStatus.CHANGED, updatedParticipation.getModificationStatus());
                    Assert.assertEquals(ParticipationStatus.ENROLLED, updatedParticipation.getStatus());
                    con.commit();
                }

                return null;
            }
        }.execute();
    }
}
