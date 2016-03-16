package my.springframework.integration.config.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class IntegrationNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
        registerBeanDefinitionParser("gateway", new GatewayParser());
        registerBeanDefinitionParser("channel", new PointToPointChannelParser());
        registerBeanDefinitionParser("publish-subscribe-channel", new PublishSubscribeChannelParser());
        registerBeanDefinitionParser("bridge", new BridgeParser());
        registerBeanDefinitionParser("service-activator", new ServiceActivatorParser());
    }

}
