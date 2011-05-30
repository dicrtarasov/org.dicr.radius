/**
 * ServerChannelListener.java 09.11.2006
 */
package org.dicr.radius.channel;

/**
 * Server Channel Listener.
 * <P>
 * This interface must be implemented by listeners of {@link ServerChannel}
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061109
 */
public interface ServerChannelListener {
	/**
     * Called by server channel when client request received.
     * 
     * @param request received request.
     */
	public void requestReceived(ClientRequest request);
}
