package my.springframework.integration.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import my.springframework.messaging.Message;
import my.springframework.messaging.MessageHandler;
import my.springframework.messaging.MessagingException;

public class DirectChannel extends AbstractSubscribableChannel {
	
	@Override
	public boolean send(Message<?> message) {
		return send(message, -1);
	}

	@Override
	public boolean send(Message<?> message, long timeout) {
		// Simple version:
		// handlers.first().handleMessage(message)
		// 
		// - Try the next handler if one fails.
		// - Wrap exception 
		boolean success = false;
		Iterator<MessageHandler> handlerIterator = handlers.iterator();
		List<RuntimeException> exceptions = new ArrayList<RuntimeException>();
		while (!success && handlerIterator.hasNext()) {
			MessageHandler handler = handlerIterator.next();
			try {
				handler.handleMessage(message);
				success = true; // we have a winner.
			}
			catch (Exception e) {
				RuntimeException runtimeException = wrapExceptionIfNecessary(message, e);
				exceptions.add(runtimeException);
				if (!handlerIterator.hasNext()) {
					throw new MessagingException(message, "All attempts to deliver Message to MessageHandlers failed.");
				}
			}
		}
		return success;
	}
	
	private RuntimeException wrapExceptionIfNecessary(Message<?> message, Exception e) {
		RuntimeException runtimeException = (e instanceof RuntimeException)
				? (RuntimeException) e
				: new MessagingException(message, "Dispatcher failed to deliver Message.", e);
		return runtimeException;
	}

}
