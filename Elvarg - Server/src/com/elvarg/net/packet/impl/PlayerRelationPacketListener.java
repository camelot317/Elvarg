package com.elvarg.net.packet.impl;

import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketConstants;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.util.Misc;
import com.elvarg.world.World;
import com.elvarg.world.entity.impl.player.Player;

/**
 * This packet listener is called when a player is doing something relative to
 * their friends or ignore list, such as adding or deleting a player from said
 * list.
 * 
 * @author relex lawl
 */

public class PlayerRelationPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		try {
			long username = packet.readLong();
			switch (packet.getOpcode()) {
			case PacketConstants.ADD_FRIEND_OPCODE:
				player.getRelations().addFriend(username);
				break;
			case PacketConstants.ADD_IGNORE_OPCODE:
				player.getRelations().addIgnore(username);
				break;
			case PacketConstants.REMOVE_FRIEND_OPCODE:
				player.getRelations().deleteFriend(username);
				break;
			case PacketConstants.REMOVE_IGNORE_OPCODE:
				player.getRelations().deleteIgnore(username);
				break;
			case PacketConstants.SEND_PM_OPCODE:
				Player friend = World
						.getPlayerByName(Misc.formatText(Misc.longToString(username)).replaceAll("_", " "));
				String message = packet.readString();
				player.getRelations().message(friend, message);
				break;
			}
		} catch (Exception e) {

		}
	}
}
