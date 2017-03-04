package jaggrab.net.ondemand;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import jaggrab.net.FileDescriptor;
import jaggrab.net.ondemand.OnDemandRequest.Priority;

/**
 * A {@link FrameDecoder} for the 'on-demand' protocol.
 * 
 * @author Graham
 */
public final class OnDemandRequestDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {

		if (buf.readableBytes() >= 6) {
			int type = buf.readUnsignedByte() + 1;
			int file = buf.readInt();
			int priority = buf.readUnsignedByte();

			FileDescriptor desc = new FileDescriptor(type, file);
			Priority p = Priority.valueOf(priority);

			out.add(new OnDemandRequest(desc, p));
		}
	}

}
