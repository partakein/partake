package in.partake.controller.api.ticket;

import java.util.UUID;

import play.mvc.Result;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventEditParticipantsPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTicketAccess;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.resource.UserErrorCode;

public class AttendAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new AttendAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String userId = getValidUserIdParameter();
        UUID ticketId = getValidTicketIdParameter();

        String status = getParameter("status");
        if (status == null)
            return renderInvalid(UserErrorCode.MISSING_ATTENDANCE_STATUS);
        if (!AttendanceStatus.isValueOf(status))
            return renderInvalid(UserErrorCode.INVALID_ATTENDANCE_STATUS);

        new AttendTransaction(user, userId, ticketId, AttendanceStatus.safeValueOf(status)).execute();
        return renderOK();
    }
}

class AttendTransaction extends Transaction<Void> {
    private UserEx user;
    private String userId;
    private UUID ticketId;
    private AttendanceStatus status;

    public AttendTransaction(UserEx user, String userId, UUID ticketId, AttendanceStatus status) {
        this.user = user;
        this.userId = userId;
        this.ticketId = ticketId;
        this.status = status;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        EventTicket ticket = daos.getEventTicketAccess().find(con, ticketId);
        if (ticket == null)
            throw new PartakeException(UserErrorCode.INVALID_TICKET_ID);

        Event event = daos.getEventAccess().find(con, ticket.getEventId());
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_TICKET_ID);

        if (!EventEditParticipantsPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);

        updateAttendanceStatus(con, daos);
        return null;
    }

    private void updateAttendanceStatus(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IUserTicketAccess enrollmentAccess = daos.getEnrollmentAccess();

        // We have already checked the event exists, so when no enrollment is found, we throw an "invalid user id"
        // exception here.
        UserTicket enrollment = enrollmentAccess.findByTicketIdAndUserId(con, ticketId, userId);
        if (enrollment == null)
            throw new PartakeException(UserErrorCode.INVALID_USER_ID);

        UserTicket newEnrollment = new UserTicket(enrollment);
        newEnrollment.setAttendanceStatus(status);
        enrollmentAccess.put(con, newEnrollment);
    }
}
