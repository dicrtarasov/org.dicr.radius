/**
 * MSChapChallenge.java 17.05.2007
 */
package org.dicr.radius.attribute.ms;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.util.*;

/**
 * MS-Chap-Channelge attribute
 * <P>
 * This Attribute contains the challenge sent by a NAS to a Microsoft Challenge-Handshake Authentication Protocol
 * (MS-CHAP) user. It MAY be used in both Access-Request and Access-Challenge packets.
 * </P>
 * <P>
 * For the MS-CHAP protocol it's value must be <B>8</B> bytes length, for the MS-CHAP-v2 value must be 16 bytes !!!
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070517
 */
public class MSChapChallenge extends OctetsAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -1293156777286987391L;

	/** Type code */
	public static final int TYPE_CODE = 11;

	/** Attribute Type */
	public static final AttributeType TYPE = new AttributeType(MicrosoftAttribute.VENDOR_CODE,
			MSChapChallenge.TYPE_CODE);

	/**
     * Constructor
     */
	public MSChapChallenge() {
		super(TYPE, MD5.randomDigestData());
	}

	/**
     * Constructor
     * 
     * @param value value
     */
	public MSChapChallenge(byte[] value) {
		super(TYPE, value);
	}

	/**
     * Set value
     * 
     * @param value byte[16] value
     */
	@Override
	public void setValue(byte[] value) {
		if (value == null || value.length < 8) throw new IllegalArgumentException("value");
		super.setValue(value);
	}
}
