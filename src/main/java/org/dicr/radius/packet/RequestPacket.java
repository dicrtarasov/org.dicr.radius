package org.dicr.radius.packet;

/**
 * RequestPacket. Superclass of request packets.
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060623
 */
public abstract class RequestPacket extends RadiusPacket {
	/** Track of request id's */
	private static int lastId = 0;

	/**
     * Constructor
     * 
     * @param aCode packet code
     */
	protected RequestPacket(int aCode) {
		super(aCode);
		this.setNextId();
		this.generateNewAuthenticator();
	}

	/**
     * Generate next id code
     * 
     * @return 0..255
     */
	private static synchronized int nextId() {
		RequestPacket.lastId = (RequestPacket.lastId + 1) % 256;
		return RequestPacket.lastId;
	}

	/**
     * Set id to next value of static counter.
     */
	public void setNextId() {
		this.setId(RequestPacket.nextId());
	}

	/**
     * Generate random authenticator for new Request packet.
     */
	public void generateNewAuthenticator() {
		this.setAuthenticator(new RequestAuthenticator());
	}
}
