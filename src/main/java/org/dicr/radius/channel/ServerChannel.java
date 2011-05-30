/**
 * ServerChannel.java 09.11.2006
 */
package org.dicr.radius.channel;

import org.dicr.radius.packet.*;
import org.dicr.radius.server.impl.*;

/**
 * Server Channel.
 * <P>
 * Server channel used by {@link DefaultRadiusServer} to read {@link RequestPacket}s from socket and generate
 * {@link ClientRequest}s.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061109
 */
public interface ServerChannel {

	/**
     * Add listener to server channel
     * 
     * @param listener listener to add
     */
	public void addListener(ServerChannelListener listener);

	/**
     * Remove listener
     * 
     * @param listener listener to remove
     */
	public void removeListener(ServerChannelListener listener);

	/**
     * Start channel
     */
	public void startChannel();

	/**
     * Stop channel
     */
	public void stopChannel();

	/**
     * Check if channel is running
     * 
     * @return true if running
     */
	public boolean isRunning();
}
