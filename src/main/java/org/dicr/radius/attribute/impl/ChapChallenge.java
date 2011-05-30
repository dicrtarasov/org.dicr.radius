package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.util.*;

/**
 * Chap-Challenge attribute
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060615
 */
public final class ChapChallenge extends OctetsAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -2700096793560729166L;

	/** Attribute type code */
	public static final int TYPE_CODE = 60;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, ChapChallenge.TYPE_CODE);

	/** Minimum value length */
	public static final int VALUE_MIN_LENGTH = 5;

	/**
	 * Constructor. Create attribute with 16-bytes random data value.
	 */
	public ChapChallenge() {
		super(ChapChallenge.TYPE, MD5.randomDigestData());
	}

	/**
	 * Constructor
	 * 
	 * @param aValue attribute value.
	 */
	public ChapChallenge(byte[] aValue) {
		super(ChapChallenge.TYPE, aValue);
	}

	/**
	 * Set value
	 * 
	 * @param challenge value of challenge. Minimum length is {@value #VALUE_MIN_LENGTH}
	 */
	@Override
	protected final void setValue(byte[] challenge) {
		if (challenge == null) throw new IllegalArgumentException("null value");
		if (challenge.length < ChapChallenge.VALUE_MIN_LENGTH) throw new IllegalArgumentException(
		        "incorrect value length: " + challenge.length);
		super.setValue(challenge);
	}
}
