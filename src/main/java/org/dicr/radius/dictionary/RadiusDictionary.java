/**
 * RadiusDictionary.java 01.11.2006
 */
package org.dicr.radius.dictionary;

import java.net.*;

/**
 * Radius Dictionary Interface
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061101
 */
public interface RadiusDictionary {

	/** Name of System Property which holds URL of dictionary resource */
	public static final String PROPERTY_URL = "org.dicr.radius.dictionary.url";

	/**
     * Initialize dictionary from specified URL
     * 
     * @param url url of dictionary resource
     */
	public void init(URL url);

	/**
     * Return vendors name by code
     * 
     * @param code vendor code
     * @return vendor name for this code or null if unknown
     */
	public String getVendorName(int code);

	/**
     * Return descriptor for attribute type
     * 
     * @param type attribute type
     * @return attribute descriptor or null if unknown
     */
	public AttributeDescriptor getAttributeDescriptor(AttributeType type);

}
