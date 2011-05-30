/**
 * MSChap2Response.java 25.05.2007
 */
package org.dicr.radius.attribute.ms;

import java.util.*;

import org.dicr.radius.attribute.impl.*;
import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.util.*;
import org.dicr.util.data.*;

/**
 * MS-CHAP2-Response Attribute
 * <P>
 * This Attribute contains the response value provided by an MS- CHAP-V2 peer in response to the challenge. It is only
 * used in Access-Request packets.
 * </P>
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070525
 */
public class MSChap2Response extends OctetsAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -9041361543905109680L;

	/** Type code */
	public static final int TYPE_CODE = 25;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(MicrosoftAttribute.VENDOR_CODE, TYPE_CODE);

	/** Value length */
	public static final int VALUE_LENGTH = 50;

	/**
     * Constructor
     * <P>
     * Initialize value with zero bytes
     * </P>
     */
	public MSChap2Response() {
		super(TYPE);
		byte[] value = new byte[VALUE_LENGTH];
		Arrays.fill(value, (byte) 0);
		this.setValue(value);
	}

	/**
     * Constructor.
     * <P>
     * Generate Peer-Challenge and calculate Response field. ident initialized as next value
     * </P>
     *
     * @param userName value of User-Name attribute
     * @param password user password in clear text
     * @param challenge value of MS-CHAP-Challenge attribute
     * @see MicrosoftAttribute#nextIdent()
     */
	public MSChap2Response(String userName, String password, MSChapChallenge challenge) {
		this();
		if (userName == null) throw new IllegalArgumentException("null userName");
		if (password == null) throw new IllegalArgumentException("null password");
		if (challenge == null) throw new IllegalArgumentException("null challenge");
		this.setIdent(MicrosoftAttribute.nextIdent());
		byte[] peerChallenge = MSCHAP.generatePeerChallenge();
		byte[] ntResponse = MSCHAP.ntResponseV2(challenge.getValue(), peerChallenge, userName, password);
		this.setPeerChallenge(peerChallenge);
		this.setResponse(ntResponse);
	}

	/**
     * @see org.dicr.radius.attribute.types.OctetsAttribute#setValue(byte[])
     */
	@Override
	public void setValue(byte[] value) {
		if (value == null || value.length != VALUE_LENGTH) throw new IllegalArgumentException("value");
		super.setValue(value);
	}

	/**
     * Return ident
     * <P>
     * Identical to the PPP MS-CHAP v2 Identifier.
     * <P>
     *
     * @return value of ident field
     */
	public byte getIdent() {
		return this.getValue()[0];
	}

	/**
     * Set ident
     *
     * @param ident new value of ident field
     */
	public void setIdent(byte ident) {
		byte[] value = this.getValue();
		value[0] = ident;
		this.setValue(value);
	}

	/**
     * Return flags
     * <P>
     * The Flags field is one octet in length. It is reserved for future use and MUST be zero.
     * </P>
     *
     * @return value of flags field
     */
	public byte getFlags() {
		return this.getValue()[1];
	}

	/**
     * Set flags
     *
     * @param flags new value for flags field
     */
	public void setFlags(byte flags) {
		byte[] value = this.getValue();
		value[1] = flags;
		this.setValue(value);
	}

	/**
     * Return peer challenge
     * <P>
     * The Peer-Challenge field is a 16-octet random number.
     * </P>
     *
     * @return byte[16] value of peer challenge field
     */
	public byte[] getPeerChallenge() {
		byte[] value = this.getValue();
		byte[] ret = new byte[16];
		System.arraycopy(value, 2, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set peer challenge
     *
     * @param challenge byte[16] value of new peer challenge
     */
	public void setPeerChallenge(byte[] challenge) {
		if (challenge == null || challenge.length != 16) throw new IllegalArgumentException("challenge");
		byte[] value = this.getValue();
		System.arraycopy(challenge, 0, value, 2, challenge.length);
		this.setValue(value);
	}

	/**
     * Set response
     * <P>
     * The Response field is 24 octets in length and holds an encoded function of the password, the Peer-Challenge field
     * and the received challenge.
     * </P>
     *
     * @return byte[24] value of response field
     */
	public byte[] getResponse() {
		byte[] value = this.getValue();
		byte[] ret = new byte[24];
		System.arraycopy(value, 26, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set response
     *
     * @param response byte[24] value f response field
     */
	public void setResponse(byte[] response) {
		if (response == null || response.length != 24) throw new IllegalArgumentException("response");
		byte[] value = this.getValue();
		System.arraycopy(response, 0, value, 26, response.length);
		this.setValue(value);
	}

	/**
     * @see org.dicr.radius.attribute.types.OctetsAttribute#getValueAsString()
     */
	@Override
	public String getValueAsString() {
		byte[] value = this.getValue();
		StringBuilder sb = new StringBuilder("{");
		sb.append("ident:").append(ByteUtils.unsigned(value[0])).append(";");
		sb.append("flags:").append(value[1]).append(";");
		sb.append("peer-challenge:").append(ByteUtils.toHexString(value, 2, 16)).append(";");
		sb.append("response:").append(ByteUtils.toHexString(value, 26, 24));
		sb.append("}");
		return sb.toString();
	}

	/**
     * Verify response against known user password
     *
     * @param userName value of User-Name attribute
     * @param password user password in clear text
     * @param challenge value of MS-CHAP-Challenge attribute
     * @return true if response field is valid, false in othe way
     */
	public boolean verifyResponse(UserName userName, String password, MSChapChallenge challenge) {
		if (userName == null) throw new IllegalArgumentException("null userName");
		if (password == null) throw new IllegalArgumentException("null password");
		if (challenge == null) throw new IllegalArgumentException("null challenge");
		byte[] response = MSCHAP.ntResponseV2(challenge.getValue(), this.getPeerChallenge(), userName.getValue(),
				password);
		return Arrays.equals(response, this.getResponse());
	}
}
