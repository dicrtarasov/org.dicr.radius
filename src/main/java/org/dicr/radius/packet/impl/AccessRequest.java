package org.dicr.radius.packet.impl;

import org.dicr.radius.packet.*;

/**
 * AccessRequest packet.
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060623
 */
public class AccessRequest extends RequestPacket {
	/** Packet code */
	public static final int CODE = 1;

	/**
     * Constructor
     */
	public AccessRequest() {
		super(AccessRequest.CODE);
	}
}
