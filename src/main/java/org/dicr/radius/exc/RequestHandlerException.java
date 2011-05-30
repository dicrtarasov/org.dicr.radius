/**
 * HandlerException.java 06.06.2007
 */
package org.dicr.radius.exc;

/**
 * Request Handler Exception
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070606
 */
public class RequestHandlerException extends RadiusException {

	/** Serial Id */
	private static final long serialVersionUID = 94693678346278312L;

	/**
     * Constructor
     */
	public RequestHandlerException() {
		super("error handling request");
	}

	/**
     * Constructor
     *
     * @param message
     */
	public RequestHandlerException(String message) {
		super(message);
	}

	/**
     * Constructor
     *
     * @param message
     * @param cause
     */
	public RequestHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Constructor
     *
     * @param cause
     */
	public RequestHandlerException(Throwable cause) {
		super(cause);
	}
}
