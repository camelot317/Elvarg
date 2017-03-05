package jaggrab.net.jaggrab;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * A {@link OneToOneEncoder} for the JAGGRAB protocol.
 * 
 * @author Graham
 */
public final class JagGrabResponseEncoder extends MessageToByteEncoder<JagGrabResponse> {

	@Override
	protected void encode(ChannelHandlerContext ctx, JagGrabResponse msg, ByteBuf out) throws Exception {

		ByteBuf file = msg.getFileData();
		int file_size = file.readableBytes();

		out.writeInt(file_size);
		out.writeBytes(msg.getFileData());
	}
}
