package org.dicr.radius.dictionary;

import java.io.*;

/**
 * Полный код аттрибута.
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 061101
 */
public final class AttributeType implements Serializable, Cloneable, Comparable<AttributeType> {
	private static final long serialVersionUID = 6050960146532066063L;

	/** Maximal value for vendor */
	public static final int VENDOR_MAX = 0x0FFFFFF;

	/** Vendor code for standard attribute */
	public static final int VENDOR_NONE = 0;

	/** Vendor code */
	private int vendorCode = 0;

	/** Value code */
	private int typeCode = 0;

	/**
     * Constructor
     * 
     * @param vendor_code vendor code
     * @param type_code type code
     */
	public AttributeType(int vendor_code, int type_code) {
		super();
		this.setVendorCode(vendor_code);
		this.setTypeCode(type_code);
	}

	/**
     * Set vendor code
     * 
     * @param code vendor code
     */
	public final void setVendorCode(int code) {
		if (code < 0 || code > AttributeType.VENDOR_MAX) throw new IllegalArgumentException("vendorCode: " + code);
		this.vendorCode = code;
	}

	/**
     * Return vendor code
     * 
     * @return vendor code
     */
	public final int getVendorCode() {
		return this.vendorCode;
	}

	/**
     * Set type code
     * 
     * @param code type code
     */
	public final void setTypeCode(int code) {
		if (code < 0 || code > 255) throw new IllegalArgumentException("typeCode: " + code);
		this.typeCode = code;
	}

	/**
     * Return type code
     * 
     * @return type code
     */
	public final int getTypeCode() {
		return this.typeCode;
	}

	/**
     * Clone
     */
	@Override
	public final AttributeType clone() {
		return new AttributeType(this.vendorCode, this.typeCode);
	}

	/**
     * Compare with another attribute
     * 
     * @param type another attribute type to compare with
     * @return &lt1 if less; 0 if equals; &gt;1 if greater
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
	public final int compareTo(AttributeType type) {
		if (type == null) throw new NullPointerException("can't compare to null object");
		if (type == this) return 0;
		if (this.vendorCode < type.vendorCode) return -1;
		else if (this.vendorCode == type.vendorCode) {
			if (this.typeCode < type.typeCode) return -1;
			else if (this.typeCode == type.typeCode) return 0;
			return 1;
		}
		return 1;
	}

	/**
     * @see java.lang.Object#hashCode()
     */
	@Override
	public final int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + this.typeCode;
		result = PRIME * result + this.vendorCode;
		return result;
	}

	/**
     * @see java.lang.Object#equals(java.lang.Object)
     */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;
		final AttributeType other = (AttributeType) obj;
		if (this.typeCode != other.typeCode) return false;
		if (this.vendorCode != other.vendorCode) return false;
		return true;
	}

	/**
     * Convert to string
     * 
     * @see java.lang.Object#toString()
     */
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder("AttributeType[");
		if (this.vendorCode != 0) sb.append(this.vendorCode).append(",");
		sb.append(this.typeCode).append("]");
		return sb.toString();
	}
}
