package errors;

public abstract class Constraint extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7243524143661679262L;

	abstract public String message();
}
