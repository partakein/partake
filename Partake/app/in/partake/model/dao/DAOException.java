package in.partake.model.dao;

public class DAOException extends Exception {

	/** */
	private static final long serialVersionUID = 6352384791276501968L;

	public DAOException() {
		// do nothing.
	}
	
	public DAOException(Throwable cause) {
		super(cause);
	}
	
	public DAOException(String message) {
		super(message);
	}
	
	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}
}
