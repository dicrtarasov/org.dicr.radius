/**
 * MSChapCPW2.java 22.05.2007
 */
package org.dicr.radius.attribute.ms;

import java.util.*;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.util.data.*;

/**
 * MS-CHAP-CPW2 Attribute
 * <P>
 * This Attribute allows the user to change their password if it has expired. This Attribute is only used in
 * Access-Request packets, and should only be included if an MS-CHAP-Error attribute was included in the immediately
 * preceding Access-Reject packet, the String field of the MS-CHAP-Error attribute indicated that the user password had
 * expired, and the MS-CHAP version is equal to 2.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070522
 */
public class MSChapCPW2 extends OctetsAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -391199440302372146L;

	/** Type code */
	public static final int TYPE_CODE = 6;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(MicrosoftAttribute.VENDOR_CODE, TYPE_CODE);

	/** Value of 'Code' field */
	public static final byte VALUE_CODE = 6;

	/** length of value in bytes */
	public static final int VALUE_LENGTH = 84;

	/**
     * Constructor
     */
	public MSChapCPW2() {
		super(TYPE);
		byte[] value = new byte[VALUE_LENGTH];
		Arrays.fill(value, (byte) 0);
		this.setValue(value);
	}

	/**
     * @see org.dicr.radius.attribute.types.OctetsAttribute#setValue(byte[])
     */
	@Override
	public void setValue(byte[] value) {
		if (value == null || value.length != VALUE_LENGTH) throw new IllegalArgumentException("value");
		value[0] = VALUE_CODE;
		super.setValue(value);
	}

	/**
     * Return Code
     * 
     * @return always 6
     */
	public byte getCode() {
		return VALUE_CODE;
	}

	/**
     * Set ident
     * <P>
     * The Ident field is one octet and aids in matching requests and replies. The value of this field MUST be identical
     * to that in the Ident field in all instances of the MS-CHAP-LM-Enc-PW, MS-CHAP-NT- Enc-PW and MS-CHAP-PW-2
     * attributes contained in a single Access- Request packet.
     * </P>
     * 
     * @return value of ident field
     */
	public byte getIdent() {
		return this.getValue()[1];
	}

	/**
     * Set ident
     * 
     * @param ident value of ident
     */
	public void setIdent(byte ident) {
		byte[] value = this.getValue();
		value[1] = ident;
		this.setValue(value);
	}

	/**
     * Return Old-NT-Hash
     * <P>
     * The Old-NT-Hash field is 16 octets in length. It contains the old Windows NT password hash encrypted with the new
     * Windows NT password hash.
     * </P>
     * 
     * @return byte[16] value of Old-NT-Hash field
     */
	public byte[] getOldNTHash() {
		byte[] value = this.getValue();
		byte[] ret = new byte[16];
		System.arraycopy(value, 2, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set Old-NT-Hash
     * 
     * @param hash byte[16] value
     */
	public void setOldNTHash(byte[] hash) {
		if (hash == null || hash.length != 16) throw new IllegalArgumentException("hash");
		byte[] value = this.getValue();
		System.arraycopy(hash, 0, value, 2, hash.length);
		this.setValue(value);
	}

	/**
     * Return Old-LM-Hash
     * <P>
     * The Old-LM-Hash field is 16 octets in length. It contains the old Lan Manager password hash encrypted with the
     * new Windows NT password hash.
     * </P>
     * 
     * @return byte[16] value of Old-LM-Hash field
     */
	public byte[] getOldLMHash() {
		byte[] value = this.getValue();
		byte[] ret = new byte[16];
		System.arraycopy(value, 18, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set Old-LM-Hash
     * 
     * @param hash byte[16] value
     */
	public void setOldLMHash(byte[] hash) {
		if (hash == null || hash.length != 16) throw new IllegalArgumentException("hash");
		byte[] value = this.getValue();
		System.arraycopy(hash, 0, value, 18, hash.length);
		this.setValue(value);
	}

	/**
     * Return LM-Response
     * <P>
     * The LM-Response field is 24 octets in length and holds an encoded function of the password and the received
     * challenge. If this field is empty, it SHOULD be zero-filled.
     * </P>
     * 
     * @return byte[24] value of LM-Response field
     */
	public byte[] getLMResponse() {
		byte[] value = this.getValue();
		byte[] ret = new byte[24];
		System.arraycopy(value, 34, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set LM-Response
     * 
     * @param response byte[24] value
     */
	public void setLMResponse(byte[] response) {
		if (response == null || response.length != 24) throw new IllegalArgumentException("hash");
		byte[] value = this.getValue();
		System.arraycopy(response, 0, value, 34, response.length);
		this.setValue(value);
	}

	/**
     * Return NT-Response
     * <P>
     * The NT-Response field is 24 octets in length and holds an encoded function of the password and the received
     * challenge. If this field is empty, it SHOULD be zero-filled.
     * </P>
     * 
     * @return byte[24] value of NT-Response field
     */
	public byte[] getNTResponse() {
		byte[] value = this.getValue();
		byte[] ret = new byte[24];
		System.arraycopy(value, 58, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set NT-Response
     * 
     * @param response byte[24] value
     */
	public void setNTResponse(byte[] response) {
		if (response == null || response.length != 24) throw new IllegalArgumentException("hash");
		byte[] value = this.getValue();
		System.arraycopy(response, 0, value, 58, response.length);
		this.setValue(value);
	}

	/**
     * Return flags
     * <P>
     * The Flags field is two octets in length. If the least significant bit (bit 0) of this field is one, the
     * NT-Response field is to be used in preference to the LM-Response field for authentication. The LM-Response field
     * MAY still be used (if present), but the NT- Response SHOULD be tried first. If least significant bit of the field
     * is zero, the NT-Response field MUST be ignored and the LM- Response field used instead. If bit 1 of the Flags
     * field is one, the Old-LM-Hash field is valid and SHOULD be used. If this bit is set, at least one instance of the
     * MS-CHAP-LM-Enc-PW attribute MUST be included in the packet.
     * </P>
     * 
     * @return value of flags field
     */
	public int getFlags() {
		byte[] value = this.getValue();
		
		return (value[82] << 8) + value[83];
	}

	/**
     * Set flags
     * 
     * @param flags value of flags
     */
	public void setFlags(int flags) {
		byte[] value = this.getValue();
		value[82] = (byte) ((flags >> 8) & 0x0FF);
		value[83] = (byte) (flags & 0x0FF);
		this.setValue(value);
	}

	/**
     * @see org.dicr.radius.attribute.types.OctetsAttribute#getValueAsString()
     */
	@Override
	public String getValueAsString() {
		StringBuilder sb = new StringBuilder("{");
		byte[] value = this.getValue();
		sb.append("code:").append(value[0]).append(";");
		sb.append("ident:").append(ByteUtils.unsigned(value[1])).append(";");
		sb.append("old-nt-hash:").append(ByteUtils.toHexString(value, 2, 16)).append(";");
		sb.append("old-lm-hash:").append(ByteUtils.toHexString(value, 18, 16)).append(";");
		sb.append("lm-response:").append(ByteUtils.toHexString(value, 34, 24)).append(";");
		sb.append("nt-response:").append(ByteUtils.toHexString(value, 58, 24)).append(";");
		sb.append("flags:").append(ByteUtils.toInteger(value, 82, 2));
		sb.append("}");
		return sb.toString();
	}
}
