package in.partake.model.daofacade;

import in.partake.base.PartakeRuntimeException;
import in.partake.base.TimeUtil;
import in.partake.model.EventTicketHolderList;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.UserTicketEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.CalculatedEnrollmentStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.ServerErrorCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EnrollmentDAOFacade {

    // TODO: Maybe we should create EventTikcetEx, which has an Event.
    public static List<UserTicketEx> getEnrollmentExs(PartakeConnection con, IPartakeDAOs daos, EventTicket ticket, Event event) throws DAOException {
        // priority のあるイベントに参加している場合、priority に 1 を付加する。

        // --- まず、EnrollmentEx を作成
        List<UserTicketEx> ps = new ArrayList<UserTicketEx>();
        for (UserTicket p : daos.getEnrollmentAccess().findByTicketId(con, ticket.getId(), 0, Integer.MAX_VALUE)) {
            if (p == null) { continue; }
            UserEx user = UserDAOFacade.getUserEx(con, daos, p.getUserId());
            if (user == null) { continue; }
            UserTicketEx pe = new UserTicketEx(p, user);
            pe.freeze();
            ps.add(pe);
        }

        Collections.sort(ps, UserTicketEx.getPriorityBasedComparator());
        return ps;
    }

    /** 参加ステータスを表示します */
    public static CalculatedEnrollmentStatus calculateEnrollmentStatus(PartakeConnection con, IPartakeDAOs daos, String userId, EventTicket ticket, Event event) throws DAOException {
        ParticipationStatus status = getParticipationStatus(con, daos, userId, ticket.getId());

        if (status == null)
            return CalculatedEnrollmentStatus.NOT_ENROLLED;

        switch (status) {
        case ENROLLED: {
            int order = getOrderOfEnrolledEvent(con, daos, ticket, event, userId);
            if (order <= ticket.getAmount() || ticket.getAmount() == 0)
                return CalculatedEnrollmentStatus.ENROLLED;
            else
                return CalculatedEnrollmentStatus.ENROLLED_ON_WAITING_LIST;
        }
        case RESERVED: {
            int order = getOrderOfEnrolledEvent(con, daos, ticket, event, userId);
            if (order <= ticket.getAmount() || ticket.getAmount() == 0)
                return CalculatedEnrollmentStatus.RESERVED;
            else
                return CalculatedEnrollmentStatus.RESERVED_ON_WAITING_LIST;
        }
        case NOT_ENROLLED:
            return CalculatedEnrollmentStatus.NOT_ENROLLED;
        case CANCELLED:
            return CalculatedEnrollmentStatus.CANCELLED;
        }

        assert false;
        throw new PartakeRuntimeException(ServerErrorCode.LOGIC_ERROR);
    }

    public static ParticipationStatus getParticipationStatus(PartakeConnection con, IPartakeDAOs daos, String userId, UUID ticketId) throws DAOException {
        UserTicket enrollment = daos.getEnrollmentAccess().findByTicketIdAndUserId(con, ticketId, userId);
        if (enrollment == null)
            return ParticipationStatus.NOT_ENROLLED;
        return enrollment.getStatus();
    }

    /**
     * event の参加順位(何番目に参加したか)を返します。
     */
    public static int getOrderOfEnrolledEvent(PartakeConnection con, IPartakeDAOs daos, EventTicket ticket, Event event, String userId) throws DAOException {
        List<UserTicketEx> enrollments = getEnrollmentExs(con, daos, ticket, event);
        EventTicketHolderList list = ticket.calculateParticipationList(event, enrollments);

        int result = 0;
        for (UserTicket e : list.getEnrolledParticipations()) {
            ++result;
            if (userId.equals(e.getUserId())) { return result; }
        }
        for (UserTicket e : list.getSpareParticipations()) {
            ++result;
            if (userId.equals(e.getUserId())) { return result; }
        }

        return -1;
    }

    // TODO: "changesOnlyComment" should die!
    public static void enrollImpl(PartakeConnection con, IPartakeDAOs daos, UserEx user, UUID ticketId, Event event,
            ParticipationStatus status, String comment, Map<UUID, List<String>> enqueteAnswers,boolean changesOnlyComment, boolean isReservationTimeOver) throws DAOException {
        String userId = user.getId();
        String eventId = event.getId();

        UserTicket oldEnrollment = daos.getEnrollmentAccess().findByTicketIdAndUserId(con, ticketId, userId);
        UserTicket newEnrollment;
        if (oldEnrollment == null) {
            String id = daos.getEnrollmentAccess().getFreshId(con);
            newEnrollment = new UserTicket(id, userId, ticketId, eventId,
                    comment, ParticipationStatus.NOT_ENROLLED, ModificationStatus.NOT_ENROLLED, AttendanceStatus.UNKNOWN, null, TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime(), null);
        } else {
            newEnrollment = new UserTicket(oldEnrollment);
        }

        newEnrollment.setComment(comment);
        newEnrollment.setEnqueteAnswers(enqueteAnswers);
        if (oldEnrollment == null) {
            newEnrollment.setStatus(status);
            newEnrollment.setModificationStatus(ModificationStatus.CHANGED);
            newEnrollment.setAppliedAt(TimeUtil.getCurrentDateTime());
        } else if (changesOnlyComment || status.equals(oldEnrollment.getStatus())) {
            // 特に変更しない
        } else if (status.isEnrolled() == oldEnrollment.getStatus().isEnrolled()) {
            // 参加する / しないの状況が変更されない場合は、status のみが更新される。
            newEnrollment.setStatus(status);
            newEnrollment.setModificationStatus(ModificationStatus.CHANGED);
        } else {
            newEnrollment.setStatus(status);
            newEnrollment.setModificationStatus(ModificationStatus.CHANGED);
            newEnrollment.setAppliedAt(TimeUtil.getCurrentDateTime());
        }

        if (oldEnrollment == null)
            newEnrollment.setModifiedAt(TimeUtil.getCurrentDateTime());

        //
        if (status != null) {
            IEventActivityAccess eaa = daos.getEventActivityAccess();

            String title;
            switch (status) {
            case ENROLLED:      title = user.getTwitterScreenName() + " さんが参加しました";        break;
            case CANCELLED:     title = user.getTwitterScreenName() + " さんが参加を取りやめました";     break;
            case RESERVED:      title = user.getTwitterScreenName() + " さんが仮参加しました";      break;
            case NOT_ENROLLED:  title = user.getTwitterScreenName() + " さんはもう参加していません"; break;
            default:            title = user.getTwitterScreenName() + " さんが不明なステータスになっています"; break; // TODO: :-P
            }

            String content = String.format("<p>詳細は <a href=\"%s\">%s</a> をごらんください。</p>", event.getEventURL(), event.getEventURL());
            eaa.put(con, new EventActivity(eaa.getFreshId(con), eventId, title, content, TimeUtil.getCurrentDateTime()));
        }

        daos.getEnrollmentAccess().put(con, newEnrollment);
    }

}
