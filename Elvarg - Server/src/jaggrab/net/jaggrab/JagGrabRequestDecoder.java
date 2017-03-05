package jaggrab.net.jaggrab;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * A {@link OneToOneDecoder} for the JAGGRAB protocol.
 * 
 * @author Graham
 */
public final class JagGrabRequestDecoder extends MessageToMessageDecoder<Object> {

	@Override
	protected void decode(ChannelHandlerContext arg0, Object msg, List<Object> out) throws Exception {

		if (msg instanceof String) {
			String str = ((String) msg);
			if (str.startsWith("JAGGRAB /")) {
				String filePath = str.substring(8).trim();
				out.add(new JagGrabRequest(filePath));
			} else {
				throw new Exception("corrupted request line");
			}
		}

	}

}
