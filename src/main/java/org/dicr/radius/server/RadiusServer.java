/**
 * RadiusServer.java 10.11.2006
 */
package org.dicr.radius.server;

import java.util.*;

import org.dicr.radius.channel.*;
import org.dicr.radius.handler.*;
import org.dicr.radius.server.impl.*;

/**
 * Radius AAA Server
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061109
 */
public interface RadiusServer {

	/**
     * Set server channels to listen
     * 
     * @param channels set of channels to read/write packets
     */
	public void setChannels(Set<ServerChannel> channels);

	/**
     * Add channel
     * 
     * @param channel server channel to add
     */
	public void addChannel(ServerChannel channel);

	/**
     * Set requests queue
     * <P>
     * Default is {@link TrackingRequestsQueue}
     * </P>
     * 
     * @param queue new requests queue
     */
	public void setRequestsQueue(RequestsQueue queue);

	/**
     * Return requests queue
     * 
     * @return current requests queue
     */
	public RequestsQueue getRequestsQueue();

	/**
     * Set the requests handler
     * 
     * @param handler requests handler
     */
	public void setRequestHandler(RequestHandler handler);

	/**
     * Return requests handler
     * 
     * @return current handler or null if not set
     */
	public RequestHandler getRequestHandler();

	/**
     * Return server state
     * 
     * @return true if server is running.
     */
	public boolean isRunning();

	/**
     * Start the server
     */
	public void startServer();

	/**
     * Stop the server.
     */
	public void stopServer();
}
