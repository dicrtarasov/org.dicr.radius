package org.dicr.radius.packet.impl;

import org.dicr.radius.packet.*;

/**
 * AccessAccept packet.
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 1.0
 */
public final class AccessAccept extends ResponsePacket {
	/** Packet code */
	public static final int CODE = 2;

	/**
     * Constructor
     */
	public AccessAccept() {
		super(AccessAccept.CODE);
	}
}
