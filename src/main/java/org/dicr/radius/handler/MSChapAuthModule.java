/**
 * MSChapAuthModule.java 25.05.2007
 */
package org.dicr.radius.handler;

import org.dicr.radius.attribute.*;
import org.dicr.radius.attribute.impl.*;
import org.dicr.radius.attribute.ms.*;
import org.dicr.radius.exc.*;

/**
 * MS-CHAP Authentication module
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070525
 */
public interface MSChapAuthModule {

	/**
     * Authenticate by MS-CHAP protocol
     * 
     * @param userName User-Name ttribute
     * @param challenge MS-CHAP-Challenge attribute
     * @param response MS-CHAP-Response attribute
     * @param requestAttributes all request attributes
     * @return response attributes
     * @throws AuthenticationException if authentication fail
     */
	public AttributesList authMSCHAP(UserName userName, MSChapChallenge challenge, MSChapResponse response, AttributesList requestAttributes) throws AuthenticationException;

}
