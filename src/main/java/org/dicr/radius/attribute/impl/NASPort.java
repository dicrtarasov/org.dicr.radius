/**
 * NASPort.java 09.12.2006
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * NAS-Port attribute
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061209
 */
public class NASPort extends IntegerAttribute {
	private static final long serialVersionUID = 4474549060060124652L;

	/** Value code */
	public static final int TYPE_CODE = 5;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, TYPE_CODE);

	/**
	 * Default constructor
	 */
	public NASPort() {
		super(TYPE);
	}

	/**
	 * Constructor
	 * 
	 * @param number port number
	 */
	public NASPort(int number) {
		super(TYPE, number);
	}

	/**
	 * Constructor
	 * 
	 * @param number port number
	 */
	public NASPort(String number) {
		super(TYPE, number);
	}
}
