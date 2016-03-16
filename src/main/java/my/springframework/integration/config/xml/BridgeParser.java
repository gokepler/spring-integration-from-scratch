package my.springframework.integration.config.xml;

import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import my.springframework.integration.config.ConsumerEndpointFactoryBean;
import my.springframework.integration.handler.BridgeHandler;

/**
 * Parser for the &lt;bridge&gt; element.
 */
public class BridgeParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder handlerBuilder = BeanDefinitionBuilder.genericBeanDefinition(BridgeHandler.class);
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(handlerBuilder, element, "output-channel");

		AbstractBeanDefinition handlerBeanDefinition = handlerBuilder.getBeanDefinition();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ConsumerEndpointFactoryBean.class);

		String handlerBeanName = BeanDefinitionReaderUtils.generateBeanName(handlerBeanDefinition, parserContext.getRegistry());
		parserContext.registerBeanComponent(new BeanComponentDefinition(handlerBeanDefinition, handlerBeanName));

		builder.addPropertyReference("handler", handlerBeanName);

		builder.addPropertyValue("inputChannelName", element.getAttribute("input-channel"));
		return builder.getBeanDefinition();
	}

}
