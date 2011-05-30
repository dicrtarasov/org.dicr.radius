/**
 * MSChapCPW1.java 22.05.2007
 */
package org.dicr.radius.attribute.ms;

import java.util.*;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.util.data.*;

/**
 * MS-CHAP-CPW-1 Attribute
 * <P>
 * This Attribute allows the user to change their password if it has expired. This Attribute is only used in
 * Access-Request packets, and should only be included if an MS-CHAP-Error attribute was included in the immediately
 * preceding Access-Reject packet, the String field of the MS-CHAP-Error attribute indicated that the user password had
 * expired, and the MS-CHAP version is less than 2.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070522
 */
public class MSChapCPW1 extends OctetsAttribute {

	/** Serial ID */
	private static final long serialVersionUID = 991465175310911865L;

	/** Type code */
	public static final int TYPE_CODE = 3;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(MicrosoftAttribute.VENDOR_CODE, TYPE_CODE);

	/** Value length */
	public static final int VALUE_LENGTH = 70;

	/** Value of code field (always equals 5) */
	public static final byte VALUE_CODE = (byte) 5;

	/**
     * Constructor
     */
	public MSChapCPW1() {
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
		if (value == null) throw new IllegalArgumentException("null value");
		if (value.length != VALUE_LENGTH) throw new IllegalArgumentException("value.length = " + value.length + " != "
				+ VALUE_LENGTH);
		value[0] = VALUE_CODE;
		super.setValue(value);
	}

	/**
     * Return code
     * <P>
     * The Code field is one octet in length. Its value is always 5.
     * </P>
     * 
     * @return 5
     */
	public byte getCode() {
		return this.getValue()[0];
	}

	/**
     * Return ident
     * <P>
     * The Ident field is one octet and aids in matching requests and replies.
     * </P>
     * 
     * @return value of ident
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
     * Return LM-Old-Password
     * <P>
     * The LM-Old-Password field is 16 octets in length. It contains the encrypted Lan Manager hash of the old password.
     * </P>
     * 
     * @return byte[16] value of LM-Old-Password field
     */
	public byte[] getLMOldPassword() {
		byte[] value = this.getValue();
		byte[] ret = new byte[16];
		System.arraycopy(value, 2, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set LM-Old-Passwd
     * 
     * @param pass byte[16] value of LM-Old-Passwd
     */
	public void setLMOldPassword(byte[] pass) {
		if (pass == null || pass.length != 16) throw new IllegalArgumentException("pass");
		byte[] value = this.getValue();
		System.arraycopy(pass, 0, value, 2, pass.length);
		this.setValue(value);
	}

	/**
     * Return LM-New-Password
     * <P>
     * New-Password The LM-New-Password field is 16 octets in length. It contains the encrypted Lan Manager hash of the
     * new password.
     * </P>
     * 
     * @return byte[16] value of LM-New-Password field
     */
	public byte[] getLMNewPassword() {
		byte[] value = this.getValue();
		byte[] ret = new byte[16];
		System.arraycopy(value, 18, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set LM-New-Passwd
     * 
     * @param pass byte[16] value of LM-New-Passwd
     */
	public void setLMNewPassword(byte[] pass) {
		if (pass == null || pass.length != 16) throw new IllegalArgumentException("pass");
		byte[] value = this.getValue();
		System.arraycopy(pass, 0, value, 18, pass.length);
		this.setValue(value);
	}

	/**
     * Return NT-Old-Password
     * <P>
     * The NT-Old-Password field is 16 octets in length. It contains the encrypted Lan Manager hash of the old password.
     * </P>
     * 
     * @return byte[16] value of NT-Old-Password field
     */
	public byte[] getNTOldPassword() {
		byte[] value = this.getValue();
		byte[] ret = new byte[16];
		System.arraycopy(value, 34, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set NT-Old-Passwd
     * 
     * @param pass byte[16] value of NT-Old-Passwd
     */
	public void setNTOldPassword(byte[] pass) {
		if (pass == null || pass.length != 16) throw new IllegalArgumentException("pass");
		byte[] value = this.getValue();
		System.arraycopy(pass, 0, value, 34, pass.length);
		this.setValue(value);
	}

	/**
     * Return NT-New-Password
     * <P>
     * The NT-New-Password field is 16 octets in length. It contains the encrypted Lan Manager hash of the new password.
     * </P>
     * 
     * @return byte[16] value of NT-New-Password field
     */
	public byte[] getNTNewPassword() {
		byte[] value = this.getValue();
		byte[] ret = new byte[16];
		System.arraycopy(value, 50, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set NT-New-Passwd
     * 
     * @param pass byte[16] value of NT-New-Passwd
     */
	public void setNTNewPassword(byte[] pass) {
		if (pass == null || pass.length != 16) throw new IllegalArgumentException("pass");
		byte[] value = this.getValue();
		System.arraycopy(pass, 0, value, 50, pass.length);
		this.setValue(value);
	}

	/**
     * Set New-LM-Password-Length
     * <P>
     * The New-LM-Password-Length field is two octets in length and contains the length in octets of the new LAN
     * Manager-compatible password.
     * </P>
     * 
     * @return value of New-LM-Password-Length field.
     */
	public int getNewLMPasswordLength() {
		byte[] value = this.getValue();
		return (value[66] << 8) + value[67];
	}

	/**
     * Set New-LM-Password-Length
     * 
     * @param length value of New-LM-Password-Length field
     */
	public void setNewLMPasswordLength(int length) {
		if (length < 0) throw new IllegalArgumentException("length: " + length);
		byte[] value = this.getValue();
		value[66] = (byte) ((length >> 8) & 0x0FF);
		value[67] = (byte) (length & 0x0FF);
		this.setValue(value);
	}

	/**
     * Return flags
     * <P>
     * The Flags field is two octets in length. If the least significant bit of the Flags field is one, this indicates
     * that the NT-New- Password and NT-Old-Password fields are valid and SHOULD be used. Otherwise, the LM-New-Password
     * and LM-Old-Password fields MUST be used.
     * </P>
     * 
     * @return value of Flags field
     */
	public int getFlags() {
		byte[] value = this.getValue();
		return (value[68] << 8) + value[69];
	}

	/**
     * Set flags
     * 
     * @param flags value of Flags field
     */
	public void setFlags(int flags) {
		byte[] value = this.getValue();
		value[68] = (byte) ((flags >> 8) & 0x0FF);
		value[69] = (byte) (flags & 0x0FF);
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
		sb.append("lm-old-password:").append(ByteUtils.toHexString(value, 2, 16)).append(";");
		sb.append("lm-new-password:").append(ByteUtils.toHexString(value, 18, 16)).append(";");
		sb.append("nt-old-password:").append(ByteUtils.toHexString(value, 34, 16)).append(";");
		sb.append("nt-new-password:").append(ByteUtils.toHexString(value, 50, 16)).append(";");
		sb.append("new-lm-password-length:").append(ByteUtils.toInteger(value, 66, 2)).append(";");
		sb.append("flags:").append(ByteUtils.toInteger(value, 68, 2));
		sb.append("}");
		return sb.toString();
	}
}
