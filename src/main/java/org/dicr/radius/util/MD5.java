/**
 * MD5.java 14.06.2006
 */
package org.dicr.radius.util;

import java.security.*;

/**
 * Radius Utils.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061104
 */
public final class MD5 {
	/** Random number generator */
	public static final SecureRandom random = new SecureRandom();

	/**
     * Return MD5Digest
     * 
     * @return MD5 MessageDigest
     * @throws Error if MD5 Algorythm not available
     */
	public final static MessageDigest getMD5Digest() throws Error {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			throw new Error("md5 digest algorythm not available", ex);
		}
		return digest;
	}

	/**
     * Generate random 16-byte digest data
     * 
     * @return byte[16] random MD5-digest
     */
	public final static byte[] randomDigestData() {
		// generate request authenticator
		MessageDigest md5 = MD5.getMD5Digest();
		md5.reset();
		byte[] randomBytes = new byte[24];
		MD5.random.nextBytes(randomBytes);
		md5.update(randomBytes);
		return md5.digest();
	}
}
