package my.springframework.integration.endpoint;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.context.SmartLifecycle;

/**
 * The base class for Message Endpoint implementations.
 *
 * <p>This class implements Lifecycle and provides an {@link #autoStartup}
 * property. If <code>true</code>, the endpoint will start automatically upon
 * initialization. Otherwise, it will require an explicit invocation of its
 * {@link #start()} method. The default value is <code>true</code>.
 * To require explicit startup, provide a value of <code>false</code>
 * to the {@link #setAutoStartup(boolean)} method.
 * 
 * <p>The Lifecycle interface does not imply specific auto-startup semantics.
 * 
 * <p>The SmartLifecycle is an extension of the Lifecycle interface for those objects that require 
 * to be started upon ApplicationContext refresh and/or shutdown in a particular order. 
 * The isAutoStartup() return value indicates 
 * whether this object should be started at the time of a context refresh.
 * 
 * 
 */
public abstract class AbstractEndpoint implements SmartLifecycle {
	
	private volatile boolean autoStartup = true;

	private volatile int phase = 0;

	private volatile boolean running;

	protected final ReentrantLock lifecycleLock = new ReentrantLock();

	protected final Condition lifecycleCondition = this.lifecycleLock.newCondition();


	public void setAutoStartup(boolean autoStartup) {
		this.autoStartup = autoStartup;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	// SmartLifecycle implementation

	@Override
	public final boolean isAutoStartup() {
		return this.autoStartup;
	}

	@Override
	public final int getPhase() {
		return this.phase;
	}

	@Override
	public final boolean isRunning() {
		this.lifecycleLock.lock();
		try {
			return this.running;
		}
		finally {
			this.lifecycleLock.unlock();
		}
	}

	@Override
	public final void start() {
		this.lifecycleLock.lock();
		try {
			if (!this.running) {
				doStart();
				this.running = true;
			}
		}
		finally {
			this.lifecycleLock.unlock();
		}
	}

	@Override
	public final void stop() {
		this.lifecycleLock.lock();
		try {
			if (this.running) {
				doStop();
				this.running = false;
			}
		}
		finally {
			this.lifecycleLock.unlock();
		}
	}

	@Override
	public final void stop(Runnable callback) {
		this.lifecycleLock.lock();
		try {
			if (this.running) {
				doStop(callback);
				this.running = false;
			}
		}
		finally {
			this.lifecycleLock.unlock();
		}
	}

	/**
	 * Stop the component and invoke callback.
	 * @param callback the Runnable to invoke.
	 */
	protected void doStop(Runnable callback) {
		doStop();
		callback.run();
	}

	/**
	 * Subclasses must implement this method with the start behavior.
	 * This method will be invoked while holding the {@link #lifecycleLock}.
	 */
	protected abstract void doStart();

	/**
	 * Subclasses must implement this method with the stop behavior.
	 * This method will be invoked while holding the {@link #lifecycleLock}.
	 */
	protected abstract void doStop();
}
