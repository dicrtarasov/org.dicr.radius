/**
 * TestClient.java 14.11.2006
 */
package org.dicr.radius.test;

import java.net.*;

import org.apache.log4j.*;
import org.dicr.radius.*;
import org.dicr.radius.attribute.impl.*;
import org.dicr.radius.authenticator.impl.*;
import org.dicr.radius.channel.impl.*;
import org.dicr.radius.client.impl.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.handler.impl.*;

/**
 * Test Client
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061113
 */
public class TestClient {
	private static final Logger log = Logger.getLogger(TestClient.class);

	/**
	 * Main
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final DefaultRadiusAuthenticator authenticator = new DefaultRadiusAuthenticator();
		authenticator.setAuthScheme(AuthScheme.MSCHAPv2);
		authenticator.setServiceType(new ServiceType(ServiceType.Value.Framed));
		authenticator.setNASIdentifier(new NASIdentifier("vpn.euromb.com"));
		// authenticator.setNASIPAddress(new IP("193.201.206.2"));

		final NIOClientChannel channel = new NIOClientChannel();
		channel.setAddress(new InetSocketAddress("localhost", RadiusConstants.PORT_ACCESS));
		channel.setSecret("radlocal");

		final DefaultRadiusClient client = new DefaultRadiusClient();
		client.setAuthenticator(authenticator);
		client.setChannel(channel);

		// while (true) {
		try {
			client.authenticate("dicr", "vvjB3Mt");
			System.out.println("Success");
		} catch (final AuthenticationException ex) {
			TestClient.log.error(ex);
		}
		// Thread.sleep(1000);
		// }
	}

}
