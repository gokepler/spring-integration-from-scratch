package my.springframework.integration.config.xml;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import my.springframework.integration.channel.PublishSubscribeChannel;

/**
 * Parser for the &lt;publish-subscribe-channel&gt; element.
 */
public class PublishSubscribeChannelParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
				PublishSubscribeChannel.class);
		String taskExecutorRef = element.getAttribute("task-executor");
		if (StringUtils.hasText(taskExecutorRef)) {
			builder.addConstructorArgReference(taskExecutorRef);
		}
		AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
		beanDefinition.setSource(parserContext.extractSource(element));
		return beanDefinition;
	}

}
