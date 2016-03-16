package my.springframework.messaging;

/**
 * Contract for handling a {@link Message}.
 */
public interface MessageHandler {

	/**
	 * Handle the given message.
	 * @param message the message to be handled
	 */
	void handleMessage(Message<?> message);

}
