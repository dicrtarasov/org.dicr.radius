/**
 * ReplyMessage.java 14.06.2006 5:57:49 dicr
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Reply-Message attribute
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060614
 */
public final class ReplyMessage extends StringAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 5983150336162250867L;

	/** Attribute type code */
	public static final int TYPE_CODE = 18;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, ReplyMessage.TYPE_CODE);

	/**
     * Constructor
     */
	public ReplyMessage() {
		super(ReplyMessage.TYPE);
	}

	/**
     * Constructor
     * 
     * @param message attribute value
     */
	public ReplyMessage(String message) {
		super(ReplyMessage.TYPE, message);
	}
}
