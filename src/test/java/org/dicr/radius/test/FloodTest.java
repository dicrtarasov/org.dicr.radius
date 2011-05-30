/**
 * FloodTest.java 22.11.2006
 */
package org.dicr.radius.test;

import java.net.*;
import java.util.*;

/**
 * Flood test
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061122
 */
public class FloodTest {

	/**
     * Main
     *
     * @param args
     * @throws Exception
     */
	public static void main(String[] args) throws Exception {
		DatagramSocket socket = new DatagramSocket();
		socket.connect(new InetSocketAddress("localhost", 6511));
		byte[] buf = new byte[50000];
		Random random = new Random();
		while (true) {
			random.nextBytes(buf);
			buf[2] = (byte) random.nextInt(1);
			int size = random.nextInt(buf.length);
			System.out.println(size);
			DatagramPacket packet = new DatagramPacket(buf, size);
			socket.send(packet);
			Thread.yield();
			Thread.sleep(1);
		}
	}
}
