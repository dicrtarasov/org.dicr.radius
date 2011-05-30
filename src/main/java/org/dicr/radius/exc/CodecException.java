/**
 * CodecException.java 08.11.2006
 */
package org.dicr.radius.exc;

/**
 * Codec exception. Exception of coding decoding radius packet.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061108
 */
public class CodecException extends RadiusException {

	/** SerialID */
	private static final long serialVersionUID = -5572042650908702006L;

	/**
     * Constructor
     */
	public CodecException() {
		super();
	}

	/**
     * Constrcutor
     * 
     * @param message error message
     */
	public CodecException(String message) {
		super(message);
	}

	/**
     * Constructor
     * 
     * @param message error message
     * @param cause cause of error
     */
	public CodecException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Constructor
     * 
     * @param cause cause of error
     */
	public CodecException(Throwable cause) {
		super(cause);
	}

}
