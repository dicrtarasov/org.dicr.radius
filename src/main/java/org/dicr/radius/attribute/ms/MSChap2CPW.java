/**
 * MSChap2CPW.java 25.05.2007
 */
package org.dicr.radius.attribute.ms;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.util.data.*;

/**
 * MS-CHAP2-CPW Attribute
 * <P>
 * This Attribute allows the user to change their password if it has expired. This Attribute is only used in conjunction
 * with the MS- CHAP-NT-Enc-PW attribute in Access-Request packets, and should only be included if an MS-CHAP-Error
 * attribute was included in the immediately preceding Access-Reject packet, the String field of the MS-CHAP-Error
 * attribute indicated that the user password had expired, and the MS-CHAP version is equal to 3.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070525
 */
public class MSChap2CPW extends OctetsAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 2072032877878508243L;

	/** Type code */
	public static final int TYPE_CODE = 27;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(MicrosoftAttribute.VENDOR_CODE, TYPE_CODE);

	/** Value length */
	public static final int VALUE_LENGTH = 68;

	/**
     * Constructor
     */
	public MSChap2CPW() {
		super(TYPE);
	}

	/**
	 * @see org.dicr.radius.attribute.types.OctetsAttribute#setValue(byte[])
	 */
	@Override
	public void setValue(byte[] value) {
		if (value == null || value.length != VALUE_LENGTH) throw new IllegalArgumentException("value");
		value[0] = 7; // override, MUST be always 7
		super.setValue(value);
	}

	/**
     * Return code
     * <P>
     * Code value is always 7
     * </P>
     * 
     * @return code field value (always 7)
     */
	public byte getCode() {
		return this.getValue()[0];
	}

	/**
     * Return Ident
     * <P>
     * The Ident field is one octet and aids in matching requests and replies. The value of this field MUST be identical
     * to that in the Ident field in all instances of the MS-CHAP-NT-Enc-PW contained in the Access-Request packet.
     * </P>
     * 
     * @return value of ident field
     */
	public byte getIdent() {
		return this.getValue()[1];
	}

	/**
     * Set Ident
     * 
     * @param ident new value for ident field
     */
	public void setIdent(byte ident) {
		byte[] value = this.getValue();
		value[1] = ident;
		this.setValue(value);
	}

	/**
     * Return Encrypted-Hash
     * <P>
     * The Ident field is one octet and aids in matching requests and replies. The value of this field MUST be identical
     * to that in the Ident field in all instances of the MS-CHAP-NT-Enc-PW contained in the Access-Request packet.
     * </P>
     * 
     * @return byte[16] value of encrypted hash field
     */
	public byte[] getEncryptedHash() {
		byte[] value = this.getValue();
		byte[] ret = new byte[16];
		System.arraycopy(value, 2, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set Encrypted-Hash
     * 
     * @param hash byte[16] value for encrypted hash field
     */
	public void setEncryptedHash(byte[] hash) {
		if (hash == null || hash.length != 16) throw new IllegalArgumentException("hash");
		byte[] value = this.getValue();
		System.arraycopy(hash, 0, value, 2, hash.length);
		this.setValue(value);
	}

	/**
     * Return Peer-Challenge
     * 
     * @return byte[24] value of peer challenge
     */
	public byte[] getPeerChallenge() {
		byte[] value = this.getValue();
		byte[] ret = new byte[24];
		System.arraycopy(value, 18, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set Peer-Challenge
     * 
     * @param challenge byte[24] new value of peer challenge
     */
	public void setPeerChallenge(byte[] challenge) {
		if (challenge == null || challenge.length != 24) throw new IllegalArgumentException("challenge ");
		byte[] value = this.getValue();
		System.arraycopy(challenge, 0, value, 18, challenge.length);
	}

	/**
     * Return NT-Response
     * <P>
     * The NT-Response field is 24 octets in length and holds an encoded function of the new password, the
     * Peer-Challenge field and the received challenge.
     * </P>
     * 
     * @return byte[24] value of NT-Response field
     */
	public byte[] getNTResponse() {
		byte[] value = this.getValue();
		byte[] ret = new byte[24];
		System.arraycopy(value, 42, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set NT-Response
     * 
     * @param response byte[24] new value for NT-Response field
     */
	public void setNTResponse(byte[] response) {
		if (response == null || response.length != 24) throw new IllegalArgumentException("response");
		byte[] value = this.getValue();
		System.arraycopy(response, 0, value, 42, response.length);
		this.setValue(value);
	}

	/**
     * Return Flags
     * <P>
     * The Flags field is two octets in length. This field is reserved for future use and MUST be zero.
     * </P>
     * 
     * @return value f flags field
     */
	public int getFlags() {
		return ByteUtils.toInteger(this.getValue(), 66, 2);
	}

	/**
     * Set Flags
     * 
     * @param flags value of Flags field
     */
	public void setFlags(int flags) {
		byte[] value = this.getValue();
		value[66] = (byte) ((flags >> 8) & 0x0FF);
		value[67] = (byte) (flags & 0x0FF);
		this.setValue(value);
	}

	/**
     * @see org.dicr.radius.attribute.types.OctetsAttribute#getValueAsString()
     */
	@Override
	public String getValueAsString() {
		byte[] value = this.getValue();
		StringBuilder sb = new StringBuilder("{");
		sb.append("code:").append(value[0]).append(";");
		sb.append("ident:").append(ByteUtils.unsigned(value[1])).append(";");
		sb.append("encrypted-hash:").append(ByteUtils.toHexString(value, 2, 16)).append(";");
		sb.append("peer-challenge:").append(ByteUtils.toHexString(value, 18, 24)).append(";");
		sb.append("nt-response:").append(ByteUtils.toHexString(value, 42, 24)).append(";");
		sb.append("flags:").append(ByteUtils.toInteger(value, 66, 2));
		sb.append(";");
		return sb.toString();
	}
}
