/**
 * AccountingResponse.java 14.06.2006
 */
package org.dicr.radius.packet.impl;

import org.dicr.radius.packet.*;

/**
 * AccountingResponse packet.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060623
 */
public class AccountingResponse extends ResponsePacket {
	/** Packet code */
	public static final int CODE = 5;

	/**
     * Constructor
     */
	public AccountingResponse() {
		super(AccountingResponse.CODE);
	}
}
