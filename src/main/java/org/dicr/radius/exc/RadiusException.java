package org.dicr.radius.exc;

/**
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 1.0
 */
public class RadiusException extends Exception {
	private static final long serialVersionUID = -790999007830860684L;

	/**
     * Constructor
     */
	public RadiusException() {
		super("radius exception");
	}

	/**
     * Constructor
     * 
     * @param message
     */
	public RadiusException(String message) {
		super(message);
	}

	/**
     * Constructor
     * 
     * @param message
     * @param cause
     */
	public RadiusException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Constructor
     * 
     * @param cause
     */
	public RadiusException(Throwable cause) {
		super("radius exception", cause);
	}
}
