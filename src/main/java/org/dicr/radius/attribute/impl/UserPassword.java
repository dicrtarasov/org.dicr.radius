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
 * User-Password attribute.
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060614
 */
public final class UserPassword extends StringAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -6162874784624501243L;

	/** Attribute type code */
	public static final int TYPE_CODE = 2;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(AttributeType.VENDOR_NONE, UserPassword.TYPE_CODE);

	/** Maximum value length */
	public static final int DATA_MAX_LENGTH = 128;

	/**
	 * Constructor
	 */
	public UserPassword() {
		super(UserPassword.TYPE);
	}

	/**
	 * Constructor
	 * 
	 * @param password clear-text password
	 */
	public UserPassword(String password) {
		super(UserPassword.TYPE, password);
	}

	/**
	 * Decode value
	 * 
	 * @param data value data
	 * @param secret shared secret
	 * @param requestAuthenticator authenticator
	 */
	@Override
	public final void decodeValue(byte[] data, String secret, RequestAuthenticator requestAuthenticator) throws CodecException {
		if (data == null) throw new IllegalArgumentException("null data");
		if (data.length % 16 != 0) throw new CodecException("incorrect password data length: " + data.length);
		if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("empty secret");
		if (requestAuthenticator == null) throw new IllegalArgumentException("null authenticator");
		try {
			// готовим шифратор
			MessageDigest md5 = MD5.getMD5Digest();
			// decode password
			byte[] secretBytes = ByteUtils.toBytes(secret);
			int partsCount = data.length / 16;
			for (int part = 0; part < partsCount; part++) {
				md5.reset();
				md5.update(secretBytes);
				if (part == 0) md5.update(requestAuthenticator.getValue());
				else md5.update(data, (part - 1) * 16, 16);
				byte[] bn = md5.digest();
				for (int j = 0; j < 16; j++)
					data[part * 16 + j] ^= bn[j];
			}
			// delete trailing zeros
			int length = 0;
			while (length < data.length && data[length] != 0)
				length++;
			// set value
			this.setValue(ByteUtils.toText(data, 0, length));
		} catch (Throwable th) {
			throw new CodecException("error decoding UserPassword value", th);
		}
	}

	/**
	 * Encode value
	 * 
	 * @param secret shared secret
	 * @param requestAuthenticator authenticator
	 */
	@Override
	public final byte[] encodeValue(String secret, RequestAuthenticator requestAuthenticator) throws CodecException {
		if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("empty secret");
		if (requestAuthenticator == null) throw new IllegalArgumentException("null authenticator");
		if (this.getValue().isEmpty()) return new byte[0];

		// convert strings to bytes
		byte[] passwordBytes = ByteUtils.toBytes(this.getValue());
		byte[] secretBytes = ByteUtils.toBytes(secret);
		// count number of 16-bytes parts
		int partsCount = passwordBytes.length / 16;
		if (passwordBytes.length % 16 != 0) partsCount++;
		// copy password bytes
		byte[] data = Arrays.copyOf(passwordBytes, partsCount * 16);
		// rest of data filled by zero
		MessageDigest md5 = MD5.getMD5Digest();
		// encode each part
		for (int part = 0; part < partsCount; part++) {
			md5.reset();
			md5.update(secretBytes);
			if (part == 0) md5.update(requestAuthenticator.getValue());
			else md5.update(data, (part - 1) * 16, 16);
			byte[] bn = md5.digest();
			for (int j = 0; j < 16; j++)
				data[part * 16 + j] ^= bn[j];
		}
		return data;
	}
}
