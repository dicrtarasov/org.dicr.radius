/**
 * AccountingModule.java 24.06.2006
 */
package org.dicr.radius.handler;

import org.dicr.radius.attribute.*;
import org.dicr.radius.exc.*;

/**
 * Accounting Module. Process AccountingRequestS and return AccountingResponse attributes. Must throw
 * {@link AccountingException} if can not process accounting request.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060624
 */
public interface AccountingModule {
	/**
     * Process accounting request
     * 
     * @param statusType accounting status type
     * @param sessionId session id
     * @param requestAttributes request attributes
     * @return attributes for accounting response
     * @throws AccountingException if accounting errors occur
     */
	public AttributesList processAccounting(int statusType, String sessionId, AttributesList requestAttributes) throws AccountingException;
}
