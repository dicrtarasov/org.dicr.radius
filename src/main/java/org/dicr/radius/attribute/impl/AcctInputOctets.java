/**
 * AcctInputOctets.java 24.06.2006
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Acct-Input-Octets attribute. Value is unsigned int.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060624
 */
public final class AcctInputOctets extends IntegerAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 7708038332588405340L;

	/** Attribute type code */
	public static final int TYPE_CODE = 42;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, AcctInputOctets.TYPE_CODE);

	/**
	 * Constructor.
	 */
	public AcctInputOctets() {
		super(AcctInputOctets.TYPE);
	}

	/**
	 * Constructor.
	 * 
	 * @param aValue count of input bytes
	 */
	public AcctInputOctets(long aValue) {
		super(AcctInputOctets.TYPE, aValue);
	}

	/**
	 * Constructor.
	 * 
	 * @param aValue count of input bytes
	 */
	public AcctInputOctets(String aValue) {
		super(AcctInputOctets.TYPE, aValue);
	}
}
