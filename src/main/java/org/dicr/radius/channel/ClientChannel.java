/**
 * ClientChannel.java 11.11.2006
 */
package org.dicr.radius.channel;

import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;

/**
 * Client Channel
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061111
 */
public interface ClientChannel {
	/**
     * Query radius server and return response
     * 
     * @param request request
     * @return response from server
     * @throws ChannelException if an IO error occur
     * @throws CodecException if erro encoding/decoding packet
     */
	public ResponsePacket query(RequestPacket request) throws CodecException, ChannelException;
}
