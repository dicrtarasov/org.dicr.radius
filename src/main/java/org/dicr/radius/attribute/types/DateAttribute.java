package org.dicr.radius.attribute.types;

import java.text.*;
import java.util.*;

import org.dicr.radius.dictionary.*;

/**
 * General attribute with Date type
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060615
 */
public class DateAttribute extends IntegerAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 1951295608138508578L;

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 */
	public DateAttribute(AttributeType aType) {
		super(aType);
	}

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 * @param seconds date value in seconds
	 */
	public DateAttribute(AttributeType aType, int seconds) {
		super(aType, seconds);
	}

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 * @param date attribute value
	 */
	public DateAttribute(AttributeType aType, Date date) {
		super(aType);
		this.setValue(date);
	}

	/**
	 * Constructor
	 * 
	 * @param aType attribute type
	 * @param date attribute value as string
	 */
	public DateAttribute(AttributeType aType, String date) {
		super(aType, date);
	}

	/**
	 * Set value
	 * 
	 * @param date value
	 */
	protected void setValue(Date date) {
		if (date == null) throw new IllegalArgumentException("null date");
		this.setValue(date.getTime() / 1000);
	}

	/**
	 * Return value
	 * 
	 * @return value as Date
	 */
	public Date getValueAsDate() {
		return new Date(this.getValue() * 1000);
	}

	/**
	 * Set value as string Used (int){@link Long#parseLong(String)}
	 * 
	 * @param value string value of seconds or date formatted
	 */
	@Override
	protected void setValue(String value) {
		if (value == null) throw new IllegalArgumentException("null value");
		try {
			this.setValue(Long.parseLong(value));
		} catch (NumberFormatException ex) {
			try {
				this.setValue(DateFormat.getDateTimeInstance().parse(value));
			} catch (ParseException pex) {
				throw new IllegalArgumentException("incorrect date value: " + value, ex);
			}
		}
	}

	/**
	 * Return value
	 * 
	 * @return {@link Date#toString()}
	 */
	@Override
	public String getValueAsString() {
		return this.getValueAsDate().toString();
	}
}
