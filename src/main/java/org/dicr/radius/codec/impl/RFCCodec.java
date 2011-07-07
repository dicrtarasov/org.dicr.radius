/**
 * RFCCodec.java 07.11.2006
 */
package org.dicr.radius.codec.impl;

import java.nio.*;
import java.security.*;
import java.util.*;

import org.dicr.radius.attribute.*;
import org.dicr.radius.codec.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;
import org.dicr.radius.util.*;
import org.dicr.util.data.*;

/**
 * RFC Packet Codec.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061107
 */
public class RFCCodec implements RadiusCodec {

	/** Maximum packet length */
	public static final int PACKET_MAX_LENGTH = 4096;

	/** Length of packet header */
	public static final int PACKET_HEADER_LENGTH = 20;

	/** Attribute header length */
	// TODO: depends on codec
	public static final int ATTRIBUTE_HEADER_LENGTH = 2;

	/** Maximum value length */
	// TODO: depends on codec
	public static final int ATTRIBUTE_VALUE_MAX_LENGTH = PACKET_MAX_LENGTH - PACKET_HEADER_LENGTH
			- ATTRIBUTE_HEADER_LENGTH;

	/**
     * @see org.dicr.radius.codec.RadiusCodec#getPacketMaxLength()
     */
	@Override
    public int getPacketMaxLength() {
		return RFCCodec.PACKET_MAX_LENGTH;
	}

	/**
     * @see org.dicr.radius.codec.RadiusCodec#getPacketHeaderLength()
     */
	@Override
    public int getPacketHeaderLength() {
		return RFCCodec.PACKET_HEADER_LENGTH;
	}

	/**
     * Make responce authenticator.
     * 
     * @param packetData byte[] packet data
     * @param secret shared secret
     * @param requestAuthenticator byte[16] request authenticator.
     * @return byte[16] responce authenticator for packet data.
     */
	private final static byte[] calculateResponceAuthenticator(byte[] packetData, int length, String secret, RequestAuthenticator requestAuthenticator) {
		if (packetData == null || packetData.length < 1) throw new IllegalArgumentException("empty data");
		if (length < RFCCodec.PACKET_HEADER_LENGTH || length > RFCCodec.PACKET_MAX_LENGTH || length > packetData.length) throw new IllegalArgumentException(
				"length: " + length);
		if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("empty secret");
		if (requestAuthenticator == null) throw new IllegalArgumentException("null authenticator");
		// prepare message digest
		MessageDigest md5 = MD5.getMD5Digest();
		// md5 of code, id, length
		md5.update(packetData, 0, 4);
		// md5 of request authenticator
		md5.update(requestAuthenticator.getValue());
		// md5 of packet data
		if (length > RFCCodec.PACKET_HEADER_LENGTH) md5.update(packetData, RFCCodec.PACKET_HEADER_LENGTH, length
				- RFCCodec.PACKET_HEADER_LENGTH);
		// md5 of secret
		md5.update(ByteUtils.toBytes(secret));
		// get result
		return md5.digest();
	}

	/**
     * Decode attribute from buffer.
     * 
     * @param vendor vendor of attribute
     * @param buf buffer to encode to
     * @param secret shared secret
     * @param requestAuthenticator request authenticator
     * @return decoded attribute
     * @throws CodecException if encoding error occur
     */
	private static RadiusAttribute decodeAttribute(int vendor, ByteBuffer buf, String secret, RequestAuthenticator requestAuthenticator) throws CodecException {
		if (vendor < 0 || vendor > AttributeType.VENDOR_MAX) throw new IllegalArgumentException("vendorCode: " + vendor);
		if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("empty secret");
		if (buf == null) throw new IllegalArgumentException("null input buffer");
		// result
		RadiusAttribute attribute = null;
		try {
			// Code
			AttributeType type = new AttributeType(vendor, ByteUtils.unsigned(buf.get()));
			attribute = AttributesFactory.createAttribute(type);

			// Length
			int length = ByteUtils.unsigned(buf.get());
			if (length < 0 || length > RFCCodec.ATTRIBUTE_VALUE_MAX_LENGTH) throw new CodecException(
					"incorrect attribute length: " + length);

			// Value
			byte data[] = new byte[length - RFCCodec.ATTRIBUTE_HEADER_LENGTH];
			buf.get(data);
			attribute.decodeValue(data, secret, requestAuthenticator);
		} catch (BufferUnderflowException ex) {
			throw new CodecException("error decoding attribute - end of data", ex);
		} catch (IllegalArgumentException ex) {
			throw new CodecException(ex);
		}
		return attribute;
	}

	/**
     * Decode packet.
     * <UL>
     * <LI>To decode instance of <CODE>{@link RequestPacket}</CODE> set <CODE>request</CODE> argument to null.</LI>
     * <LI>To decode instance of <CODE>{@link ResponsePacket}</CODE> set <CODE>request</CODE> argument to instance
     * of <CODE>{@link RequestPacket}</CODE>, for which to decode response.</LI>
     * </UL>
     * <P>
     * If decoded packet is not of awaiting instance, then <CODE>{@link ChannelException}</CODE> is thrown.
     * </P>
     * <P>
     * If <CODE>id<CODE> of decoded <CODE>{@link ResponsePacket}</CODE> instance does not match <CODE>id</CODE> of
     * <CODE>request</CODE> packet, or it's <CODE>authenticator</CODE> not pass verification agains requests's
     * authenticator, then <CODE>{@link ChannelException}</CODE> is thrown.
     * </P>
     * 
     * @param data encoded packet data
     * @param request request packet for decoding {@link ResponsePacket} or null for decoding {@link RequestPacket}.
     * @param secret shared secret
     * @return decoded packet (instance of {@link RequestPacket} packet if this parameter is null or instance of
     *         {@link ResponsePacket} packet if this set to instance of a {@link RequestPacket})
     * @throws CodecException TODO
     */
	@SuppressWarnings("null")
	private static final RadiusPacket decodePacket(byte[] data, RequestPacket request, String secret) throws CodecException {
		if (data == null) throw new IllegalArgumentException("null data");
		if (data.length < RFCCodec.PACKET_HEADER_LENGTH) throw new CodecException("incorrect data length: "
				+ data.length);
		if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("empty secret");
		RadiusPacket packet = null;
		try {
			// check data length
			int length = (ByteUtils.unsigned(data[2]) << 8) + ByteUtils.unsigned(data[3]);
			if (length < RFCCodec.PACKET_HEADER_LENGTH || length > RFCCodec.PACKET_MAX_LENGTH || length > data.length) throw new CodecException(
					"length field=" + length);

			// create buffer
			ByteBuffer buf = ByteBuffer.wrap(data, 0, length);

			// CODE
			int code = ByteUtils.unsigned(buf.get());
			packet = PacketFactory.createPacket(code);
			if (request == null) {
				if (!(packet instanceof RequestPacket)) throw new CodecException("received packet " + packet.getClass()
						+ " is not a request packet");
			} else if (!(packet instanceof ResponsePacket)) throw new CodecException("received packet "
					+ packet.getClass() + " is not a reapone packet");

			// ID
			int id = ByteUtils.unsigned(buf.get());
			if (packet instanceof ResponsePacket && id != request.getId()) throw new CodecException(
					"received response id=" + id + " does not match request id=" + request.getId());
			packet.setId(id);

			// Length - skip, already decoded before
			buf.getShort();

			// RequestAuthenticator
			byte[] authenticatorData = new byte[RequestAuthenticator.LENGTH];
			buf.get(authenticatorData);
			RequestAuthenticator authenticator = new RequestAuthenticator(authenticatorData);
			if (packet instanceof ResponsePacket) {
				byte[] responseAuthenticator = RFCCodec.calculateResponceAuthenticator(data, length, secret,
						request.getAuthenticator());
				if (!Arrays.equals(authenticatorData, responseAuthenticator)) throw new CodecException(
						"incorrect response authenticator");
			}
			packet.setAuthenticator(authenticator);

			// Attributes
			AttributesList attribs = packet.getAttributes();
			while (buf.remaining() > 0)
				attribs.add(RFCCodec.decodeAttribute(0, buf, secret, authenticator));
		} catch (BufferUnderflowException ex) {
			throw new CodecException(ex);
		} catch (IndexOutOfBoundsException ex) {
			throw new CodecException(ex);
		} catch (IllegalArgumentException ex) {
			throw new CodecException(ex);
		}
		return packet;
	}

	/**
     * Encode attrubyte to buffer
     * 
     * @param attr attribute to encode
     * @param buf buffer to encode to
     * @param secret shared secret
     * @param requestAuthenticator request authenticator
     * @throws CodecException encoding error
     */
	public static void encodeAttribute(RadiusAttribute attr, ByteBuffer buf, String secret, RequestAuthenticator requestAuthenticator) throws CodecException {
		if (attr == null) throw new IllegalArgumentException("null attribute");
		if (buf == null) throw new IllegalArgumentException("null input buffer");
		if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("empty secret");
		if (requestAuthenticator == null) throw new IllegalArgumentException("null authenticator");

		// encode value
		byte[] data = attr.encodeValue(secret, requestAuthenticator);
		if (data == null) throw new IllegalStateException("not initialized");
		if (data.length > RFCCodec.ATTRIBUTE_VALUE_MAX_LENGTH) throw new CodecException("attribute data length: "
				+ data.length + " exceed maximum length: " + RFCCodec.ATTRIBUTE_VALUE_MAX_LENGTH);
		try {
			// Type
			buf.put((byte) attr.getType().getTypeCode());

			// Length
			buf.put((byte) (data.length + RFCCodec.ATTRIBUTE_HEADER_LENGTH));

			// Value
			buf.put(data);
		} catch (BufferOverflowException ex) {
			throw new CodecException("error encoding attribute '" + attr + "' - data buffer overflow", ex);
		}
	}

	/**
     * Encode packet.
     * <P>
     * If packet is instance of <CODE>{@link RequestPacket}</CODE>, then simply encode all fields of it and <CODE>requestPacket</CODE>
     * argument must be <CODE>null</CODE>.
     * </P>
     * <P>
     * If packet is instance of <CODE>{@link ResponsePacket}</CODE>, argument <CODE>requestPacket</CODE> must be
     * set to instance of a <CODE>{@link RequestPacket}</CODE> for wich responce is. Before encoding, <CODE>id</CODE>
     * from <CODE>requestPacket</CODE> is set to <CODE>packet</CODE>. After encoding, the response authenticator
     * calculated, based on authenticator of the <CODE>requestPacket</CODE> and <CODE>authenticator</CODE> of
     * response <CODE>packet</CODE> will set to calculated response authenticator.
     * </P>
     * 
     * @param packet packet to encode - instance of {@link RequestPacket} or {@link ResponsePacket}.
     * @param requestPacket instance of {@link RequestPacket} if encoding packet is instance of {@link ResponsePacket}
     *            or <CODE>null</CODE> for other.
     * @param sharedSecret shared secret
     * @return encoded packet data
     * @throws CodecException TODO
     */
	@SuppressWarnings("null")
	private static final byte[] encodePacket(RadiusPacket packet, RequestPacket requestPacket, String sharedSecret) throws CodecException {
		if (packet == null) throw new IllegalArgumentException("null packet");
		if (packet instanceof ResponsePacket) {
			if (requestPacket == null) throw new IllegalArgumentException(
					"request argument must be not null for response packets");
		} else if (packet instanceof RequestPacket) {
			if (requestPacket != null) throw new IllegalArgumentException(
					"request argument must be null for request packets");
		} else throw new IllegalArgumentException("unknown packet type [ResponsePacket, RequestPacket] of '"
				+ packet.getClass() + "'");
		if (sharedSecret == null || sharedSecret.isEmpty()) throw new IllegalArgumentException("empty secret");

		// result
		byte[] result = null;

		try {
			// buffer to encode
			byte[] data = new byte[RFCCodec.PACKET_MAX_LENGTH];
			ByteBuffer buf = ByteBuffer.wrap(data);
			buf.clear();

			// Code
			buf.put((byte) packet.getCode());

			// ID
			if (requestPacket != null) packet.setId(requestPacket.getId());
			buf.put((byte) packet.getId());

			// Length - skip for later calculation
			buf.putShort((short) 0);

			// RequestAuthenticator
			buf.put(packet.getAuthenticator().getValue());

			// attributes
			List<? extends RadiusAttribute> attrs = packet.getAttributes().toList();
			for (RadiusAttribute attr : attrs)
				RFCCodec.encodeAttribute(attr, buf, sharedSecret, packet.getAuthenticator());

			// Afterwrite calculated length
			buf.flip();
			int length = buf.limit();
			if (length > RFCCodec.PACKET_MAX_LENGTH) throw new CodecException("too lage radius packet size: " + length);
			buf.putShort(2, (short) length);

			// Afterwrite calculated authenticator
			if (packet instanceof ResponsePacket) {
				byte[] responseAuthenticator = RFCCodec.calculateResponceAuthenticator(data, length, sharedSecret,
						requestPacket.getAuthenticator());
				packet.setAuthenticator(new RequestAuthenticator(responseAuthenticator));
				buf.position(4);
				buf.put(responseAuthenticator);
			}

			// copy result
			result = new byte[length];
			System.arraycopy(data, 0, result, 0, length);
		} catch (BufferOverflowException ex) {
			throw new CodecException("to lage packet size", ex);
		} catch (IndexOutOfBoundsException ex) {
			throw new CodecException(ex);
		} catch (IllegalArgumentException ex) {
			throw new CodecException(ex);
		}
		return result;
	}

	/**
     * Encode request packet.
     * 
     * @param packet request packet to encode
     * @param secret shared secret
     * @return encoded data of packet
     * @see org.dicr.radius.codec.RadiusCodec#encodeRequest(org.dicr.radius.packet.RequestPacket, java.lang.String)
     */
	@Override
    public final byte[] encodeRequest(RequestPacket packet, String secret) throws CodecException {
		if (packet == null) throw new IllegalArgumentException("null packet");
		if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("empty secret");
		return RFCCodec.encodePacket(packet, null, secret);
	}

	/**
     * Encode response packet.
     * <P>
     * The <CODE>id</CODE> of response <CODE>packet</CODE> is set to <CODE>id</CODE> of <CODE>requestPacket</CODE>
     * and authenticator of response <CODE>packet</CODE> calculated and updated, based on authenticator of <CODE>requestPacket</CODE>.
     * </P>
     * 
     * @param packet response packet to encode
     * @param request request packet of this response
     * @param secret shared secret
     * @return encoded packet data
     * @see org.dicr.radius.codec.RadiusCodec#encodeResponse(org.dicr.radius.packet.ResponsePacket,
     *      org.dicr.radius.packet.RequestPacket, java.lang.String)
     */
	@Override
    public final byte[] encodeResponse(ResponsePacket packet, RequestPacket request, String secret) throws CodecException {
		if (packet == null) throw new IllegalArgumentException("null response packet");
		if (request == null) throw new IllegalArgumentException("null request packet");
		if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("empty secret");
		return RFCCodec.encodePacket(packet, request, secret);
	}

	/**
     * Decode request packet.
     * <P>
     * If decoded packet is not instance of <CODE>{@link RequestPacket}</CODE>, then <CODE>{@link ChannelException}
     * is thrown.
     * </P>
     * 
     * @param data encoded packet data
     * @param secret shared secret
     * @return decoded request packet
     * @see org.dicr.radius.codec.RadiusCodec#decodeRequest(byte[], java.lang.String)
     */
	@Override
    public RequestPacket decodeRequest(byte[] data, String secret) throws CodecException {
		if (data == null) throw new IllegalArgumentException("null data");
		if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("empty secret");
		return (RequestPacket) RFCCodec.decodePacket(data, null, secret);
	}

	/**
     * Decode response packet.
     * <P>
     * It check that <CODE>data</CODE> is encoded instance of <CODE>{@link ResponsePacket}</CODE> and the <CODE>id</CODE>
     * of decoded packet match <CODE>id</CODE> of the <CODE>requestPacket</CODE>, verify <CODE>authenticator</CODE>
     * of decoded packet agains <CODE>authenticator</CODE> of <CODE>requestPacket</CODE>. If some condition is
     * fail, the <CODE>{@link ChannelException} will thrown.
     * </P>
     * 
     * @param data encoded packet data
     * @param request request packet for which response is decoding
     * @param secret shared secret
     * @return decoded response packet which match request packet
     * @see org.dicr.radius.codec.RadiusCodec#decodeResponse(byte[], org.dicr.radius.packet.RequestPacket,
     *      java.lang.String)
     */
	@Override
    public ResponsePacket decodeResponse(byte[] data, RequestPacket request, String secret) throws CodecException {
		if (data == null) throw new IllegalArgumentException("null data");
		if (request == null) throw new IllegalArgumentException("null request");
		if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("null secret");
		return (ResponsePacket) RFCCodec.decodePacket(data, request, secret);
	}
}
