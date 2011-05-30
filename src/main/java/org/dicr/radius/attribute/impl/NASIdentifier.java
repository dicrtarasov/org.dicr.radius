/**
 * NASIdentifier.java 16.06.2006 7:48:39 dicr
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * NAS-Identifier attribute.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060616
 */
public final class NASIdentifier extends StringAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 8550357017618806379L;

	/** Attribute type code */
	public static final int TYPE_CODE = 32;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, NASIdentifier.TYPE_CODE);

	/**
     * Constructor
     */
	public NASIdentifier() {
		super(NASIdentifier.TYPE);
	}

	/**
     * Constructor
     * 
     * @param identifier
     */
	public NASIdentifier(String identifier) {
		super(NASIdentifier.TYPE, identifier);
	}
}
