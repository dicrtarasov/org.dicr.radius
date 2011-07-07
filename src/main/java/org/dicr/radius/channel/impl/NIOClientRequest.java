package org.dicr.radius.channel.impl;

import java.net.*;
import java.nio.channels.*;

import org.dicr.radius.channel.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;

/**
 * NIO Client Request. Store information about client request in server queue.
 */
public class NIOClientRequest implements ClientRequest {

	/** Server _serverChannel */
	private NIOServerChannel _serverChannel = null;

	/** Request Packet */
	private RequestPacket _requestPacket = null;

	/** Address of client */
	private InetSocketAddress _clientAddress = null;

	/** Created time */
	private final long timestamp = System.currentTimeMillis();

	/** Shared secre */
	private String _secret = null;

	/** Channel from which request was received */
	private DatagramChannel _dataChannel = null;

	/**
	 * Constructor.
	 * 
	 * @param serverChannel server channel
	 * @param requestPacket client request packet
	 * @param clientAddress client address
	 * @param secret client secret
	 * @param dataChannel data channel
	 */
	protected NIOClientRequest(NIOServerChannel serverChannel, RequestPacket requestPacket, InetSocketAddress clientAddress, String secret, DatagramChannel dataChannel) {
		if (serverChannel == null) throw new IllegalArgumentException("null _serverChannel");
		if (requestPacket == null) throw new IllegalArgumentException("null _requestPacket");
		if (clientAddress == null) throw new IllegalArgumentException("null address");
		if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("empty secret");
		if (dataChannel == null) throw new IllegalArgumentException("null data channel");
		this._serverChannel = serverChannel;
		this._requestPacket = requestPacket;
		this._clientAddress = clientAddress;
		this._secret = secret;
		this._dataChannel = dataChannel;
	}

	/**
	 * Return server _serverChannel.
	 * 
	 * @return server _serverChannel, which receive this request
	 * @see org.dicr.radius.channel.ClientRequest#getServerChannel()
	 */
	@Override
    public NIOServerChannel getServerChannel() {
		return this._serverChannel;
	}

	/**
	 * Retur address
	 * 
	 * @return client address
	 * @see org.dicr.radius.channel.ClientRequest#getClientAddress()
	 */
	@Override
    public InetSocketAddress getClientAddress() {
		return this._clientAddress;
	}

	/**
	 * Return _requestPacket
	 * 
	 * @return request _requestPacket
	 * @see org.dicr.radius.channel.ClientRequest#getRequestPacket()
	 */
	@Override
    public RequestPacket getRequestPacket() {
		return this._requestPacket;
	}

	/**
	 * Return time stamp
	 * 
	 * @return time, when request was created
	 * @see org.dicr.radius.channel.ClientRequest#getTimeStamp()
	 */
	@Override
    public long getTimeStamp() {
		return this.timestamp;
	}

	/**
	 * Return channel
	 * 
	 * @return channel from which packet incoming
	 */
	protected DatagramChannel getDataChannel() {
		return this._dataChannel;
	}

	/**
	 * Return cashed shared secret
	 * 
	 * @return secret
	 */
	protected String getSecret() {
		return this._secret;
	}

	/**
	 * @see org.dicr.radius.channel.ClientRequest#sendResponse(org.dicr.radius.packet.ResponsePacket)
	 */
	@Override
    public void sendResponse(ResponsePacket responsePacket) throws ChannelException {
		if (this._requestPacket.getId() != responsePacket.getId()) throw new IllegalStateException(
		        "response id does not match request id");
		this._serverChannel.sendResponse(responsePacket, this);
	}
}
