package my.springframework.integration.channel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.util.Assert;

import my.springframework.messaging.Message;
import my.springframework.messaging.MessagingException;
import my.springframework.messaging.PollableChannel;

public class QueueChannel implements PollableChannel {
	
	private final BlockingQueue<Message<?>> queue;

	/**
	 * Create a channel with "unbounded" queue capacity. The actual capacity value is
	 * {@link Integer#MAX_VALUE}. Note that a bounded queue is recommended, since an
	 * unbounded queue may lead to OutOfMemoryErrors.
	 */
	public QueueChannel() {
		this.queue = new LinkedBlockingQueue<Message<?>>();
	}
	
	
	/**
	 * Receive the first available message from this channel. If the channel
	 * contains no messages, this method will block.
	 *
	 * @return the first available message or <code>null</code> if the
	 * receiving thread is interrupted.
	 */
	@Override
	public final Message<?> receive() {
		return receive(-1);
	}

	/**
	 * Receive the first available message from this channel. If the channel
	 * contains no messages, this method will block until the allotted timeout
	 * elapses. If the specified timeout is 0, the method will return
	 * immediately. If less than zero, it will block indefinitely (see
	 * {@link #receive()}).
	 *
	 * @param timeout the timeout in milliseconds
	 *
	 * @return the first available message or <code>null</code> if no message
	 * is available within the allotted time or the receiving thread is
	 * interrupted.
	 */
	@Override
	public final Message<?> receive(long timeout) {
		try {
			if (timeout > 0) {
				return this.queue.poll(timeout, TimeUnit.MILLISECONDS);
			}
			if (timeout == 0) {
				return this.queue.poll();
			}

			return this.queue.take();
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}
	}
	
	
	/**
	 * Send a message on this channel. If the channel is at capacity, this
	 * method will block until either space becomes available or the sending
	 * thread is interrupted.
	 *
	 * @param message the Message to send
	 *
	 * @return <code>true</code> if the message is sent successfully or
	 * <code>false</code> if the sending thread is interrupted.
	 */
	@Override
	public final boolean send(Message<?> message) {
		return this.send(message, -1);
	}

	/**
	 * Send a message on this channel. If the channel is at capacity, this
	 * method will block until either the timeout occurs or the sending thread
	 * is interrupted. If the specified timeout is 0, the method will return
	 * immediately. If less than zero, it will block indefinitely (see
	 * {@link #send(Message)}).
	 *
	 * @param message the Message to send
	 * @param timeout the timeout in milliseconds
	 *
	 * @return <code>true</code> if the message is sent successfully,
	 * <code>false</code> if the message cannot be sent within the allotted
	 * time or the sending thread is interrupted.
	 */
	@Override
	public final boolean send(Message<?> message, long timeout) {
		Assert.notNull(message, "message must not be null");
		Assert.notNull(message.getPayload(), "message payload must not be null");

		try {
			try {
				if (timeout > 0) {
					return this.queue.offer(message, timeout, TimeUnit.MILLISECONDS);
				}
				if (timeout == 0) {
					return this.queue.offer(message);
				}
				this.queue.put(message);
				return true;
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		catch (Exception e) {
			throw new MessagingException(message, "Failed to send Message.", e);
		}
	}

}
