package org.dicr.radius.dictionary;

import java.io.*;
import java.util.*;

/**
 * @author Igor A Tarasov, java@dicr.org
 * @version 1.0
 */
public final class AttributeDescriptor implements Serializable {
	private static final long serialVersionUID = 3131651085111280115L;

	/** Attribute type */
	private AttributeType type = null;

	/** Attribute name */
	private String name = null;

	/** Attribute value type */
	private ValueType valueType = null;

	/** Attribute encoding method */
	private String encoding = null;

	/** Possible attribute values */
	private transient final Map<String, Long> values = new HashMap<String, Long>();

	/**
	 * Full constructor.
	 * 
	 * @param aType attribute type
	 * @param aName attribute name
	 * @param aValueType attribute value type
	 * @param aEncoding attribute vendor encoding
	 */
	public AttributeDescriptor(AttributeType aType, String aName, ValueType aValueType, String aEncoding) {
		super();
		this.setType(aType);
		this.setName(aName);
		this.setValueType(aValueType);
		this.setEncoding(aEncoding);
	}

	/**
	 * Return attribute code in vendor space.
	 * 
	 * @return int
	 */
	public final AttributeType getType() {
		return this.type;
	}

	/**
	 * Set attribute code.
	 * 
	 * @param aType attribute type
	 */
	public final void setType(AttributeType aType) {
		if (aType == null) throw new IllegalArgumentException("empty type");
		this.type = aType;
	}

	/**
	 * Return text name
	 * 
	 * @return String
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Set text name.
	 * 
	 * @param aName text name
	 */
	public final void setName(String aName) {
		if (aName == null || aName.length() < 1) throw new IllegalArgumentException("empty name");
		this.name = aName;
	}

	/**
	 * Return type of value.
	 * 
	 * @return AttributeValueType
	 */
	public final ValueType getValueType() {
		return this.valueType;
	}

	/**
	 * Set value type.
	 * 
	 * @param aValueType type of value
	 */
	public final void setValueType(ValueType aValueType) {
		if (aValueType == null) throw new IllegalArgumentException("null type");
		this.valueType = aValueType;
	}

	/**
	 * Encoding method of value.
	 * 
	 * @return some vendor-valued string
	 */
	public final String getEncoding() {
		return this.encoding;
	}

	/**
	 * Set encoding of value
	 * 
	 * @param aEncoding vendor encoding method name
	 */
	public final void setEncoding(String aEncoding) {
		this.encoding = aEncoding;
	}

	/**
	 * Add value description.
	 * 
	 * @param value value
	 * @param aName name of value
	 */
	public final void addValueDescriptor(long value, String aName) {
		if (aName == null || aName.length() < 1) throw new IllegalArgumentException("empty name");
		this.values.put(aName, Long.valueOf(value));
	}

	/**
	 * Return value name by value code.
	 * 
	 * @param value value code
	 * @return value name
	 */
	public final String getValueName(long value) {
		String _name = null;
		for (String n : this.values.keySet())
			if (this.values.get(n).longValue() == value) {
				_name = n;
				break;
			}
		return _name;
	}

	/**
	 * Return value code by name
	 * 
	 * @param aName value name
	 * @return value code
	 */
	public final long getValueCode(String aName) {
		if (aName == null || aName.length() < 1) throw new IllegalArgumentException("empty name");
		return this.values.containsKey(aName) ? this.values.get(aName).intValue() : -1;
	}

	/**
	 * Return values description.
	 * 
	 * @return map of value names to value codes.
	 */
	public final Map<String, Long> getValues() {
		return new HashMap<String, Long>(this.values);
	}

	/**
	 * Format to string representation.
	 * 
	 * @return string representation
	 */
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("<attribute vendor=\"%d\" code=\"%d\" name=\"%s\" valuetype=\"%s\" encoding=\"%s\"",
		        Integer.valueOf(this.type.getVendorCode()), Integer.valueOf(this.type.getTypeCode()), this.name,
		        this.type.toString(), this.encoding != null ? this.encoding : ""));
		if (this.values.size() < 1) sb.append("/>");
		else {
			sb.append(">");
			for (String valname : this.values.keySet())
				sb.append(String.format("<value code=\"%d\" name=\"%s\"/>", this.values.get(valname), valname));
			sb.append("</attribute>");
		}
		return sb.toString();
	}
}
