package org.dicr.radius.attribute.types;

import java.util.*;

import org.dicr.radius.attribute.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;
import org.dicr.util.data.*;

/**
 * General attribute with Octects type
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060615
 */
public class OctetsAttribute extends RadiusAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -1179629247577402407L;

	/** Value */
	private byte[] value = new byte[0];

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 */
	public OctetsAttribute(AttributeType aType) {
		super(aType);
	}

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 * @param aValue attribute value
	 */
	public OctetsAttribute(AttributeType aType, byte[] aValue) {
		super(aType);
		this.setValue(aValue);
	}

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 * @param aValue attribute value
	 * @see ByteUtils#fromHexString(String)
	 */
	public OctetsAttribute(AttributeType aType, String aValue) {
		super(aType);
		this.setValue(aValue);
	}

	/**
	 * Set attribute value
	 * 
	 * @param aValue attribute value
	 */
	protected void setValue(byte[] aValue) {
		if (aValue == null) throw new IllegalArgumentException("null value");
		this.value = new byte[aValue.length];
		System.arraycopy(aValue, 0, this.value, 0, aValue.length);
	}

	/**
	 * Return value
	 * 
	 * @return attribute value
	 */
	public byte[] getValue() {
		return this.value;
	}

	/**
	 * Set attribute value
	 * 
	 * @param aValue attribute value
	 * @see ByteUtils#fromHexString(String)
	 */
	@Override
	protected void setValue(String aValue) {
		if (aValue == null) throw new IllegalArgumentException("null aValue");
		this.setValue(ByteUtils.fromHexString(aValue));
	}

	/**
	 * Return value as String
	 * 
	 * @return value as string
	 * @see ByteUtils#toHexString(byte[], int, int)
	 */
	@Override
	public String getValueAsString() {
		return ByteUtils.toHexString(this.value, 0, this.value.length);
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
		this.setValue(data);
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
		return this.value;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + Arrays.hashCode(this.value);
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
		final OctetsAttribute other = (OctetsAttribute) obj;
		if (!Arrays.equals(this.value, other.value)) return false;
		return true;
	}
}
