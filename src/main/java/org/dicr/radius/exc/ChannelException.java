/**
 * ChannelException.java 14.06.2006
 */
package org.dicr.radius.exc;

/**
 * Corrupt packet data
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060614
 */
public class ChannelException extends RadiusException {
	private static final long serialVersionUID = -4449755041940918899L;

	/**
     * Constructor
     */
	public ChannelException() {
		super("ncorrect packet");
	}

	/**
     * Costructor
     * 
     * @param message
     */
	public ChannelException(String message) {
		super(message);
	}

	/**
     * Costructor
     * 
     * @param message
     * @param cause
     */
	public ChannelException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Constructor
     * 
     * @param cause
     */
	public ChannelException(Throwable cause) {
		super("incorrect packet", cause);
	}

}
