package in.partake.model;

import java.util.List;

public class EventTicketHolderList {
    private List<UserTicketEx> enrolledParticipations;
    private List<UserTicketEx> spareParticipations;
    private List<UserTicketEx> cancelledParticipations;
    /** 参加者のうち、仮参加者の人数 */
    private int reservedEnrolled;
    /** 補欠のうち、仮参加者の人数 */
    private int reservedSpare;

    public EventTicketHolderList(
            List<UserTicketEx> enrolledParticipations, List<UserTicketEx> spareParticipations, List<UserTicketEx> cancelledParticipations, int reservedEnrolled, int reservedSpare) {
        this.enrolledParticipations = enrolledParticipations;
        this.spareParticipations = spareParticipations;
        this.cancelledParticipations = cancelledParticipations;
        this.reservedEnrolled = reservedEnrolled;
        this.reservedSpare = reservedSpare;
    }

    public List<UserTicketEx> getEnrolledParticipations() {
        return enrolledParticipations;
    }

    public List<UserTicketEx> getSpareParticipations() {
        return spareParticipations;
    }

    public List<UserTicketEx> getCancelledParticipations() {
        return cancelledParticipations;
    }

    public int getReservedEnrolled() {
        return reservedEnrolled;
    }

    public int getReservedSpare() {
        return reservedSpare;
    }
}
