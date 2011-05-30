/**
 * CHAPAuthModule.java 24.06.2006
 */
package org.dicr.radius.handler;

import org.dicr.radius.attribute.*;
import org.dicr.radius.attribute.impl.*;
import org.dicr.radius.exc.*;

/**
 * CHAP Authentication Module. Authenticate users using CHAP authentication scheme and return response attributes list.
 * Must throw {@link AuthenticationException} if authentication fail.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060624
 */
public interface CHAPAuthModule {
	/**
     * Authenticate using CHAP method.
     * 
     * @param userName user name
     * @param password chap password
     * @param challenge chap challenge
     * @param requestAttributes attributes from AccessRequest packet
     * @return response attributes for AccessAccept packet (may be empty or null)
     * @throws AuthenticationException if authentication fails any reason. Message will used in Reply-Message attribute
     *             of Access-Reject response.
     */
	public AttributesList authChap(String userName, ChapPassword password, ChapChallenge challenge, AttributesList requestAttributes) throws AuthenticationException;
}
