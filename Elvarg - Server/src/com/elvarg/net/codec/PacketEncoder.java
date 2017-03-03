package com.elvarg.net.codec;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.security.IsaacRandom;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Encodes packets before they're sent to the channel.
 * @author Swiffy
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {

	/**
	 * The encoder used for encryption of the packet.
	 */
	private final IsaacRandom encoder;
	
	/**
	 * The GamePacketEncoder constructor.
	 * @param encoder	The encoder used for the packets.
	 */
	public PacketEncoder(IsaacRandom encoder) {
		this.encoder = encoder;
	}


	@Override
	protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out)
			throws Exception {
		
		final int opcode = (packet.getOpcode() + encoder.nextInt()) & 0xFF;
		final int size = packet.getSize();
		final ByteBuf buf = packet.getBuffer();
		
		out.writeByte(opcode);
		out.writeShort(size);
		out.writeBytes(buf);

	}

}
