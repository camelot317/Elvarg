package com.elvarg.net;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.elvarg.net.codec.PacketDecoder;
import com.elvarg.net.codec.PacketEncoder;
import com.elvarg.net.login.LoginDetailsMessage;
import com.elvarg.net.login.LoginResponsePacket;
import com.elvarg.net.login.LoginResponses;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketBuilder;
import com.elvarg.net.packet.PacketConstants;
import com.elvarg.util.Misc;
import com.elvarg.world.World;
import com.elvarg.world.entity.impl.player.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;

/**
 * The session handler dedicated to a player that will handle input and output
 * operations.
 *
 * @author Lare96
 * @author Swiffy
 */
public class PlayerSession {

	/**
	 * The queue which contains PRIORITIZED packets that will be handled on the
	 * next sequence.
	 */
	private final Queue<Packet> prioritizedPacketsQueue = new ConcurrentLinkedQueue<>();

	/**
	 * The queue of packets that will be handled on the next sequence.
	 */
	private final Queue<Packet> packetsQueue = new ConcurrentLinkedQueue<>();

	/**
	 * The channel that will manage the connection for this player.
	 */
	private final Channel channel;

	/**
	 * The player I/O operations will be executed for.
	 */
	private Player player;

	/**
	 * The current state of this I/O session.
	 */
	private SessionState state = SessionState.LOGGING_IN;

	/**
	 * Creates a new {@link PlayerSession}.
	 *
	 * @param key
	 *            the selection key registered to the selector.
	 * @param response
	 *            the current login response for this session.
	 */
	public PlayerSession(SocketChannel channel) {
		this.channel = channel;
		this.player = new Player(this);
	}

	/**
	 * Attempts to finalize a player's login.
	 * 
	 * @param msg
	 *            The player's login information.
	 */
	public void finalizeLogin(LoginDetailsMessage msg) {
		SocketChannel channel = (SocketChannel) msg.getContext().channel();

		// Update the player
		player.setUsername(msg.getUsername()).setLongUsername(Misc.stringToLong(msg.getUsername()))
				.setPassword(msg.getPassword()).setHostAddress(msg.getHost());

		// Get the response code
		int response = LoginResponses.evaulate(player, msg);

		// Write the response and flush the channel
		ChannelFuture future = channel.writeAndFlush(new LoginResponsePacket(response, player.getRights()));

		// Close the channel after sending the response if it wasn't a
		// successful login
		if (response != LoginResponses.LOGIN_SUCCESSFUL) {
			future.addListener(ChannelFutureListener.CLOSE);
			return;
		}

		// Wait...
		future.awaitUninterruptibly();

		// Replace decoder/encoder to packets
		channel.pipeline().replace("encoder", "encoder", new PacketEncoder(msg.getEncryptor()));

		channel.pipeline().replace("decoder", "decoder", new PacketDecoder(msg.getDecryptor()));

		// Queue the login
		if (!World.getLoginQueue().contains(player)) {
			World.getLoginQueue().add(player);
		}
	}

	/**
	 * Queues a recently decoded packet received from the channel.
	 * 
	 * @param msg
	 *            The packet that should be queued.
	 */
	public void queuePacket(Packet msg) {

		// Are our queues already full?
		// A player may be packet flooding.
		// Simply don't add more packets to the queues.
		int total_size = (packetsQueue.size() + prioritizedPacketsQueue.size());
		if (total_size >= NetworkConstants.PACKET_PROCESS_LIMIT) {
			return;
		}

		// Add the packet to the queue.
		// If it's prioritized, add it to the prioritized queue instead.
		if (msg.getOpcode() == PacketConstants.EQUIP_ITEM_OPCODE
				|| msg.getOpcode() == PacketConstants.FIRST_ITEM_ACTION_OPCODE) {
			prioritizedPacketsQueue.add(msg);
		} else {
			packetsQueue.add(msg);
		}
	}

	/**
	 * Processes all of the queued messages from the {@link PacketDecoder} by
	 * polling the internal queue, and then handling them via the
	 * handleInputMessage. This method is called EACH GAME CYCLE.
	 * 
	 * @param priorityOnly
	 *            Should only prioritized packets be read?
	 */
	public void handleQueuedPackets(boolean priorityOnly) {

		Packet msg;

		int processed = 0;

		while ((msg = prioritizedPacketsQueue.poll()) != null && ++processed < NetworkConstants.PACKET_PROCESS_LIMIT) {
			processPacket(msg);
		}

		if (priorityOnly) {
			return;
		}

		while ((msg = packetsQueue.poll()) != null && ++processed < NetworkConstants.PACKET_PROCESS_LIMIT) {
			processPacket(msg);
		}
	}

	/**
	 * Handles an incoming message.
	 * 
	 * @param msg
	 *            The message to handle.
	 */
	public void processPacket(Packet msg) {
		PacketConstants.PACKETS[msg.getOpcode()].handleMessage(player, msg);
	}

	/**
	 * Queues the {@code msg} for this session to be encoded and sent to the
	 * client.
	 *
	 * @param builder
	 *            the packet to queue.
	 */
	public void write(PacketBuilder builder) {
		try {

			Packet packet = builder.toPacket();
			channel.write(packet);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Processes a packet immediately to be sent to the client.
	 *
	 * @param builder
	 *            the packet to send.
	 */
	public void writeAndFlush(PacketBuilder builder) {
		try {

			Packet packet = builder.toPacket();
			channel.writeAndFlush(packet);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Flushes the channel.
	 */
	public void flush() {
		try {
			channel.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Gets the player I/O operations will be executed for.
	 *
	 * @return the player I/O operations.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the current state of this I/O session.
	 *
	 * @return the current state.
	 */
	public SessionState getState() {
		return state;
	}

	/**
	 * Sets the value for {@link PlayerSession#state}.
	 *
	 * @param state
	 *            the new value to set.
	 */
	public void setState(SessionState state) {
		this.state = state;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
