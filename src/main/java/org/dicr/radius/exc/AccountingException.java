/**
 * AccountingException.java 15.06.2006
 */
package org.dicr.radius.exc;

/**
 * Accounting Exception
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060615
 */
public class AccountingException extends RadiusException {
	private static final long serialVersionUID = 2529635416673155847L;

	/**
     * Constructor
     */
	public AccountingException() {
		super("accounting exception");
	}

	/**
     * Constructor
     * 
     * @param message
     */
	public AccountingException(String message) {
		super(message);
	}

	/**
     * Constructor
     * 
     * @param message
     * @param cause
     */
	public AccountingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Constructor
     * 
     * @param cause
     */
	public AccountingException(Throwable cause) {
		super(cause);
	}
}
