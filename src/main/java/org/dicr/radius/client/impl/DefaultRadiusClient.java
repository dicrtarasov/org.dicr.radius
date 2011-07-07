/**
 * DefaultRadiusClient.java 14.11.2006
 */
package org.dicr.radius.client.impl;

import org.apache.log4j.*;
import org.dicr.radius.attribute.*;
import org.dicr.radius.authenticator.*;
import org.dicr.radius.authenticator.impl.*;
import org.dicr.radius.channel.*;
import org.dicr.radius.client.*;
import org.dicr.radius.exc.*;

/**
 * Default implementation of Radius Client
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061114
 */
public class DefaultRadiusClient implements RadiusClient {
	/** Logger */
	private static final Logger log = Logger.getLogger(RadiusClient.class);

	/** Authenticator */
	private RadiusAuthenticator authenticator = new DefaultRadiusAuthenticator();

	/** Channel */
	private ClientChannel channel = null;

	/**
	 * Constructor
	 */
	public DefaultRadiusClient() {
		super();
	}

	/**
	 * Set authenticator
	 * 
	 * @param radiusAuthenticator radius authenticator
	 */
	public void setAuthenticator(final RadiusAuthenticator radiusAuthenticator) {
		if (radiusAuthenticator == null) throw new IllegalArgumentException("null authenticator");
		synchronized (this) {
			this.authenticator = radiusAuthenticator;
		}
		DefaultRadiusClient.log.debug("configured authenticator: " + radiusAuthenticator);
	}

	/**
	 * Return authenticator
	 * 
	 * @return radius authenticator
	 */
	public RadiusAuthenticator getAuthenticator() {
		synchronized (this) {
			return this.authenticator;
		}
	}

	/**
	 * Set channel
	 * 
	 * @param clientChannel radius client channel
	 */
	public void setChannel(final ClientChannel clientChannel) {
		if (clientChannel == null) throw new IllegalArgumentException("null channel");
		synchronized (this) {
			this.channel = clientChannel;
		}
		DefaultRadiusClient.log.debug("configured channel: " + clientChannel);
	}

	/**
	 * Return channel
	 * 
	 * @return radius client channel
	 */
	public ClientChannel getChannel() {
		synchronized (this) {
			return this.channel;
		}
	}

	/**
	 * @see org.dicr.radius.client.RadiusClient#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
    public AttributesList authenticate(final String user, final String password) throws AuthenticationException, CodecException, ChannelException {
		synchronized (this) {
			return this.authenticator.authenticate(user, password, this.channel);
		}

	}

}
