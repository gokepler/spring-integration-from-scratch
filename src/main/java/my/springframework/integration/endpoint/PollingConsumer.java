package my.springframework.integration.endpoint;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;

import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.util.Assert;

import my.springframework.messaging.Message;
import my.springframework.messaging.MessageHandler;
import my.springframework.messaging.MessagingException;
import my.springframework.messaging.PollableChannel;

/**
 * Message Endpoint that connects any {@link MessageHandler} implementation
 * to a {@link PollableChannel}.
 */
public class PollingConsumer extends AbstractEndpoint {

	private final PollableChannel inputChannel;

	private final MessageHandler handler;

	private volatile long receiveTimeout = 1000;
	
	
	private volatile ScheduledFuture<?> runningTask;

	private volatile Runnable poller;

	private volatile boolean initialized;

	private volatile long maxMessagesPerPoll = -1;


	public PollingConsumer(PollableChannel inputChannel, MessageHandler handler) {
		Assert.notNull(inputChannel, "inputChannel must not be null");
		Assert.notNull(handler, "handler must not be null");
		this.inputChannel = inputChannel;
		this.handler = handler;
	}


	public void setReceiveTimeout(long receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}

	protected void handleMessage(Message<?> message) {
		Message<?> theMessage = message;
		try {
			this.handler.handleMessage(theMessage);
		}
		catch (Exception ex) {
			if (ex instanceof MessagingException) {
				throw (MessagingException) ex;
			}
			String description = "Failed to handle " + theMessage + " to " + this + " in " + this.handler;
			throw new MessagingException(theMessage, description, ex);
		}
	}

	protected Message<?> receiveMessage() {
		return (this.receiveTimeout >= 0)
				? this.inputChannel.receive(this.receiveTimeout)
				: this.inputChannel.receive();
	}

	
	private Runnable createPoller() throws Exception {
		Callable<Boolean> pollingTask = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return doPoll();
			}
		};
		return new Poller(pollingTask);
	}

	// LifecycleSupport implementation

	private volatile TaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
	private volatile Executor taskExecutor = new SyncTaskExecutor();
	private volatile Trigger trigger = new PeriodicTrigger(10);
	
	@Override 
	public void doStart() {
		if (this.initialized) {
			return;
		}
		Assert.notNull(this.trigger, "Trigger is required");
		try {
			this.poller = this.createPoller();
		}
		catch (Exception e) {
			throw new MessagingException("Failed to create Poller", e);
		}
		this.runningTask = this.taskScheduler.schedule(this.poller, this.trigger);
		this.initialized = true;
	}

	@Override 
	protected void doStop() {
		if (this.runningTask != null) {
			this.runningTask.cancel(true);
		}
		this.runningTask = null;
		this.initialized = false;
	}

	private boolean doPoll() {
		Message<?> message = null;
		try {
			message = this.receiveMessage();
		}
		catch (Exception e) {
			if (Thread.interrupted()) {
				return false;
			}
			else {
				throw (RuntimeException) e;
			}
		}
		boolean result;
		if (message == null) {
			result = false;
		}
		else {
			this.handleMessage(message);
			result = true;
		}
		return result;
	}
	
	private class Poller implements Runnable {

		private final Callable<Boolean> pollingTask;


		public Poller(Callable<Boolean> pollingTask) {
			this.pollingTask = pollingTask;
		}

		@Override
		public void run() {
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					int count = 0;
					while (initialized && (maxMessagesPerPoll <= 0 || count < maxMessagesPerPoll)) {
						try {
							if (!pollingTask.call()) {
								break;
							}
							count++;
						}
						catch (Exception e) {
							if (e instanceof RuntimeException) {
								throw (RuntimeException) e;
							}
							else {
								throw new MessagingException("", e);
							}
						}
					}
				}
			});
		}

	}

}
