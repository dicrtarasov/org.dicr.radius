/**
 * IncorrectRequestException.java 14.06.2006 3:02:52 dicr
 */
package org.dicr.radius.exc;

import org.dicr.radius.packet.*;

/**
 * Incorrect request packet
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060614
 */
public class IncorrectRequestException extends RequestHandlerException {
	private static final long serialVersionUID = 7235491012068476639L;

	/**
     * Constructor
     */
	public IncorrectRequestException() {
		super("Incorrect request packet");
	}

	/**
     * Constructor
     *
     * @param message
     */
	public IncorrectRequestException(String message) {
		super(message);
	}

	/**
     * Constructor
     *
     * @param packet
     */
	public IncorrectRequestException(RequestPacket packet) {
		super("incorrect request: " + packet);
	}

	/**
     * Constructor
     *
     * @param message
     * @param cause
     */
	public IncorrectRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Constructor
     *
     * @param cause
     */
	public IncorrectRequestException(Throwable cause) {
		super("incorrect request", cause);
	}
}
