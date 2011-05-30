/**
 * DefaultRadiusAuthenticator.java 04.06.2007
 */
package org.dicr.radius.authenticator.impl;

import java.net.*;
import java.util.*;

import org.apache.log4j.*;
import org.dicr.radius.attribute.*;
import org.dicr.radius.attribute.impl.*;
import org.dicr.radius.attribute.ms.*;
import org.dicr.radius.attribute.types.*;
import org.dicr.radius.authenticator.*;
import org.dicr.radius.channel.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.handler.impl.*;
import org.dicr.radius.packet.*;
import org.dicr.radius.packet.impl.*;
import org.dicr.util.net.*;

/**
 * Default Radius Authenticator.
 * <P>
 * By default, {@link #getAuthScheme() authentication scheme} is set to {@link AuthScheme#PAP}<BR>
 * {@link #getNASIdentifier() NAS-Identifier} and {@link #getNASIPAddress() NAS-IP-Address} attributes in request is set
 * to local host name and addres ({@link InetAddress#getLocalHost()})
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070604
 */
public class DefaultRadiusAuthenticator implements RadiusAuthenticator {
	/** Logger */
	private static final Logger log = Logger.getLogger(DefaultRadiusAuthenticator.class);

	/** Auth Scheme */
	private AuthScheme authScheme = AuthScheme.PAP;

	/** NAS-Identifier */
	private NASIdentifier nasIdentifier = null;

	/** NAS-IP-Address */
	private NASIPAddress nasIPAddress = null;

	/** Service-Type */
	private ServiceType serviceType = null;

	/**
	 * Constructor
	 */
	public DefaultRadiusAuthenticator() {
		super();
		try {
			final InetAddress addr = InetAddress.getLocalHost();
			this.nasIdentifier = new NASIdentifier(addr.getHostName());
			this.nasIPAddress = new NASIPAddress(IP.parse(addr.getAddress()));
		} catch (final UnknownHostException ex) {
			DefaultRadiusAuthenticator.log.error("can't get local host address");
		}
	}

	/**
	 * Constructor
	 * 
	 * @param scheme authentication scheme
	 * @param identifier NAS-Identifier
	 */
	public DefaultRadiusAuthenticator(final AuthScheme scheme, final NASIdentifier identifier) {
		this();
		this.setNASIdentifier(identifier);
		this.setAuthScheme(scheme);
	}

	/**
	 * Set authentication scheme
	 * 
	 * @param scheme authentication scheme
	 */
	public final void setAuthScheme(final AuthScheme scheme) {
		if (scheme == null) throw new IllegalArgumentException("null scheme");
		synchronized (this) {
			this.authScheme = scheme;
		}
	}

	/**
	 * Return authentication scheme. Default is {@link AuthScheme#PAP}
	 * 
	 * @return authentication scheme
	 */
	public final AuthScheme getAuthScheme() {
		synchronized (this) {
			return this.authScheme;
		}
	}

	/**
	 * Set NAS-Identifier attribute
	 * 
	 * @param identifier set NAS-Identifier attribute for requests
	 */
	public void setNASIdentifier(final NASIdentifier identifier) {
		if (identifier == null) throw new IllegalArgumentException("null identifier");
		synchronized (this) {
			this.nasIdentifier = identifier;
		}
		DefaultRadiusAuthenticator.log.info("configured NAS-Identifier: " + identifier.getValue());
	}

	/**
	 * Return NAS-Identifier attribute
	 * 
	 * @return identifier NAS-Identifier attribute
	 */
	public NASIdentifier getNASIdentifier() {
		synchronized (this) {
			return this.nasIdentifier;
		}
	}

	/**
	 * Set NAS-IP-Address attribute
	 * 
	 * @param address NAS-IP-Address attribute for requests
	 */
	public void setNASIPAddress(final NASIPAddress address) {
		if (address == null) throw new IllegalArgumentException("null address");
		synchronized (this) {
			this.nasIPAddress = address;
		}
		DefaultRadiusAuthenticator.log.info("configured NAS-IP-Address: " + address.getValueAsString());
	}

	/**
	 * Return NAS-IP-Address attribute
	 * 
	 * @return NAS-IP-Address attribute
	 */
	public NASIPAddress getNASIPAddress() {
		synchronized (this) {
			return this.nasIPAddress;
		}
	}

	/**
	 * Set Service-Type attribute
	 * 
	 * @param type Service-Type attribute for requests
	 */
	public void setServiceType(final ServiceType type) {
		synchronized (this) {
			this.serviceType = type;
		}
	}

	/**
	 * Return Service-Type attribute
	 * 
	 * @return Service-Type attribute
	 */
	public ServiceType getServiceType() {
		synchronized (this) {
			return this.serviceType;
		}
	}

	/**
	 * @see org.dicr.radius.authenticator.RadiusAuthenticator#authenticate(java.lang.String, java.lang.String,
	 *      org.dicr.radius.channel.ClientChannel)
	 */
	public synchronized AttributesList authenticate(final String username, final String password, final ClientChannel clientChannel) throws AuthenticationException, ChannelException, CodecException {
		if (username == null) throw new IllegalArgumentException("null username");
		if (password == null) throw new IllegalArgumentException("null password");
		if (clientChannel == null) throw new IllegalArgumentException("null channel");

		// user-name attribute
		final UserName userName = new UserName(username);

		// prepare request
		final RequestPacket request = new AccessRequest();
		final AttributesList attribs = request.getAttributes();
		attribs.set(userName);

		// store MSCHAP2 request attributes to verify response
		MSChapChallenge msChapChallenge = null;
		MSChap2Response msChap2Response = null;

		// add password attributes
		switch (this.authScheme) {
			case PAP:
				attribs.set(new UserPassword(password));
				break;
			case CHAP:
				final ChapChallenge chapChallenge = new ChapChallenge();
				attribs.set(chapChallenge);
				attribs.set(new ChapPassword(password, chapChallenge));
				break;
			case MSCHAP:
				msChapChallenge = new MSChapChallenge();
				final MSChapResponse msChapResponse = new MSChapResponse(msChapChallenge, password);
				attribs.add(new MicrosoftAttribute(msChapChallenge));
				attribs.add(new MicrosoftAttribute(msChapResponse));
				break;
			case MSCHAPv2:
				msChapChallenge = new MSChapChallenge();
				msChap2Response = new MSChap2Response(username, password, msChapChallenge);
				attribs.add(new MicrosoftAttribute(msChapChallenge));
				attribs.add(new MicrosoftAttribute(msChap2Response));
				break;
			default:
				DefaultRadiusAuthenticator.log.error("unknown authentication scheme: " + this.authScheme);
				throw new AuthenticationException("client error");
		}

		// finish request
		attribs.set(this.getNASIdentifier());
		attribs.set(this.getNASIPAddress());
		if (this.getServiceType() != null) attribs.set(this.getServiceType());

		// send/receive
		final ResponsePacket response = clientChannel.query(request);
		if (!(response instanceof AccessAccept)) {
			final ReplyMessage reply = response.getAttributes().getFirst(ReplyMessage.TYPE);
			DefaultRadiusAuthenticator.log.warn("user '" + username + "' PAP authentication failure"
			        + (reply != null ? ": " + reply.getValue() : ""));
			throw new AuthenticationException(reply != null ? reply.getValue() : "");
		}

		// verify mschapv2 success response
		if (this.authScheme == AuthScheme.MSCHAPv2) {
			MSChap2Success succ = null;
			final List<VendorAttribute> vendorAttrs = response.getAttributes().findAll(VendorAttribute.TYPE);
			for (final VendorAttribute attr : vendorAttrs) {
				if (attr.getVendorId() != MicrosoftAttribute.VENDOR_CODE) continue;
				succ = attr.getAttributes().getFirst(MSChap2Success.TYPE);
				if (succ != null) break;
			}
			if (succ == null) throw new AuthenticationException(
			        "server response not contains MS-CHAP2-Success attribute");
			if (!succ.verify(userName, password, msChapChallenge, msChap2Response)) throw new AuthenticationException(
			        "incorrect value of MS-Chap2-Success response from server");
		}

		DefaultRadiusAuthenticator.log.debug("user '" + username + "' " + this.authScheme + " authentication success");
		return response.getAttributes();
	}
}
