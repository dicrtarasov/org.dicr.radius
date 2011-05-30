/**
 * NIOClientChannel.java 11.11.2006
 */
package org.dicr.radius.channel.impl;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import org.apache.log4j.*;
import org.dicr.radius.*;
import org.dicr.radius.channel.*;
import org.dicr.radius.codec.*;
import org.dicr.radius.codec.impl.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;

/**
 * NIO Client Channel
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061111
 */
public class NIOClientChannel implements ClientChannel {
	/** Logger */
	private static final Logger log = Logger.getLogger(NIOClientChannel.class);

	/** Last request id counter */
	private static int lastId = 0;

	/** Radius Server address */
	private SocketAddress address = new InetSocketAddress(RadiusConstants.PORT_ACCESS);

	/** Shared secret */
	private String secret = null;

	/** Request timeout */
	private long requestTimeout = 10000;

	/** Retries count */
	private int retriesCount = 3;

	/** Codec */
	private RadiusCodec codec = new RFCCodec();

	/**
	 * Constructor
	 */
	public NIOClientChannel() {
		super();
	}

	/**
	 * Set address
	 * 
	 * @param addr socket address of radius server
	 */
	public void setAddress(final SocketAddress addr) {
		if (addr == null) throw new IllegalArgumentException("null address");
		synchronized (this) {
			this.address = addr;
		}
		NIOClientChannel.log.debug("configured address: " + addr);
	}

	/**
	 * Return address
	 * <P>
	 * Default is local address with port {@link RadiusConstants#PORT_ACCESS}
	 * </P>
	 * 
	 * @return socket address of radius server
	 */
	public SocketAddress getAddress() {
		synchronized (this) {
			return this.address;
		}
	}

	/**
	 * Set shared secret
	 * 
	 * @param sharedSecret shared secret for radius server
	 */
	public void setSecret(final String sharedSecret) {
		if (sharedSecret == null || sharedSecret.isEmpty()) throw new IllegalArgumentException("null secret");
		synchronized (this) {
			this.secret = sharedSecret;
		}
		NIOClientChannel.log.debug("shared secret configured");
	}

	/**
	 * Set request timeout
	 * 
	 * @param timeout request timeout in milliseconds
	 */
	public void setRequestTimeout(final long timeout) {
		if (timeout < 0) throw new IllegalArgumentException("timeout: " + timeout);
		synchronized (this) {
			this.requestTimeout = timeout;
		}
		NIOClientChannel.log.debug("configured request timeout: " + timeout);
	}

	/**
	 * Return request timeout
	 * 
	 * @return request timeout in milliseconds
	 */
	public long getRequestTimeout() {
		synchronized (this) {
			return this.requestTimeout;
		}
	}

	/**
	 * Set retries count
	 * 
	 * @param count retries count
	 */
	public void setRetriesCount(final int count) {
		if (count < 0) throw new IllegalArgumentException("count: " + count);
		synchronized (this) {
			this.retriesCount = count;
		}
		NIOClientChannel.log.debug("configured retries count: " + count);
	}

	/**
	 * Return retries count
	 * 
	 * @return retries count
	 */
	public int getRetriesCount() {
		synchronized (this) {
			return this.retriesCount;
		}
	}

	/**
	 * Set codec
	 * 
	 * @param radiusCodec radius codec to encode/decode packets
	 */
	public void setCodec(final RadiusCodec radiusCodec) {
		if (radiusCodec == null) throw new IllegalArgumentException("null codec");
		synchronized (this) {
			this.codec = radiusCodec;
		}
		NIOClientChannel.log.debug("configured radius codec: " + radiusCodec);
	}

	/**
	 * Return radius codec
	 * <P>
	 * Default is {@link RFCCodec}
	 * </P>
	 * 
	 * @return radius codec which used to encode/decode packets
	 */
	public RadiusCodec getCodec() {
		synchronized (this) {
			return this.codec;
		}
	}

	/**
	 * Return next request id
	 * 
	 * @return next request id to send
	 */
	private static int getNextId() {
		synchronized (NIOClientChannel.class) {
			NIOClientChannel.lastId = (NIOClientChannel.lastId + 1) % 255;
			NIOClientChannel.log.trace("increment request id counter: " + NIOClientChannel.lastId);
			return NIOClientChannel.lastId;
		}
	}

	/**
	 * Query radius server
	 * <P>
	 * <B>Warning:</B> this methos update <CODE>id</CODE> of <CODE>request</CODE> packet before sending !!!
	 * </P>
	 * <P>
	 * Implementation note: Datagram socket must be not connected, beacause reply from server can be send from other
	 * network adapter address, if server listen 0.0.0.0 address
	 * </P>
	 * 
	 * @param request request to send
	 * @return reply from server
	 * @throws ChannelException if unable to open connection to server, or unable to read valid reply
	 * @throws CodecException if error encoding request packet
	 * @see org.dicr.radius.channel.ClientChannel#query(org.dicr.radius.packet.RequestPacket)
	 */
	@SuppressWarnings("null")
	public synchronized ResponsePacket query(final RequestPacket request) throws ChannelException, CodecException {
		if (request == null) throw new IllegalArgumentException("null request");
		if (this.secret == null) throw new IllegalStateException("shared secret not configured");
		if (this.address == null) throw new IllegalArgumentException("addreess not configured");

		// response
		ResponsePacket response = null;

		// channel and selectors
		DatagramChannel channel = null;
		Selector writeSelector = null;
		Selector readSelector = null;

		try {
			try {
				// connect
				channel = DatagramChannel.open();
				channel.configureBlocking(false);
				channel.socket().setTrafficClass(0x10);
				channel.socket().setSendBufferSize(this.codec.getPacketMaxLength() * 10);
				channel.socket().setReceiveBufferSize(this.codec.getPacketMaxLength() * 10);
				writeSelector = Selector.open();
				channel.register(writeSelector, SelectionKey.OP_WRITE);
				readSelector = Selector.open();
				channel.register(readSelector, SelectionKey.OP_READ);
			} catch (final IOException ex) {
				throw new ChannelException("error opening connection to server", ex);
			}

			// encode request packet
			request.setId(NIOClientChannel.getNextId());
			final byte[] encodedRequest = this.getCodec().encodeRequest(request, this.secret);

			// prepare buffers
			final ByteBuffer sndBuf = ByteBuffer.wrap(encodedRequest);
			final ByteBuffer rcvBuf = ByteBuffer.allocate(this.codec.getPacketMaxLength());

			// try number of retries
			for (int retry = 0; retry < this.retriesCount && response == null; retry++) {
				// send data
				try {
					NIOClientChannel.log.trace("sending request " + request + " to server " + this.address + ", retry "
					        + (retry + 1) + " of " + this.retriesCount);
					sndBuf.rewind();
					while (sndBuf.remaining() > 0) {
						if (writeSelector.select() < 1) continue;
						final Iterator<SelectionKey> iterator = writeSelector.selectedKeys().iterator();
						while (iterator.hasNext() && sndBuf.remaining() > 0) {
							final SelectionKey key = iterator.next();
							if (!key.isWritable()) continue;
							iterator.remove();
							((DatagramChannel) key.channel()).send(sndBuf, this.address);
						}
					}
				} catch (final IOException ex) {
					NIOClientChannel.log.error("error sending request " + request + " to server " + this.address
					        + ", retry " + (retry + 1) + " of " + this.retriesCount, ex);
					continue;
				}

				// read data
				try {
					if (readSelector.select(this.requestTimeout) <= 0) {
						NIOClientChannel.log.warn("timeout waiting reply from server " + this.address + ", retry "
						        + (retry + 1) + " of " + this.retriesCount);
						continue;
					}
					final Iterator<SelectionKey> iterator = readSelector.selectedKeys().iterator();
					while (iterator.hasNext() && response == null) {
						final SelectionKey key = iterator.next();
						if (!key.isReadable()) continue;
						iterator.remove();
						// read packet
						rcvBuf.clear();
						final SocketAddress rcvAddress = ((DatagramChannel) key.channel()).receive(rcvBuf);
						// decode packet
						response = this.getCodec().decodeResponse(rcvBuf.array(), request, this.secret);
						NIOClientChannel.log.trace("received response " + response + " from server " + rcvAddress);
						break;
					}
				} catch (final IOException ex) {
					NIOClientChannel.log.error("error receiving reply from server " + this.address + ", retry "
					        + (retry + 1) + " of " + this.retriesCount, ex);
					continue;
				} catch (final CodecException ex) {
					NIOClientChannel.log.error("error decoding reply from server " + this.address + ", retry "
					        + (retry + 1) + " of " + this.retriesCount, ex);
					continue;
				}
			}
		} finally {
			if (writeSelector != null) try {
				writeSelector.close();
			} catch (final Throwable th) {
				// NOP
			}
			if (readSelector != null) try {
				readSelector.close();
			} catch (final Throwable th) {
				// NOP
			}
			if (channel != null) try {
				channel.close();
			} catch (final Throwable th) {
				// NOP
			}
		}
		if (response == null) throw new RequestTimeoutException("no reply from server " + this.address);
		return response;
	}
}
