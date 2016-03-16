package my.springframework.integration.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import my.springframework.integration.core.ChannelResolver;
import my.springframework.integration.endpoint.AbstractEndpoint;
import my.springframework.integration.endpoint.EventDrivenConsumer;
import my.springframework.integration.endpoint.PollingConsumer;
import my.springframework.messaging.MessageChannel;
import my.springframework.messaging.MessageHandler;
import my.springframework.messaging.PollableChannel;
import my.springframework.messaging.SubscribableChannel;

public class ConsumerEndpointFactoryBean
		implements FactoryBean<AbstractEndpoint>, BeanFactoryAware, SmartLifecycle, InitializingBean {

	private volatile MessageHandler handler;

	private volatile String inputChannelName;

	private volatile MessageChannel inputChannel;

	private volatile ConfigurableBeanFactory beanFactory;

	private volatile AbstractEndpoint endpoint;

	private volatile ChannelResolver channelResolver;

	public void setHandler(MessageHandler handler) {
		Assert.notNull(handler, "handler must not be null");
		this.handler = handler;
	}

	public void setInputChannel(MessageChannel inputChannel) {
		this.inputChannel = inputChannel;
	}
	
	public void setInputChannelName(String inputChannelName) {
		this.inputChannelName = inputChannelName;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		Assert.isInstanceOf(ConfigurableBeanFactory.class, beanFactory, "a ConfigurableBeanFactory is required");
		this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		this.channelResolver = new ChannelResolver(this.beanFactory);
	}
	
	/*
	 * InitializingBean implementation
	 */
	
	@Override
	public void afterPropertiesSet() throws Exception {
		MessageChannel channel = null;
		if (StringUtils.hasText(this.inputChannelName)) {
			channel = this.channelResolver.resolveDestination(this.inputChannelName);
		}
		if (this.inputChannel != null) {
			channel = this.inputChannel;
		}
		Assert.state(channel != null, "one of inputChannelName or inputChannel is required");
		if (channel instanceof SubscribableChannel) {
			this.endpoint = new EventDrivenConsumer((SubscribableChannel) channel, this.handler);
		}
		else if (channel instanceof PollableChannel) {
			PollingConsumer pollingConsumer = new PollingConsumer((PollableChannel) channel, this.handler);
			this.endpoint = pollingConsumer;
		}
		else {
			throw new IllegalArgumentException("unsupported channel type: [" + channel.getClass() + "]");
		}
	}
	
	/*
	 * FactoryBean implementation
	 */

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public AbstractEndpoint getObject() throws Exception {
		return this.endpoint;
	}

	@Override
	public Class<?> getObjectType() {
		return AbstractEndpoint.class;
	}

	/*
	 * SmartLifecycle implementation (delegates to the created endpoint)
	 */

	@Override
	public boolean isAutoStartup() {
		return (this.endpoint == null) || this.endpoint.isAutoStartup();
	}

	@Override
	public int getPhase() {
		return (this.endpoint != null) ? this.endpoint.getPhase() : 0;
	}

	@Override
	public boolean isRunning() {
		return (this.endpoint != null) && this.endpoint.isRunning();
	}

	@Override
	public void start() {
		if (this.endpoint != null) {
			this.endpoint.start();
		}
	}

	@Override
	public void stop() {
		if (this.endpoint != null) {
			this.endpoint.stop();
		}
	}

	@Override
	public void stop(Runnable callback) {
		if (this.endpoint != null) {
			this.endpoint.stop(callback);
		}
	}

}
