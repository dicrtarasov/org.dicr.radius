package org.dicr.radius.dictionary.impl;

import java.lang.ref.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.*;

import org.apache.log4j.*;
import org.dicr.radius.dictionary.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * Default Radius Dictionary. This dictionary read data from an xml-file dictionary resource. By default it load
 * <CODE>{@value #DEFAULT_RESOURCE_NAME}</CODE> resource from this dictionary class loader. Alternativelly,
 * {@value RadiusDictionary#PROPERTY_URL} system property can specify URL of resource to load.
 * 
 * @author Igor A Tarasov, java@dicr.org
 * @version 1.0
 */
public final class DefaultDictionary implements RadiusDictionary {
	/** Logger */
	private static final Logger log = Logger.getLogger(DefaultDictionary.class);

	/** Default dictionary resource name */
	public static final String DEFAULT_RESOURCE_NAME = "default-dictionary.xml";

	/** Resource data URL; */
	private URL url = null;

	/** Cache of vendors */
	private SoftReference<Map<Integer, String>> vendorsRef = null;

	/** Cache of attributes */
	private SoftReference<Map<AttributeType, AttributeDescriptor>> attributesRef = null;

	/**
	 * Constructor
	 */
	public DefaultDictionary() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param resourceURL URL of dictionary xml-file
	 */
	public DefaultDictionary(final URL resourceURL) {
		super();
		this.init(resourceURL);
	}

	/**
	 * Set new URL of dictionary resource file.
	 * 
	 * @see org.dicr.radius.dictionary.RadiusDictionary#init(java.net.URL)
	 */
	public synchronized void init(final URL resourceURL) {
		this.url = resourceURL;
		this.vendorsRef = null;
		this.attributesRef = null;
	}

	/**
	 * Load Attributes from a dictionary file
	 */
	private final synchronized void loadData() {
		final URL resourceURL = this.url != null ? this.url : this.getClass().getResource(
		        DefaultDictionary.DEFAULT_RESOURCE_NAME);
		if (resourceURL != null) {
			final DictionaryHandler handler = new DictionaryHandler();
			DefaultDictionary.log.info("initializing radius dictionary '" + resourceURL + "'");
			try {
				final SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setValidating(false);
				final SAXParser parser = factory.newSAXParser();
				parser.parse(resourceURL.toURI().toString(), handler);
			} catch (final Exception ex) {
				DefaultDictionary.log.warn("error loading radius dictionary from '" + resourceURL + "'", ex);
			}
			this.vendorsRef = new SoftReference<Map<Integer, String>>(handler.vendors);
			this.attributesRef = new SoftReference<Map<AttributeType, AttributeDescriptor>>(handler.attributes);
		} else {
			log.warn("dictionary resource not found: " + DefaultDictionary.DEFAULT_RESOURCE_NAME);
			this.vendorsRef = new SoftReference<Map<Integer, String>>(new HashMap<Integer, String>());
			this.attributesRef = new SoftReference<Map<AttributeType, AttributeDescriptor>>(
			        new HashMap<AttributeType, AttributeDescriptor>());
		}
	}

	/**
	 * @see org.dicr.radius.dictionary.RadiusDictionary#getVendorName(int)
	 */
	public synchronized String getVendorName(final int code) {
		if (this.vendorsRef == null || this.vendorsRef.get() == null) this.loadData();
		Map<Integer, String> vendorsMap = null;
		if (this.vendorsRef != null) vendorsMap = this.vendorsRef.get();
		if (vendorsMap == null) return null;
		return vendorsMap.get(Integer.valueOf(code));
	}

	/**
	 * @see org.dicr.radius.dictionary.RadiusDictionary#getAttributeDescriptor(org.dicr.radius.dictionary.AttributeType)
	 */
	public final synchronized AttributeDescriptor getAttributeDescriptor(final AttributeType type) {
		if (type == null) throw new IllegalArgumentException("null code");
		if (this.attributesRef == null || this.attributesRef.get() == null) this.loadData();
		Map<AttributeType, AttributeDescriptor> attributesMap = null;
		if (this.attributesRef != null) attributesMap = this.attributesRef.get();
		if (attributesMap == null) return null;
		return attributesMap.get(type);
	}

	/**
	 * Состояние обработчика XML.
	 */
	private enum HandlerState {
		/** inside document */
		DOCUMENT,
		/** inside dictionary tag */
		DICTIONARY,
		/** inside vendor tag */
		VENDOR,
		/** inside attribute tag */
		ATTRIBUTE,
		/** inside value tag */
		VALUE;
	}

	/**
	 * Обработчик документа словаря.
	 */
	protected static class DictionaryHandler extends DefaultHandler {
		private static final String S_DICTIONARY = "dictionary";

		private static final String S_VENDOR = "vendor";

		private static final String S_ATTRIBUTE = "attribute";

		private static final String S_NAME = "name";

		private static final String S_CODE = "code";

		private static final String S_TYPE = "type";

		private static final String S_ENC = "encoding";

		private static final String S_VALUE = "value";

		/** Current state */
		protected HandlerState state = null;

		/** Current processing attribute type */
		private AttributeType currAttribute = null;

		/** Vendors map */
		protected Map<Integer, String> vendors = new Hashtable<Integer, String>();

		/** Attributes map */
		protected Map<AttributeType, AttributeDescriptor> attributes = new Hashtable<AttributeType, AttributeDescriptor>();

		@SuppressWarnings("unused")
		private final void parseDocument(final String qName, final Attributes attrs) throws SAXException {
			if (DictionaryHandler.S_DICTIONARY.equals(qName)) this.state = HandlerState.DICTIONARY;
			else throw new SAXException("unknown element '" + qName + "'");
		}

		private final void parseDictionary(final String qName, final Attributes attrs) throws SAXException {
			if (DictionaryHandler.S_VENDOR.equals(qName)) {
				this.state = HandlerState.VENDOR;
				Integer vCode = null;
				String vName = null;
				for (int i = 0; i < attrs.getLength(); i++)
					if (DictionaryHandler.S_CODE.equals(attrs.getQName(i))) vCode = new Integer(attrs.getValue(i));
					else if (DictionaryHandler.S_NAME.equals(attrs.getQName(i))) vName = attrs.getValue(i);
					else throw new SAXException("unknown vendor attribute '" + attrs.getQName(i) + "'");
				if (vCode == null) throw new SAXException("no code attribute in vendor");
				if (vName == null) throw new SAXException("no name attribute in vendor");
				this.vendors.put(vCode, vName);
			} else if (DictionaryHandler.S_ATTRIBUTE.equals(qName)) {
				this.state = HandlerState.ATTRIBUTE;
				Integer aVendor = null;
				Integer aCode = null;
				String aName = null;
				ValueType aType = null;
				String aEnc = null;
				for (int i = 0; i < attrs.getLength(); i++)
					if (DictionaryHandler.S_VENDOR.equals(attrs.getQName(i))) aVendor = new Integer(attrs.getValue(i));
					else if (DictionaryHandler.S_CODE.equals(attrs.getQName(i))) aCode = new Integer(attrs.getValue(i));
					else if (DictionaryHandler.S_NAME.equals(attrs.getQName(i))) aName = attrs.getValue(i);
					else if (DictionaryHandler.S_TYPE.equals(attrs.getQName(i))) aType = ValueType.fromString(attrs.getValue(i));
					else if (DictionaryHandler.S_ENC.equals(attrs.getQName(i))) aEnc = attrs.getValue(i);
					else throw new SAXException("unknown attribute '" + attrs.getQName(i) + "' in attribute element");
				if (aVendor == null) aVendor = Integer.valueOf(0);
				if (aCode == null) throw new SAXException("no 'code' attribute in attribute element");
				if (aName == null) throw new SAXException("no attribute name");
				if (aType == null) throw new SAXException("empty or unknown attribute type in '" + aName
				        + "' attribute");
				// skip non-protocol attributes with type > 255
				if (aCode.intValue() <= 255) {
					this.currAttribute = new AttributeType(aVendor.intValue(), aCode.intValue());
					this.attributes.put(this.currAttribute, new AttributeDescriptor(this.currAttribute, aName, aType,
					        aEnc));
				}
			} else throw new SAXException("unknown element '" + qName + "'");
		}

		@SuppressWarnings("unused")
		private final void parseVendor(final String qName, final Attributes attrs) throws SAXException {
			throw new SAXException("unknown internal element '" + qName + "' of vendor");
		}

		private final void parseAttribute(final String qName, final Attributes attrs) throws SAXException {
			if (DictionaryHandler.S_VALUE.equals(qName)) {
				this.state = HandlerState.VALUE;
				Integer vCode = null;
				String vName = null;
				for (int i = 0; i < attrs.getLength(); i++)
					if (DictionaryHandler.S_CODE.equals(attrs.getQName(i))) vCode = new Integer(attrs.getValue(i));
					else if (DictionaryHandler.S_NAME.equals(attrs.getQName(i))) vName = attrs.getValue(i);
					else throw new SAXException("unknown attribute '" + attrs.getQName(i) + "' of value element");
				if (vCode == null) throw new SAXException("no code attribute of 'value' element");
				if (vName == null) throw new SAXException("no 'name' attribute of 'value' element");
				final AttributeDescriptor attDesc = this.attributes.get(this.currAttribute);
				assert attDesc != null;
				attDesc.addValueDescriptor(vCode.intValue(), vName);
			} else throw new SAXException("unknown internal element '" + qName + "' of attribute element");
		}

		/**
		 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
		 */
		@Override
		public final void startDocument() {
			this.state = HandlerState.DOCUMENT;
		}

		/**
		 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
		 */
		@Override
		public final void endDocument() {
			this.state = null;
		}

		/**
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
		 *      org.xml.sax.Attributes)
		 */
		@Override
		public final void startElement(final String uri, final String localName, final String qName, final Attributes attrs) throws SAXException {
			if (this.state == HandlerState.DOCUMENT) this.parseDocument(qName, attrs);
			else if (this.state == HandlerState.DICTIONARY) this.parseDictionary(qName, attrs);
			else if (this.state == HandlerState.VENDOR) this.parseVendor(qName, attrs);
			else if (this.state == HandlerState.ATTRIBUTE) this.parseAttribute(qName, attrs);
			else throw new SAXException("internal error: unknown state");
		}

		/**
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public final void endElement(final String uri, final String localName, final String qName) throws SAXException {
			if (this.state == HandlerState.DICTIONARY && DictionaryHandler.S_DICTIONARY.equals(qName)) this.state = HandlerState.DOCUMENT;
			else if (this.state == HandlerState.VENDOR && DictionaryHandler.S_VENDOR.equals(qName)) this.state = HandlerState.DICTIONARY;
			else if (this.state == HandlerState.ATTRIBUTE && DictionaryHandler.S_ATTRIBUTE.equals(qName)) this.state = HandlerState.DICTIONARY;
			else if (this.state == HandlerState.VALUE && DictionaryHandler.S_VALUE.equals(qName)) this.state = HandlerState.ATTRIBUTE;
			else throw new SAXException("end of error element '" + qName + "'");
		}
	}
}
