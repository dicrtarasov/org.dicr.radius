package org.dicr.radius.attribute.types;

import org.dicr.radius.dictionary.*;
import org.dicr.util.net.*;

/**
 * General attribute with Address type.
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060615
 */
public class AddressAttribute extends IntegerAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 2687544774444495775L;

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 */
	public AddressAttribute(AttributeType aType) {
		super(aType);
	}

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 * @param ip IP-address
	 */
	public AddressAttribute(AttributeType aType, int ip) {
		super(aType, ip);
	}

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 * @param ip IP-address
	 */
	public AddressAttribute(AttributeType aType, IP ip) {
		this(aType);
		this.setValue(ip);
	}

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 * @param ip IP-address
	 */
	public AddressAttribute(AttributeType aType, String ip) {
		super(aType, ip);
	}

	/**
	 * Set value
	 * 
	 * @param ip IP-address
	 */
	protected void setValue(IP ip) {
		if (ip == null) throw new IllegalArgumentException("null ip");
		this.setValue(ip.toInteger());
	}

	/**
	 * Return value as IP
	 * 
	 * @return value
	 */
	public IP getValueAsIP() {
		return new IP((int) this.getValue());
	}

	/**
	 * Set value
	 * 
	 * @param address IP-address
	 */
	@Override
	protected void setValue(String address) {
		if (address == null) throw new IllegalArgumentException("null address");
		try {
			this.setValue(IP.parse(address));
		} catch (IncorrectAddressException ex) {
			throw new IllegalArgumentException("address", ex);
		}
	}

	/**
	 * Return value as string
	 * 
	 * @return value {@link IP#toString()}
	 */
	@Override
	public String getValueAsString() {
		return this.getValueAsIP().toString();
	}
}
