/**
 * RadiusAuthenticator.java 13.11.2006
 */
package org.dicr.radius.authenticator;

import org.dicr.radius.attribute.*;
import org.dicr.radius.channel.*;
import org.dicr.radius.exc.*;

/**
 * RadiusAuthenticator. This is the client part of radius protocol. Authenticate client using radius server
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 0611
 */
public interface RadiusAuthenticator {
	/**
     * Authenticate user
     *
     * @param username login
     * @param password password
     * @param channel radius channel to use for authentication
     * @return response attributes
     * @throws AuthenticationException incorrect login or password
     * @throws ChannelException error accessing radius server
     * @throws CodecException error in request or response attributes
     */
	public AttributesList authenticate(String username, String password, ClientChannel channel) throws AuthenticationException, ChannelException, CodecException;
}
