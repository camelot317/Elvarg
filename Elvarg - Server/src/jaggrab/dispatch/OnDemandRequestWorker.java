package jaggrab.dispatch;

import java.io.IOException;

import com.elvarg.Elvarg;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import jaggrab.net.FileDescriptor;
import jaggrab.net.ondemand.OnDemandRequest;
import jaggrab.net.ondemand.OnDemandResponse;

/**
 * A worker which services 'on-demand' requests.
 * 
 * @author Graham
 */
public final class OnDemandRequestWorker extends RequestWorker<OnDemandRequest> {

	/**
	 * The maximum length of a chunk, in bytes.
	 */
	private static final int CHUNK_LENGTH = 500;

	@Override
	protected ChannelRequest<OnDemandRequest> nextRequest() throws InterruptedException {
		return RequestDispatcher.nextOnDemandRequest();
	}

	@Override
	protected void service(Channel channel, OnDemandRequest request) throws IOException {
		FileDescriptor desc = request.getFileDescriptor();

		ByteBuf buf = Elvarg.getCache().getFile(desc);
		int length = buf.readableBytes();

		for (int chunk = 0; buf.readableBytes() > 0; chunk++) {
			int chunkSize = buf.readableBytes();
			if (chunkSize > CHUNK_LENGTH) {
				chunkSize = CHUNK_LENGTH;
			}

			byte[] tmp = new byte[chunkSize];
			buf.readBytes(tmp, 0, tmp.length);
			ByteBuf chunkData = Unpooled.wrappedBuffer(tmp, 0, chunkSize);

			OnDemandResponse response = new OnDemandResponse(desc, length, chunk, chunkData);
			channel.writeAndFlush(response);
		}
	}

}
