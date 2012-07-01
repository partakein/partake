package in.partake.model.dto.auxiliary;


public enum CalculatedEnrollmentStatus {
    UNKNOWN("unknown"),
	ENROLLED("enrolled"),
	ENROLLED_ON_WAITING_LIST("enrolledOnWaitingList"),
	RESERVED("reserved"),
	RESERVED_ON_WAITING_LIST("reservedOnWaitingList"),
	CANCELLED("cancelled"),
	NOT_ENROLLED("notEnrolled");

	private final String jsonString;
	
	private CalculatedEnrollmentStatus(String jsonString) {
	    this.jsonString = jsonString;
	}
	
	@Override
	public String toString() {
	    return jsonString;
	}
}


