package jaggrab;

import java.util.logging.Logger;

import com.elvarg.net.NetworkConstants;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import jaggrab.dispatch.RequestWorkerPool;
import jaggrab.net.FileServerHandler;
import jaggrab.net.JagGrabPipelineFactory;
import jaggrab.net.OnDemandPipelineFactory;

/**
 * The core class of the file server.
 * 
 * @author Graham
 * 
 *         06 February 2017 - upgraded to Netty 4.1.8 and other improvements.
 * @author Professor Oak
 */
public final class Jaggrab {

	/**
	 * The logger for this class.
	 */
	private static final Logger logger = Logger.getLogger(Jaggrab.class.getName());

	/**
	 * The entry point of the application.
	 * 
	 * @param args
	 *            The command-line arguments.
	 */
	/*
	 * public static void main(String[] args) { try { new Jaggrab().start(); }
	 * catch (Throwable t) { logger.log(java.util.logging.Level.SEVERE,
	 * "Error starting server.", t); } }
	 */

	/**
	 * The request worker pool.
	 */
	private final RequestWorkerPool pool = new RequestWorkerPool();

	/**
	 * The file server event handler.
	 */
	private final FileServerHandler handler = new FileServerHandler();

	/**
	 * Starts the file server.
	 * 
	 * @throws Exception
	 *             if an error occurs.
	 */
	public void init() throws Exception {
		logger.info("Starting workers...");
		pool.start();

		logger.info("Starting services...");
		try {
			// start("HTTP", new HttpPipelineFactory(handler),
			// NetworkConstants.HTTP_PORT);
		} catch (Throwable t) {
			logger.log(java.util.logging.Level.SEVERE, "Failed to start HTTP service.", t);
			logger.warning(
					"HTTP will be unavailable. JAGGRAB will be used as a fallback by clients but this isn't reccomended!");
		}

		start("JAGGRAB", new JagGrabPipelineFactory(handler), NetworkConstants.JAGGRAB_PORT);
		start("ondemand", new OnDemandPipelineFactory(handler), NetworkConstants.SERVICE_PORT);

		logger.info("Ready for connections.");
	}

	/**
	 * Starts the specified service.
	 * 
	 * @param name
	 *            The name of the service.
	 * @param pipelineFactory
	 *            The pipeline factory.
	 * @param port
	 *            The port.
	 */
	private void start(String name, ChannelInitializer<SocketChannel> pipelineFactory, int port) {

		logger.info("Binding " + name + " service to port " + port + "...");

		ResourceLeakDetector.setLevel(Level.DISABLED);
		EventLoopGroup loopGroup = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(loopGroup).channel(NioServerSocketChannel.class).childHandler(pipelineFactory).bind(port)
				.syncUninterruptibly();
	}
}
