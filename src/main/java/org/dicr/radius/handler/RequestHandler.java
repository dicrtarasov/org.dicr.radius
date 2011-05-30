package org.dicr.radius.handler;

import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;

/**
 * Handler of RequestPacketS.
 *
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 1.0
 */
public interface RequestHandler {

	/**
     * Handle request. If handler can process this request packet, it must return ResponsePacket as a result of
     * processing request. Otherwise, if request is unknown type, it must return null. If handler throw exception, then
     * handling of request is stopped and no reply send to user.
     *
     * @param request request to handle
     * @return response packet or null if request can't be handled (request or attributes is unknown)
     * @throws IncorrectRequestException if request was processed, but attributes values is incorrect
     * @throws RequestHandlerException if request was processed, but some exceptions eccut (f.e. FileNotFound)
     */
	public ResponsePacket handleRequest(RequestPacket request) throws RequestHandlerException;
}
