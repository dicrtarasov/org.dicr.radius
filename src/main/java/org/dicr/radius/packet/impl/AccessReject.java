package org.dicr.radius.packet.impl;

import org.dicr.radius.packet.*;

/**
 * AccessReject packet
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 1.0
 */
public final class AccessReject extends ResponsePacket {
	/** Packet code */
	public static final int CODE = 3;

	/**
     * Constructor
     */
	public AccessReject() {
		super(AccessReject.CODE);
	}
}
