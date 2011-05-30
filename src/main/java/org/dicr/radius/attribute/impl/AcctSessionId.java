/**
 * AcctSessionId.java 23.06.2006
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Acct-Session-Id attribute
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060623
 */
public final class AcctSessionId extends StringAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -3451430649715945606L;

	/** Attribute type code */
	public static final int TYPE_CODE = 44;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, AcctSessionId.TYPE_CODE);

	/** Minimum length */
	public static final int VALUE_LENGTH_MIN = 3;

	/**
     * Constructor
     */
	public AcctSessionId() {
		super(AcctSessionId.TYPE);
	}

	/**
     * Constructor
     * 
     * @param id
     */
	public AcctSessionId(String id) {
		super(AcctSessionId.TYPE, id);
	}

	/**
     * Set value. Minimum value length is {@value #VALUE_LENGTH_MIN}
     * 
     * @see org.dicr.radius.attribute.types.StringAttribute#setValue(java.lang.String)
     */
	@Override
	protected final void setValue(String id) {
		if (id == null || id.length() < AcctSessionId.VALUE_LENGTH_MIN) throw new IllegalArgumentException(
				"sessionId: " + id);
		super.setValue(id);
	}
}
