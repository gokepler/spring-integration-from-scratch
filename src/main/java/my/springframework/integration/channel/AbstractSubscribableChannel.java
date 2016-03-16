package my.springframework.integration.channel;

import java.util.concurrent.CopyOnWriteArrayList;

import my.springframework.messaging.MessageHandler;
import my.springframework.messaging.SubscribableChannel;

public abstract class AbstractSubscribableChannel implements SubscribableChannel {
	
	/** 
	 * It is thread-safe, best suited for applications in which set sizes generally
	 * stay small, read-only operations
	 * vastly outnumber mutative operations, and you need
	 * to prevent interference among threads during traversal.
	 */
	protected final CopyOnWriteArrayList<MessageHandler> handlers = new CopyOnWriteArrayList<MessageHandler>();

	@Override
	public boolean subscribe(MessageHandler handler) {
		handlers.add(handler);
		return true;
	}

	@Override
	public boolean unsubscribe(MessageHandler handler) {
		handlers.remove(handler);
		return true;
	}
	
}
