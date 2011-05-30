/**
 * MicrosoftAttribute.java 17.05.2007
 */
package org.dicr.radius.attribute.ms;

import org.dicr.radius.attribute.*;
import org.dicr.radius.attribute.types.*;

/**
 * Microsoft Vendor Attribute
 * <P>
 * Vendor-Specific attribute of Microsoft vendor, which incapsulate all microsoft specific attributes.
 * </P>
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070517
 */
public class MicrosoftAttribute extends VendorAttribute {
	/** serial */
	private static final long serialVersionUID = -6777424092581979624L;

	/** Vendor code of Microsoft */
	public static final int VENDOR_CODE = 311;

	/** MSCHAP request ident counter */
	private static byte ident = 0;

	/**
     * Constructor
     */
	public MicrosoftAttribute() {
		super(VENDOR_CODE);
	}

	/**
     * Constructor
     *
     * @param attribute microsoft vendor attribute to add
     */
	public MicrosoftAttribute(RadiusAttribute attribute) {
		super(VENDOR_CODE);
		this.getAttributes().add(attribute);
	}

	/**
	 * Generate next MSCHAP ident field value
	 *
	 * @return next value of ident
	 */
	public static synchronized byte nextIdent() {
		return ident++;
	}
}
