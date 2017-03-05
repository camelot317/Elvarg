package jaggrab.net.service;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * A {@link FrameDecoder} which decodes {@link ServiceRequest} messages.
 * 
 * @author Graham
 */
public final class ServiceRequestDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> decoded) throws Exception {
		if (buf.isReadable()) {

			ServiceRequest request = new ServiceRequest(buf.readUnsignedByte());

			ChannelPipeline pipeline = ctx.pipeline();
			pipeline.remove(this);

			if (buf.isReadable()) {
				decoded.add(new Object[] { request, buf.readBytes(buf.readableBytes()) });
			} else {
				decoded.add(request);
			}

		}
	}

}
