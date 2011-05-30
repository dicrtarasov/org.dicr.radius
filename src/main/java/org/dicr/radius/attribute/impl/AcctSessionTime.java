/**
 * AcctSessionTime.java 24.06.2006
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Acct-Session-Time attribute
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060624
 */
public final class AcctSessionTime extends IntegerAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 6708404780530803449L;

	/** Attribute type code */
	public static final int TYPE_CODE = 46;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, AcctSessionTime.TYPE_CODE);

	/**
	 * Constructor
	 */
	public AcctSessionTime() {
		super(AcctSessionTime.TYPE);
	}

	/**
	 * Constructor
	 * 
	 * @param seconds value of session time in seconds
	 */
	public AcctSessionTime(int seconds) {
		super(AcctSessionTime.TYPE, seconds);
	}

	/**
	 * Constructor
	 * 
	 * @param seconds value of session time in seconds
	 */
	public AcctSessionTime(String seconds) {
		super(AcctSessionTime.TYPE, seconds);
	}
}
