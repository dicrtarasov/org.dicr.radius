/**
 * MSChapDomain.java 21.05.2007
 */
package org.dicr.radius.attribute.ms;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * MS-ChAP-Domain attribute
 * <P>
 * The MS-CHAP-Domain Attribute indicates the Windows NT domain in which the user was authenticated. It MAY be included
 * in both Access-Accept and Accounting-Request packets.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070521
 */
public class MSChapDomain extends IdentStringAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -1448487905030610304L;

	/** Type code */
	public static final int TYPE_CODE = 10;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(MicrosoftAttribute.VENDOR_CODE, TYPE_CODE);

	/**
     * Constructor
     */
	public MSChapDomain() {
		super(TYPE);
	}
}
