/**
 * AcctOutputOctets.java 24.06.2006
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Acct-Output-Octets attribute
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060624
 */
public final class AcctOutputOctets extends IntegerAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -7981873326817887125L;

	/** Attribute type code */
	public static final int TYPE_CODE = 43;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, AcctOutputOctets.TYPE_CODE);

	/**
	 * Constructor
	 */
	public AcctOutputOctets() {
		super(AcctOutputOctets.TYPE);
	}

	/**
	 * Constructor
	 * 
	 * @param octets count of output bytes
	 */
	public AcctOutputOctets(long octets) {
		super(AcctOutputOctets.TYPE, octets);
	}

	/**
	 * Constructor
	 * 
	 * @param octets count of output bytes
	 */
	public AcctOutputOctets(String octets) {
		super(AcctOutputOctets.TYPE, octets);
	}
}
