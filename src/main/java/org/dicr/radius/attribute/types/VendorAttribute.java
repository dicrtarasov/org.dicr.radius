package org.dicr.radius.attribute.types;

import java.nio.*;
import java.util.*;

import org.dicr.radius.attribute.*;
import org.dicr.radius.codec.impl.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;
import org.dicr.util.data.*;

/**
 * Standard Vendor Attribute
 * <P>
 * This is standart attribute with AttributeType.VendorID = 0 and attribute Code 26.
 * </P>
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060615
 */
public class VendorAttribute extends RadiusAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -3164340883518632400L;

	/** Attribute type code */
	public static final int TYPE_CODE = 26;

	/** Attribute Type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, TYPE_CODE);

	/** Vendor Type */
	private int vendorId = 0;

	/** Vendor attributes */
	private AttributesList attributes = null;

	/**
	 * Constructor
	 * <P>
	 * No Vendor Id and attributes list initialization. Used by decoders when creating new attribute. Vendor id must be
	 * initialized after early, before attributes used.
	 * </P>
	 */
	public VendorAttribute() {
		super(TYPE);
		this.vendorId = AttributeType.VENDOR_NONE;
		this.attributes = new AttributesList(AttributeType.VENDOR_NONE);
	}

	/**
	 * Constructor
	 * 
	 * @param theVendorId vendor code
	 */
	public VendorAttribute(int theVendorId) {
		super(TYPE);
		this.setVendorId(theVendorId);
	}

	/**
	 * Constructor
	 * 
	 * @param attrib attribute to add initially in attributes list of this vendor attribute
	 */
	public VendorAttribute(RadiusAttribute attrib) {
		super(TYPE);
		if (attrib == null) throw new IllegalArgumentException("null attrib");
		this.setVendorId(attrib.getType().getVendorCode());
		this.attributes.add(attrib);
	}

	/**
	 * Constructor
	 * 
	 * @param attribs vendor attributes
	 */
	public VendorAttribute(AttributesList attribs) {
		super(TYPE);
		if (attribs == null) throw new IllegalArgumentException("null attribs");
		this.setVendorId(attribs.getVendorCode());
		this.attributes.addAll(attribs);
	}

	/**
	 * Set vendor Id.
	 * <P>
	 * Clear and initialize attribute list. This method must be called before first call of {@link #getAttributes()}
	 * </P>
	 * 
	 * @param theVendorId vendor id code
	 */
	protected void setVendorId(int theVendorId) {
		if (theVendorId < 0) throw new IllegalArgumentException("vendorId: " + theVendorId);
		if (theVendorId == AttributeType.VENDOR_NONE) throw new IllegalArgumentException(
		        "vendor attribute can't contain standard, non-vendor attributes");
		this.vendorId = theVendorId;
		this.attributes = new AttributesList(theVendorId);
	}

	/**
	 * Return vendor ID
	 * 
	 * @return vendor ID of this attribute
	 */
	public int getVendorId() {
		return this.vendorId;
	}

	/**
	 * Set value. Unsupported.
	 * 
	 * @param aValue something
	 */
	@Override
	protected void setValue(String aValue) {
		throw new UnsupportedOperationException("attribute value: " + aValue);
	}

	/**
	 * Return value as String Convert all attributes to string
	 * 
	 * @return all attributes as string
	 */
	@Override
	public String getValueAsString() {
		StringBuilder sb = new StringBuilder("{");
		sb.append("Vendor:").append(DictionaryFactory.getDictionary().getVendorName(this.vendorId));
		Iterator<RadiusAttribute> attributesIterator = this.attributes.toList().iterator();
		while (attributesIterator.hasNext()) {
			sb.append(", ");
			RadiusAttribute attr = attributesIterator.next();
			sb.append(attr.toString());
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Return attributes
	 * 
	 * @return attributes list
	 */
	public AttributesList getAttributes() {
		return this.attributes;
	}

	/**
	 * Decode value
	 * 
	 * @param data value data
	 * @param secret value secret
	 * @param requestAuthenticator authenticator
	 */
	@Override
	public void decodeValue(byte[] data, String secret, RequestAuthenticator requestAuthenticator) throws CodecException {
		if (data == null) throw new IllegalArgumentException("null data");
		ByteBuffer buf = ByteBuffer.wrap(data);
		int vendor = buf.getInt();
		if (vendor < 0 || vendor > AttributeType.VENDOR_MAX) throw new CodecException("incorrect vendor id: " + vendor);
		this.setVendorId(vendor);
		try {
			while (buf.remaining() > 0) {
				AttributeType type = new AttributeType(vendor, ByteUtils.unsigned(buf.get()));
				RadiusAttribute attribute = AttributesFactory.createAttribute(type);
				int length = ByteUtils.unsigned(buf.get());
				if (length < 0) throw new CodecException("incorrect attribute length: " + length);
				byte attrdata[] = new byte[length - RFCCodec.ATTRIBUTE_HEADER_LENGTH];
				buf.get(attrdata);
				attribute.decodeValue(attrdata, secret, requestAuthenticator);
				this.attributes.add(attribute);
			}
		} catch (BufferUnderflowException ex) {
			throw new CodecException("unexpected end of data");
		}
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
		byte[] data = null;
		try {
			// TODO: max attribute length
			ByteBuffer buf = ByteBuffer.allocate(10000);
			buf.putInt(this.getVendorId());
			for (RadiusAttribute attr : this.attributes.toList()) {
				byte[] attrdata = attr.encodeValue(secret, requestAuthenticator);
				if (attrdata == null) throw new IllegalStateException("not initialized");
				buf.put((byte) attr.getType().getTypeCode());
				buf.put((byte) (attrdata.length + RFCCodec.ATTRIBUTE_HEADER_LENGTH));
				buf.put(attrdata);
			}
			buf.flip();
			data = new byte[buf.limit()];
			buf.get(data);
		} catch (BufferOverflowException ex) {
			throw new CodecException("data overflow");
		}
		return data;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((this.attributes == null) ? 0 : this.attributes.hashCode());
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
		final VendorAttribute other = (VendorAttribute) obj;
		if (this.attributes == null) {
			if (other.attributes != null) return false;
		} else if (!this.attributes.equals(other.attributes)) return false;
		return true;
	}
}
