package my.springframework.integration.handler;

import my.springframework.messaging.Message;

/**
 * A simple MessageHandler implementation that passes the request Message
 * directly to the output channel without modifying it. The main purpose of this
 * handler is to bridge a PollableChannel to a SubscribableChannel or
 * vice-versa.
 * <p>
 * The BridgeHandler can be used as a stopper at the end of an assembly line of
 * channels. In this setup the output channel doesn't have to be set, but if the
 * output channel is omitted the <tt>REPLY_CHANNEL</tt> MUST be set on the
 * message. Otherwise, a MessagingException will be thrown at runtime.
 */
public class BridgeHandler extends AbstractMessageProducingHandler {

	@Override
	protected void handleMessageInternal(Message<?> message) throws Exception {
		getReplyChannel(message).send(message);
	}

}
