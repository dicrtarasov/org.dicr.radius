/**
 * AcctInterimInterval.java 23.06.2006
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.packet.impl.*;

/**
 * Acct-Interim-Interval attribute. This attribute used in {@link AccessAccept} response packets to specify accounting
 * interval in seconds (interval of AccountingRequest packets from NAS).
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060623
 */
public final class AcctInterimInterval extends IntegerAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 7997822209724004625L;

	/** Attribute type code */
	public static final int TYPE_CODE = 85;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, AcctInterimInterval.TYPE_CODE);

	/**
	 * Constructor
	 */
	public AcctInterimInterval() {
		super(AcctInterimInterval.TYPE);
	}

	/**
	 * Constructor.
	 * 
	 * @param seconds interval value in seconds
	 */
	public AcctInterimInterval(long seconds) {
		super(AcctInterimInterval.TYPE, seconds);
	}

	/**
	 * Constructor.
	 * 
	 * @param seconds interval value in seconds
	 */
	public AcctInterimInterval(String seconds) {
		super(AcctInterimInterval.TYPE, seconds);
	}
}
