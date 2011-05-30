/**
 * PAPAuthModule.java 18.06.2006 15:58:27 dicr
 */
package org.dicr.radius.handler;

import org.dicr.radius.attribute.*;
import org.dicr.radius.exc.*;

/**
 * PAP Authentication module. Authenticate user using PAP authentication scheme and return list attributes for response
 * packet. Must throw {@link AuthenticationException} if authentication fail.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060618
 */
public interface PAPAuthModule {
	/**
     * Authenticate using PAP method.
     * 
     * @param userName user name
     * @param userPassword user password
     * @param requestAttributes attributes from AccessRequest packet
     * @return response attributes for AccessAccept packet (may be empty or null)
     * @throws AuthenticationException if authentication fails in any reason. Message will used in Reply-Message
     *             attribute of Access-Reject response.
     */
	public AttributesList authPap(String userName, String userPassword, AttributesList requestAttributes) throws AuthenticationException;
}
