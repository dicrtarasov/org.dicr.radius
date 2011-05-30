/**
 * MSChapLMEncPW.java 25.05.2007
 */
package org.dicr.radius.attribute.ms;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.util.data.*;

/**
 * MS-CHAP-NT-Enc-PW attribute
 * <P>
 * This Attribute contains the new Windows NT password encrypted with the old Windows NT password hash. The encrypted
 * Windows NT password is 516 octets in length; since this is longer than the maximum lengtth of a RADIUS attribute, the
 * password must be split into several attibutes for transmission. A 2 octet sequence number is included in the
 * attribute to help preserve ordering of the password fragments.
 * </P>
 * <P>
 * This Attribute is only used in Access-Request packets, in conjunc- tion with the MS-CHAP-CPW-2 and MS-CHAP2-CPW
 * attributes. It should only be included if an MS-CHAP-Error attribute was included in the immediately preceding
 * Access-Reject packet, the String field of the MS-CHAP-Error attribute indicated that the user password had expired,
 * and the MS-CHAP version is 2 or greater.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070525
 */
public class MSChapNTEncPW extends OctetsAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 5783607114813090516L;

	/** Type Code */
	public static final int TYPE_CODE = 6;

	/** Attribute Type */
	public static final AttributeType TYPE = new AttributeType(MicrosoftAttribute.VENDOR_CODE, TYPE_CODE);

	/** Minimal length of value */
	public static final int VALUE_LENGTH_MIN = 4;

	/**
     * Constructor
     */
	public MSChapNTEncPW() {
		super(TYPE);
	}

	/**
     * @see org.dicr.radius.attribute.types.OctetsAttribute#setValue(byte[])
     */
	@Override
	public void setValue(byte[] value) {
		if (value == null || value.length < VALUE_LENGTH_MIN) throw new IllegalArgumentException("value");
		super.setValue(value);
	}

	/**
     * Return code
     * <P>
     * Code is the same as for the MS-CHAP-PW-2 attribute.
     * </P>
     * 
     * @return value of code field
     */
	public byte getCode() {
		return this.getValue()[0];
	}

	/**
     * Set code
     * 
     * @param code new value for code field
     */
	public void setCode(byte code) {
		byte[] value = this.getValue();
		value[0] = code;
		this.setValue(value);
	}

	/**
     * Return ident
     * <P>
     * The Ident field is one octet and aids in matching requests and replies. The value of this field MUST be identical
     * in all instances of the MS-CHAP-LM-Enc-PW, MS-CHAP-NT-Enc-PW and MS- CHAP-PW-2 attributes which are present in
     * the same Access-Request packet.
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
     * @param ident new value for ident field
     */
	public void setIdent(byte ident) {
		byte[] value = this.getValue();
		value[1] = ident;
		this.setValue(value);
	}

	/**
     * Return sequence-number
     * <P>
     * The Sequence-Number field is two octets in length and indicates which "chunk" of the encrypted password is
     * contained in the following String field.
     * </P>
     * 
     * @return value of sequence-number field
     */
	public int getSequenceNumber() {
		return ByteUtils.toInteger(this.getValue(), 2, 2);
	}

	/**
     * Set sequence-number
     * 
     * @param number new value for sequence-number field
     */
	public void setSequenceNumber(int number) {
		if (number < 0) throw new IllegalArgumentException("number: " + number);
		byte[] value = this.getValue();
		value[2] = (byte) ((number >> 8) & 0x0FF);
		value[3] = (byte) (number & 0x0FF);
		this.setValue(value);
	}

	/**
     * Return encoded password
     * <P>
     * The String field contains a portion of the encrypted password.
     * </P>
     * 
     * @return value of String field
     */
	public byte[] getEncodedPassword() {
		byte[] value = this.getValue();
		byte[] ret = new byte[value.length - 4];
		System.arraycopy(value, 4, ret, 0, ret.length);
		return ret;
	}

	/**
     * Set encoded password
     * 
     * @param passwd
     */
	public void setEncodedPassword(byte[] passwd) {
		if (passwd == null) throw new IllegalArgumentException("null passwd");
		byte[] oldvalue = this.getValue();
		byte[] newvalue = new byte[4 + passwd.length];
		System.arraycopy(oldvalue, 0, newvalue, 0, 4);
		System.arraycopy(passwd, 0, newvalue, 4, passwd.length);
		this.setValue(newvalue);
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
		sb.append("sequence-number:").append(ByteUtils.toInteger(value, 2, 2)).append(";");
		sb.append("encpw:").append(ByteUtils.toHexString(value, 4, value.length - 4));
		sb.append("}");
		return sb.toString();
	}
}
