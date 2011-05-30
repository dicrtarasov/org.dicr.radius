/**
 * PacketFactory.java 06.11.2006
 */
package org.dicr.radius.packet;

import java.util.*;

import org.apache.log4j.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.packet.impl.*;

/**
 * Packet Factory. Create packets by code.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061105
 */
public class PacketFactory {
	/** Logger */
	private static final Logger log = Logger.getLogger(PacketFactory.class);

	/** Registered Packet Codes */
	private static final Map<Integer, Class<? extends RadiusPacket>> registeredPackets = new HashMap<Integer, Class<? extends RadiusPacket>>();

	/** Register known packets */
	static {
		PacketFactory.registerPacket(AccessRequest.CODE, AccessRequest.class);
		PacketFactory.registerPacket(AccessChallenge.CODE, AccessChallenge.class);
		PacketFactory.registerPacket(AccessAccept.CODE, AccessAccept.class);
		PacketFactory.registerPacket(AccessReject.CODE, AccessReject.class);
		PacketFactory.registerPacket(AccountingRequest.CODE, AccountingRequest.class);
		PacketFactory.registerPacket(AccountingResponse.CODE, AccountingResponse.class);
	}

	/**
	 * Register packet type
	 * 
	 * @param code packet code
	 * @param clazz packet class for code
	 */
	public static void registerPacket(final int code, final Class<? extends RadiusPacket> clazz) {
		if (clazz == null) throw new IllegalArgumentException("null packet class");
		synchronized (PacketFactory.registeredPackets) {
			PacketFactory.registeredPackets.put(Integer.valueOf(code), clazz);
			PacketFactory.log.trace("registered packet class " + clazz + " for type code " + code);
		}
	}

	/**
	 * Create packet for specified code
	 * 
	 * @param code type code
	 * @return packet for this code
	 * @throws CodecException if code is unknown
	 */
	public static RadiusPacket createPacket(final int code) throws CodecException {
		final Class<? extends RadiusPacket> clazz = PacketFactory.registeredPackets.get(Integer.valueOf(code));
		if (clazz == null) throw new CodecException("unknown packet code: " + code);
		try {
			return clazz.newInstance();
		} catch (final Exception ex) {
			throw new RuntimeException("error creating packet for code: " + code, ex);
		}
	}
}
