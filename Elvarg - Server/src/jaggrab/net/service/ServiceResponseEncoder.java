package jaggrab.net.service;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * A {@link OneToOneEncoder} which encodes {@link ServiceResponse} messages.
 * 
 * @author Graham
 */
public final class ServiceResponseEncoder extends MessageToMessageEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {

		if (msg instanceof ServiceResponse) {
			ByteBuf buf = Unpooled.buffer(8);
			buf.writeLong(0);
			out.add(buf);
		} else {
			out.add(msg);
		}
	}
}
