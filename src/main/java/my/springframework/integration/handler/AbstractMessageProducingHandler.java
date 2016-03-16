package my.springframework.integration.handler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;

import my.springframework.integration.core.ChannelResolver;
import my.springframework.integration.core.MessagingTemplate;
import my.springframework.messaging.Message;
import my.springframework.messaging.MessageChannel;
import my.springframework.messaging.MessageHandler;
import my.springframework.messaging.MessagingException;

public abstract class AbstractMessageProducingHandler implements MessageHandler, BeanFactoryAware {

	protected final MessagingTemplate messagingTemplate = new MessagingTemplate();

	private volatile MessageChannel outputChannel;

	private volatile ChannelResolver channelResolver;

	
	public void setOutputChannel(MessageChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	
	protected MessageChannel getReplyChannel(Message<?> message) {
		MessageChannel replyChannel = this.outputChannel;
		if (replyChannel == null) {
			replyChannel = (MessageChannel) message.getHeaders().getReplyChannel();
		}
		return replyChannel;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.channelResolver = new ChannelResolver(beanFactory);
		this.messagingTemplate.setChannelResolver(this.channelResolver);
	}

	/**
	 * Base implementation that provides basic validation
	 * and error handling capabilities. Asserts that the incoming Message is not
	 * null and that it does not contain a null payload. Converts checked exceptions
	 * into runtime {@link MessagingException}s.
	 */
	@Override
	public final void handleMessage(Message<?> message) {
		Assert.notNull(message, "Message must not be null");
		Assert.notNull(message.getPayload(), "Message payload must not be null");//NOSONAR - false positive
		try {
			this.handleMessageInternal(message);
		}
		catch (Exception e) {
			if (e instanceof MessagingException) {
				throw (MessagingException) e;
			}
			throw new MessagingException(message, "error occurred in message handler [" + this + "]", e);
		}
	}
	
	protected abstract void handleMessageInternal(Message<?> message) throws Exception;
}
