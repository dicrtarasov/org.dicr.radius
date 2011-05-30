package org.dicr.radius.packet.impl;

import org.dicr.radius.packet.*;

/**
 * AccountingRequest packet.
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060623
 */
public class AccountingRequest extends RequestPacket {
	/** Packet code */
	public static final int CODE = 4;

	/**
     * Constructor
     */
	public AccountingRequest() {
		super(AccountingRequest.CODE);
	}
}
