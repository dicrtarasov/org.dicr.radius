/**
 * RequestsQueue.java 09.11.2006
 */
package org.dicr.radius.server.impl;

import java.net.*;
import java.util.*;

import org.apache.log4j.*;
import org.dicr.radius.channel.*;
import org.dicr.radius.server.*;

/**
 * Requests Queue.
 * <P>
 * This queue hold only last request per active client. It track last request id from client and filter requests from
 * client, with incorrect id (accept requests with id > last id only).
 * </P>
 * <P>
 * Client is identificated by it socket address. Client is active upon <CODE>sessionTimeout</CODE>. Maximal number of
 * active clients is <CODE>maxClients</CODE>.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061109
 */
public class TrackingRequestsQueue implements RequestsQueue {
	private static final Logger log = Logger.getLogger(TrackingRequestsQueue.class);

	/** Clients list */
	private final List<ClientSession> list = new LinkedList<ClientSession>();

	/** Session timeout */
	private long sessionTimeout = 30000;

	/** Limit queue maxClients */
	private int maxClients = 1000;

	/**
	 * Constructor
	 */
	public TrackingRequestsQueue() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param millis session timeout in milliseconds
	 */
	public TrackingRequestsQueue(final long millis) {
		super();
		this.setSessionTimeout(millis);
	}

	/**
	 * Set session timeout
	 * 
	 * @param millis timeout in milliseconds
	 */
	public void setSessionTimeout(final long millis) {
		if (millis < 0) throw new IllegalArgumentException("sessionTimeout: " + millis);
		this.sessionTimeout = millis;
		TrackingRequestsQueue.log.debug("configured session timeout: " + millis);
	}

	/**
	 * Return session timeout
	 * 
	 * @return session timeout
	 */
	public long getSessionTimeout() {
		return this.sessionTimeout;
	}

	/**
	 * Set maximum active clients limit
	 * <P>
	 * Limit maximal count of active clients, holds in queue. Default 1000
	 * </P>
	 * 
	 * @param acapacity maximum capacity
	 */
	public void setMaxClients(final int acapacity) {
		if (acapacity < 1) throw new IllegalArgumentException("maxClients: " + acapacity);
		synchronized (this) {
			this.maxClients = acapacity;
		}
	}

	/**
	 * Return maxClients
	 * 
	 * @return capacity limit in clients number
	 */
	public int getMaxClients() {
		synchronized (this) {
			return this.maxClients;
		}
	}

	/**
	 * @see org.dicr.radius.server.RequestsQueue#putRequest(org.dicr.radius.channel.ClientRequest)
	 */
	@Override
    public synchronized void putRequest(final ClientRequest request) {
		if (request == null) throw new IllegalArgumentException("null request");
		ClientSession activeSession = null;
		final long currentTime = System.currentTimeMillis();
		final SocketAddress address = request.getClientAddress();

		// check all sessions
		final Iterator<ClientSession> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			final ClientSession session = iterator.next();
			// expiring
			if (currentTime - session.request.getTimeStamp() > this.sessionTimeout) {
				//TrackingRequestsQueue.log.trace("stopping client session " + session.request.getClientAddress());
				iterator.remove();
			} else if (session.request.getClientAddress().equals(address)) {
				activeSession = session;
				break;
			}
		}

		// need enqueue request
		boolean needEnqueue = false;

		// check active session
		if (activeSession == null) {
			if (this.list.size() >= this.maxClients) TrackingRequestsQueue.log.error("requests overflow. To many active clients: "
			        + this.list.size());
			else {
				activeSession = new ClientSession(request);
				this.list.add(activeSession);
				//TrackingRequestsQueue.log.trace("started client session: " + address);
				needEnqueue = true;
			}
		} else {
			final int newid = request.getRequestPacket().getId();
			final int lastid = activeSession.request.getRequestPacket().getId();
			
			// check old packet
			if ((newid < lastid) && (newid > 1) && (lastid < 255)) TrackingRequestsQueue.log.warn("ignoring old request id="
			        + newid + " from client " + address + ", last id=" + lastid);
			else {
				// check request overflow
				if (newid == lastid) TrackingRequestsQueue.log.warn("client " + address + " repeat last request, id=" + newid);
				// check request duplicate
				else if (activeSession.pending) TrackingRequestsQueue.log.warn("request overflow from client address "
				        + address + ", id=" + newid + ", lastid=" + lastid);
				needEnqueue = true;
			}
		}

		// chage new request
		if (needEnqueue && activeSession != null) {
			activeSession.request = request;
			activeSession.pending = true;
			//TrackingRequestsQueue.log.trace("enqueued request id=" + request.getRequestPacket().getId() + " from client session " + address);
			this.notify();
		}
	}

	/**
	 * @see org.dicr.radius.server.RequestsQueue#takeRequest()
	 */
	@Override
    public synchronized ClientRequest takeRequest() throws InterruptedException {
		ClientSession activeSession = null;
		while (activeSession == null) {
			final long currentTime = System.currentTimeMillis();
			final Iterator<ClientSession> iterator = this.list.iterator();
			while (iterator.hasNext()) {
				final ClientSession session = iterator.next();
				if (currentTime - session.request.getTimeStamp() > this.sessionTimeout) {
					// TrackingRequestsQueue.log.trace("stopping client session " + session.request.getClientAddress());
					iterator.remove();
				} else if (session.pending == true) {
					activeSession = session;
					break;
				}
			}
			if (activeSession == null) this.wait();
		}
		activeSession.pending = false;
		// TrackingRequestsQueue.log.trace("dequeued request id=" + activeSession.request.getRequestPacket().getId() + " from client session: " + activeSession.request.getClientAddress());
		return activeSession.request;
	}

	/*******************************************************************************************************************
	 * Information about active Client Session
	 ******************************************************************************************************************/
	protected static class ClientSession {
		/** Request */
		protected ClientRequest request = null;

		/** Pending request flag */
		protected boolean pending = true;

		/**
		 * Constructor
		 * 
		 * @param clientRequest initial client request
		 */
		protected ClientSession(final ClientRequest clientRequest) {
			if (clientRequest == null) throw new IllegalArgumentException("null client request");
			this.request = clientRequest;
		}
	}
}
