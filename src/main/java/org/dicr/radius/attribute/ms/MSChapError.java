/**
 * MSChapError.java 22.05.2007
 */
package org.dicr.radius.attribute.ms;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * MS-Chap-Error Attribute
 * <P>
 * The MS-CHAP-Error Attribute contains error data related to the preceding MS-CHAP exchange. This Attribute may be used
 * in both MS-CHAP-V1 and MS-CHAP-V2 (see below) exchanges. It is only used in Access-Reject packets.
 * </P>
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070522
 */
public class MSChapError extends IdentStringAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -3331545907036334076L;

	/** Type code */
	public static final int TYPE_CODE = 2;

	/** Atribute type */
	public static final AttributeType TYPE = new AttributeType(MicrosoftAttribute.VENDOR_CODE, TYPE_CODE);

	/**
     * Constructor
     */
	public MSChapError() {
		super(TYPE);
	}
}
