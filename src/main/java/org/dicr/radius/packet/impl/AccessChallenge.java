package org.dicr.radius.packet.impl;

import org.dicr.radius.packet.*;

/**
 * AccessChallenge packet
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060623
 */
public final class AccessChallenge extends ResponsePacket {
	/** Packet code */
	public static final int CODE = 11;

	/**
     * Constructor
     */
	public AccessChallenge() {
		super(AccessChallenge.CODE);
	}
}
