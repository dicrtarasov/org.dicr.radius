/**
 * NASIPAddress.java 16.06.2006 7:27:31 dicr
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.util.net.*;

/**
 * NAS-IP-Address attribute.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060616
 */
public final class NASIPAddress extends AddressAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -7580561368374069665L;

	/** Attribute type code */
	public static final int TYPE_CODE = 4;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, NASIPAddress.TYPE_CODE);

	/**
     * Constructor
     */
	public NASIPAddress() {
		super(NASIPAddress.TYPE);
	}

	/**
     * Constructor
     * 
     * @param ip IP-address
     */
	public NASIPAddress(int ip) {
		super(NASIPAddress.TYPE, ip);
	}

	/**
     * Constructor
     * 
     * @param ip IP-address
     */
	public NASIPAddress(IP ip) {
		super(NASIPAddress.TYPE, ip);
	}

	/**
     * Constructor
     * 
     * @param ip IP-address
     */
	public NASIPAddress(String ip) {
		super(NASIPAddress.TYPE, ip);
	}
}
