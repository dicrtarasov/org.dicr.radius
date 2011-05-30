/**
 * MSChapDomain.java 21.05.2007
 */
package org.dicr.radius.attribute.types;

import java.io.*;

import org.dicr.radius.attribute.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;
import org.dicr.util.data.*;

/**
 * General type of attributes, which contains 2 field: byte ident and string
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070521
 */
public class IdentStringAttribute extends RadiusAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -7654784180077246317L;

	/** Ident */
	private byte ident = 0;

	/** String value */
	private String value = null;

	/**
	 * Constructor
	 * 
	 * @param type attribute type
	 */
	public IdentStringAttribute(AttributeType type) {
		super(type);
	}

	/**
	 * Set ident
	 * <P>
	 * The Ident field is one octet and aids in matching requests and replies.
	 * </P>
	 * 
	 * @param aIdent ident value
	 */
	public void setIdent(byte aIdent) {
		this.ident = aIdent;
	}

	/**
	 * Return ident
	 * 
	 * @return ident value
	 */
	public byte getIdent() {
		return this.ident;
	}

	/**
	 * Return string value
	 * 
	 * @return value of string field
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Set string value
	 * <P>
	 * Set value of string field
	 * </P>
	 * 
	 * @see org.dicr.radius.attribute.RadiusAttribute#setValue(java.lang.String)
	 */
	@Override
	protected void setValue(String aValue) throws UnsupportedOperationException {
		this.value = aValue;
	}

	/**
	 * Return string value
	 * 
	 * @see org.dicr.radius.attribute.RadiusAttribute#getValueAsString()
	 * @see #getValue()
	 */
	@Override
	protected String getValueAsString() {
		StringBuilder sb = new StringBuilder("{");
		sb.append("ident:").append(ByteUtils.unsigned(this.ident)).append(";");
		sb.append("value:").append(this.value);
		sb.append("}");
		return sb.toString();
	}

	/**
	 * @see org.dicr.radius.attribute.RadiusAttribute#decodeValue(byte[], java.lang.String,
	 *      org.dicr.radius.packet.RequestAuthenticator)
	 */
	@Override
	public void decodeValue(byte[] data, String secret, RequestAuthenticator requestAuthenticator) throws CodecException {
		if (data == null) throw new IllegalArgumentException("null data");
		if (data.length < 1) throw new CodecException("empty data");
		this.ident = data[0];
		try {
			this.value = (data.length > 1) ? new String(data, 1, data.length - 1, "UTF8") : null;
		} catch (UnsupportedEncodingException ex) {
			throw new Error(ex);
		}
	}

	/**
	 * @see org.dicr.radius.attribute.RadiusAttribute#encodeValue(java.lang.String,
	 *      org.dicr.radius.packet.RequestAuthenticator)
	 */
	@Override
	public byte[] encodeValue(String secret, RequestAuthenticator requestAuthenticator) throws CodecException {
		byte[] strbytes = null;
		if (this.value == null) strbytes = new byte[0];
		else try {
			strbytes = this.value.getBytes("UTF8");
		} catch (UnsupportedEncodingException ex) {
			throw new Error(ex);
		}
		byte[] data = new byte[strbytes.length + 1];
		data[0] = this.ident;
		System.arraycopy(strbytes, 0, data, 1, strbytes.length);
		return data;
	}

	/**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	    final int prime = 31;
	    int result = super.hashCode();
	    result = prime * result + this.ident;
	    result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
	    return result;
    }

	/**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (!super.equals(obj)) return false;
	    if (!(obj instanceof IdentStringAttribute)) return false;
	    IdentStringAttribute other = (IdentStringAttribute) obj;
	    if (this.ident != other.ident) return false;
	    if (this.value == null) {
		    if (other.value != null) return false;
	    } else if (!this.value.equals(other.value)) return false;
	    return true;
    }


}
