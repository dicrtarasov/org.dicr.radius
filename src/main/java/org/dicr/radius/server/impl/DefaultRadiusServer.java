package org.dicr.radius.server.impl;

import java.util.*;

import javax.management.*;

import org.apache.log4j.*;
import org.dicr.radius.channel.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.handler.*;
import org.dicr.radius.packet.*;
import org.dicr.radius.server.*;

/**
 * Radius AAA Server.
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060616
 */
public class DefaultRadiusServer implements MBeanRegistration, DefaultRadiusServerMBean {
	/** Logger */
	protected static final Logger log = Logger.getLogger(DefaultRadiusServer.class);

	/** Default MBean Name */
	public static final String DEFAULT_MBEAN_NAME = "org.dicr:service=auth;type=radius;name=dicr";

	/** Channels */
	private final Collection<ServerChannel> channels = new ArrayList<ServerChannel>();

	/** Channels listeners */
	private final RadiusChannelListener channelsListener = new RadiusChannelListener();

	/** Requests queue */
	private RequestsQueue requestsQueue = null;

	/** Request handler */
	private RequestHandler requestHandler = null;

	/** Handler Thread */
	private HandlerThread handlerThread = null;

	/** Running flag */
	private boolean running = false;

	/**
	 * Constructor
	 */
	public DefaultRadiusServer() {
		super();
	}

	/**
	 * @see org.dicr.radius.server.impl.DefaultRadiusServerMBean#addChannel(org.dicr.radius.channel.ServerChannel)
	 */
	@Override
    public void addChannel(final ServerChannel channel) {
		if (channel == null) throw new IllegalArgumentException("null channel");
		synchronized (this) {
			if (!this.channels.contains(channel)) {
				channel.addListener(this.channelsListener);
				this.channels.add(channel);
			}
		}
		DefaultRadiusServer.log.debug("added channel: " + channel);
	}

	/**
	 * @see org.dicr.radius.server.RadiusServer#setChannels(Set)
	 */
	@Override
    public void setChannels(final Set<ServerChannel> achannels) {
		if (achannels == null) throw new IllegalArgumentException("null channels");
		synchronized (this) {
			this.channels.clear();
			final Iterator<ServerChannel> iterator = achannels.iterator();
			while (iterator.hasNext()) {
				final ServerChannel channel = iterator.next();
				if (channel == null) throw new IllegalArgumentException("null channel in list");
				this.channels.add(channel);
				channel.addListener(this.channelsListener);
			}
			this.channels.addAll(achannels);
		}
		DefaultRadiusServer.log.debug("configured " + achannels.size() + " channels");
	}

	/**
	 * @see org.dicr.radius.server.impl.DefaultRadiusServerMBean#removeChannel(org.dicr.radius.channel.ServerChannel)
	 */
	@Override
    public void removeChannel(final ServerChannel channel) {
		if (channel == null) throw new IllegalArgumentException("null channel");
		synchronized (this) {
			channel.removeListener(this.channelsListener);
			this.channels.remove(channel);
		}
		DefaultRadiusServer.log.debug("removed channel: " + channel);
	}

	/**
	 * @see org.dicr.radius.server.impl.DefaultRadiusServerMBean#setRequestsQueue(org.dicr.radius.server.RequestsQueue)
	 */
	@Override
    public final void setRequestsQueue(final RequestsQueue queue) {
		if (queue == null) throw new IllegalArgumentException("null queue");
		synchronized (this) {
			this.requestsQueue = queue;
		}
		DefaultRadiusServer.log.debug("configured requests queue: " + queue);
	}

	/**
	 * @see org.dicr.radius.server.impl.DefaultRadiusServerMBean#getRequestsQueue()
	 */
	@Override
    public final RequestsQueue getRequestsQueue() {
		if (this.requestsQueue == null) this.requestsQueue = new TrackingRequestsQueue();
		synchronized (this) {
			return this.requestsQueue;
		}
	}

	/**
	 * @see org.dicr.radius.server.impl.DefaultRadiusServerMBean#setRequestHandler(org.dicr.radius.handler.RequestHandler)
	 */
	@Override
    public final void setRequestHandler(final RequestHandler handler) {
		if (handler == null) throw new IllegalArgumentException("null handler");
		synchronized (this) {
			this.requestHandler = handler;
		}
		DefaultRadiusServer.log.debug("configured requests handler: " + handler);
	}

	/**
	 * @see org.dicr.radius.server.impl.DefaultRadiusServerMBean#getRequestHandler()
	 */
	@Override
    public final RequestHandler getRequestHandler() {
		synchronized (this) {
			return this.requestHandler;
		}
	}

	/**
	 * @see org.dicr.radius.server.impl.DefaultRadiusServerMBean#isRunning()
	 */
	@Override
    public final boolean isRunning() {
		synchronized (this) {
			return this.running && this.handlerThread != null && this.handlerThread.isAlive();
		}
	}

	/**
	 * @see org.dicr.radius.server.impl.DefaultRadiusServerMBean#startServer()
	 */
	@Override
    public final void startServer() {
		synchronized (this) {
			if (this.isRunning()) DefaultRadiusServer.log.debug("radius server already running");
			else {
				DefaultRadiusServer.log.info("starting Radius AAA Server...");
				this.handlerThread = new HandlerThread();
				this.handlerThread.start();
				for (final ServerChannel channel : this.channels)
					try {
						channel.startChannel();
					} catch (final Throwable th) {
						DefaultRadiusServer.log.error("error starting channel " + channel, th);
					}
				this.running = true;
			}
		}
	}

	/**
	 * @see org.dicr.radius.server.impl.DefaultRadiusServerMBean#stopServer()
	 */
	@Override
    public final void stopServer() {
		synchronized (this) {
			if (!this.isRunning()) DefaultRadiusServer.log.debug("radius server is already stopped");
			else {
				DefaultRadiusServer.log.info("stopping Radius AAA Server...");
				for (final ServerChannel channel : this.channels)
					try {
						channel.stopChannel();
					} catch (final Throwable th) {
						DefaultRadiusServer.log.error("error stopping channel " + channel, th);
					}
				this.handlerThread.interrupt();
				this.handlerThread = null;
				this.running = false;
			}
		}
	}

	/*******************************************************************************************************************
	 * Listener thread. Listen for incoming requests and put to clients queue.
	 ******************************************************************************************************************/
	protected final class RadiusChannelListener implements ServerChannelListener {
		/**
		 * @see org.dicr.radius.channel.ServerChannelListener#requestReceived(org.dicr.radius.channel.ClientRequest)
		 */
		@Override
        public void requestReceived(final ClientRequest request) {
			if (request == null) throw new IllegalArgumentException("null request");
			DefaultRadiusServer.this.getRequestsQueue().putRequest(request);
		}
	}

	/*******************************************************************************************************************
	 * Requests handler thread. handle requests from queue and send replies
	 ******************************************************************************************************************/
	protected final class HandlerThread extends Thread {
		/** Constructor. */
		protected HandlerThread() {
			super("Radius Request Handler");
			this.setDaemon(false);
			this.setPriority(Thread.MAX_PRIORITY - 1);
		}

		/** Handle client requests. */
		@Override
		public final void run() {
			DefaultRadiusServer.log.debug("starting radius requests handler thread...");

			// do work
			while (!this.isInterrupted()) {
				ResponsePacket responsePacket = null;
				try {
					// get next request
					final ClientRequest clientRequest = DefaultRadiusServer.this.getRequestsQueue().takeRequest();
					final RequestPacket requestPacket = clientRequest.getRequestPacket();

					// handle request
					final RequestHandler requestsHandler = DefaultRadiusServer.this.getRequestHandler();
					if (requestsHandler == null) DefaultRadiusServer.log.warn("request handler not configured");
					else {
						responsePacket = requestsHandler.handleRequest(requestPacket);
						if (responsePacket == null) DefaultRadiusServer.log.warn("no handler response for request: "
						        + requestPacket);
					}

					// send response
					if (responsePacket != null) {
						// set id and authenticator
						responsePacket.setId(requestPacket.getId());
						responsePacket.setAuthenticator(requestPacket.getAuthenticator());

						// send response
						clientRequest.sendResponse(responsePacket);
					}

				} catch (final ChannelException ex) {
					DefaultRadiusServer.log.error("error sending response " + responsePacket, ex);
				} catch (final IncorrectRequestException ex) {
					DefaultRadiusServer.log.debug("incorrect request: " + ex.getLocalizedMessage());
				} catch (final RequestHandlerException ex) {
					DefaultRadiusServer.log.error("handler error", ex);
				} catch (final InterruptedException e) {
					DefaultRadiusServer.log.debug("radius requests handler thread interrupted");
					break;
				} catch (final Throwable th) {
					DefaultRadiusServer.log.fatal("unexpected error", th);
				}
			}
			DefaultRadiusServer.log.debug("radius request handler thread finished");
		}
	}

	/**
	 * @see javax.management.MBeanRegistration#preRegister(javax.management.MBeanServer, javax.management.ObjectName)
	 */
	@Override
    public final ObjectName preRegister(final MBeanServer server, final ObjectName name) throws Exception {
		return name != null ? name : ObjectName.getInstance(DefaultRadiusServer.DEFAULT_MBEAN_NAME);
	}

	/**
	 * @see javax.management.MBeanRegistration#postRegister(java.lang.Boolean)
	 */
	@Override
    public final void postRegister(final Boolean registrationDone) {
		if (registrationDone.booleanValue()) this.startServer();
	}

	/**
	 * @see javax.management.MBeanRegistration#preDeregister()
	 */
	@Override
    public final void preDeregister() throws Exception {
		this.stopServer();
	}

	/**
	 * @see javax.management.MBeanRegistration#postDeregister()
	 */
	@Override
    public final void postDeregister() {
	// nop
	}
}
