package my.springframework.integration.endpoint;

import org.springframework.util.Assert;

import my.springframework.messaging.Message;
import my.springframework.messaging.MessageHandler;
import my.springframework.messaging.MessagingException;
import my.springframework.messaging.SubscribableChannel;

/**
 * Message Endpoint that connects any {@link MessageHandler} implementation to a
 * {@link SubscribableChannel}.
 */
public class EventDrivenConsumer extends AbstractEndpoint {

	private final SubscribableChannel inputChannel;

	private final MessageHandler handler;

	public EventDrivenConsumer(SubscribableChannel inputChannel, MessageHandler handler) {
		Assert.notNull(inputChannel, "inputChannel must not be null");
		Assert.notNull(handler, "handler must not be null");
		this.inputChannel = inputChannel;
		this.handler = handler;
	}

	protected void handleMessage(Message<?> message) {
		Message<?> theMessage = message;
		try {
			this.handler.handleMessage(theMessage);
		} catch (Exception ex) {
			if (ex instanceof MessagingException) {
				throw (MessagingException) ex;
			}
			String description = "Failed to handle " + theMessage + " to " + this + " in " + this.handler;
			throw new MessagingException(theMessage, description, ex);
		}
	}

	@Override
	protected void doStart() {
		this.inputChannel.subscribe(this.handler);
	}

	@Override
	protected void doStop() {
		this.inputChannel.unsubscribe(this.handler);
	}

}
