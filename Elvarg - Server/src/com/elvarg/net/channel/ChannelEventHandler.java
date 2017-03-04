package com.elvarg.net.channel;

import com.elvarg.net.NetworkConstants;
import com.elvarg.net.PlayerSession;
import com.elvarg.net.SessionState;
import com.elvarg.net.login.LoginDetailsMessage;
import com.elvarg.net.packet.Packet;
import com.elvarg.world.World;
import com.elvarg.world.entity.impl.player.Player;
import com.google.common.base.Objects;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * An implementation of netty's {@link SimpleChannelInboundHandler} to handle
 * all of netty's incoming events..
 * 
 * @author Swiffy
 */
@Sharable
public final class ChannelEventHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {

			PlayerSession session = ctx.channel().attr(NetworkConstants.SESSION_KEY).get();

			if (session == null) {
				throw new IllegalStateException("session == null");
			}

			if (msg instanceof LoginDetailsMessage) {
				session.finalizeLogin((LoginDetailsMessage) msg);
			} else if (msg instanceof Packet) {
				session.queuePacket((Packet) msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		PlayerSession session = ctx.channel().attr(NetworkConstants.SESSION_KEY).get();

		if (session == null) {
			throw new IllegalStateException("session == null");
		}

		Player player = session.getPlayer();

		if (player == null) {
			return;
		}

		// Queue the player for logout
		if (player.getSession().getState() == SessionState.LOGGED_IN
				|| player.getSession().getState() == SessionState.REQUESTED_LOG_OUT) {

			if (!World.getLogoutQueue().contains(player)) {

				// After 90 seconds of logout attempts, it will force it.
				player.getLogoutTimer().reset();

				World.getLogoutQueue().add(player);
			}

		}
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
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
		if (!NetworkConstants.IGNORED_NETWORK_EXCEPTIONS.stream().anyMatch($it -> Objects.equal($it, e.getMessage()))) {
			e.printStackTrace();
		}

		ctx.channel().close();
	}

}
