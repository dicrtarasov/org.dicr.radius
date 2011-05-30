/**
 * SessionTimeout.java 24.09.2006
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Session-Timeout Attribute. This Attribute sets the maximum number of seconds of service to be provided to the user
 * before termination of the session or prompt. This Attribute is available to be sent by the server to the client in an
 * Access-Accept or Access-Challenge.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060924
 */
public final class SessionTimeout extends IntegerAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 8792649937729882848L;

	/** Value Code */
	public static final int TYPE_CODE = 27;

	/** Attribute Value */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, SessionTimeout.TYPE_CODE);

	/**
	 * Constructor.
	 */
	public SessionTimeout() {
		super(SessionTimeout.TYPE);
	}

	/**
	 * Constructor.
	 * 
	 * @param seconds value (maximum session time in seconds)
	 */
	public SessionTimeout(long seconds) {
		super(SessionTimeout.TYPE, seconds);
	}

	/**
	 * Constructor.
	 * 
	 * @param seconds value (maximum session time in seconds)
	 */
	public SessionTimeout(String seconds) {
		super(SessionTimeout.TYPE, seconds);
	}
}
