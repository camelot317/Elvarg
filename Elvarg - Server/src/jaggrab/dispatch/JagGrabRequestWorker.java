package jaggrab.dispatch;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import jaggrab.net.jaggrab.JagGrabRequest;
import jaggrab.net.jaggrab.JagGrabResponse;

/**
 * A worker which services JAGGRAB requests.
 * 
 * @author Graham
 */
public final class JagGrabRequestWorker extends RequestWorker<JagGrabRequest> {

	@Override
	protected ChannelRequest<JagGrabRequest> nextRequest() throws InterruptedException {
		return RequestDispatcher.nextJagGrabRequest();
	}

	@Override
	protected void service(Channel channel, JagGrabRequest request) throws IOException {

		String path = request.getFilePath();
		ByteBuf buf = ResourceRequester.request(path);

		if (buf == null) {
			channel.close();
		} else {
			channel.writeAndFlush(new JagGrabResponse(buf)).addListener(ChannelFutureListener.CLOSE);
		}
	}
}
