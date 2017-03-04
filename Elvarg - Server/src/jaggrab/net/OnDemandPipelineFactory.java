package jaggrab.net;

import com.elvarg.net.NetworkConstants;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jaggrab.net.ondemand.OnDemandRequestDecoder;
import jaggrab.net.ondemand.OnDemandResponseEncoder;
import jaggrab.net.service.ServiceRequestDecoder;
import jaggrab.net.service.ServiceResponseEncoder;

/**
 * A {@link ChannelPipelineFactory} for the 'on-demand' protocol.
 * 
 * @author Graham
 */
public final class OnDemandPipelineFactory extends ChannelInitializer<SocketChannel> {

	/**
	 * The file server event handler.
	 */
	private final FileServerHandler handler;

	/**
	 * Creates an 'on-demand' pipeline factory.
	 * 
	 * @param handler
	 *            The file server event handler.
	 * @param timer
	 *            The timer used for idle checking.
	 */
	public OnDemandPipelineFactory(FileServerHandler handler) {
		this.handler = handler;
	}

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		final ChannelPipeline pipeline = channel.pipeline();

		// decoders
		pipeline.addLast("serviceDecoder", new ServiceRequestDecoder());
		pipeline.addLast("decoder", new OnDemandRequestDecoder());

		// encoders
		pipeline.addLast("serviceEncoder", new ServiceResponseEncoder());
		pipeline.addLast("encoder", new OnDemandResponseEncoder());

		// handler
		pipeline.addLast("timeout", new IdleStateHandler(NetworkConstants.SESSION_TIMEOUT, 0, 0));
		pipeline.addLast("handler", handler);

	}

}
