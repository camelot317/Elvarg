package jaggrab.dispatch;

import java.io.IOException;

import io.netty.channel.Channel;

/**
 * The base class for request workers.
 * 
 * @author Graham
 * @param <T>
 *            The type of request.
 */
public abstract class RequestWorker<T> implements Runnable {

	/**
	 * An object used for locking checks to see if the worker is running.
	 */
	private final Object lock = new Object();

	/**
	 * A flag indicating if the worker should be running.
	 */
	private boolean running = true;

	/**
	 * Stops this worker. The worker's thread may need to be interrupted.
	 */
	public final void stop() {
		synchronized (lock) {
			running = false;
		}
	}

	@Override
	public final void run() {
		while (true) {

			synchronized (lock) {
				if (!running) {
					break;
				}
			}

			ChannelRequest<T> request;
			try {
				request = nextRequest();
			} catch (InterruptedException e) {
				continue;
			}

			Channel channel = request.getChannel();

			try {
				service(channel, request.getRequest());
			} catch (IOException e) {
				e.printStackTrace();
				channel.close();
			}
		}
	}

	/**
	 * Gets the next request.
	 * 
	 * @return The next request.
	 * @throws InterruptedException
	 *             if the thread is interrupted.
	 */
	protected abstract ChannelRequest<T> nextRequest() throws InterruptedException;

	/**
	 * Services a request.
	 * 
	 * @param fs
	 *            The file system.
	 * @param channel
	 *            The channel.
	 * @param request
	 *            The request to service.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	protected abstract void service(Channel channel, T request) throws IOException;

}
