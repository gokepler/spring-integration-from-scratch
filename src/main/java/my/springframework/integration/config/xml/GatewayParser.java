package my.springframework.integration.config.xml;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import my.springframework.integration.config.GatewayProxyFactoryBean;

/**
 * Parser for the &lt;gateway/&gt; element.
 */
public class GatewayParser extends AbstractBeanDefinitionParser {

	@Override
	public AbstractBeanDefinition parseInternal(final Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(GatewayProxyFactoryBean.class);
		String serviceInterface = element.getAttribute("service-interface");
		builder.addConstructorArgValue(new TypedStringValue(serviceInterface));
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "default-request-channel");
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "default-reply-channel");
		
		return builder.getBeanDefinition();
	}

}
