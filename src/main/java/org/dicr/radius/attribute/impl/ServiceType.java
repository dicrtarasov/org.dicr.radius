/**
 * ServiceType.java 03.06.2007
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Service-Type Attribute
 * <P>
 * This Attribute indicates the type of service the user has requested, or the type of service to be provided. It MAY be
 * used in both Access-Request and Access-Accept packets. A NAS is not required to implement all of these service types,
 * and MUST treat unknown or unsupported Service-Types as though an Access-Reject had been received instead.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070603
 */
public class ServiceType extends IntegerAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 970517185989662029L;

	/** Type code */
	public static final int TYPE_CODE = 6;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, TYPE_CODE);

	/**
	 * Constructor
	 */
	public ServiceType() {
		super(ServiceType.TYPE);
	}

	/**
	 * Constructor
	 * 
	 * @param valueCode value code
	 */
	public ServiceType(int valueCode) {
		super(ServiceType.TYPE, valueCode);
	}

	/**
	 * Constructor
	 * 
	 * @param value attribute value
	 */
	public ServiceType(Value value) {
		super(ServiceType.TYPE);
		this.setValue(value);
	}

	/**
	 * Constructor
	 * 
	 * @param value string value
	 */
	public ServiceType(String value) {
		super(ServiceType.TYPE, value);
	}

	/**
	 * @see org.dicr.radius.attribute.types.IntegerAttribute#setValue(long)
	 */
	@Override
	public void setValue(long aValue) {
		if (Value.byCode(aValue) == null) throw new IllegalArgumentException("unknown value code: " + aValue);
		super.setValue(aValue);
	}

	/**
	 * Set value
	 * 
	 * @param value attribute value
	 */
	public void setValue(Value value) {
		if (value == null) throw new IllegalArgumentException("null value");
		super.setValue(value.getCode());
	}

	/**
	 * Set value
	 * 
	 * @param aValue string name of {@link Value} constant
	 * @throws IllegalArgumentException if string does'nt specify correct constant name
	 */
	@Override
	protected void setValue(String aValue) throws IllegalArgumentException {
		if (aValue == null) throw new IllegalArgumentException("null value");
		this.setValue(Enum.valueOf(Value.class, aValue));
	}

	/**
	 * Return value as Type
	 * 
	 * @return attribute value
	 */
	public Value getValueAsType() {
		return Value.byCode(this.getValue());
	}

	/**
	 * Return value as string
	 * 
	 * @return name of value constant
	 */
	@Override
	public String getValueAsString() {
		return this.getValueAsType().toString();
	}

	/**
	 * Attribute value.
	 * <P>
	 * The service types are defined as follows when used in an Access- Accept. When used in an Access-Request, they MAY
	 * be considered to be a hint to the RADIUS server that the NAS has reason to believe the user would prefer the kind
	 * of service indicated, but the server is not required to honor the hint.
	 * </P>
	 */
	public enum Value {
		/**
		 * The user should be connected to a host.
		 */
		Login(1),
		/**
		 * A Framed Protocol should be started for the User, such as PPP or SLIP.
		 */
		Framed(2),
		/**
		 * The user should be disconnected and called back, then connected to a host.
		 */
		CallbackLogin(3),
		/**
		 * The user should be disconnected and called back, then a Framed Protocol should be started for the User, such
		 * as PPP or SLIP.
		 */
		CallbackFramed(4),
		/**
		 * The user should be granted access to outgoing devices.
		 */
		Outbond(5),
		/**
		 * The user should be granted access to the administrative interface to the NAS from which privileged commands
		 * can be executed.
		 */
		Administrative(6),
		/**
		 * The user should be provided a command prompt on the NAS from which non-privileged commands can be executed.
		 */
		NASPrompt(7),
		/**
		 * Only Authentication is requested, and no authorization information needs to be returned in the Access-Accept
		 * (typically used by proxy servers rather than the NAS itself).
		 */
		AuthenticateOnly(8),
		/**
		 * The user should be disconnected and called back, then provided a command prompt on the NAS from which
		 * non-privileged commands can be executed.
		 */
		CallbackNASPrompt(9),
		/**
		 * Used by the NAS in an Access-Request packet to indicate that a call is being received and that the RADIUS
		 * server should send back an Access-Accept to answer the call, or an Access-Reject to not accept the call,
		 * typically based on the Called-Station-Id or Calling-Station-Id attributes. It is recommended that such
		 * Access-Requests use the value of Calling-Station-Id as the value of the User-Name.
		 */
		CallCheck(10),
		/**
		 * The user should be disconnected and called back, then granted access to the administrative interface to the
		 * NAS from which privileged commands can be executed.
		 */
		CallbackAdministrative(11);

		/** Value code */
		private int code = 0;

		/** Constructor */
		private Value(int acode) {
			this.code = acode;
		}

		/**
		 * Return code
		 * 
		 * @return code of value
		 */
		public int getCode() {
			return this.code;
		}

		/**
		 * Return value by code
		 * 
		 * @param code code of value
		 * @return value by code or null if code unknown
		 */
		public static Value byCode(long code) {
			for (Value value : Value.values()) {
				if (value.code == code) return value;
			}
			return null;
		}
	}
}
