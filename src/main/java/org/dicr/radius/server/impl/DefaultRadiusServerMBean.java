/**
 * DefaultRadiusServerMBean.java 11.11.2006
 */
package org.dicr.radius.server.impl;

import org.dicr.radius.channel.*;
import org.dicr.radius.handler.*;
import org.dicr.radius.server.*;

/**
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version XXXXXX
 */
public interface DefaultRadiusServerMBean extends RadiusServer {

	/**
     * @see org.dicr.radius.server.RadiusServer#addChannel(org.dicr.radius.channel.ServerChannel)
     */
	@Override
    public void addChannel(ServerChannel channel);

	/**
     * Remove channel from server
     * 
     * @param channel channel to remove
     */
	public void removeChannel(ServerChannel channel);

	/**
     * @see org.dicr.radius.server.RadiusServer#setRequestsQueue(org.dicr.radius.server.RequestsQueue)
     */
	@Override
    public void setRequestsQueue(RequestsQueue queue);

	/**
     * @see org.dicr.radius.server.RadiusServer#getRequestsQueue()
     */
	@Override
    public RequestsQueue getRequestsQueue();

	/**
     * @see org.dicr.radius.server.RadiusServer#setRequestHandler(org.dicr.radius.handler.RequestHandler)
     */
	@Override
    public void setRequestHandler(RequestHandler handler);

	/**
     * @see org.dicr.radius.server.RadiusServer#getRequestHandler()
     */
	@Override
    public RequestHandler getRequestHandler();

	/**
     * @see org.dicr.radius.server.RadiusServer#isRunning()
     */
	@Override
    public boolean isRunning();

	/**
     * @see org.dicr.radius.server.RadiusServer#startServer()
     */
	@Override
    public void startServer();

	/**
     * @see org.dicr.radius.server.RadiusServer#stopServer()
     */
	@Override
    public void stopServer();

}
