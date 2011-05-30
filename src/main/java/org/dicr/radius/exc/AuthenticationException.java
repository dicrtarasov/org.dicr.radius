package org.dicr.radius.exc;

/**
 * Authentication failed exception
 *
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 1.0
 */
public class AuthenticationException extends RadiusException {
	/** Serial ID */
	private static final long serialVersionUID = -8227243307974616843L;

	/**
     * Constructor
     */
	public AuthenticationException() {
		super("Authentication failed");
	}

	/**
     * Constructor
     *
     * @param message
     */
	public AuthenticationException(String message) {
		super(message);
	}

	/**
     * Constructor
     *
     * @param message
     * @param cause
     */
	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Constructor
     *
     * @param cause
     */
	public AuthenticationException(Throwable cause) {
		super("Authentication failed", cause);
	}
}
