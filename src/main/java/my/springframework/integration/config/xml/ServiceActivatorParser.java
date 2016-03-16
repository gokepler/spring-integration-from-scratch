package my.springframework.integration.config.xml;

import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import my.springframework.integration.config.ConsumerEndpointFactoryBean;
import my.springframework.integration.handler.ServiceActivatingHandler;

/**
 * Parser for the &lt;service-activator&gt; element.
 * 
 * This class uses a FactoryBean implementation to construct the actual endpoint
 * instance.
 */
public class ServiceActivatorParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder handlerBuilder = BeanDefinitionBuilder.genericBeanDefinition(ServiceActivatingHandler.class);
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(handlerBuilder, element, "ref");
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(handlerBuilder, element, "output-channel");
		AbstractBeanDefinition handlerBeanDefinition = handlerBuilder.getBeanDefinition();
		String handlerBeanName = BeanDefinitionReaderUtils.generateBeanName(handlerBeanDefinition, parserContext.getRegistry());
		parserContext.registerBeanComponent(new BeanComponentDefinition(handlerBeanDefinition, handlerBeanName));

		
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ConsumerEndpointFactoryBean.class);
		builder.addPropertyReference("handler", handlerBeanName);
		String inputChannelName = element.getAttribute("input-channel");
		builder.addPropertyValue("inputChannelName", inputChannelName);
		AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
		String beanName = this.resolveId(element, beanDefinition, parserContext);
		parserContext.registerBeanComponent(new BeanComponentDefinition(beanDefinition, beanName));
		
		return null;
	}

}
