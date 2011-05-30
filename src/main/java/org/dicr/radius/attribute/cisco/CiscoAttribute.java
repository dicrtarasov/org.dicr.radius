/**
 * CiscoAttribute.java 17.05.2007
 */
package org.dicr.radius.attribute.cisco;

import org.dicr.radius.attribute.*;
import org.dicr.radius.attribute.types.*;

/**
 * Cisco Vendor Specific Attribute (Cisco VSA)
 * <P>
 * Vendor-Specific attribute of Cisco vendor, which incapsulate all cisco specific attributes.<BR>
 * (See http://www.cisco.com/en/US/docs/ios/12_2/security/configuration/guide/scfrdat3.html#wp1034050)
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 091028
 */
public class CiscoAttribute extends VendorAttribute {
	/** serial */
	private static final long serialVersionUID = -6777424092581979624L;

	/** Vendor code of Cisco */
	public static final int VENDOR_CODE = 9;

	/**
	 * Constructor
	 */
	public CiscoAttribute() {
		super(VENDOR_CODE);
	}

	/**
	 * Constructor
	 * 
	 * @param attribute cisco vendor attribute to add
	 */
	public CiscoAttribute(RadiusAttribute attribute) {
		super(VENDOR_CODE);
		this.getAttributes().add(attribute);
	}
}
