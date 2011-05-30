/**
 * RequestAuthenticator.java 13.11.2006
 */
package org.dicr.radius.packet;

import org.dicr.radius.util.*;

/**
 * RequestAuthenticator
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061112
 */
public class RequestAuthenticator {
	/** Length of value in bytes */
	public static final int LENGTH = 16;

	/** Value */
	private byte[] value = MD5.randomDigestData();

	/**
     * Constructor
     * <P>
     * Create authenticator with a random digest value
     * </P>
     */
	public RequestAuthenticator() {
		super();
	}

	/**
     * Constructor
     * 
     * @param data byte[{@link #LENGTH}] value data
     */
	public RequestAuthenticator(byte[] data) {
		super();
		this.setValue(data);
	}

	/**
     * Set value
     * 
     * @param avalue byte[{@link #LENGTH}] value data
     */
	private void setValue(byte[] avalue) {
		if (avalue == null) throw new IllegalArgumentException("null value");
		if (avalue.length != LENGTH) throw new IllegalArgumentException("incorrect value length: " + avalue.length);
		this.value = new byte[LENGTH];
		System.arraycopy(avalue, 0, this.value, 0, this.value.length);
	}

	/**
     * Return value data
     * 
     * @return byte[{@link #LENGTH}] value data
     */
	public byte[] getValue() {
		return this.value;
	}
}
