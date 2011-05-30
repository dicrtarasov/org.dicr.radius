package org.dicr.radius.attribute.types;

import org.dicr.radius.attribute.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;
import org.dicr.util.data.*;

/**
 * General attribute with String type
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060614
 */
public class StringAttribute extends RadiusAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -4537970965290075714L;

	/** Value */
	private String value = "";

	/**
	 * Constructor
	 * 
	 * @param attributeType attribute type
	 */
	public StringAttribute(AttributeType attributeType) {
		super(attributeType);
	}

	/**
	 * Constructor
	 * 
	 * @param attributeType attribute type
	 * @param aValue attribute value
	 */
	public StringAttribute(AttributeType attributeType, String aValue) {
		this(attributeType);
		this.setValue(aValue);
	}

	/**
	 * Set attribute value
	 * 
	 * @param aValue value of attribute
	 */
	@Override
	protected void setValue(String aValue) {
		if (aValue == null) throw new IllegalArgumentException("null value");
		this.value = aValue;
	}

	/**
	 * Return attribute value
	 * 
	 * @return attribute value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Return attribute value as String
	 * 
	 * @return {@link #getValue()}
	 */
	@Override
	protected String getValueAsString() {
		return this.getValue();
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
		this.setValue(ByteUtils.toText(data, 0, data.length));
	}

	/**
	 * Encode value
	 * 
	 * @param secret unused
	 * @param requestAuthenticator unused
	 */
	@Override
	public byte[] encodeValue(String secret, RequestAuthenticator requestAuthenticator) throws CodecException {
		return ByteUtils.toBytes(this.value);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		final StringAttribute other = (StringAttribute) obj;
		if (this.value == null) {
			if (other.value != null) return false;
		} else if (!this.value.equals(other.value)) return false;
		return true;
	}
}
