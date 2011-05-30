/**
 * UserName.java 14.06.2006 2:36:53 dicr
 */
package org.dicr.radius.attribute.impl;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * SysUser-Name attribute
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060614
 */
public final class UserName extends StringAttribute {
	/** Serial ID */
	private static final long serialVersionUID = 7413195493614689167L;

	/** Attribute type code */
	public static final int TYPE_CODE = 1;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, UserName.TYPE_CODE);

	/**
     * Constructor
     */
	public UserName() {
		super(UserName.TYPE);
	}

	/**
     * Constructor
     * 
     * @param name user name
     */
	public UserName(String name) {
		super(UserName.TYPE, name);
	}
}
