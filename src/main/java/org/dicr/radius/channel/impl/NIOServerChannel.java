/**
 * NIOServerChannel.java 09.11.2006
 */
package org.dicr.radius.channel.impl;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import org.apache.log4j.*;
import org.dicr.radius.channel.*;
import org.dicr.radius.codec.*;
import org.dicr.radius.codec.impl.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;
import org.dicr.radius.server.impl.*;

/**
 * NonBlocking Server Channel.
 * <P>
 * This server channel use NonBlokingIO {@link DatagramChannel} to read packets from {@link DatagramSocket}. It can be
 * used by {@link DefaultRadiusServer} to listen many ports simulaniusly.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061109
 */
public class NIOServerChannel implements ServerChannel {
	/** Logger */
	protected static final Logger log = Logger.getLogger(NIOServerChannel.class);

	/** Listeners */
	private final Set<ServerChannelListener> listeners = new HashSet<ServerChannelListener>();

	/** Ports */
	private final Set<InetSocketAddress> addresses = new HashSet<InetSocketAddress>();

	/** Shared secrets */
	private final Map<InetAddress, String> secrets = new Hashtable<InetAddress, String>();

	/** Codec */
	private RadiusCodec codec = new RFCCodec();

	/** Socket listener thread */
	private SocketListenerThread thread = null;

	/**
	 * Constructor
	 */
	public NIOServerChannel() {
		super();
	}

	/**
	 * Set shared secrets.
	 * <P>
	 * Shared secrets used to encode/decode packets by codec.
	 * </P>
	 * 
	 * @param sharedSecrets shared secrets for communicate with clients. Key is the hostname of client and value is se
	 *            secret.
	 * @throws UnknownHostException if client host resolve error
	 */
	public void setSharedSecrets(final Map<String, String> sharedSecrets) throws UnknownHostException {
		if (sharedSecrets == null) throw new IllegalArgumentException("null shared secrets");
		synchronized (this) {
			final Iterator<String> keyIterator = sharedSecrets.keySet().iterator();
			while (keyIterator.hasNext()) {
				final String addr = keyIterator.next();
				if (addr == null) throw new IllegalArgumentException("null address in map");
				final String secret = sharedSecrets.get(addr);
				if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("empty secret in map");
				this.secrets.put(InetAddress.getByName(addr), secret);
			}
		}
		NIOServerChannel.log.debug("configured " + sharedSecrets.size() + " shared secrets");
	}

	/**
	 * Set shared secret
	 * 
	 * @param address host name or ip-address
	 * @param secret shared secret to use with this address
	 * @throws UnknownHostException host name is unknown
	 */
	public void setSharedSecret(final String address, final String secret) throws UnknownHostException {
		if (address == null) throw new IllegalArgumentException("null address");
		if (secret == null) throw new IllegalArgumentException("null shared secret");
		final InetAddress addr = InetAddress.getByName(address);
		synchronized (this) {
			this.secrets.put(addr, secret);
		}
		NIOServerChannel.log.debug("configured shared secret for address: " + address);
	}

	/**
	 * Return secret for address.
	 * 
	 * @param address address of client
	 * @return shared secret for this client
	 */
	protected String getSharedSecret(final InetAddress address) {
		if (address == null) throw new IllegalArgumentException("null address");
		synchronized (this) {
			return this.secrets.get(address);
		}
	}

	/**
	 * Set listening addresses
	 * <P>
	 * Must restart to activate changes.
	 * </P>
	 * 
	 * @param listenAdresses socket addresses to listen
	 */
	public void setListenAddresses(final Set<InetSocketAddress> listenAdresses) {
		if (listenAdresses == null) throw new IllegalArgumentException("null addresses");
		synchronized (this) {
			this.addresses.clear();
			this.addresses.addAll(listenAdresses);
		}
		NIOServerChannel.log.debug("configured " + listenAdresses.size() + " listening addresses");
	}

	/**
	 * Return listen addresses
	 * 
	 * @return socket addresses which listen
	 */
	public Set<InetSocketAddress> getListenAddresses() {
		synchronized (this) {
			return new HashSet<InetSocketAddress>(this.addresses);
		}
	}

	/**
	 * Set codec.
	 * 
	 * @param radiusCodec codec to encode/decode packets
	 */
	public void setCodec(final RadiusCodec radiusCodec) {
		if (radiusCodec == null) throw new IllegalArgumentException("null codec");
		synchronized (this) {
			this.codec = radiusCodec;
		}
		NIOServerChannel.log.debug("configured radius codec: " + radiusCodec);
	}

	/**
	 * Return codec.
	 * 
	 * @return codec with used to encode/decode packets
	 */
	public RadiusCodec getCodec() {
		synchronized (this) {
			return this.codec;
		}
	}

	/**
	 * Add listener to this channel
	 * 
	 * @see org.dicr.radius.channel.ServerChannel#addListener(org.dicr.radius.channel.ServerChannelListener)
	 */
	public void addListener(final ServerChannelListener listener) {
		if (listener == null) throw new IllegalArgumentException("null listener");
		synchronized (this.listeners) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Remove listener from this channel
	 * 
	 * @param listener listener to remove
	 */
	public void removeListener(final ServerChannelListener listener) {
		if (listener == null) throw new IllegalArgumentException("null listener");
		synchronized (this.listeners) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * Fire client request to listeners
	 * 
	 * @param request request to fire
	 */
	protected void fireClientRequest(final NIOClientRequest request) {
		if (request == null) throw new IllegalArgumentException("null request");
		synchronized (this) {
			for (final ServerChannelListener listener : this.listeners)
				try {
					listener.requestReceived(request);
				} catch (final Throwable th) {
					NIOServerChannel.log.error("listener cause error on client request", th);
				}
		}
	}

	/**
	 * Check if channel is running
	 * 
	 * @return true if running
	 * @see org.dicr.radius.channel.ServerChannel#isRunning()
	 */
	public boolean isRunning() {
		synchronized (this) {
			return this.thread != null && this.thread.isAlive();
		}
	}

	/**
	 * Start channel
	 * 
	 * @see org.dicr.radius.channel.ServerChannel#startChannel()
	 */
	public void startChannel() {
		synchronized (this) {
			if (this.isRunning()) NIOServerChannel.log.debug("server channel already running");
			else {
				this.thread = new SocketListenerThread();
				this.thread.start();
			}
		}
	}

	/**
	 * Stop channel
	 * 
	 * @see org.dicr.radius.channel.ServerChannel#stopChannel()
	 */
	public synchronized void stopChannel() {
		synchronized (this) {
			if (!this.isRunning()) NIOServerChannel.log.debug("server channel is already stopped");
			else {
				this.thread.interrupt();
				this.thread = null;
			}
		}
	}

	/**
	 * Send response to client
	 * 
	 * @param responsePacket response packet to send
	 * @param clientRequest request for which response is sending
	 * @throws ChannelException error in channel
	 */
	protected synchronized void sendResponse(final ResponsePacket responsePacket, final NIOClientRequest clientRequest) throws ChannelException {
		if (responsePacket == null) throw new IllegalArgumentException("null response");
		if (clientRequest == null) throw new IllegalArgumentException("null request");
		if (clientRequest.getServerChannel() != this) throw new IllegalArgumentException(
		        "clientRequest generated by other server channel");

		if (NIOServerChannel.log.isTraceEnabled()) NIOServerChannel.log.trace("sending response: " + responsePacket
		        + " to client " + clientRequest.getClientAddress() + ", delay="
		        + (System.currentTimeMillis() - clientRequest.getTimeStamp()) + "ms");

		Selector selector = null;

		try {
			// prepare data
			final byte[] data = new byte[this.getCodec().getPacketMaxLength()];
			final ByteBuffer buf = ByteBuffer.wrap(data);
			buf.put(this.getCodec().encodeResponse(responsePacket, clientRequest.getRequestPacket(),
			        clientRequest.getSecret()));
			buf.flip();

			// register selector
			selector = Selector.open();
			clientRequest.getDataChannel().register(selector, SelectionKey.OP_WRITE);

			// write data
			while (buf.hasRemaining()) {
				if (selector.select() < 1) continue;
				final Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
				while (keysIterator.hasNext() && buf.hasRemaining()) {
					final SelectionKey key = keysIterator.next();
					if (!key.isWritable()) continue;
					keysIterator.remove();
					final DatagramChannel channel = (DatagramChannel) key.channel();
					channel.send(buf, clientRequest.getClientAddress());
				}
			}
		} catch (final IOException ex) {
			throw new ChannelException("error sending response " + responsePacket, ex);
		} catch (final CodecException ex) {
			throw new ChannelException("error sending response " + responsePacket, ex);
		} finally {
			if (selector != null) try {
				selector.close();
			} catch (final Throwable th) {
				// NOP
			}
		}
	}

	/**
	 * Socket listener thread
	 */
	public class SocketListenerThread extends Thread {
		/**
		 * Constructor
		 */
		public SocketListenerThread() {
			super("Radius NIOServerChannel");
			this.setDaemon(false);
			this.setPriority(Thread.NORM_PRIORITY + 1);
		}

		/**
		 * Run socket listener
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			NIOServerChannel.log.debug("starting radius server NIO socket channel");

			// channel selector
			Selector selector = null;
			final Collection<DatagramChannel> openedChannels = new ArrayList<DatagramChannel>();

			try {
				// create selector
				selector = Selector.open();

				// open channels on ports
				if (NIOServerChannel.this.getListenAddresses().isEmpty()) NIOServerChannel.log.error("no addresses configured to listen for requests");
				else for (final SocketAddress addr : NIOServerChannel.this.getListenAddresses())
					try {
						final DatagramChannel channel = DatagramChannel.open();
						channel.configureBlocking(false);
						channel.socket().setReceiveBufferSize(
						        NIOServerChannel.this.getCodec().getPacketMaxLength() * 10);
						channel.socket().setSendBufferSize(NIOServerChannel.this.getCodec().getPacketMaxLength() * 10);
						channel.socket().setTrafficClass(0x1C);
						channel.socket().bind(addr);
						channel.register(selector, SelectionKey.OP_READ);
						openedChannels.add(channel);
						NIOServerChannel.log.info("listening for radius requests on socket: " + addr);
					} catch (final IOException ex) {
						NIOServerChannel.log.error("error opening channel on socket " + addr, ex);
					}

				final byte[] data = new byte[NIOServerChannel.this.getCodec().getPacketMaxLength()];
				final ByteBuffer buffer = ByteBuffer.wrap(data);

				// do the work
				while (!this.isInterrupted())
					try {
						if (selector.select() < 1) continue;
						// iterate over events
						final Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
						while (keysIterator.hasNext()) {
							// ket ready key
							final SelectionKey key = keysIterator.next();
							if (!key.isReadable()) continue; // write threads can register other keys
							keysIterator.remove();

							// receive datagram
							buffer.clear();
							final DatagramChannel channel = (DatagramChannel) key.channel();
							final InetSocketAddress addr = (InetSocketAddress) channel.receive(buffer);
							if (addr == null) {
								NIOServerChannel.log.error("null address of received UDP packet");
								continue;
							}

							// check received data length
							buffer.flip();
							if (buffer.limit() < NIOServerChannel.this.getCodec().getPacketHeaderLength()) {
								NIOServerChannel.log.warn("ignoring short data from address: " + addr);
								continue;
							}

							// find shred sharedSecret
							final String secret = NIOServerChannel.this.getSharedSecret(addr.getAddress());
							if (secret == null) {
								NIOServerChannel.log.warn("no shared shared secret for address: " + addr);
								continue;
							}

							// decode packet
							final RequestPacket packet = NIOServerChannel.this.getCodec().decodeRequest(data, secret);
							if (NIOServerChannel.log.isTraceEnabled()) NIOServerChannel.log.trace("received request: "
							        + packet + " from address: " + addr);

							// fire request to listeners
							NIOServerChannel.this.fireClientRequest(new NIOClientRequest(NIOServerChannel.this, packet,
							        addr, secret, channel));
						}
					} catch (final ClosedByInterruptException ex) {
						NIOServerChannel.log.debug("radius server socket channel thread interrupted");
						break;
					} catch (final CodecException ex) {
						NIOServerChannel.log.warn("error decoding packet", ex);
					} catch (final IOException ex) {
						NIOServerChannel.log.warn("I/O error while receiving packet", ex);
					} catch (final Throwable ex) {
						NIOServerChannel.log.error("unknown error occured", ex);
					}
			} catch (final IOException ex) {
				NIOServerChannel.log.fatal("error starting socket listener thread", ex);
			} finally {
				// close selector
				if (selector != null) try {
					selector.close();
				} catch (final Throwable th) {
					// NOP
				}
				// close channels
				for (final DatagramChannel channel : openedChannels) {
					NIOServerChannel.log.debug("closing channel on port " + channel.socket().getLocalPort());
					try {
						channel.close();
					} catch (final Throwable th) {
						// NOP
					}
				}
				NIOServerChannel.log.info("radius server NIO socket channel stopped");
			}
		}
	}
}
