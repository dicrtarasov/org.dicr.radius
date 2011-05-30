/**
 * MSChap2AuthModule.java 25.05.2007
 */
package org.dicr.radius.handler;

import org.dicr.radius.attribute.*;
import org.dicr.radius.attribute.impl.*;
import org.dicr.radius.attribute.ms.*;
import org.dicr.radius.exc.*;

/**
 * MS-CHAP2 Authentication Module
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070525
 */
public interface MSChap2AuthModule {
	/**
     * Authenticate using MS-CHAP v2 protocol
     * 
     * @param userName User-Name attribute
     * @param challenge MS-CHAP-Challenge attribute
     * @param response MS-CHAP2-Response attribute
     * @param requestAttributes all request attributes
     * @return response attributes
     * @throws AuthenticationException if authentication fail
     */
	public AttributesList authMSChap2(UserName userName, MSChapChallenge challenge, MSChap2Response response, AttributesList requestAttributes) throws AuthenticationException;
}
