/**
 * MSChapTest.java 29.05.2007
 */
package org.dicr.radius.test;

import java.util.*;

import org.dicr.radius.util.*;
import org.dicr.util.data.*;

/**
 * Test MS-Chap encryption
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070528
 */
public class MSChapTest {
	/** Etalon UserName */
	public static final String userName = "user";

	/** Etalon password */
	public static final String password = "accept";

	/**
	 * Test authenticator response
	 */
	public void testResponse() {
		String s_authenticatorChallenge = "29893076e65ecfe9040afab6f9624186";
		String s_peerChallenge = "d5d10c301207d25acc109cac9dc0ae0a";
		String s_ntResponse = "b14870297bc636fa3425f61a2686a5cffc9ccc285e5d913f";
		String s_authenticatorResponse = "17C3369176DCF23D6862E00600578AE2923354DF";
		byte[] authenticatorChallenge = ByteUtils.fromHexString(s_authenticatorChallenge);
		byte[] peerChallenge = ByteUtils.fromHexString(s_peerChallenge);
		byte[] ntResponse = ByteUtils.fromHexString(s_ntResponse);
		byte[] authenticatorResponse = ByteUtils.fromHexString(s_authenticatorResponse);

		System.out.print("Test MS-CHAP-V2 NTResponse... ");
		byte[] response = MSCHAP.ntResponseV2(authenticatorChallenge, peerChallenge, userName, password);
		boolean pass = Arrays.equals(response, ntResponse);
		System.out.println(pass ? "Pass" : "Fail");

		System.out.print("Test MS-CHAP-v2 Authenticator Response... ");
		response = MSCHAP.authenticatorResponse(password, ntResponse, peerChallenge, authenticatorChallenge, userName);
		pass = Arrays.equals(response, authenticatorResponse);
		System.out.println(pass ? "Pass" : "Fail");
	}

	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MSChapTest test = new MSChapTest();
		test.testResponse();
	}
}
