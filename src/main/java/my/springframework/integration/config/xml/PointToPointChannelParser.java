package my.springframework.integration.config.xml;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import my.springframework.integration.channel.DirectChannel;
import my.springframework.integration.channel.QueueChannel;

/**
 * Parser for the &lt;channel&gt; element.
 */
public class PointToPointChannelParser extends AbstractBeanDefinitionParser {


	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = null;

		// configure a queue-based channel if any queue sub-element is defined
		if ((DomUtils.getChildElementByTagName(element, "queue")) != null) {
			builder = BeanDefinitionBuilder.genericBeanDefinition(QueueChannel.class);
		} else {
			builder = BeanDefinitionBuilder.genericBeanDefinition(DirectChannel.class);
		}
		
		AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
		beanDefinition.setSource(parserContext.extractSource(element));
		return beanDefinition;
	}

}
