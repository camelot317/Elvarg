package jaggrab.net;

import java.nio.charset.Charset;

import com.elvarg.net.NetworkConstants;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import jaggrab.net.jaggrab.JagGrabRequestDecoder;
import jaggrab.net.jaggrab.JagGrabResponseEncoder;

/**
 * A {@link ChannelPipelineFactory} for the JAGGRAB protocol.
 * 
 * @author Graham
 */
public final class JagGrabPipelineFactory extends ChannelInitializer<SocketChannel> {

	/**
	 * The maximum length of a request, in bytes.
	 */
	private static final int MAX_REQUEST_LENGTH = 8192;

	/**
	 * The character set used in the request.
	 */
	private static final Charset JAGGRAB_CHARSET = Charset.forName("US-ASCII");

	/**
	 * A buffer with two line feed (LF) characters in it.
	 */
	private static final ByteBuf DOUBLE_LINE_FEED_DELIMITER = Unpooled.buffer(2);

	/**
	 * Populates the double line feed buffer.
	 */
	static {
		DOUBLE_LINE_FEED_DELIMITER.writeByte(10);
		DOUBLE_LINE_FEED_DELIMITER.writeByte(10);
	}

	/**
	 * The file server event handler.
	 */
	private final FileServerHandler handler;

	/**
	 * Creates a {@code JAGGRAB} pipeline factory.
	 * 
	 * @param handler
	 *            The file server event handler.
	 * @param timer
	 *            The timer used for idle checking.
	 */
	public JagGrabPipelineFactory(FileServerHandler handler) {
		this.handler = handler;
	}

	@Override
	public void initChannel(SocketChannel channel) throws Exception {

		final ChannelPipeline pipeline = channel.pipeline();

		// decoders
		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(MAX_REQUEST_LENGTH, DOUBLE_LINE_FEED_DELIMITER));
		pipeline.addLast("string-decoder", new StringDecoder(JAGGRAB_CHARSET));
		pipeline.addLast("jaggrab-decoder", new JagGrabRequestDecoder());

		// encoders
		pipeline.addLast("jaggrab-encoder", new JagGrabResponseEncoder());

		// handler
		pipeline.addLast("timeout", new IdleStateHandler(NetworkConstants.SESSION_TIMEOUT, 0, 0));
		pipeline.addLast("handler", handler);

	}

}
