/**
 * OctetsDirection.java 24.09.2006
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Octets Direction Attribute. Non-standard radius attribute for pppd. Set the direction of session traffic limit.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060924
 * @see SessionOctetsLimit
 */
public final class OctetsDirection extends IntegerAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -2270044049607657332L;

	/** Value code */
	public static final int TYPE_CODE = 228;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, OctetsDirection.TYPE_CODE);

	/**
	 * Constructor
	 */
	public OctetsDirection() {
		super(OctetsDirection.TYPE);
	}

	/**
	 * Constructor
	 * 
	 * @param direction ordinal value of direction
	 * @see Value
	 */
	public OctetsDirection(int direction) {
		super(OctetsDirection.TYPE, direction);
	}

	/**
	 * Constructor
	 * 
	 * @param direction name value of direction
	 * @see Value
	 */
	public OctetsDirection(String direction) {
		super(OctetsDirection.TYPE, direction);
	}

	/**
	 * Constructor
	 * 
	 * @param direction direction value
	 */
	public OctetsDirection(Value direction) {
		super(OctetsDirection.TYPE);
		this.setValue(direction);
	}

	/**
	 * Set value
	 * 
	 * @param value ordinal value of {@link Value}
	 */
	@Override
	protected void setValue(long value) {
		if (Value.byCode(value) == null) throw new IllegalArgumentException("incorrect attribute value code: " + value);
		super.setValue(value);
	}

	/**
	 * Set value
	 * 
	 * @param value string name of {@link Value}
	 */
	@Override
	protected void setValue(String value) {
		if (value == null || value.isEmpty()) throw new IllegalArgumentException("empty value");
		super.setValue(Enum.valueOf(Value.class, value).ordinal());
	}

	/**
	 * Set value
	 * 
	 * @param value direction value
	 */
	protected void setValue(Value value) {
		if (value == null) throw new IllegalArgumentException("null value");
		super.setValue(value.ordinal());
	}

	/**
	 * Return value as direction
	 * 
	 * @return direction value or null if value not correct
	 */
	public Value getValueAsDirection() {
		return Value.byCode(this.getValue());
	}

	/**
	 * Return value as string.
	 * 
	 * @return name of direction value
	 * @see Value
	 */
	@Override
	public String getValueAsString() {
		return this.getValueAsDirection().toString();
	}

	/**
	 * Attribute value
	 */
	public static enum Value {
		/** Input + output */
		SUM,
		/** Input only */
		IN,
		/** Output only */
		OUT,
		/** Maximum overall */
		MAXOVERAL,
		/** Max. session */
		MAXSESSION;

		/**
		 * Return Value by code
		 * 
		 * @param code code
		 * @return Value by code or null
		 */
		public static Value byCode(long code) {
			for (Value value : Value.values()) {
				if (value.ordinal() == code) return value;
			}
			return null;
		}
	}
}
