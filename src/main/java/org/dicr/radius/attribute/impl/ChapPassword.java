package org.dicr.radius.attribute.impl;

import java.security.*;
import java.util.*;

import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;
import org.dicr.radius.util.*;
import org.dicr.util.data.*;

/**
 * Chap-Password attribute
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060615
 */
public final class ChapPassword extends OctetsAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -7774153289900067114L;

	/** Attribute code */
	public static final int TYPE_CODE = 3;

	/** Attribute Value */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, ChapPassword.TYPE_CODE);

	/** Value data size */
	public static final int VALUE_LENGTH = 17;

	/**
	 * Constructor
	 */
	public ChapPassword() {
		super(ChapPassword.TYPE, new byte[17]);
	}

	/**
	 * Constructor
	 * 
	 * @param encodedValue byte[17] crypted value
	 */
	public ChapPassword(byte[] encodedValue) {
		super(ChapPassword.TYPE, encodedValue);
	}

	/**
	 * Constructor
	 * 
	 * @param hexString byte[17] encoded in string value
	 */
	public ChapPassword(String hexString) {
		super(ChapPassword.TYPE, hexString);
	}

	/**
	 * Constructor
	 * 
	 * @param password clear-text password
	 * @param ident random ident byte
	 * @param challenge challenge
	 */
	public ChapPassword(String password, byte ident, ChapChallenge challenge) {
		super(ChapPassword.TYPE);
		this.setValue(password, ident, challenge);
	}

	/**
	 * Constructor
	 * 
	 * @param password
	 * @param challenge
	 */
	public ChapPassword(String password, ChapChallenge challenge) {
		super(ChapPassword.TYPE);
		this.setValue(password, (byte) MD5.random.nextInt(255), challenge);
	}

	/**
	 * Set value
	 * 
	 * @param aValue byte[17] data, contains of CHAP-ID at byte[0] and crypted password at byte[1..16]
	 */
	@Override
	protected final void setValue(byte[] aValue) {
		if (aValue == null) throw new IllegalArgumentException("null value");
		if (aValue.length != ChapPassword.VALUE_LENGTH) throw new IllegalArgumentException("incorrect value length: "
		        + aValue.length);
		super.setValue(aValue);
	}

	/**
	 * Set value by encoding password It MD5-encode password by random generated CHAP-ID and given challenge
	 * 
	 * @param password clear-text password
	 * @param ident ident byte
	 * @param challenge challenge to encode password
	 */
	protected final void setValue(String password, byte ident, ChapChallenge challenge) {
		if (password == null) throw new IllegalArgumentException("null password");
		if (challenge == null) throw new IllegalArgumentException("null challenge");

		// prepare message digest
		MessageDigest md5 = MD5.getMD5Digest();
		md5.reset();
		md5.update(ident);
		if (!password.isEmpty()) md5.update(ByteUtils.toBytes(password));
		md5.update(challenge.getValue());
		byte[] digest = md5.digest();
		byte[] value = new byte[VALUE_LENGTH];
		value[0] = ident;
		System.arraycopy(digest, 0, value, 1, digest.length);
		this.setValue(value);
	}

	/**
	 * Decode attribute from data
	 * 
	 * @param data byte[17] encrypted attribute data
	 * @param secret shared secret (not used)
	 * @param requestAuthenticator authenticator (not used)
	 */
	@Override
	public void decodeValue(byte[] data, String secret, RequestAuthenticator requestAuthenticator) throws CodecException {
		if (data == null) throw new IllegalArgumentException("null data");
		if (data.length != ChapPassword.VALUE_LENGTH) throw new CodecException("too long data length: " + data.length);
		this.setValue(data);
	}

	/**
	 * Return Chap ID
	 * 
	 * @return CHAP ID (getValue()[0]) or 0 if value not set
	 */
	public final byte getChapId() {
		return this.getValue()[0];
	}

	/**
	 * Return Chap Password
	 * 
	 * @return encrypted password (getValue()[1-16]) or null if value not set
	 */
	public final byte[] getChapPassword() {
		return Arrays.copyOfRange(this.getValue(), 1, ChapPassword.VALUE_LENGTH);
	}

	/**
	 * Check CHAP password
	 * 
	 * @param password clear text password
	 * @param challenge CHAP challenge attribute
	 * @return true if equals
	 */
	public boolean equals(String password, ChapChallenge challenge) {
		if (password == null) throw new IllegalArgumentException("null password");
		if (challenge == null) throw new IllegalArgumentException("null challenge");
		ChapPassword chapPassword = new ChapPassword(password, this.getChapId(), challenge);
		return this.equals(chapPassword);
	}
}
