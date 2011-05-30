/**
 * RequestsQueue.java 09.11.2006
 */
package org.dicr.radius.server;

import java.util.concurrent.*;

import org.dicr.radius.channel.*;

/**
 * Requests queue.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061109
 */
public interface RequestsQueue {

	/**
     * Put request to queue.
     * 
     * @param request client request
     * @see BlockingQueue#put(Object)
     */
	public void putRequest(ClientRequest request);

	/**
     * Return next pending request.
     * 
     * @return next pending request from queue
     * @throws InterruptedException thread interrupted
     */
	public ClientRequest takeRequest() throws InterruptedException;

}
