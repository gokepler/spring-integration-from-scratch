package my.springframework.integration.config.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.Conventions;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Shared utility methods for integration namespace parsers.
 */
public abstract class IntegrationNamespaceUtils {


	public static void setReferenceIfAttributeDefined(BeanDefinitionBuilder builder, Element element,
			String attributeName, String propertyName, boolean emptyStringAllowed) {
		if (element.hasAttribute(attributeName)) {
			String attributeValue = element.getAttribute(attributeName);
			if (StringUtils.hasText(attributeValue)) {
				builder.addPropertyReference(propertyName, attributeValue);
			}
			else if (emptyStringAllowed) {
				builder.addPropertyValue(propertyName, null);
			}
		}
	}

	/**
	 * Configures the provided bean definition builder with a property reference to a bean. The bean reference is
	 * identified by the value from the attribute whose name is provided if that attribute is defined in the given
	 * element.
	 *
	 * <p>
	 * The property name will be the camel-case equivalent of the lower case hyphen separated attribute (e.g. the
	 * "foo-bar" attribute would match the "fooBar" property).
	 *
	 * @see Conventions#attributeNameToPropertyName(String)
	 *
	 * @param builder the bean definition builder to be configured
	 * @param element - the XML element where the attribute should be defined
	 * @param attributeName - the name of the attribute whose value will be used as a bean reference to populate the
	 * property
	 *
	 * @see Conventions#attributeNameToPropertyName(String)
	 */
	public static void setReferenceIfAttributeDefined(BeanDefinitionBuilder builder, Element element,
			String attributeName) {
		setReferenceIfAttributeDefined(builder, element, attributeName, false);
	}

	public static void setReferenceIfAttributeDefined(BeanDefinitionBuilder builder, Element element,
			String attributeName, boolean emptyStringAllowed) {
		setReferenceIfAttributeDefined(builder, element, attributeName,
				Conventions.attributeNameToPropertyName(attributeName), emptyStringAllowed);
	}

}
