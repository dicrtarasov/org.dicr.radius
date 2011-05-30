/**
 * RequestTimeoutException.java 12.11.2006
 */
package org.dicr.radius.exc;

/**
 * Request Timeout Exception
 * <P>
 * This exception occur while client waiting response packet from radius server after sending request packet.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061112
 */
public class RequestTimeoutException extends ChannelException {
	private static final long serialVersionUID = 6961241274261198850L;

	/**
     * Constructor
     */
	public RequestTimeoutException() {
		super();
	}

	/**
     * Constructor
     * 
     * @param message
     */
	public RequestTimeoutException(String message) {
		super(message);
	}

	/**
     * Constructor
     * 
     * @param message
     * @param cause
     */
	public RequestTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Constructor
     * 
     * @param cause
     */
	public RequestTimeoutException(Throwable cause) {
		super(cause);
	}

}
