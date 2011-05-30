package org.dicr.radius.packet;

import java.util.*;

import org.dicr.radius.attribute.*;
import org.dicr.radius.dictionary.*;

/**
 * Radius packet.
 *
 * @author not attributable
 * @version 060623
 */
public abstract class RadiusPacket {
	/** Packet type code */
	private int code = 0;

	/** Packet ID */
	private int id = 0;

	/** RequestAuthenticator */
	private RequestAuthenticator requestAuthenticator = new RequestAuthenticator();

	/** Attributes */
	private final AttributesList attributes = new AttributesList(AttributeType.VENDOR_NONE);

	/**
     * Constructor
     */
	protected RadiusPacket() {
		super();
	}

	/**
     * Constructor
     *
     * @param aCode packet code
     */
	protected RadiusPacket(int aCode) {
		super();
		this.setCode(aCode);
	}

	/**
     * Constructor
     *
     * @param aCode packet code type
     * @param aAuthenticator requestAuthenticator
     */
	protected RadiusPacket(int aCode, RequestAuthenticator aAuthenticator) {
		super();
		this.setCode(aCode);
		this.setAuthenticator(aAuthenticator);
	}

	/**
     * Set packet code
     *
     * @param aCode packet code
     */
	protected void setCode(int aCode) {
		if (aCode < 0 || aCode > 255) throw new IllegalArgumentException("code: " + aCode);
		this.code = aCode;
	}

	/**
     * Return the code of packet.
     *
     * @return code with is determine packet type
     */
	public int getCode() {
		return this.code;
	}

	/**
     * Set id of packet.
     *
     * @param aId id
     */
	public void setId(int aId) {
		if (aId < 0 || aId > 255) throw new IllegalArgumentException("incorrect id: " + aId);
		this.id = aId;
	}

	/**
     * Return id of the packet.
     *
     * @return int
     */
	public int getId() {
		return this.id;
	}

	/**
     * Set RequestAuthenticator. New RequestAuthenticator must generated for a request packets.
     *
     * @param newAuthenticator new requestAuthenticator
     */
	public void setAuthenticator(RequestAuthenticator newAuthenticator) {
		if (newAuthenticator == null) throw new IllegalArgumentException("null requestAuthenticator");
		synchronized (this) {
			this.requestAuthenticator = newAuthenticator;
		}
	}

	/**
     * Return packet requestAuthenticator.<BR>
     * For request packet it is request requestAuthenticator, for responce - responce requestAuthenticator.
     *
     * @return byte[16] or null if not set.
     */
	public RequestAuthenticator getAuthenticator() {
		synchronized (this) {
			return this.requestAuthenticator;
		}
	}

	/**
     * Return packet attributes.
     *
     * @return list of packet attributes.
     */
	public AttributesList getAttributes() {
		return this.attributes;
	}

	/**
     * Convert to String
     *
     * @return строковое представление пакета.
     */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
		sb.append("{ id=").append(this.id);
		if (this.attributes.getSize() > 0) {
			sb.append(",");
			Iterator<RadiusAttribute> attributesIterator = this.attributes.toList().iterator();
			while (attributesIterator.hasNext()) {
				sb.append(" ");
				sb.append(attributesIterator.next());
				if (attributesIterator.hasNext()) sb.append(",");
			}
		}
		sb.append(" }");
		return sb.toString();
	}
}
