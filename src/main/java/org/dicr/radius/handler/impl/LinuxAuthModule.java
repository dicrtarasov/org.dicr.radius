/**
 * LinuxAuthModule.java 19.06.2006 2:15:09 dicr
 */
package org.dicr.radius.handler.impl;

import java.io.*;

import org.apache.log4j.*;
import org.dicr.radius.attribute.*;
import org.dicr.radius.dictionary.*;
import org.dicr.radius.exc.*;
import org.dicr.radius.handler.*;
import org.dicr.sys.linux.*;
import org.dicr.util.data.exc.*;

/**
 * Linux authentication module. Support only PAP authentication scheme. Check user name and password against Linux
 * system account database.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060618
 * @see ShadowUtils
 */
public final class LinuxAuthModule implements PAPAuthModule {
	private static final Logger log = Logger.getLogger(LinuxAuthModule.class);

	/**
	 * Constructor.
	 */
	public LinuxAuthModule() {
		super();
	}

	/**
	 * Authenticate using PAP. Check user name and password against shadow system accounts database.
	 * 
	 * @param userName user name
	 * @param userPassword password
	 * @param requestAttributes request attributes
	 * @requestAttributes unused
	 * @return empty list
	 * @throws AuthenticationException if authentication fails.
	 */
	@Override
    public final AttributesList authPap(final String userName, final String userPassword, final AttributesList requestAttributes) throws AuthenticationException {
		if (userName == null || userName.length() < 1) throw new IllegalArgumentException("empty user name");
		if (userPassword == null || userPassword.length() < 1) throw new IllegalArgumentException("empty password");
		if (requestAttributes == null) throw new IllegalArgumentException("null attributes");
		try {
			if (!ShadowUtils.verifyUserPassword(userName, userPassword)) throw new AuthenticationException(
			        "Password incorrect");
			LinuxAuthModule.log.debug("system account '" + userName + "' authentication successfull");
		} catch (final NotFoundException ex) {
			throw new AuthenticationException("User incorrect");
		} catch (final IOException ex) {
			throw new AuthenticationException("Server error", ex);
		}
		return new AttributesList(AttributeType.VENDOR_NONE);
	}
}
