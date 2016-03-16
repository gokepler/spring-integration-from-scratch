package my.springframework.integration.channel;

import java.util.concurrent.Executor;

import my.springframework.messaging.Message;
import my.springframework.messaging.MessageHandler;

public class PublishSubscribeChannel extends AbstractSubscribableChannel {

	/** 
	 * It is thread-safe, best suited for applications in which set sizes generally
	 * stay small, read-only operations
	 * vastly outnumber mutative operations, and you need
	 * to prevent interference among threads during traversal.
	 */
	private static Executor CALLER_RUNS = runnable -> runnable.run();
	
	private Executor executor;
	
	public PublishSubscribeChannel() {
		this(CALLER_RUNS);
	}
	
	public PublishSubscribeChannel(Executor executor) {
		this.executor = executor;
	}
	
	@Override
	public boolean send(Message<?> message) {
		return send(message, -1);
	}

	@Override
	public boolean send(Message<?> message, long timeout) {
		for (MessageHandler handler : handlers) {
			executor.execute(() -> handler.handleMessage(message));
		}
		return true;
	}
	
}
