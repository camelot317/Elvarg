package jaggrab.net;

import com.elvarg.net.NetworkConstants;
import com.google.common.base.Objects;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import jaggrab.dispatch.RequestDispatcher;
import jaggrab.net.jaggrab.JagGrabRequest;
import jaggrab.net.ondemand.OnDemandRequest;
import jaggrab.net.service.ServiceRequest;
import jaggrab.net.service.ServiceResponse;

/**
 * An {@link IdleStateAwareChannelUpstreamHandler} for the {@link FileServer}.
 * 
 * @author Graham
 */
@Sharable
public final class FileServerHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
		if (!NetworkConstants.IGNORED_NETWORK_EXCEPTIONS.stream().anyMatch($it -> Objects.equal($it, e.getMessage()))) {
			e.printStackTrace();
		}

		ctx.channel().close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				ctx.channel().close();
			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// System.out.println("Channel closed");
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof ServiceRequest) {
			ServiceRequest request = (ServiceRequest) msg;
			if (request.getId() != ServiceRequest.SERVICE_ONDEMAND) {
				ctx.channel().close();
			} else {
				ctx.channel().writeAndFlush(new ServiceResponse());
			}
		} else if (msg instanceof OnDemandRequest) {
			RequestDispatcher.dispatch(ctx.channel(), (OnDemandRequest) msg);
		} else if (msg instanceof JagGrabRequest) {
			RequestDispatcher.dispatch(ctx.channel(), (JagGrabRequest) msg);
		} else if (msg instanceof HttpRequest) {
			RequestDispatcher.dispatch(ctx.channel(), (HttpRequest) msg);
		} else {
			throw new Exception("unknown message type");
		}
	}

}
