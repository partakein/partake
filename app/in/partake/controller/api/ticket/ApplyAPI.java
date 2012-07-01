package in.partake.controller.api.ticket;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.MessageDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.UserErrorCode;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import play.mvc.Result;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class ApplyAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new ApplyAPI().execute();
    }

    @Override
    protected Result doExecute() throws PartakeException, DAOException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();

        UUID ticketId = getValidTicketIdParameter();
        String status = getParameter("status");
        String comment = getParameter("comment");
        Map<UUID, List<String>> enqueteAnswers = convertToMap(getParameter("enqueteAnswers"));

        // If the comment does not exist, we use empty string instead.
        if (comment == null)
            comment = "";
        if (comment.length() > 1024)
            return renderInvalid(UserErrorCode.INVALID_COMMENT_TOOLONG);

        ParticipationStatus participationStatus;
        if ("enroll".equalsIgnoreCase(status))
            participationStatus = ParticipationStatus.ENROLLED;
        else if ("reserve".equalsIgnoreCase(status))
            participationStatus = ParticipationStatus.RESERVED;
        else if ("cancel".equals(status))
            participationStatus = ParticipationStatus.CANCELLED;
        else
            return renderInvalid(UserErrorCode.INVALID_ENROLL_STATUS);

        new EnrollTransaction(user, ticketId, participationStatus, comment, enqueteAnswers).execute();
        return renderOK();
    }

    private Map<UUID, List<String>> convertToMap(String jsonStr) throws PartakeException {
        try {
            JSONObject map = JSONObject.fromObject(jsonStr);
            return Util.parseEnqueteAnswers(map);
        } catch (JSONException e) {
            throw new PartakeException(UserErrorCode.INVALID_ENQUETE_ANSWERS);
        }
    }
}

class EnrollTransaction extends Transaction<Void> {
    private UserEx user;
    private UUID ticketId;
    private ParticipationStatus status;
    private String comment;
    private Map<UUID, List<String>> enqueteAnswers;

    public EnrollTransaction(UserEx user, UUID ticketId, ParticipationStatus status, String comment, Map<UUID, List<String>> enqueteAnswers) {
        this.user = user;
        this.ticketId = ticketId;
        this.status = status;
        this.comment = comment;
        this.enqueteAnswers = enqueteAnswers;
    }

    // TODO: We should share a lot of code with ChangeEnrollmentCommentAPI.
    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        EventTicket ticket = daos.getEventTicketAccess().find(con, ticketId);
        if (ticket == null)
            throw new PartakeException(UserErrorCode.INVALID_TICKET_ID);

        Event event = daos.getEventAccess().find(con, ticket.getEventId());
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        // もし、締め切りを過ぎている場合、変更が出来なくなる。
        if (!ticket.acceptsApplication(event, TimeUtil.getCurrentDateTime()))
            throw new PartakeException(UserErrorCode.INVALID_ENROLL_TIMEOVER);

        // もし、予約締切を過ぎている場合、reservation にはできない。
        if (ticket.isReservationTimeOver(event) && ParticipationStatus.RESERVED.equals(status))
            throw new PartakeException(UserErrorCode.INVALID_APPLICATION_RESERVATION_TIMEOVER);

        EnrollmentDAOFacade.enrollImpl(con, daos, user, ticketId, event, status, comment, enqueteAnswers, false, ticket.isReservationTimeOver(event));
        tweetEnrollment(con, daos, user, event, status);
        return null;
    }

    private void tweetEnrollment(PartakeConnection con, IPartakeDAOs daos, UserEx user, Event event, ParticipationStatus status) throws DAOException {
        UserPreference pref = daos.getUserPreferenceAccess().find(con, user.getId());
        if (pref == null)
            pref = UserPreference.getDefaultPreference(user.getId());

        if (!pref.tweetsAttendanceAutomatically())
            return;

        String left = "[PARTAKE] ";
        String right;
        switch (status) {
        case ENROLLED:
            right = " (" + event.getEventURL() + ") へ参加します。";
            break;
        case RESERVED:
            right = " (" + event.getEventURL() + ") へ参加予定です。";
            break;
        case CANCELLED:
            right = " (" + event.getEventURL() + ") への参加を取りやめました。";
            break;
        default:
            right = null;
        }

        if (right == null)
            return;

        String message = left + Util.shorten(event.getTitle(), 140 - Util.codePointCount(left) - Util.codePointCount(right)) + right;
        MessageDAOFacade.tweetMessageImpl(con, daos, user, message);
    }
}
