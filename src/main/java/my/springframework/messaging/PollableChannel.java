package my.springframework.messaging;

/**
 * A {@link MessageChannel} from which messages may be actively received through polling.
 *
 * @author Mark Fisher
 * @since 4.0
 */
public interface PollableChannel extends MessageChannel {

	/**
	 * Receive a message from this channel, blocking indefinitely if necessary.
	 * @return the next available {@link Message} or {@code null} if interrupted
	 */
	Message<?> receive();

	/**
	 * Receive a message from this channel, blocking until either a message is available
	 * or the specified timeout period elapses.
	 * @param timeout the timeout in milliseconds or {@link MessageChannel#INDEFINITE_TIMEOUT}.
	 * @return the next available {@link Message} or {@code null} if the specified timeout
	 * period elapses or the message reception is interrupted
	 */
	Message<?> receive(long timeout);

}
