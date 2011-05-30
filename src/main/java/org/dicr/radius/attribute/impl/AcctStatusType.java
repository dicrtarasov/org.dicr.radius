/**
 * AcctStatusType.java 23.06.2006
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Acct-Status-Type attribute.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060623
 */
public final class AcctStatusType extends IntegerAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 4456190678671552126L;

	/** Attribute type code */
	public static final int TYPE_CODE = 40;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, AcctStatusType.TYPE_CODE);

	/**
	 * Constructor
	 */
	public AcctStatusType() {
		super(AcctStatusType.TYPE);
	}

	/**
	 * Constructor
	 * 
	 * @param typeCode status type (value) code
	 */
	public AcctStatusType(int typeCode) {
		super(AcctStatusType.TYPE, typeCode);
	}

	/**
	 * Constructor
	 * 
	 * @param value value to set
	 */
	public AcctStatusType(Value value) {
		super(AcctStatusType.TYPE);
		this.setValue(value);
	}

	/**
	 * Constructor
	 * 
	 * @param typeCode value to set
	 */
	public AcctStatusType(String typeCode) {
		super(AcctStatusType.TYPE, typeCode);
	}

	/**
	 * @see org.dicr.radius.attribute.types.IntegerAttribute#setValue(long)
	 */
	@Override
	public final void setValue(long typeCode) {
		if (Value.byCode(typeCode) == null) throw new IllegalArgumentException("unknown value code: " + typeCode);
		super.setValue(typeCode);
	}

	/**
	 * Set attribute value
	 * 
	 * @param typeString value to set
	 */
	@Override
	public final void setValue(String typeString) {
		if (typeString == null || typeString.isEmpty()) throw new IllegalArgumentException("empty type");
		this.setValue(Enum.valueOf(Value.class, typeString));
	}

	/**
	 * Set attribute value
	 * 
	 * @param value value to set
	 */
	public final void setValue(Value value) {
		if (value == null) throw new IllegalArgumentException("null type");
		super.setValue(value.getCode());
	}

	/**
	 * Return value as StatusType
	 * 
	 * @return value
	 */
	public Value getValueAsStatusType() {
		return Value.byCode(this.getValue());
	}

	/**
	 * @see org.dicr.radius.attribute.types.IntegerAttribute#getValueAsString()
	 */
	@Override
	public String getValueAsString() {
		return this.getValueAsStatusType().toString();
	}

	/**
	 * Value type for AcctStatusType
	 */
	public static enum Value {
		/** Start session */
		START(1),
		/** Stop session */
		STOP(2),
		/** Update status */
		INTERIM_UPDATE(3),
		/** Enable accounting */
		ACCOUNTING_ON(7),
		/** Disable accounting */
		ACCOUNTING_OFF(8);

		/** Value code */
		private int valueCode = 0;

		/** Constructor */
		private Value(int value_code) {
			this.valueCode = value_code;
		}

		/**
		 * Return value code
		 * 
		 * @return code of value
		 */
		public final int getCode() {
			return this.valueCode;
		}

		/**
		 * Return type by code
		 * 
		 * @param code value code
		 * @return status value
		 */
		public static Value byCode(long code) {
			for (Value value : Value.values())
				if (value.getCode() == code) return value;
			throw new IllegalArgumentException("value code: " + code);
		}
	}
}
