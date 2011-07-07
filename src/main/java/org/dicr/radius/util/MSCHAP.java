/**
 * MSCHAP.java 25.05.2007
 */
package org.dicr.radius.util;

import gnu.crypto.cipher.*;
import gnu.crypto.hash.*;

import java.io.*;
import java.security.*;
import java.util.*;

/**
 * MS-CHAP-V2 utilities
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070525
 */
public class MSCHAP {
	/** Random generator */
	private static final Random random = new Random();

	/** "Magic" constants used in authenticator response generation */
	private static final byte[] magic1 = new byte[] { 0x4D, 0x61, 0x67, 0x69, 0x63, 0x20, 0x73, 0x65, 0x72, 0x76, 0x65,
			0x72, 0x20, 0x74, 0x6F, 0x20, 0x63, 0x6C, 0x69, 0x65, 0x6E, 0x74, 0x20, 0x73, 0x69, 0x67, 0x6E, 0x69, 0x6E,
			0x67, 0x20, 0x63, 0x6F, 0x6E, 0x73, 0x74, 0x61, 0x6E, 0x74 };

	/** "Magic" constants used in authenticator response generation */
	private static final byte[] magic2 = new byte[] { 0x50, 0x61, 0x64, 0x20, 0x74, 0x6F, 0x20, 0x6D, 0x61, 0x6B, 0x65,
			0x20, 0x69, 0x74, 0x20, 0x64, 0x6F, 0x20, 0x6D, 0x6F, 0x72, 0x65, 0x20, 0x74, 0x68, 0x61, 0x6E, 0x20, 0x6F,
			0x6E, 0x65, 0x20, 0x69, 0x74, 0x65, 0x72, 0x61, 0x74, 0x69, 0x6F, 0x6E };

	/**
     * Generate random Peer-Challenge
     *
     * @return byte[16] random challenge
     */
	public static byte[] generatePeerChallenge() {
		byte[] challenge = new byte[16];
		random.nextBytes(challenge);
		return challenge;
	}

	/**
     * Special unicode
     * <P>
     * Convert string to UTF16 bytes, skip 2 bytes header and swap bytes order
     * </P>
     *
     * @param text text to convert
     * @return result bytes
     * @throws UnsupportedEncodingException UTF16 unsupported
     */
	private static byte[] specialUnicode(String text) {
		if (text == null) throw new IllegalArgumentException("null text");
		if (text.isEmpty()) return new byte[0];
		try {
			byte[] utf16 = text.getBytes("UTF16");
			byte[] result = new byte[utf16.length - 2];
			for (int i = 0; i < result.length / 2; i++) {
				result[i * 2] = utf16[i * 2 + 3];
				result[i * 2 + 1] = utf16[i * 2 + 2];
			}
			return result;
		} catch (UnsupportedEncodingException ex) {
			throw new Error(ex);
		}
	}

	/**
     * Calculate Challange Hash function
     *
     * @param peerChallenge byte[16] value of Peer-Challenge field from MS-CHAP2-Response attribute
     * @param authenticatorChallenge byte[16] value of MS-CHAP-Challenge attribute
     * @param userName [0..256] value of User-Name attribute
     * @return byte[8] challenge hash
     */
	private static byte[] challengeHash(byte[] peerChallenge, byte[] authenticatorChallenge, String userName) {
		if (userName == null) throw new IllegalArgumentException("null userName");
		if (peerChallenge == null || peerChallenge.length != 16) throw new IllegalArgumentException("peerChallenge");
		if (authenticatorChallenge == null || authenticatorChallenge.length != 16) throw new IllegalArgumentException(
				"authenticatorChallenge");
		byte[] ret = new byte[8];
		try {
			IMessageDigest md = HashFactory.getInstance("SHA-1");
			md.update(peerChallenge, 0, peerChallenge.length);
			md.update(authenticatorChallenge, 0, authenticatorChallenge.length);
			byte[] bytes = userName.getBytes("ASCII");
			md.update(bytes, 0, bytes.length);
			System.arraycopy(md.digest(), 0, ret, 0, ret.length);
		} catch (UnsupportedEncodingException ex) {
			throw new Error(ex);
		}
		return ret;
	}

	/**
     * Calculate NT Password Hash
     *
     * @param password clear text unicode user password (0..256 chars)
     * @return byte[16] password hash
     * @throws NoSuchAlgorithmException MD4 is not supported
     */
	private static byte[] ntPasswordHash(String password) {
		if (password == null) throw new IllegalArgumentException("null password");
		byte[] bytes = specialUnicode(password);
		IMessageDigest md = HashFactory.getInstance("MD4");
		md.update(bytes, 0, bytes.length);
		return md.digest();
	}

	/**
     * Calculate NT Password Hash Hash
     *
     * @param passwordHash byte[16] NT Password Hash
     * @return byte[16] NT Password Hash Hash
     */
	private static byte[] hashNtPasswordHash(byte[] passwordHash) {
		if (passwordHash == null || passwordHash.length != 16) throw new IllegalArgumentException("passwordhash");
		IMessageDigest md = HashFactory.getInstance("MD4");
		md.update(passwordHash, 0, passwordHash.length);
		return md.digest();
	}

	/**
     * Encrypt using DES algorythm
     *
     * @param clear byte[8] data to encrypt
     * @param key byte[7] encryption key
     * @return byte[8] encrypted value
     */
	private static byte[] desEncrypt(byte[] clear, byte[] key) {
		if (clear == null || clear.length != 8) throw new IllegalArgumentException("clear");
		if (key == null || key.length != 7) throw new IllegalArgumentException("key");
		byte[] ret = new byte[8];
		try {
			byte[] parited = new byte[8];
			int tmp = 0;
			int rest = 0;
			for (int i = 0; i < 7; i++) {
				tmp = 0x0FF & key[i];
				parited[i] = (byte) (((tmp >> i) | rest | 1) & 0x0FF);
				rest = ((tmp << (7 - i)));
			}
			parited[7] = (byte) (rest | 1);
			IBlockCipher cipher = CipherFactory.getInstance("DES");
			Map<Object, Object> attributes = new HashMap<Object, Object>();
			attributes.put(IBlockCipher.CIPHER_BLOCK_SIZE, Integer.valueOf(8));
			attributes.put(IBlockCipher.KEY_MATERIAL, parited);
			cipher.init(attributes);
			cipher.encryptBlock(clear, 0, ret, 0);
		} catch (WeakKeyException ex) {
			ex.printStackTrace();
		} catch (InvalidKeyException ex) {
			throw new Error(ex);
		}
		return ret;
	}

	/**
     * Calculate Challenge Response
     *
     * @param challengeHash byte[8] challange hash
     * @param passwordHash byte[16] password hash
     * @return byte[24] challenge response
     */
	private static byte[] challengeResponse(byte[] challengeHash, byte[] passwordHash) {
		if (challengeHash == null || challengeHash.length != 8) throw new IllegalArgumentException("challengehash");
		if (passwordHash == null || passwordHash.length != 16) throw new IllegalArgumentException("passwordHash");

		// padding password to 21 bytes
		byte[] zpassword = new byte[21];
		Arrays.fill(zpassword, (byte) 0);
		System.arraycopy(passwordHash, 0, zpassword, 0, passwordHash.length);

		// temporary vars
		byte[] ret = new byte[24];
		byte[] keypart = new byte[7];

		// calculate cypher part by part
		for (int i = 0; i < 3; i++) {
			System.arraycopy(zpassword, i * 7, keypart, 0, keypart.length);
			byte[] part = desEncrypt(challengeHash, keypart);
			System.arraycopy(part, 0, ret, i * 8, part.length);
		}

		return ret;
	}

	/**
     * Calculate NT-Response of MS-CHAP-v1 protocol
     *
     * @param authenticatorChallenge byte[8] value of MS-CHAP-Challange
     * @param password [0..256] clear text user password
     * @return byte[24] value for NTResponse field of MS-CHAP-Respponse
     */
	public static byte[] ntResponseV1(byte[] authenticatorChallenge, String password) {
		if (authenticatorChallenge == null || authenticatorChallenge.length < 8) throw new IllegalArgumentException(
				"authenticatorChallenge");
		if (password == null) throw new IllegalArgumentException("null password");
		byte[] trimmed8 = new byte[8];
		System.arraycopy(authenticatorChallenge, 0, trimmed8, 0, trimmed8.length);
		return challengeResponse(trimmed8, ntPasswordHash(password));
	}

	/**
     * Calculate NT-Response of MS-CHAP-v2 protocol
     *
     * @param authenticatorChallenge byte[16] value of MS-CHAP-Challenge attribute
     * @param peerChallenge random byte[16] value of Peer-Challenge field of MS-CHAP2-Response attribute
     * @param userName value of User-Name attribute
     * @param password unicode clear text user password (0..256 chars)
     * @return byte[24] value for NT-Response field of MS-CHAP2-Response attribute
     */
	public static byte[] ntResponseV2(byte[] authenticatorChallenge, byte[] peerChallenge, String userName, String password) {
		if (authenticatorChallenge == null || authenticatorChallenge.length != 16) throw new IllegalArgumentException(
				"authenticatorChallenge");
		if (peerChallenge == null || peerChallenge.length != 16) throw new IllegalArgumentException("peerChallenge");
		if (userName == null) throw new IllegalArgumentException("null userName");
		if (password == null) throw new IllegalArgumentException("null password");

		byte[] challengeHash = challengeHash(peerChallenge, authenticatorChallenge, userName);
		byte[] passwordHash = ntPasswordHash(password);
		return challengeResponse(challengeHash, passwordHash);
	}

	/**
     * Calculate authenticator response
     *
     * @param password clear text user password
     * @param response byte[24] response field from MS-CHAP2-Response
     * @param peerChallenge byte[16] peer-challenge field from MS-CHAP2-Response
     * @param authenticatorChallenge byte[16] value of MS-CHAP-Challenge
     * @param userName user name
     * @return byte[20] value of authenticator response ("S=") bytes
     */
	public static byte[] authenticatorResponse(String password, byte[] response, byte[] peerChallenge, byte[] authenticatorChallenge, String userName) {
		if (password == null) throw new IllegalArgumentException("null password");
		if (response == null || response.length != 24) throw new IllegalArgumentException("response");
		if (peerChallenge == null || peerChallenge.length != 16) throw new IllegalArgumentException("peerChallenge");
		if (authenticatorChallenge == null || authenticatorChallenge.length != 16) throw new IllegalArgumentException(
				"authenticatorChallenge");
		if (userName == null) throw new IllegalArgumentException("null userName");
		byte[] passwordHashHash = hashNtPasswordHash(ntPasswordHash(password));
		IMessageDigest md = HashFactory.getInstance("SHA-1");
		md.update(passwordHashHash, 0, passwordHashHash.length);
		md.update(response, 0, response.length);
		md.update(magic1, 0, magic1.length);
		byte[] digest = md.digest();
		byte[] challengeHash = challengeHash(peerChallenge, authenticatorChallenge, userName);
		md.reset();
		md.update(digest, 0, digest.length);
		md.update(challengeHash, 0, challengeHash.length);
		md.update(magic2, 0, magic2.length);
		return md.digest();
	}

}
