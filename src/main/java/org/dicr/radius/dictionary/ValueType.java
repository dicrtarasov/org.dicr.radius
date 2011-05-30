package org.dicr.radius.dictionary;

import java.util.*;

import org.dicr.util.net.*;

/**
 * Тип значения аттрибута.
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 1.0
 */
public enum ValueType {
	/**
	 * 1-253 octets containing binary data (values 0 through 255 decimal, inclusive). Strings of length zero (0) MUST
	 * NOT be sent; omit the entire attribute instead.
	 */
	STRING(String.class),
	/**
	 * 32 bit value, most significant octet first.
	 */
	ADDRESS(IP.class),
	/**
	 * 32 bit unsigned value, most significant octet first.
	 */
	INTEGER(Integer.class),
	/**
	 * 32 bit unsigned value, most significant octet first -- seconds since 00:00:00 UTC, January 1, 1970. The standard
	 * Attributes do not use this data type but it is presented here for possible use in future attributes.
	 */
	DATE(Date.class),
	/**
	 * Non RFC. Bytes data.
	 */
	OCTETS(byte[].class);

	/** Attribute value class */
	private Class<?> valueClass = null;

	/**
	 * Constructor
	 * 
	 * @param clazz value clas
	 */
	private ValueType(Class<?> clazz) {
		if (clazz == null) throw new IllegalArgumentException("null class");
		this.valueClass = clazz;
	}

	/**
	 * Возвращает Class значения аттрибута.
	 * 
	 * @return Class
	 */
	public final Class<?> getValueClass() {
		return this.valueClass;
	}

	/**
	 * Translate string to type.
	 * 
	 * @param type string value of type.
	 * @return attribute data type
	 */
	public static final ValueType fromString(String type) {
		if (type == null || type.length() < 1) throw new IllegalArgumentException("empty type");
		String lt = type.toLowerCase(Locale.getDefault());
		ValueType res = null;
		if (lt.equals("string") || lt.equals("text")) res = STRING;
		else if (lt.equals("integer")) res = INTEGER;
		else if (lt.equals("ipaddr") || lt.equals("address")) res = ADDRESS;
		else if (lt.equals("time") || lt.equals("date")) res = DATE;
		else res = OCTETS;
		return res;
	}
}
