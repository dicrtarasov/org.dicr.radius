/**
 * MSChap2Success.java 25.05.2007
 */
package org.dicr.radius.attribute.ms;

import java.util.*;

import org.dicr.radius.attribute.impl.*;
import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.util.*;
import org.dicr.util.data.*;

/**
 * MS-CHAP2-Success Attribute
 * <P>
 * This Attribute contains a 42-octet authenticator response string. This string MUST be included in the Message field
 * of the MS-CHAP- V2 Success packet sent from the NAS to the peer. This Attribute is only used in Access-Accept
 * packets.
 * </P>
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070525
 */
public class MSChap2Success extends IdentStringAttribute {
	/** Serial ID */
	private static final long serialVersionUID = -2187738483836876668L;

	/** Type code */
	public static final int TYPE_CODE = 26;

	/** Attribute type */
	public static final AttributeType TYPE = new AttributeType(MicrosoftAttribute.VENDOR_CODE, TYPE_CODE);

	/** Value length */
	public static final int VALUE_LENGTH = 43;

	/**
     * Constructor
     */
	public MSChap2Success() {
		super(TYPE);
	}

	/**
     * Constructor
     *
     * @param ident value of ident field from response
     * @param authenticatorResponse byte[20] value of authenticator response
     */
	public MSChap2Success(byte ident, byte[] authenticatorResponse) {
		super(TYPE);
		this.setIdent(ident);
		this.setAuthenticatorResponse(authenticatorResponse);
	}

	/**
     * Constructor
     *
     * @param username User-Name request attribute
     * @param passwd clear text user password
     * @param challenge MS-Chap-Challenge attribute from request
     * @param response MSChap2-Response attribute from request
     */
	public MSChap2Success(UserName username, String passwd, MSChapChallenge challenge, MSChap2Response response) {
		super(TYPE);
		this.setValue(username, passwd, challenge, response);
	}

	/**
     * Generate authenticator response strign from authenticator response bytes.
     * <P>
     * Response string is in the form: S=XXXXX..XXX, where XX if 20 octets hexadecimal characters string
     * </P>
     *
     * @param authenticatorResponse byte[20] authenticator response value
     * @return authenticator response string
     */
	public static final String generateAuthenticatorResponseString(byte[] authenticatorResponse) {
		if (authenticatorResponse == null || authenticatorResponse.length != 20) throw new IllegalArgumentException(
				"authenticatorResponse");
		return "S=" + ByteUtils.toHexString(authenticatorResponse, 0, authenticatorResponse.length).toUpperCase(Locale.getDefault());
	}

	/**
     * Set authenticator response
     *
     * @param authenticatorResponse byte[20] value of authenticator response
     */
	public void setAuthenticatorResponse(byte[] authenticatorResponse) {
		if (authenticatorResponse == null || authenticatorResponse.length != 20) throw new IllegalArgumentException(
				"authenticatorResponse");
		this.setValue(generateAuthenticatorResponseString(authenticatorResponse));
	}

	/**
     * Calculate and set ident and authenticator response values from given fields.
     *
     * @param userName user name
     * @param password clear text user password
     * @param response MS-CHAP2-Response user response from NAS
     * @param challenge MS-Chap-Challenge
     */
	public void setValue(UserName userName, String password, MSChapChallenge challenge, MSChap2Response response) {
		if (userName == null) throw new IllegalArgumentException("null userName");
		if (password == null) throw new IllegalArgumentException("null password");
		if (response == null) throw new IllegalArgumentException("null response");
		if (challenge == null || challenge.getValue().length != 16) throw new IllegalArgumentException("challenge: "
				+ challenge);
		this.setAuthenticatorResponse(MSCHAP.authenticatorResponse(password, response.getResponse(),
				response.getPeerChallenge(), challenge.getValue(), userName.getValue()));
		this.setIdent(response.getIdent());
	}

	/**
     * Verify reply attribute values against request
     *
     * @param userName request attribute UserName
     * @param password clear text user password
     * @param challenge MS-CHAP2-Challenger request attribute
     * @param response MS-CHAP2-Response request attribute
     * @return true if attribute value match request attributes
     */
	public boolean verify(UserName userName, String password, MSChapChallenge challenge, MSChap2Response response) {
		if (userName == null) throw new IllegalArgumentException("null userName");
		if (password == null) throw new IllegalArgumentException("null password");
		if (response == null) throw new IllegalArgumentException("null response");
		if (challenge == null || challenge.getValue().length != 16) throw new IllegalArgumentException("challenge: "
				+ challenge);
		if (this.getIdent() != response.getIdent()) return false;
		byte[] authResponse = MSCHAP.authenticatorResponse(password, response.getResponse(),
				response.getPeerChallenge(), challenge.getValue(), userName.getValue());
		return generateAuthenticatorResponseString(authResponse).equals(this.getValue());
	}
}
