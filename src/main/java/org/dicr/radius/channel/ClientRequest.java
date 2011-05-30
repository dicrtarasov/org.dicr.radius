/**
 * ClientRequest.java 09.11.2006
 */
package org.dicr.radius.channel;

import java.net.*;

import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;

/**
 * Client Request.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061109
 */
public interface ClientRequest {

	/**
     * Return server channel.
     * 
     * @return server channel, which receive this request.
     */
	public ServerChannel getServerChannel();

	/**
     * Request request
     * 
     * @return request packet
     */
	public RequestPacket getRequestPacket();

	/**
     * Return address
     * 
     * @return client address
     */
	public SocketAddress getClientAddress();

	/**
     * Return time stamp
     * 
     * @return time, when request was received
     */
	public long getTimeStamp();

	/**
     * Send response back to the client
     * 
     * @param response response to reply for this request to client.
     * @throws ChannelException
     */
	public void sendResponse(ResponsePacket response) throws ChannelException;
}
