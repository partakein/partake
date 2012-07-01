package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTicketAccess;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserTicketTestDataProvider extends TestDataProvider<UserTicket> {

    @Override
    public UserTicket create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new UserTicket(uuid.toString(), "userId" + objNumber, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, new DateTime(0L), new DateTime(0L), null);
    }

    @Override
    public List<UserTicket> createSamples() {
        List<UserTicket> array = new ArrayList<UserTicket>();
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, new DateTime(0L), new DateTime(0L), null));
        array.add(new UserTicket("id1", "userId", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, new DateTime(0L), new DateTime(0L), null));
        array.add(new UserTicket("id", "userId1", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, new DateTime(0L), new DateTime(0L), null));
        array.add(new UserTicket("id", "userId", new UUID(0, 1), "comment", "eventId", ParticipationStatus.ENROLLED, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, new DateTime(0L), new DateTime(0L), null));
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment1", "eventId", ParticipationStatus.ENROLLED, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, new DateTime(0L), new DateTime(0L), null));
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment1", "eventId", ParticipationStatus.RESERVED, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, new DateTime(0L), new DateTime(0L), null));
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, ModificationStatus.CHANGED, AttendanceStatus.PRESENT, null, new DateTime(0L), new DateTime(0L), null));
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, ModificationStatus.ENROLLED, AttendanceStatus.ABSENT, null, new DateTime(0L), new DateTime(0L), null));
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, new DateTime(1L), new DateTime(0L), null));
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, new DateTime(0L), new DateTime(1L), null));
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, null, new DateTime(0L), new DateTime(0L), new DateTime(0L)));
        return array;
    }

    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IUserTicketAccess dao = daos.getEnrollmentAccess();

        dao.truncate(con);
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                EVENT_ENROLLED_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED,
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, null, TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime(), null));
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                EVENT_RESERVED_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.RESERVED,
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, null, TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime(), null));
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                EVENT_CANCELLED_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.CANCELLED,
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, null, TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime(), null));

        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                ATTENDANCE_PRESENT_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED,
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, null, TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime(), null));
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                ATTENDANCE_ABSENT_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED,
                ModificationStatus.CHANGED, AttendanceStatus.ABSENT, null, TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime(), null));
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                ATTENDANCE_UNKNOWN_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED,
                ModificationStatus.CHANGED, AttendanceStatus.UNKNOWN, null, TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime(), null));

        // Enrollment for private event
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                EVENT_ENROLLED_USER_ID, PRIVATE_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED,
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, null, TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime(), null));
    }
}
