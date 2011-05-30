/**
 * AuthScheme.java 30.05.2007
 */
package org.dicr.radius.handler.impl;

/**
 * Authentication Scheme
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070530
 */
public enum AuthScheme {
	/** PAP authentication scheme */
	PAP,
	/** CHAP MD5 authentication scheme */
	CHAP,
	/** MSCHAP authentication scheme */
	MSCHAP,
	/** MSCHAPv2 authentication scheme */
	MSCHAPv2;
}
