package org.dicr.radius.attribute.types;

import org.dicr.radius.attribute.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;
import org.dicr.util.data.*;

/**
 * General unsigned integer (32bit) attribute type.<BR>
 * Unsigned integer (32bit = 4byte) value (from <CODE>0</CODE> to <CODE>2^32 = 0x0FFFFFFFFL</CODE>) stored in
 * <CODE>long</CODE> java type.
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060615
 */
public class IntegerAttribute extends RadiusAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 7960261381209948515L;

	/** Length of value in bytes */
	public static final int VALUE_LENGTH = 4;

	/** Attribute value (unsigned integer stored as long) */
	private long value = 0;

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 */
	public IntegerAttribute(AttributeType aType) {
		super(aType);
	}

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 * @param aValue attribute value
	 */
	public IntegerAttribute(AttributeType aType, long aValue) {
		this(aType);
		this.setValue(aValue);
	}

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 * @param aValue attribute value as string
	 */
	public IntegerAttribute(AttributeType aType, String aValue) {
		this(aType);
		this.setValue(aValue);
	}

	/**
	 * Set attribute value
	 * 
	 * @param aValue unsigned integer value from 0 to 0x0FFFFFFFFL
	 */
	protected void setValue(long aValue) {
		this.value = aValue & 0x0FFFFFFFFL;
	}

	/**
	 * Set value as string
	 * 
	 * @param aValue value as string
	 */
	@Override
	protected void setValue(String aValue) {
		if (aValue == null || aValue.isEmpty()) this.setValue(0);
		else try {
			this.setValue(Long.parseLong(aValue));
		} catch (NumberFormatException ex) {
			AttributeDescriptor desc = this.getDescriptor();
			long valCode = desc.getValueCode(aValue);
			if (valCode == -1) throw new IllegalArgumentException("unknown value '" + aValue + "' of attribute '"
			        + this.getType() + "'", ex);
			this.setValue(valCode);
		}
	}

	/**
	 * Return value
	 * 
	 * @return attribute value (unsigned 32bit)
	 */
	public long getValue() {
		return this.value & 0x0FFFFFFFFL;
	}

	/**
	 * Return value as string
	 * 
	 * @return attribute value as unsigned decimal number string
	 */
	@Override
	public String getValueAsString() {
		StringBuilder sb = new StringBuilder();
		AttributeDescriptor desc = this.getDescriptor();
		if (desc == null) sb.append(this.getValue());
		else {
			String valName = desc.getValueName(this.getValue());
			if (valName != null) sb.append(valName);
			else sb.append(this.getValue());
		}
		return sb.toString();
	}

	/**
	 * Decode value
	 * 
	 * @param data value data
	 * @param secret shared secret
	 * @param requestAuthenticator authenticator
	 */
	@Override
	public void decodeValue(byte[] data, String secret, RequestAuthenticator requestAuthenticator) throws CodecException {
		if (data == null) throw new IllegalArgumentException("null data");
		if (data.length != IntegerAttribute.VALUE_LENGTH) throw new CodecException("incorrect data.length: "
		        + data.length);
		this.setValue(ByteUtils.unsigned(ByteUtils.toInteger(data, 0, IntegerAttribute.VALUE_LENGTH)));
	}

	/**
	 * Encode value
	 * 
	 * @param secret shared secret
	 * @param requestAuthenticator authenticator
	 * @return value data
	 */
	@Override
	public byte[] encodeValue(String secret, RequestAuthenticator requestAuthenticator) throws CodecException {
		return ByteUtils.toBytes((int) this.getValue());
	}

	/**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	    final int prime = 31;
	    int result = super.hashCode();
	    result = prime * result + (int) (this.value ^ (this.value >>> 32));
	    return result;
    }

	/**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (!super.equals(obj)) return false;
	    if (!(obj instanceof IntegerAttribute)) return false;
	    IntegerAttribute other = (IntegerAttribute) obj;
	    if (this.value != other.value) return false;
	    return true;
    }

}
