/**
 * DictionaryFactory.java 02.11.2006
 */
package org.dicr.radius.dictionary;

import java.net.*;

import org.apache.log4j.*;
import org.dicr.radius.dictionary.impl.*;

/**
 * Dictionary Factory. Factory create and return single instance of dictionary of specified class and resource URL in
 * each call to {@link #getDictionary()}. If class or url properties changed after last dictionary return, the new
 * dictionary instance will recreated with new class and URL. If class or URL is set to <CODE>null</CODE>, the System
 * properties {@value #PROPERTY_CLASS} and {@value #PROPERTY_URL} are cheked for the values. If no class name is
 * specified in any way, the default class is {@link DefaultDictionary}. If URL is specified, then
 * {@link RadiusDictionary#init(URL)} method is called after dictionary is created. If an error occur upon dictionary
 * creation or initialisation, it logged to logger and empty implementation of dictionary is returned.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061102
 */
public class DictionaryFactory {
	private static final Logger log = Logger.getLogger(DictionaryFactory.class);

	/** Property name for dictionary class */
	public static final String PROPERTY_CLASS = "org.dicr.radius.dictionary.class";

	/** Property name for dictionary URL */
	public static final String PROPERTY_URL = "org.dicr.radius.dictionary.url";

	/** Default dictionary class */
	public static final Class<? extends RadiusDictionary> DEFAULT_CLASS = DefaultDictionary.class;

	private static Class<? extends RadiusDictionary> clazz = null;

	private static URL url = null;

	private static RadiusDictionary dictionary = null;

	/**
	 * Set dictionary class. After this method invokation dictionary will recreated with new class.
	 */
	public static synchronized void setDictionaryClass(final Class<? extends RadiusDictionary> dictionaryClass) {
		DictionaryFactory.clazz = dictionaryClass;
		DictionaryFactory.dictionary = null;
	}

	/**
	 * Set dictionary resource URL. After this method invokation dictionary will recreated with new url.
	 */
	public static synchronized void setDictionaryURL(final URL dictionaryURL) {
		DictionaryFactory.url = dictionaryURL;
		DictionaryFactory.dictionary = null;
	}

	/**
	 * Return dictionary. At firs invokation this method dictionary created with current class and URL.
	 */
	private static synchronized void createDictionary() {
		try {
			if (DictionaryFactory.clazz == null) {
				final String className = System.getProperty(DictionaryFactory.PROPERTY_CLASS);
				DictionaryFactory.clazz = className != null ? Class.forName(className).asSubclass(
				        RadiusDictionary.class) : DictionaryFactory.DEFAULT_CLASS;
			}
			DictionaryFactory.dictionary = DictionaryFactory.clazz.newInstance();
			log.debug("created default dictionary of type type: " + DictionaryFactory.clazz.getName());
			if (DictionaryFactory.url == null) {
				final String urlString = System.getProperty(DictionaryFactory.PROPERTY_URL);
				if (urlString != null) DictionaryFactory.url = new URL(urlString);
				log.debug("configured default dictionary URL: " + DictionaryFactory.url);
			}
			if (DictionaryFactory.url != null) DictionaryFactory.dictionary.init(DictionaryFactory.url);
		} catch (final Throwable th) {
			DictionaryFactory.log.error("error creating dictionary, reverting to " + FailSafeDictionary.class.getSimpleName(), th);
			DictionaryFactory.dictionary = new FailSafeDictionary();
		}
	}

	/**
	 * Return dictionary. If any of parameters (class or url) was changed after last call to this method, new dictionary
	 * object with new parameters will created. Otherwice, cached instance returned.
	 * 
	 * @return dictionary instance
	 */
	public static synchronized RadiusDictionary getDictionary() {
		if (DictionaryFactory.dictionary == null) DictionaryFactory.createDictionary();
		return DictionaryFactory.dictionary;
	}

	/**
	 * Fail-safe radius dictionary
	 */
	protected static class FailSafeDictionary implements RadiusDictionary {
		/** @see org.dicr.radius.dictionary.RadiusDictionary#init(java.net.URL) */
		@Override
        public void init(final URL aurl) {
		// empty
		}

		/** @see org.dicr.radius.dictionary.RadiusDictionary#getAttributeDescriptor(org.dicr.radius.dictionary.AttributeType) */
		@Override
        public AttributeDescriptor getAttributeDescriptor(final AttributeType atype) {
			return null;
		}

		/** @see org.dicr.radius.dictionary.RadiusDictionary#getVendorName(int) */
		@Override
        public String getVendorName(final int acode) {
			return null;
		}
	}
}
