/**
 * StandardRequestHandler.java 16.06.2006 6:50:30 dicr
 */
package org.dicr.radius.handler.impl;

import java.util.*;

import org.apache.log4j.*;
import org.dicr.radius.attribute.*;
import org.dicr.radius.attribute.impl.*;
import org.dicr.radius.attribute.ms.*;
import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.handler.*;
import org.dicr.radius.packet.*;
import org.dicr.radius.packet.impl.*;

/**
 * Standard Request handler. Process AccessRequests packets with PAP and CHAP authentication scheme. To make
 * authentication it use {@link PAPAuthModule}. If request packet type or authentication is unknown, or authentication
 * module is not set or not able to handle authentication scheme, it simply skip request processing and return null
 * Response.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060616
 */
public class StandardRequestHandler implements RequestHandler {
	private static final Logger log = Logger.getLogger(StandardRequestHandler.class);

	/** PAP authentication module */
	private PAPAuthModule papAuthModule = null;

	/** CHAP authentication module */
	private CHAPAuthModule chapAuthModule = null;

	/** MS-CHAP Authentication module */
	private MSChapAuthModule msChapAuthModule = null;

	/** MS-CHAP-2 Authentication module */
	private MSChap2AuthModule msChap2AuthModule = null;

	/** Accounting module */
	private AccountingModule accountingModule = null;

	/** Accounting interval */
	private int accountingInterval = -1;

	/**
	 * Constructor
	 */
	public StandardRequestHandler() {
		super();
	}

	/**
	 * Set authentication module for PAP scheme. This module must able to handle PAP authentication.
	 * 
	 * @param module authentication module to handle PAP authentication scheme or null to disable
	 */
	public void setPapAuthModule(final PAPAuthModule module) {
		this.papAuthModule = module;
	}

	/**
	 * Return module, which process PAP authentication.
	 * 
	 * @return PAP authentication module or null if not set
	 */
	public PAPAuthModule getPapAuthModule() {
		return this.papAuthModule;
	}

	/**
	 * Set authentication module for CHAP scheme. This module must be able to handle CHAP authentication.
	 * 
	 * @param module authentication module to handle CHAP authentication scheme or null to disable
	 */
	public void setChapAuthModule(final CHAPAuthModule module) {
		this.chapAuthModule = module;
	}

	/**
	 * Return module, which process CHAP authentication.
	 * 
	 * @return CHAP authentication module or null if not set
	 */
	public CHAPAuthModule getChapAuthModule() {
		return this.chapAuthModule;
	}

	/**
	 * Set accounting module
	 * 
	 * @param module module to process AccountingRequestS
	 */
	public void setAccountingModule(final AccountingModule module) {
		this.accountingModule = module;
	}

	/**
	 * Return MS-CHAP authentication module
	 * 
	 * @return auth module
	 */
	public MSChapAuthModule getMSChapAuthModule() {
		return this.msChapAuthModule;
	}

	/**
	 * Set MS-CHAP authentication module
	 * 
	 * @param module auth module
	 */
	public void setMSChapAuthModule(final MSChapAuthModule module) {
		this.msChapAuthModule = module;
	}

	/**
	 * Return MS-CHAP-V2 authentication module
	 * 
	 * @return auth module
	 */
	public MSChap2AuthModule getMSChap2AuthModule() {
		return this.msChap2AuthModule;
	}

	/**
	 * Set MS-CHAP-V2 authentication module
	 * 
	 * @param module auth modules
	 */
	public void setMSChap2AuthModule(final MSChap2AuthModule module) {
		this.msChap2AuthModule = module;
	}

	/**
	 * Return accounting module
	 * 
	 * @return current accounting module or null if not set.
	 */
	public AccountingModule getAccountingModule() {
		return this.accountingModule;
	}

	/**
	 * Set accounting interval
	 * 
	 * @param interval accounting interval in seconds (0 to default, &lt;0 to disable)
	 */
	public void setAccountingInterval(final int interval) {
		this.accountingInterval = interval;
		if (interval >= 0) StandardRequestHandler.log.info("configured accounting interval: " + interval + " seconds");
		else StandardRequestHandler.log.info("accounting interval is disabled");
	}

	/**
	 * Return accounting interval
	 * 
	 * @return accounting interval (0 is the default, &lt;0 - disabled)
	 */
	public int getAccountingInterval() {
		return this.accountingInterval;
	}

	/**
	 * Process requests.
	 * 
	 * @param request request packet
	 * @return response packet or null if can't process request
	 */
	private ResponsePacket handleAccessRequest(final AccessRequest request) {
		if (request == null) throw new IllegalArgumentException("null request");
		// response
		ResponsePacket response = null;
		String scheme = null;
		final AttributesList attrs = request.getAttributes();
		// check user name attribute is support
		final UserName userName = attrs.getFirst(UserName.TYPE);
		if (userName == null) return null;

		// authenticate
		try {
			// response attributes
			AttributesList responseAttributes = null;
			// check scheme if PAP
			if (attrs.getFirst(UserPassword.TYPE) != null) {
				scheme = "PAP";
				final UserPassword userPassword = attrs.getFirst(UserPassword.TYPE);
				// not handle if PAP authentication if module not set
				if (this.papAuthModule == null) return null;
				responseAttributes = this.papAuthModule.authPap(userName.getValue(), userPassword.getValue(), attrs);
			} else if (attrs.getFirst(ChapPassword.TYPE) != null && attrs.getFirst(ChapChallenge.TYPE) != null) {
				scheme = "CHAP";
				final ChapPassword chapPassword = attrs.getFirst(ChapPassword.TYPE);
				final ChapChallenge chapChallenge = attrs.getFirst(ChapChallenge.TYPE);
				if (this.chapAuthModule == null) return null;
				responseAttributes = this.chapAuthModule.authChap(userName.getValue(), chapPassword, chapChallenge,
				        attrs);
			} else {
				// search Microsoft attributes
				MSChapChallenge challenge = null;
				MSChapResponse chapresponse = null;
				MSChap2Response chap2response = null;
				final List<VendorAttribute> vendorAttrs = attrs.findAll(VendorAttribute.TYPE);
				for (final VendorAttribute attr : vendorAttrs) {
					if (attr.getVendorId() != MicrosoftAttribute.VENDOR_CODE) continue;
					final AttributesList includes = attr.getAttributes();
					if (challenge == null) challenge = includes.getFirst(MSChapChallenge.TYPE);
					if (chapresponse == null) chapresponse = includes.getFirst(MSChapResponse.TYPE);
					if (chap2response == null) chap2response = includes.getFirst(MSChap2Response.TYPE);
				}
				// authenticate
				if (challenge != null) {
					if (chapresponse != null) {
						scheme = "MS-CHAP";
						if (this.msChapAuthModule != null) responseAttributes = this.msChapAuthModule.authMSCHAP(
						        userName, challenge, chapresponse, attrs);
					} else if (chap2response != null) {
						scheme = "MS-CHAP-V2";
						if (this.msChap2AuthModule != null) responseAttributes = this.msChap2AuthModule.authMSChap2(
						        userName, challenge, chap2response, attrs);
					}
				}
			}

			// check if scheme is known
			if (scheme != null) {
				// double check if response attributes is out of specification
				if (responseAttributes == null) responseAttributes = new AttributesList(AttributeType.VENDOR_NONE);
				// set accounting interval
				if (this.accountingInterval >= 0 && responseAttributes.getFirst(AcctInterimInterval.TYPE) == null) responseAttributes.add(new AcctInterimInterval(
				        this.accountingInterval));
				// create response
				response = new AccessAccept();
				response.setId(request.getId());
				response.setAuthenticator(request.getAuthenticator());
				response.getAttributes().addAll(responseAttributes);
			}
		} catch (final AuthenticationException ex) {
			// reject access
			StandardRequestHandler.log.warn("user '" + userName.getValue() + "' " + scheme
			        + " authentication failure: " + ex.getLocalizedMessage());
			response = new AccessReject();
			response.setId(request.getId());
			response.setAuthenticator(request.getAuthenticator());

			response.getAttributes().add(new ReplyMessage(ex.getMessage()));
		}
		return response;
	}

	/**
	 * Process accounnting requests
	 * 
	 * @param request request to process
	 * @return response packet or null if can't handle
	 */
	private ResponsePacket handleAccountingRequest(final AccountingRequest request) {
		if (request == null) throw new IllegalArgumentException("null request");
		ResponsePacket response = null;
		// check accounting module is set
		if (this.accountingModule == null) return null;
		// check Acct-Status-Value attribute
		final AcctStatusType typeAttrib = request.getAttributes().getFirst(AcctStatusType.TYPE);
		if (typeAttrib == null) return null;
		// check Acct-Session-Id attribute
		final AcctSessionId idAttrib = request.getAttributes().getFirst(AcctSessionId.TYPE);
		if (idAttrib == null) return null;
		// process accounting
		try {
			final AttributesList attribs = this.accountingModule.processAccounting((int) typeAttrib.getValue(),
			        idAttrib.getValue(), request.getAttributes());
			attribs.set(idAttrib);
			response = new AccountingResponse();
			response.setId(request.getId());
			response.setAuthenticator(request.getAuthenticator());
			response.getAttributes().addAll(attribs);
		} catch (final AccountingException ex) {
			StandardRequestHandler.log.error("accounting error", ex);
		}
		return response;
	}

	/**
	 * Process requests.
	 * 
	 * @param request request packet
	 * @return response packet or null if can't process request
	 */
	@Override
    public ResponsePacket handleRequest(final RequestPacket request) {
		if (request == null) throw new IllegalArgumentException("null request");
		ResponsePacket response = null;
		if (request instanceof AccessRequest) response = this.handleAccessRequest((AccessRequest) request);
		else if (request instanceof AccountingRequest) response = this.handleAccountingRequest((AccountingRequest) request);
		return response;
	}
}
