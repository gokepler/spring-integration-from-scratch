package my.springframework.integration.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import my.springframework.messaging.Message;
import my.springframework.messaging.MessageChannel;
import my.springframework.messaging.MessageHeaders;
import my.springframework.messaging.support.GenericMessage;

/**
 * The default message builder; creates immutable {@link GenericMessage}s.
 * Named MessageBuilder instead of DefaultMessageBuilder for backwards
 * compatibility.
 */
public final class MessageBuilder<T> {

	private final T payload;
	private final Map<String, Object> headers; // MessageHeaders is immutable, we should create a Map
	private final Message<T> originalMessage;

	private volatile boolean modified;

	/**
	 * Private constructor to be invoked from the static factory methods only.
	 */
	private MessageBuilder(T payload, Message<T> originalMessage) {
		Assert.notNull(payload, "payload must not be null");
		this.payload = payload;
		this.originalMessage = originalMessage;
		this.headers = new HashMap<>(); 
		if (originalMessage != null) {
			this.headers.putAll(originalMessage.getHeaders());
			this.modified = (!this.payload.equals(originalMessage.getPayload()));
		} 
	}

	/**
	 * Create a builder for a new {@link Message} instance pre-populated with all of the headers copied from the
	 * provided message. The payload of the provided Message will also be used as the payload for the new message.
	 *
	 * @param message the Message from which the payload and all headers will be copied
	 * @param <T> The type of the payload.
	 * @return A MessageBuilder.
	 */
	public static <T> MessageBuilder<T> fromMessage(Message<T> message) {
		Assert.notNull(message, "message must not be null");
		MessageBuilder<T> builder = new MessageBuilder<T>(message.getPayload(), message);
		return builder;
	}

	/**
	 * Create a builder for a new {@link Message} instance with the provided payload.
	 *
	 * @param payload the payload for the new message
	 * @param <T> The type of the payload.
	 * @return A MessageBuilder.
	 */
	public static <T> MessageBuilder<T> withPayload(T payload) {
		MessageBuilder<T> builder = new MessageBuilder<T>(payload, null);
		return builder;
	}

	/**
	 * Retrieve the value for the header with the given name.
	 * @param headerName the name of the header
	 * @return the associated value, or {@code null} if none found
	 */
	private Object getHeader(String headerName) {
		return this.headers.get(headerName);
	}
	
	/**
	 * Set the value for the given header name. If the provided value is <code>null</code>, the header will be removed.
	 */
	public MessageBuilder<T> setHeader(String name, Object value) {
		if (isReadOnly(name)) {
			throw new IllegalArgumentException("'" + name + "' header is read-only");
		}
		if (!ObjectUtils.nullSafeEquals(value, getHeader(name))) {
			this.modified = true;
			if (value != null) {
				this.headers.put(name, value);
			}
			else {
				this.headers.remove(name);
			}
		}
		return this;
	}

	/**
	 * Copy the name-value pairs from the provided Map. This operation will overwrite any existing values. Use {
	 * {@link #copyHeadersIfAbsent(Map)} to avoid overwriting values. Note that the 'id' and 'timestamp' header values
	 * will never be overwritten.
	 *
	 * @param headersToCopy The headers to copy.
	 * @return this MessageBuilder.
	 *
	 * @see MessageHeaders#ID
	 * @see MessageHeaders#TIMESTAMP
	 */
	public MessageBuilder<T> copyHeaders(Map<String, ?> headersToCopy) {
		if (headersToCopy != null) {
			for (Map.Entry<String, ?> entry : headersToCopy.entrySet()) {
				if (!isReadOnly(entry.getKey())) {
					setHeader(entry.getKey(), entry.getValue());
				}
			}
		}
		return this;
	}

	public MessageBuilder<T> setReplyChannel(MessageChannel replyChannel) {
		return this.setHeader(MessageHeaders.REPLY_CHANNEL, replyChannel);
	}

	public Message<T> build() {
		if (!this.modified && this.originalMessage != null) {
			return this.originalMessage;
		}
		return new GenericMessage<T>(this.payload, new HashMap<String, Object>(this.headers));
	}

	protected boolean isReadOnly(String headerName) {
		return MessageHeaders.ID.equals(headerName);
	}
	
}