package org.dicr.radius.packet;

/**
 * ResponsePacket. Superclass of all response packets.
 * 
 * @author Igor A Tarasov, &lt;java@dicr.org&gt;
 * @version 060623
 */
public abstract class ResponsePacket extends RadiusPacket {
	/**
	 * Constructor
	 * 
	 * @param aCode packet code
	 */
	protected ResponsePacket(int aCode) {
		super(aCode);
	}
}
