/**
 * FileAuthModule.java 18.06.2006 16:16:57 dicr
 */
package org.dicr.radius.handler.impl;

import java.io.*;

import org.apache.log4j.*;
import org.dicr.radius.attribute.*;
import org.dicr.radius.attribute.impl.*;
import org.dicr.radius.attribute.ms.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.handler.*;
import org.dicr.sys.linux.*;
import org.dicr.util.crypt.*;
import org.dicr.util.data.exc.*;
import org.dicr.util.net.*;

/**
 * File Authentication module. Use files to authenticte. Support PAP and CHAP authentication scheme. For PAP scheme
 * support authentication against user password file with clear-text and crypted passwords and also authentication
 * against system accounts database if password in password file is empty. For CHAP authentication scheme support only
 * authentication agains user password file with clear-text passwords. In case of success authentication, add
 * Framed-IP-Address attribute to response if pap(chap)-file contains IP-address for uthenticated user or allow
 * IP-address, supplied in request attributes.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060618
 * @see PapFile
 * @see ShadowUtils
 */
public final class FileAuthModule implements PAPAuthModule, CHAPAuthModule, MSChap2AuthModule, MSChapAuthModule {
	private static final Logger log = Logger.getLogger(StandardRequestHandler.class);

	/** pap-file to authentificate users */
	private PapFile papFile = null;

	/** Allow PAP scheme */
	private boolean papEnabled = true;

	/** Allow CHAP scheme */
	private boolean chapEnabled = true;

	/** Allow System authentication for null PAP-passwords */
	private boolean shadowPapEnabled = false;

	/**
	 * Constructor
	 */
	public FileAuthModule() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param pap PapFile to use
	 */
	public FileAuthModule(final PapFile pap) {
		super();
		this.setPapFile(pap);
	}

	/**
	 * Set PapFile
	 * 
	 * @param file pap file to use in authentication
	 */
	public final void setPapFile(final PapFile file) {
		if (file == null) throw new IllegalArgumentException("null pap file");
		this.papFile = file;
	}

	/**
	 * Return PapFile.
	 * 
	 * @return PapFile used in authentication or null if not set
	 */
	public final PapFile getPapFile() {
		return this.papFile;
	}

	/**
	 * Set allow using the PAP authentication scheme (default true).
	 * 
	 * @param enabled false to disallow PAP authentication
	 */
	public final void setPapEnabled(final boolean enabled) {
		this.papEnabled = enabled;
	}

	/**
	 * Check if PAP authentications is allowed
	 * 
	 * @return true if allowed
	 */
	public final boolean isPapEnabled() {
		return this.papEnabled;
	}

	/**
	 * Set allow using system account authentication in PAP scheme. Shadow PAP scheme is used if password in user file
	 * set to empty.
	 * 
	 * @param enabled false to disable authentication agains system account
	 */
	public final void setShadowPapEnabled(final boolean enabled) {
		this.shadowPapEnabled = enabled;
	}

	/**
	 * Check if system authentication in PAP scheme is enabled.
	 * 
	 * @return true if empty passwords in user file upon PAP scheme force system authentication.
	 */
	public final boolean isShadowPapEnabled() {
		return this.shadowPapEnabled;
	}

	/**
	 * Set allow to use CHAP authentication scheme (default true)
	 * 
	 * @param enabled false to disallow
	 */
	public final void setChapEnabled(final boolean enabled) {
		this.chapEnabled = enabled;
	}

	/**
	 * Checl if CHAP authentication method is allowed.
	 * 
	 * @return true if allowed
	 */
	public final boolean isChapEnabled() {
		return this.chapEnabled;
	}

	/**
	 * Detect response ip address from papEntry or from requestAttributes. If papEntry allow all address, them address
	 * is taken from requestattributes.
	 * 
	 * @param papEntry papentry of authenticated user
	 * @param requestAttribs request attributes
	 * @return IP-address for response or null if none
	 */
	private final static IP detectResponseIP(final PapEntry papEntry, final AttributesList requestAttribs) {
		if (papEntry == null) throw new IllegalArgumentException("null papEntry");
		if (requestAttribs == null) throw new IllegalArgumentException("null requestAttribs");
		IP ip = null;
		// check ip-address is specified
		final String allowedIPs = papEntry.getIps();
		if (allowedIPs != null) if (allowedIPs.equals(PapEntry.ANY)) {
			// allow given IP if any
			final FramedIPAddress addr = (FramedIPAddress) requestAttribs.getFirst(FramedIPAddress.TYPE);
			if (addr != null) ip = addr.getValueAsIP();
		} else {
			// forse ip address
			final String[] ips = allowedIPs.split("\\s+");
			try {
				ip = new IP(ips[0]);
			} catch (final IncorrectAddressException ex) {
				FileAuthModule.log.warn("error parsing ip address of pap entry: " + papEntry, ex);
			}
		}
		return ip;
	}

	/**
	 * Detect client's server ident from request attribtes.
	 * 
	 * @param requestAttributes request attributes
	 * @return server ident or null
	 */
	private final static String detectServerIdent(final AttributesList requestAttributes) {
		if (requestAttributes == null) throw new IllegalArgumentException("null requestAttributes");
		String server = null;
		final NASIdentifier nasId = (NASIdentifier) requestAttributes.getFirst(NASIdentifier.TYPE);
		if (nasId != null) server = nasId.getValue();
		else {
			final NASIPAddress nasIp = (NASIPAddress) requestAttributes.getFirst(NASIPAddress.TYPE);
			if (nasIp != null) server = nasIp.getValueAsString();
		}
		return server;
	}

	/**
	 * Authenticate using PAP scheme against user file and system accounts. If password in {@link PapFile} is empty,
	 * then password is checked against system accounts using {@link ShadowUtils}.
	 * 
	 * @param userName user name
	 * @param userPassword user password
	 * @param requestAttributes attributes from AccessRequest packet
	 * @return response attributes for AccessAccept packet (Framed-IP-Address attribute if specified)
	 * @throws AuthenticationException if authentication fails in any reason. Message will used in Reply-Message
	 *             attribute of Access-Reject response.
	 * @see PapFile
	 * @see ShadowUtils
	 */
	public final AttributesList authPap(final String userName, final String userPassword, final AttributesList requestAttributes) throws AuthenticationException {
		if (userName == null) throw new IllegalArgumentException("null userName");
		if (userPassword == null) throw new IllegalArgumentException("null userPassword");
		if (requestAttributes == null) throw new IllegalArgumentException("null requestAttributes");
		// check if allowed
		if (!this.papEnabled) throw new AuthenticationException("PAP not allowed");
		// check pap-file
		if (this.papFile == null) {
			FileAuthModule.log.error("papFile not initialized. skipping request");
			throw new IllegalStateException("Server error: no papFile is configured");
		}
		final AttributesList responseAttributes = new AttributesList(AttributeType.VENDOR_NONE);
		try {
			// crypt password
			String password = userPassword;
			if (this.papFile.isCryptedPasswords()) {
				FileAuthModule.log.trace("using crypted password in pap-file");
				password = UnixCrypt.crypt(password);
			}
			// get server id
			final String serverIdent = FileAuthModule.detectServerIdent(requestAttributes);
			// search PapEntry
			final PapEntry papEntry = this.papFile.search(userName, serverIdent);
			if (papEntry == null) throw new AuthenticationException("User not allowed");
			// if shadow used
			if (papEntry.getSecret() == null && this.shadowPapEnabled) {
				FileAuthModule.log.trace("using system shadow authentication for user '" + userName + "'");
				// check password agains system account
				if (!ShadowUtils.verifyUserPassword(userName, password)) throw new AuthenticationException(
				        "Password incorrect");
			} else // compare passwords
			if (papEntry.getSecret() == null) {
				if (password.length() != 0) {
					FileAuthModule.log.debug("shadow auth not enabled and pap-password is empty - user password MUST be empty too");
					throw new AuthenticationException("password incorrect");
				}
			} else if (!password.equals(papEntry.getSecret())) throw new AuthenticationException("Password incorrect");
			FileAuthModule.log.debug("user '" + userName + "' PAP authentication success");

			// set response IP-address
			final IP ip = FileAuthModule.detectResponseIP(papEntry, requestAttributes);
			if (ip != null) {
				FileAuthModule.log.debug("setting remote IP to '" + ip + "' for user '" + userName + "'");
				responseAttributes.add(new FramedIPAddress(ip));
			}
		} catch (final IOException ex) {
			throw new AuthenticationException("server error", ex);
		} catch (final NotFoundException ex) {
			// upon check in shadow
			throw new AuthenticationException("User incorrect");
		}
		return responseAttributes;
	}

	/**
	 * Authenticate using CHAP method against user file. PapFile must contains clear-text (not crypted) and not empty
	 * passwords.
	 * 
	 * @param userName user name
	 * @param password byte[17] value of Chap-Password attribute (byte[0] is ChapId and byte[1..16] is ChapPassword)
	 * @param challenge byte[16] value of Chap-Chalenge attribute
	 * @param requestAttributes attributes from AccessRequest packet
	 * @return response attributes for AccessAccept packet (Framed-IP-Address attribute if specified)
	 * @throws AuthenticationException if authentication fails any reason. Message will used in Reply-Message attribute
	 *             of Access-Reject response.
	 * @see PapFile
	 */
	public final AttributesList authChap(final String userName, final ChapPassword password, final ChapChallenge challenge, final AttributesList requestAttributes) throws AuthenticationException {
		if (userName == null) throw new IllegalArgumentException("null userName");
		if (password == null) throw new IllegalArgumentException("null chapPassword");
		if (challenge == null) throw new IllegalArgumentException("null challenge");
		if (requestAttributes == null) throw new IllegalArgumentException("null requestAttributes");
		// check if allowed
		if (!this.chapEnabled) throw new AuthenticationException("CHAP not allowed");
		// check pap-file
		if (this.papFile == null) {
			FileAuthModule.log.error("papFile not initialized. skipping request");
			throw new IllegalStateException("Server error: no papFile is configured");
		}
		final AttributesList attribs = new AttributesList(AttributeType.VENDOR_NONE);
		try {
			// check if pap file contain clear-text (ot crypted) passwords
			if (this.papFile.isCryptedPasswords()) {
				FileAuthModule.log.debug("can't authenticate CHAP scheme for user '" + userName
				        + "' because pap-file '" + this.papFile.getFileName() + "' paswords is crypted");
				throw new AuthenticationException("CHAP not supported: passwords is crypted");
			}
			// get server id
			final String serverIdent = FileAuthModule.detectServerIdent(requestAttributes);
			// search PapEntry
			final PapEntry papEntry = this.papFile.search(userName, serverIdent);
			if (papEntry == null) throw new AuthenticationException("User not allowed");
			// pap entry must have not empty secret
			if (papEntry.getSecret() == null) {
				FileAuthModule.log.debug("empty CHAP password in pap-file '" + this.papFile.getFileName()
				        + "' for user '" + userName + "'");
				throw new AuthenticationException("CHAP not supported with system accounts database");
			}
			// verify password
			if (!password.equals(papEntry.getSecret(), challenge)) throw new AuthenticationException(
			        "Password incorrect");
			FileAuthModule.log.debug("user '" + userName + "' CHAP authentication success");

			// set response IP-address
			final IP ip = FileAuthModule.detectResponseIP(papEntry, requestAttributes);
			if (ip != null) {
				FileAuthModule.log.debug("setting remote IP to: " + ip);
				attribs.add(new FramedIPAddress(ip));
			}
		} catch (final IOException ex) {
			throw new AuthenticationException("server error", ex);
		}
		return attribs;
	}

	/**
	 * @see org.dicr.radius.handler.MSChap2AuthModule#authMSChap2(org.dicr.radius.attribute.impl.UserName,
	 *      org.dicr.radius.attribute.ms.MSChapChallenge, org.dicr.radius.attribute.ms.MSChap2Response,
	 *      org.dicr.radius.attribute.AttributesList)
	 */
	public AttributesList authMSChap2(final UserName userName, final MSChapChallenge challenge, final MSChap2Response response, final AttributesList requestAttributes) throws AuthenticationException {
		if (userName == null) throw new IllegalArgumentException("null userName");
		if (challenge == null) throw new IllegalArgumentException("null challenge");
		if (response == null) throw new IllegalArgumentException("null response");
		if (requestAttributes == null) throw new IllegalArgumentException("null requestAttributes");
		// check pap-file
		if (this.papFile == null) {
			FileAuthModule.log.error("papFile not initialized. skipping request");
			throw new IllegalStateException("Server error: no papFile is configured");
		}
		final AttributesList attribs = new AttributesList(AttributeType.VENDOR_NONE);
		try {
			// check if pap file contain clear-text (ot crypted) passwords
			if (this.papFile.isCryptedPasswords()) {
				FileAuthModule.log.debug("can't authenticate CHAP scheme for user '" + userName
				        + "' because pap-file '" + this.papFile.getFileName() + "' paswords is crypted");
				throw new AuthenticationException("CHAP not supported: passwords is crypted");
			}
			// get server id
			final String serverIdent = FileAuthModule.detectServerIdent(requestAttributes);
			// search PapEntry
			final PapEntry papEntry = this.papFile.search(userName.getValue(), serverIdent);
			if (papEntry == null) throw new AuthenticationException("User not allowed");
			// pap entry must have not empty secret
			if (papEntry.getSecret() == null) {
				FileAuthModule.log.debug("empty CHAP password in pap-file '" + this.papFile.getFileName()
				        + "' for user '" + userName + "'");
				throw new AuthenticationException("MSCHAP not supported with system accounts database");
			}
			// verify password
			if (!response.verifyResponse(userName, papEntry.getSecret(), challenge)) throw new AuthenticationException(
			        "Password incorrect");
			FileAuthModule.log.debug("user '" + userName.getValue() + "' MSCHAPv2 authentication success");

			// set response IP-address
			final IP ip = FileAuthModule.detectResponseIP(papEntry, requestAttributes);
			if (ip != null) {
				FileAuthModule.log.debug("setting remote IP to: " + ip);
				attribs.add(new FramedIPAddress(ip));
			}

			// add authenticator response
			final MicrosoftAttribute mattr = new MicrosoftAttribute();
			mattr.getAttributes().add(
			        new MSChap2Success(new UserName(papEntry.getName()), papEntry.getSecret(), challenge, response));
			attribs.add(mattr);
		} catch (final IOException ex) {
			FileAuthModule.log.warn("ms-chap-v2 authentication error", ex);
			throw new AuthenticationException("server error", ex);
		}
		return attribs;
	}

	/**
	 * Authenticate using MSCHAPv1 protocol
	 * 
	 * @see org.dicr.radius.handler.MSChapAuthModule#authMSCHAP(org.dicr.radius.attribute.impl.UserName,
	 *      org.dicr.radius.attribute.ms.MSChapChallenge, org.dicr.radius.attribute.ms.MSChapResponse,
	 *      org.dicr.radius.attribute.AttributesList)
	 */
	public AttributesList authMSCHAP(final UserName userName, final MSChapChallenge challenge, final MSChapResponse response, final AttributesList requestAttributes) throws AuthenticationException {
		if (userName == null) throw new IllegalArgumentException("null userName");
		if (challenge == null) throw new IllegalArgumentException("null challenge");
		if (response == null) throw new IllegalArgumentException("null response");
		if (requestAttributes == null) throw new IllegalArgumentException("null requestAttributes");
		// check pap-file
		if (this.papFile == null) {
			FileAuthModule.log.error("papFile not initialized. skipping request");
			throw new IllegalStateException("Server error: no papFile is configured");
		}
		final AttributesList attribs = new AttributesList(AttributeType.VENDOR_NONE);
		try {
			// check if pap file contain clear-text (ot crypted) passwords
			if (this.papFile.isCryptedPasswords()) {
				FileAuthModule.log.debug("can't authenticate MSCHAPv1 scheme for user '" + userName
				        + "' because pap-file '" + this.papFile.getFileName() + "' paswords is crypted");
				throw new AuthenticationException("CHAP not supported: passwords is crypted");
			}
			// get server id
			final String serverIdent = FileAuthModule.detectServerIdent(requestAttributes);
			// search PapEntry
			final PapEntry papEntry = this.papFile.search(userName.getValue(), serverIdent);
			if (papEntry == null) throw new AuthenticationException("User not allowed");
			// pap entry must have not empty secret
			if (papEntry.getSecret() == null) {
				FileAuthModule.log.debug("empty CHAP password in pap-file '" + this.papFile.getFileName()
				        + "' for user '" + userName + "'");
				throw new AuthenticationException("CHAP not supported with system accounts database");
			}
			// verify password
			if (!response.verifyResponse(challenge, papEntry.getSecret())) throw new AuthenticationException(
			        "Password incorrect");
			FileAuthModule.log.debug("user '" + userName.getValue() + "' MSCHAPv1 authentication success");

			// set response IP-address
			final IP ip = FileAuthModule.detectResponseIP(papEntry, requestAttributes);
			if (ip != null) {
				FileAuthModule.log.debug("setting remote IP to: " + ip);
				attribs.add(new FramedIPAddress(ip));
			}
		} catch (final IOException ex) {
			FileAuthModule.log.warn("ms-chap-v2 authentication error", ex);
			throw new AuthenticationException("server error", ex);
		}
		return attribs;
	}
}
