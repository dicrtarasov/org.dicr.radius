/**
 * TestServer.java 14.11.2006
 */
package org.dicr.radius.test;

import java.net.*;
import java.util.*;

import org.dicr.radius.channel.impl.*;
import org.dicr.radius.server.impl.*;

/**
 * Test Radius Server
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061113
 */
public class TestServer {

	/**
	 * @param args
	 * @throws Exception error
	 */
	public static void main(final String[] args) throws Exception {
		final NIOServerChannel channel = new NIOServerChannel();
		channel.setListenAddresses(Collections.singleton(new InetSocketAddress("localhost", 6511)));
		channel.setSharedSecret("localhost", "radsecret");

		final DefaultRadiusServer server = new DefaultRadiusServer();
		server.addChannel(channel);

		server.startServer();
	}

}
