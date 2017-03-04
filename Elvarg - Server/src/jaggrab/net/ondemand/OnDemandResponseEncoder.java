package jaggrab.net.ondemand;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import jaggrab.net.FileDescriptor;

/**
 * A {@link OneToOneEncoder} for the 'on-demand' protocol.
 * 
 * @author Graham
 */
public final class OnDemandResponseEncoder extends MessageToMessageEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {

		if (msg instanceof OnDemandResponse) {
			OnDemandResponse resp = (OnDemandResponse) msg;

			FileDescriptor fileDescriptor = resp.getFileDescriptor();
			int fileSize = resp.getFileSize();
			int chunkId = resp.getChunkId();

			ByteBuf chunkData = resp.getChunkData();

			ByteBuf buf = Unpooled.buffer(6 + chunkData.readableBytes());
			buf.writeByte(fileDescriptor.getType() - 1);
			buf.writeMedium(fileDescriptor.getFile());
			buf.writeInt(fileSize);
			buf.writeShort(chunkId);
			buf.writeBytes(chunkData);

			// We must retain the buffer before sending it.
			// Cannot send a raw bytebuffer!
			out.add(buf.retain());

		} else {
			out.add(msg);
		}
	}
}
