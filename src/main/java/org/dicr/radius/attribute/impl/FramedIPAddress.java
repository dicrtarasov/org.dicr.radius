/**
 * FramedIPAddress.java 16.06.2006 9:26:32 dicr
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.util.net.*;

/**
 * Framed-IP-Address attribute.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060616
 */
public final class FramedIPAddress extends AddressAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 1672964578123512072L;

	/** Attribute type code */
	public static final int TYPE_CODE = 8;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, FramedIPAddress.TYPE_CODE);

	/**
     * Constructor
     */
	public FramedIPAddress() {
		super(FramedIPAddress.TYPE);
	}

	/**
     * Constructor.
     * 
     * @param ip ip-address
     */
	public FramedIPAddress(int ip) {
		super(FramedIPAddress.TYPE, ip);
	}

	/**
     * Constructor.
     * 
     * @param ip ip-address
     */
	public FramedIPAddress(IP ip) {
		super(FramedIPAddress.TYPE, ip);
	}

	/**
     * Constructor.
     * 
     * @param ip ip-address
     */
	public FramedIPAddress(String ip) {
		super(FramedIPAddress.TYPE, ip);
	}
}
