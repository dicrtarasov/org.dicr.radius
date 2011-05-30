/**
 * RadiusCodec.java 07.11.2006
 */
package org.dicr.radius.codec;

import org.dicr.radius.exc.*;
import org.dicr.radius.packet.*;

/**
 * Packet codec
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061107
 */
public interface RadiusCodec {
	/**
     * Encode request packet
     * 
     * @param request request packet to encode
     * @param secret shared secret
     * @return encoded value as bytes
     * @throws CodecException if encoding error occur
     */
	public byte[] encodeRequest(RequestPacket request, String secret) throws CodecException;

	/**
     * Encode response packet
     * 
     * @param response response packet
     * @param request request packet of response
     * @param secret shared secret
     * @return encoded data
     * @throws CodecException if encoding error occur
     */
	public byte[] encodeResponse(ResponsePacket response, RequestPacket request, String secret) throws CodecException;

	/**
     * Decode request packet
     * 
     * @param data encoded data
     * @param secret shared secret
     * @return request packet
     * @throws CodecException if decoding error occur
     */
	public RequestPacket decodeRequest(byte[] data, String secret) throws CodecException;

	/**
     * Decode response packet
     * 
     * @param data encoded data
     * @param request request packet of response
     * @param secret shared secret
     * @return decoded response
     * @throws CodecException if decoding error occur
     */
	public ResponsePacket decodeResponse(byte[] data, RequestPacket request, String secret) throws CodecException;

	/**
     * Return maximum length of packet
     * 
     * @return maximum length of packet
     */
	public int getPacketMaxLength();

	/**
     * Return p[acket header length
     * 
     * @return packet header length
     */
	public int getPacketHeaderLength();
}
