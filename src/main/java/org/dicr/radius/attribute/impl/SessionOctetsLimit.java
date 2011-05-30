/**
 * SessionOctetsLimit.java 24.09.2006
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Session Octets Limit Attribute. Non-standard radius attribute for pppd. Limit session traffic.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060924
 * @see OctetsDirection
 */
public final class SessionOctetsLimit extends IntegerAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 5941978093576752285L;

	/** Value code */
	public static final int TYPE_CODE = 227;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, SessionOctetsLimit.TYPE_CODE);

	/**
	 * Constructor
	 */
	public SessionOctetsLimit() {
		super(SessionOctetsLimit.TYPE);
	}

	/**
	 * Constructor
	 * 
	 * @param bytes limit in bytes
	 */
	public SessionOctetsLimit(int bytes) {
		super(SessionOctetsLimit.TYPE, bytes);
	}

	/**
	 * Constructor
	 * 
	 * @param bytes limit in bytes
	 */
	public SessionOctetsLimit(String bytes) {
		super(SessionOctetsLimit.TYPE, bytes);
	}
}
