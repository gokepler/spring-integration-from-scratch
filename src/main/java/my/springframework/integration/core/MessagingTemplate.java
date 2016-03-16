package my.springframework.integration.core;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import my.springframework.integration.support.MessageBuilder;
import my.springframework.messaging.Message;
import my.springframework.messaging.MessageChannel;
import my.springframework.messaging.MessageHeaders;
import my.springframework.messaging.PollableChannel;

public class MessagingTemplate {

	private volatile ChannelResolver channelResolver;
	
	private volatile long receiveTimeout = -1;

	/**
	 * Configure the timeout value to use for receive operations.
	 * @param receiveTimeout the receive timeout in milliseconds
	 */
	public void setReceiveTimeout(long receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}
	
	public void setChannelResolver(ChannelResolver channelResolver) {
		this.channelResolver = channelResolver;
	}
	
	public Message<?> receive(MessageChannel channel) {
		Assert.notNull(channel, "'channel' is required");
		Assert.state(channel instanceof PollableChannel, "A PollableChannel is required to receive messages");

		long timeout = this.receiveTimeout;
		Message<?> message = (timeout >= 0 ?
				((PollableChannel) channel).receive(timeout) : ((PollableChannel) channel).receive());

		return message;
	}
	
	public <T> T receiveAndConvert(String destinationName, Class<T> targetClass) {
		MessageChannel destination = channelResolver.resolveDestination(destinationName);
		return receiveAndConvert(destination, targetClass);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T receiveAndConvert(MessageChannel destination, Class<T> targetClass) {
		Message<?> message = receive(destination);
		if (message != null) {
			return (T) message.getPayload();
		}
		else {
			return null;
		}
	}

	public void send(MessageChannel channel, Message<?> message) {
		channel.send(message);
	}

	public Message<?> sendAndReceive(MessageChannel channel, Message<?> requestMessage) {
		Assert.notNull(channel, "'channel' is required");
		Object originalReplyChannelHeader = requestMessage.getHeaders().getReplyChannel();

		TemporaryReplyChannel tempReplyChannel = new TemporaryReplyChannel();
		requestMessage = MessageBuilder.fromMessage(requestMessage).setReplyChannel(tempReplyChannel).build();

		try {
			send(channel, requestMessage);
		}
		catch (RuntimeException ex) {
			tempReplyChannel.setSendFailed(true);
			throw ex;
		}

		Message<?> replyMessage = receive(tempReplyChannel);
		if (replyMessage != null) {
			replyMessage = MessageBuilder.fromMessage(replyMessage)
					.setHeader(MessageHeaders.REPLY_CHANNEL, originalReplyChannelHeader)
					.build();
		}

		return replyMessage;
	}

	/**
	 * A temporary channel for receiving a single reply message.
	 */
	private class TemporaryReplyChannel implements PollableChannel {

		private final Log logger = LogFactory.getLog(TemporaryReplyChannel.class);

		private final CountDownLatch replyLatch = new CountDownLatch(1);

		private volatile Message<?> replyMessage;

		private volatile boolean hasReceived;

		private volatile boolean hasTimedOut;

		private volatile boolean hasSendFailed;

		public void setSendFailed(boolean hasSendError) {
			this.hasSendFailed = hasSendError;
		}

		@Override
		public Message<?> receive() {
			return this.receive(-1);
		}

		@Override
		public Message<?> receive(long timeout) {
			try {
				if (MessagingTemplate.this.receiveTimeout < 0) {
					this.replyLatch.await();
					this.hasReceived = true;
				}
				else {
					if (this.replyLatch.await(MessagingTemplate.this.receiveTimeout, TimeUnit.MILLISECONDS)) {
						this.hasReceived = true;
					}
					else {
						this.hasTimedOut = true;
					}
				}
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return this.replyMessage;
		}

		@Override
		public boolean send(Message<?> message) {
			return this.send(message, -1);
		}

		@Override
		public boolean send(Message<?> message, long timeout) {
			this.replyMessage = message;
			boolean alreadyReceivedReply = this.hasReceived;
			this.replyLatch.countDown();

			String errorDescription = null;
			if (this.hasTimedOut) {
				errorDescription = "Reply message received but the receiving thread has exited due to a timeout";
			}
			else if (alreadyReceivedReply) {
				errorDescription = "Reply message received but the receiving thread has already received a reply";
			}
			else if (this.hasSendFailed) {
				errorDescription = "Reply message received but the receiving thread has exited due to " +
						"an exception while sending the request message";
			}

			if (errorDescription != null) {
				logger.warn(errorDescription + ":" + message);
			}

			return true;
		}
	}

	
}
