/**
 * MSChapResponse.java 17.05.2007
 */
package org.dicr.radius.attribute.ms;

import java.util.*;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.util.*;
import org.dicr.util.data.*;

/**
 * MS-Chap-Response Attribute
 * <P>
 * This Attribute contains the challenge sent by a NAS to a Microsoft Challenge-Handshake Authentication Protocol
 * (MS-CHAP) user. It MAY be used in both Access-Request and Access-Challenge packets.
 * </P>
 * <P>
 * Only <B>NTResponse</B> is supported, because LMResponse is deprecated
 * </P>
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070517
 */
public class MSChapResponse extends OctetsAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -8491427923475880856L;

	/** Type Code */
	public static final int TYPE_CODE = 1;

	/** Attribute Type */
	public static final AttributeType TYPE = new AttributeType(MicrosoftAttribute.VENDOR_CODE, TYPE_CODE);

	/** Value length */
	public static final int VALUE_LENGTH = 50;

	/**
     * Constructor
     */
	public MSChapResponse() {
		super(TYPE);
		byte[] value = new byte[50];
		Arrays.fill(value, (byte) 0);
		// force 'flags' to 1 (NTResponse vs deprecated LMResponse)
		value[1] = 1;
		this.setValue(value);
	}

	/**
     * Constructor
     *
     * @param value attribute value
     */
	public MSChapResponse(byte[] value) {
		super(TYPE);
		if (value == null) throw new IllegalArgumentException("null value");
		this.setValue(value);
	}

	/**
     * Constructor
     * <P>
     * generate NT-Response field from challenge and password, also set ident field to next ident
     * </P>
     *
     * @param challenge MS-Chap-Challenge attribute from request
     * @param password clear text original user password
     * @see MicrosoftAttribute#nextIdent()
     */
	public MSChapResponse(MSChapChallenge challenge, String password) {
		this();
		if (challenge == null) throw new IllegalArgumentException("null challenge");
		if (password == null) throw new IllegalArgumentException("null password");
		this.setIdent(MicrosoftAttribute.nextIdent());
		this.setNTResponse(challenge, password);
	}

	/**
     * Set encoded whole value of attribute.
     *
     * @param encodedValue byte[{@value #VALUE_LENGTH}] encodedValue encoded value of whole attribute.
     */
	@Override
	public void setValue(byte[] encodedValue) {
		if (encodedValue == null || encodedValue.length != VALUE_LENGTH) throw new IllegalArgumentException("value");
		super.setValue(encodedValue);
	}

	/**
     * Return ident. <BR>
     * (Identical to the PPP CHAP Identifier).
     *
     * @return value of 'ident' part of attribute value
     */
	public byte getIdent() {
		return this.getValue()[0];
	}

	/**
     * Set ident
     *
     * @param ident value of ident
     */
	public void setIdent(byte ident) {
		byte[] value = this.getValue();
		value[0] = ident;
		this.setValue(value);
	}

	/**
     * Set NT-Response field of value
     *
     * @param response byte[24]
     */
	public void setNTResponse(byte[] response) {
		if (response == null || response.length != 24) throw new IllegalArgumentException("null response");
		byte[] value = this.getValue();
		System.arraycopy(response, 0, value, 26, response.length);
		value[1] = 1;
		this.setValue(value);
	}

	/**
     * Set NT-Response, generated from given challenge and password
     *
     * @param challenge MA-Chap-Challenge attribute
     * @param password clear text password
     */
	public void setNTResponse(MSChapChallenge challenge, String password) {
		if (challenge == null) throw new IllegalArgumentException("null challenge");
		if (password == null) throw new IllegalArgumentException("null password");
		this.setNTResponse(MSCHAP.ntResponseV1(challenge.getValue(), password));
	}

	/**
     * Return NT-Response
     * <P>
     * The NT-Response field is 24 octets in length and holds an encoded function of the password and the received
     * challenge. If this field is empty, it SHOULD be zero-filled.
     * </P>
     *
     * @return byte[24] value of NT-Response part of attribute value
     */
	public byte[] getNTResponse() {
		byte[] value = this.getValue();
		byte[] nt = new byte[24];
		System.arraycopy(value, 26, nt, 0, 24);
		return nt;
	}

	/**
     * @see org.dicr.radius.attribute.types.OctetsAttribute#getValueAsString()
     */
	@Override
	public String getValueAsString() {
		StringBuilder sb = new StringBuilder("{");
		byte[] value = this.getValue();
		sb.append("ident:").append(ByteUtils.unsigned(value[0])).append(";");
		sb.append("flags:").append(value[1]).append(";");
		sb.append("lm-response:").append(ByteUtils.toHexString(value, 2, 24)).append(";");
		sb.append("nt-response:").append(ByteUtils.toHexString(value, 26, 24));
		sb.append("}");
		return sb.toString();
	}

	/**
     * Verify response value
     *
     * @param challenge MS-Chap-Challenge with 8-bytes value
     * @param password [0..256] clear text user password
     * @return true if response is valid
     */
	public boolean verifyResponse(MSChapChallenge challenge, String password) {
		if (challenge == null) throw new IllegalArgumentException("null challenge");
		if (challenge.getValue().length < 8) throw new IllegalArgumentException(
				"challenge: " + challenge);
		if (password == null) throw new IllegalArgumentException("null password");
		byte[] result = MSCHAP.ntResponseV1(challenge.getValue(), password);
		return Arrays.equals(this.getNTResponse(), result);
	}
}
