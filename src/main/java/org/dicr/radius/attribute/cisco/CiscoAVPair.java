package org.dicr.radius.attribute.cisco;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Cisco AVPair attribute
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 091027
 */
public class CiscoAVPair extends StringAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 3688214770695284296L;

	/** Type code */
	public static final int TYPE_CODE = 1;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(CiscoAttribute.VENDOR_CODE, TYPE_CODE);

	/**
	 * Constructor
	 */
	public CiscoAVPair() {
		super(CiscoAVPair.TYPE);
	}

	/**
	 * Constructor
	 * 
	 * @param protocol protocol name
	 * @param protocolAttributes protocol attributes
	 */
	public CiscoAVPair(String protocol, String protocolAttributes) {
		super(CiscoAVPair.TYPE);
		this.setValue(protocol, protocolAttributes);
	}

	/**
	 * Split string value
	 * 
	 * @param val value in format <CODE>protocol:protocol attributes</CODE>
	 * @return String[2] { "protocol", "protocol attributes" } or null if value format is incorrect
	 */
	private static final String[] splitValue(String val) {
		if (val == null || val.isEmpty()) return null;
		String[] parts = val.split(":");
		if (parts == null || parts.length != 2) return null;
		return parts;
	}

	/**
	 * Return protocol
	 * 
	 * @return protocol part of attribute value
	 */
	public String getProtocol() {
		String[] parts = CiscoAVPair.splitValue(this.getValue());
		return parts != null ? parts[0].trim() : null;
	}

	/**
	 * Return protocol attributes
	 * 
	 * @return protocol attributes part of value
	 */
	public String getProtocolAttributes() {
		String[] parts = CiscoAVPair.splitValue(this.getValue());
		return parts != null ? parts[1].trim() : null;
	}

	/**
	 * Set attributes value
	 * 
	 * @param protocol protocol
	 * @param protocolAttributes protocol attributes
	 */
	public void setValue(String protocol, String protocolAttributes) {
		if (protocol == null || protocol.isEmpty()) throw new IllegalArgumentException("protocol=" + protocol);
		if (protocolAttributes == null || protocolAttributes.isEmpty()) throw new IllegalArgumentException(
		        "protocolAttributes=" + protocolAttributes);
		super.setValue(protocol.trim() + ":" + protocolAttributes.trim());
	}

	/**
	 * Set attribute value
	 * 
	 * @param value attribute value in format <CODE>protocol:protocol attributes</CODE>
	 */
	@Override
	protected void setValue(String value) {
		if (value == null || value.isEmpty()) throw new IllegalArgumentException("value=" + value);
		String[] parts = CiscoAVPair.splitValue(value);
		if (parts == null) throw new IllegalArgumentException("value=" + value);
		this.setValue(parts[0].trim(), parts[1].trim());
	}
}
