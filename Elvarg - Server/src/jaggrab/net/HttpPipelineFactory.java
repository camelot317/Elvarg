package jaggrab.net;

import com.elvarg.net.NetworkConstants;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * A {@link ChannelPipelineFactory} for the HTTP protocol.
 * 
 * @author Graham
 */
public final class HttpPipelineFactory extends ChannelInitializer<SocketChannel> {

	/**
	 * The maximum length of a request, in bytes.
	 */
	private static final int MAX_REQUEST_LENGTH = 8192;

	/**
	 * The file server event handler.
	 */
	private final FileServerHandler handler;

	/**
	 * Creates the HTTP pipeline factory.
	 * 
	 * @param handler
	 *            The file server event handler.
	 * @param timer
	 *            The timer used for idle checking.
	 */
	public HttpPipelineFactory(FileServerHandler handler) {
		this.handler = handler;
	}

	@Override
	public void initChannel(SocketChannel channel) throws Exception {

		final ChannelPipeline pipeline = channel.pipeline();

		// decoders
		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("chunker", new HttpObjectAggregator(MAX_REQUEST_LENGTH));

		// encoders
		pipeline.addLast("encoder", new HttpResponseEncoder());

		// handler
		pipeline.addLast("timeout", new IdleStateHandler(NetworkConstants.SESSION_TIMEOUT, 0, 0));
		pipeline.addLast("handler", handler);

	}

}
