package org.dicr.radius.attribute;

import java.io.*;

import org.dicr.radius.dictionary.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;

/**
 * Radius Attribute.
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060615
 */
public abstract class RadiusAttribute implements Serializable {
	/** Serial ID */
    private static final long serialVersionUID = 1846636227155119839L;

	/** Dictionary descriptor */
	private AttributeDescriptor descriptor = null;

	/** Attribute type */
	private AttributeType type = null;

	/**
     * Constructor
     * 
     * @param aType attribute type
     */
	protected RadiusAttribute(AttributeType aType) {
		super();
		this.setType(aType);
	}

	/**
     * Constructor
     * 
     * @param aType attribute type
     * @param encodedValue encoded value
     * @param sharedSecret shared secret
     * @param authenticator request authenticator
     * @throws CodecException codec exception
     */
	protected RadiusAttribute(AttributeType aType, byte[] encodedValue, String sharedSecret, RequestAuthenticator authenticator) throws CodecException {
		super();
		this.setType(aType);
		this.decodeValue(encodedValue, sharedSecret, authenticator);
	}

	/**
     * Set attribute type
     * 
     * @param aType the attribute type
     */
	protected void setType(AttributeType aType) {
		if (aType == null) throw new IllegalArgumentException("null type");
		this.type = aType;
	}

	/**
     * Return attribute type
     * 
     * @return attribute type
     */
	public AttributeType getType() {
		return this.type;
	}

	/**
     * Return attribute descriptor
     * 
     * @return attribute descriptor
     */
	public AttributeDescriptor getDescriptor() {
		if (this.type != null && this.descriptor == null) this.descriptor = DictionaryFactory.getDictionary().getAttributeDescriptor(
				this.type);
		return this.descriptor;
	}

	/**
     * Set attribute value from string
     * 
     * @param value attribute value
     * @throws UnsupportedOperationException if unable to set value from string
     */
	protected abstract void setValue(String value) throws UnsupportedOperationException;

	/**
     * Return attribute value as string
     * <P>
     * It used in {@link #toString()}
     * </P>
     * 
     * @return attribute value as string
     */
	protected abstract String getValueAsString();

	/**
     * Decode attribute value
     * 
     * @param data value data
     * @param secret shared secret
     * @param requestAuthenticator authenticator
     * @throws CodecException TODO
     */
	public abstract void decodeValue(byte[] data, String secret, RequestAuthenticator requestAuthenticator) throws CodecException;

	/**
     * Encode attribute value
     * 
     * @param secret shared secret
     * @param requestAuthenticator authenticator
     * @return encoded value
     * @throws CodecException TODO
     */
	public abstract byte[] encodeValue(String secret, RequestAuthenticator requestAuthenticator) throws CodecException;

	/**
     * Convert to String
     * 
     * @return string representation
     */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.type == null) sb.append(this.getClass().getSimpleName());
		else {
			AttributeDescriptor desc = this.getDescriptor();
			sb.append(desc == null ? this.type : desc.getName());
		}
		sb.append("=").append(this.getValueAsString());
		return sb.toString();
	}

	/**
     * @see java.lang.Object#hashCode()
     */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.type == null) ? 0 : this.type.hashCode());
		return result;
	}

	/**
     * @see java.lang.Object#equals(java.lang.Object)
     */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final RadiusAttribute other = (RadiusAttribute) obj;
		if (this.type == null) {
			if (other.type != null) return false;
		} else if (!this.type.equals(other.type)) return false;
		return true;
	}
}
