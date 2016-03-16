package my.springframework.messaging;

/**
 * The base exception for any failures related to messaging.
 */
@SuppressWarnings("serial")
public class MessagingException extends RuntimeException {

	private final Message<?> failedMessage;

	public MessagingException(Message<?> message, String description) {
		super(description);
		this.failedMessage = message;
	}

	public MessagingException(Message<?> message, String description, Throwable cause) {
		super(description, cause);
		this.failedMessage = message;
	}
	
	public MessagingException(String description, Throwable cause) {
		this(null, description, cause);
	}

	public Message<?> getFailedMessage() {
		return this.failedMessage;
	}

}
