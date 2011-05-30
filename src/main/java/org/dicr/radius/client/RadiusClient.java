/**
 * RadiusClient.java 13.11.2006
 */
package org.dicr.radius.client;

import org.dicr.radius.attribute.*;
import org.dicr.radius.exc.*;

/**
 * Radius Client
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061113
 */
public interface RadiusClient {
	/**
     * Authenticate user against radius server
     * 
     * @param user user name
     * @param password password (clear-text)
     * @return response attributes
     * @throws AuthenticationException incorrect login or password
     * @throws CodecException packet codec error (mismatch attributes)
     * @throws ChannelException error in channel
     */
	public AttributesList authenticate(String user, String password) throws AuthenticationException, CodecException, ChannelException;
}
