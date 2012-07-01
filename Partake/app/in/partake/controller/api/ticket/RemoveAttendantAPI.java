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
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.resource.UserErrorCode;

public class RemoveAttendantAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new RemoveAttendantAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        UUID ticketId = getValidTicketIdParameter();
        String userId = getValidUserIdParameter();

        new RemoveAttendantTransaction(user, ticketId, userId).execute();
        return renderOK();
    }
}

class RemoveAttendantTransaction extends Transaction<Void> {
    private UserEx user;
    private UUID ticketId;
    private String userId;

    public RemoveAttendantTransaction(UserEx user, UUID ticketId, String userId) {
        this.user = user;
        this.ticketId = ticketId;
        this.userId = userId;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        EventTicket ticket = daos.getEventTicketAccess().find(con, ticketId);
        if (ticket == null)
            throw new PartakeException(UserErrorCode.INVALID_TICKET_ID);

        IEventAccess eventDao = daos.getEventAccess();
        Event event = eventDao.find(con, ticket.getEventId());
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_TICKET_ID);

        if (!EventEditParticipantsPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);

        daos.getEnrollmentAccess().removeByEventTicketIdAndUserId(con, ticketId, userId);
        return null;
    }
}
