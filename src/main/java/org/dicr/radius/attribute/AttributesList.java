/**
 * AttributeList.java 30.10.2006
 */
package org.dicr.radius.attribute;

import java.io.*;
import java.util.*;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Attributes List. Class for manipulate attributes collections.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061030
 */
public class AttributesList implements Serializable {
	/** Serial ID */
	private static final long serialVersionUID = 1L;

	/** Vendor type of list */
	private int vendorCode = -1;

	/** List to store attributes */
	private transient final List<RadiusAttribute> list = new ArrayList<RadiusAttribute>();

	/**
	 * Constructor
	 * 
	 * @param attributeVendorCode vendor code of attributes for this list
	 */
	public AttributesList(final int attributeVendorCode) {
		super();
		this.vendorCode = attributeVendorCode;
	}

	/**
	 * Return vendor code
	 * 
	 * @return vendor code of attributes in list
	 */
	public int getVendorCode() {
		return this.vendorCode;
	}

	/**
	 * Add attribute to list
	 * 
	 * @param attr attribute to add
	 */
	public synchronized void add(final RadiusAttribute attr) {
		if (attr == null) throw new IllegalArgumentException("null attribute");
		if (this.vendorCode != attr.getType().getVendorCode()) {
			if (this.vendorCode == AttributeType.VENDOR_NONE) this.list.add(new VendorAttribute(attr));
			else throw new IllegalArgumentException("Can't add attribute with vendor code="
			        + attr.getType().getVendorCode() + " to attributes list with vendor code="
			        + this.list.get(0).getType().getVendorCode());
		}
		this.list.add(attr);
	}

	/**
	 * Add all attributes.
	 * 
	 * @param attributes
	 */
	public synchronized void addAll(final AttributesList attributes) {
		if (attributes == null) throw new IllegalArgumentException("null list");
		if (attributes.vendorCode != this.vendorCode) throw new IllegalArgumentException("list vendor code="
		        + attributes.vendorCode + " not match this list vendor code=" + this.vendorCode);
		for (final RadiusAttribute attrib : attributes.toList())
			this.add(attrib);
	}

	/**
	 * Remove attributes with specified type
	 * 
	 * @param type attribute type to remove
	 */
	public synchronized void removeAll(final AttributeType type) {
		if (type == null) throw new IllegalArgumentException("null type");
		final Iterator<RadiusAttribute> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			final RadiusAttribute attr = iterator.next();
			if (attr.getType().equals(type)) iterator.remove();
			else if (attr instanceof VendorAttribute) ((VendorAttribute) attr).getAttributes().removeAll(type);
		}
	}

	/**
	 * Set attribute. Remove all attributes with equals type and add specified attribute.
	 * 
	 * @param attribute attribute to set
	 */
	public synchronized void set(final RadiusAttribute attribute) {
		if (attribute == null) throw new IllegalArgumentException("null attribute");
		this.removeAll(attribute.getType());
		this.add(attribute);
	}

	/**
	 * Return first attribute with given type.
	 * 
	 * @param <A> attribute type
	 * @param type attribute type
	 * @return first attribute with given type or null if not exists
	 */
	@SuppressWarnings("unchecked") public synchronized <A extends RadiusAttribute> A getFirst(final AttributeType type) {
		if (type == null) throw new IllegalArgumentException("null type");
		A result = null;
		final Iterator<RadiusAttribute> it = this.list.iterator();
		while (it.hasNext()) {
			final RadiusAttribute attr = it.next();
			if (attr.getType().equals(type)) {
				result = (A) attr;
				break;
			} else if (attr instanceof VendorAttribute) {
				result = (A) ((VendorAttribute) attr).getAttributes().getFirst(type);
				if (result != null) break;
			}
		}
		return result;
	}

	/**
	 * Check if list contain at least one attribute with specified type
	 * 
	 * @param type type to search
	 * @return true if list contains attribute with specified type, false otherway
	 */
	public synchronized boolean contains(final AttributeType type) {
		if (type == null) throw new IllegalArgumentException("null type");
		return this.getFirst(type) != null;
	}

	/**
	 * Return attributes with specified type
	 * 
	 * @param <A> attribute type
	 * @param type attribute type
	 * @return list, possible empty of attributes
	 */
	@SuppressWarnings("unchecked") public synchronized <A extends RadiusAttribute> List<A> findAll(final AttributeType type) {
		if (type == null) throw new IllegalArgumentException("null attribute type");
		final List<A> result = new ArrayList<A>();
		final Iterator<RadiusAttribute> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			final RadiusAttribute attr = iterator.next();
			if (attr.getType().equals(type)) result.add((A) attr);
			else if (attr instanceof VendorAttribute) {
				final List<A> vattrs = ((VendorAttribute) attr).getAttributes().findAll(type);
				result.addAll(vattrs);
			}
		}
		return result;
	}

	/**
	 * Delete all attributes from list.
	 */
	public synchronized void clear() {
		this.list.clear();
	}

	/**
	 * Return List of attributes
	 * 
	 * @return copy of attributes list
	 */
	public synchronized List<RadiusAttribute> toList() {
		return new ArrayList<RadiusAttribute>(this.list);
	}

	/**
	 * Return size
	 * 
	 * @return count of attributes in list.
	 */
	public synchronized int getSize() {
		return this.list.size();
	}

	/**
	 * Return string presentation
	 * 
	 * @return string representation
	 */
	@Override public String toString() {
		final StringBuffer sb = new StringBuffer(AttributesList.class.getSimpleName() + "{");
		final Iterator<RadiusAttribute> it = this.list.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) sb.append(", ");
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (this.list == null ? 0 : this.list.hashCode());
		result = PRIME * result + this.vendorCode;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;
		final AttributesList other = (AttributesList) obj;
		if (this.list == null) {
			if (other.list != null) return false;
		} else if (!this.list.equals(other.list)) return false;
		if (this.vendorCode != other.vendorCode) return false;
		return true;
	}
}
