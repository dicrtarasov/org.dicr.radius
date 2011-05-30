/**
 * AttributesFactory.java 01.11.2006
 */
package org.dicr.radius.attribute;

import java.util.*;

import org.apache.log4j.*;
import org.dicr.radius.attribute.impl.*;
import org.dicr.radius.attribute.ms.*;
import org.dicr.radius.attribute.types.*;
import org.dicr.radius.dictionary.*;

/**
 * Radius Attributes Factory. Creates attributes by type.
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061101
 */
public class AttributesFactory {
	private static final Map<AttributeType, Class<? extends RadiusAttribute>> registeredTypes = new HashMap<AttributeType, Class<? extends RadiusAttribute>>();

	private static final Logger log = Logger.getLogger(AttributesFactory.class);

	/**
	 * Register already known attributes
	 */
	static {
		// register known attributes
		AttributesFactory.registerType(UserName.TYPE, UserName.class);
		AttributesFactory.registerType(ReplyMessage.TYPE, ReplyMessage.class);
		AttributesFactory.registerType(NASIPAddress.TYPE, NASIPAddress.class);
		AttributesFactory.registerType(NASIdentifier.TYPE, NASIdentifier.class);
		AttributesFactory.registerType(NASPort.TYPE, NASPort.class);
		AttributesFactory.registerType(CallerID.TYPE, CallerID.class);
		AttributesFactory.registerType(ServiceType.TYPE, ServiceType.class);

		// PAP
		AttributesFactory.registerType(UserPassword.TYPE, UserPassword.class);

		// CHAP
		AttributesFactory.registerType(ChapPassword.TYPE, ChapPassword.class);
		AttributesFactory.registerType(ChapChallenge.TYPE, ChapChallenge.class);

		// IP
		AttributesFactory.registerType(FramedIPAddress.TYPE, FramedIPAddress.class);

		// accounting
		AttributesFactory.registerType(AcctSessionId.TYPE, AcctSessionId.class);
		AttributesFactory.registerType(AcctStatusType.TYPE, AcctStatusType.class);
		AttributesFactory.registerType(AcctInterimInterval.TYPE, AcctInterimInterval.class);
		AttributesFactory.registerType(AcctSessionTime.TYPE, AcctSessionTime.class);
		AttributesFactory.registerType(AcctInputOctets.TYPE, AcctInputOctets.class);
		AttributesFactory.registerType(AcctOutputOctets.TYPE, AcctOutputOctets.class);

		// session limits
		AttributesFactory.registerType(SessionTimeout.TYPE, SessionTimeout.class);
		AttributesFactory.registerType(SessionOctetsLimit.TYPE, SessionOctetsLimit.class);
		AttributesFactory.registerType(OctetsDirection.TYPE, OctetsDirection.class);

		// microsoft MSCHAP
		AttributesFactory.registerType(MSChapChallenge.TYPE, MSChapChallenge.class);
		AttributesFactory.registerType(MSChapCPW1.TYPE, MSChapCPW1.class);
		AttributesFactory.registerType(MSChapCPW2.TYPE, MSChapCPW2.class);
		AttributesFactory.registerType(MSChapDomain.TYPE, MSChapDomain.class);
		AttributesFactory.registerType(MSChapError.TYPE, MSChapError.class);
		AttributesFactory.registerType(MSChapResponse.TYPE, MSChapResponse.class);
		AttributesFactory.registerType(MSChapLMEncPW.TYPE, MSChapLMEncPW.class);
		AttributesFactory.registerType(MSChapNTEncPW.TYPE, MSChapNTEncPW.class);
		AttributesFactory.registerType(MSChap2CPW.TYPE, MSChap2CPW.class);
		AttributesFactory.registerType(MSChap2Response.TYPE, MSChap2Response.class);
		AttributesFactory.registerType(MSChap2Success.TYPE, MSChap2Success.class);
	}

	/**
	 * Disabled constructo
	 */
	private AttributesFactory() {
		super();
	}

	/**
	 * Register user class for specified attribute type.
	 * 
	 * @param type attribute type
	 * @param attributeClass class for this type
	 */
	public static void registerType(final AttributeType type, final Class<? extends RadiusAttribute> attributeClass) {
		if (type == null) throw new IllegalArgumentException("null type");
		if (attributeClass == null) throw new IllegalArgumentException("null class");
		synchronized (AttributesFactory.registeredTypes) {
			AttributesFactory.registeredTypes.put(type, attributeClass);
		}
		AttributesFactory.log.trace("registered attribute class " + attributeClass.getName() + " for type " + type);
	}

	/**
	 * Create attribute for specified type. If attribute class of this type is registered, then instantiate attribute
	 * from it class, Else if atribute value type is known from RadiusDictionary - create attribute for this type. Else
	 * create {@link OctetsAttribute} instance.
	 * 
	 * @param type attribute type
	 * @return attribute for this type
	 */
	public static RadiusAttribute createAttribute(final AttributeType type) {
		if (type == null) throw new IllegalArgumentException("null type");

		RadiusAttribute attr = null;

		// try create registered attribute
		Class<? extends RadiusAttribute> clazz = null;
		synchronized (AttributesFactory.registeredTypes) {
			clazz = AttributesFactory.registeredTypes.get(type);
		}
		if (clazz != null) try {
			attr = clazz.newInstance();
		} catch (Throwable th) {
			AttributesFactory.log.error("error instantinate attribute of type " + type + " and class " + clazz
			        + ", class must define default public constructor", th);
		}

		// try create vendor attribute
		if (attr == null && type.getTypeCode() == VendorAttribute.TYPE_CODE) attr = new VendorAttribute();

		// try create by type
		if (attr == null) {
			AttributeDescriptor descriptor = DictionaryFactory.getDictionary().getAttributeDescriptor(type);
			if (descriptor != null) {
				ValueType valueType = descriptor.getValueType();
				if (valueType == ValueType.INTEGER) attr = new IntegerAttribute(type);
				else if (valueType == ValueType.STRING) attr = new StringAttribute(type);
				else if (valueType == ValueType.DATE) attr = new DateAttribute(type);
				else if (valueType == ValueType.ADDRESS) attr = new AddressAttribute(type);
			}
		}

		// create Octets attribute as default attribute value container
		if (attr == null) {
			AttributesFactory.log.debug("unknown attribute type " + type);
			attr = new OctetsAttribute(type);
		}

		// result
		return attr;
	}
}
