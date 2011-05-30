/**
 * CallerID.java 09.12.2006
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Caller-ID attribute
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061209
 */
public class CallerID extends StringAttribute {
	private static final long serialVersionUID = -939241677733929706L;

	/** Value code */
	public static final int TYPE_CODE = 31;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, TYPE_CODE);

	/**
     * Default constructor
     */
	public CallerID() {
		super(TYPE);
	}

	/**
     * Constructor
     * 
     * @param id caller id
     */
	public CallerID(String id) {
		super(TYPE, id);
	}
}
