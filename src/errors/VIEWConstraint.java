package errors;

public class VIEWConstraint extends Constraint {

	/**
	 * 
	 */
	private static final long serialVersionUID = -234235325235235325L;
	public VIEWConstraint(String tableName) {
		Message = "VIEW " + tableName + " IS NOT UPDATABLE";
	}
	public static String Message = "VIEW";
	public String message() {
		return Message;
	}
}
