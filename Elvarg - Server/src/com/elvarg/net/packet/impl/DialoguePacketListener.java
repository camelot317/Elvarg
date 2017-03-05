package com.elvarg.net.packet.impl;

import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketConstants;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.dialogue.DialogueManager;

/**
 * This packet listener handles player's mouse click on the "Click here to
 * continue" option, etc.
 * 
 * @author relex lawl
 */

public class DialoguePacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case PacketConstants.DIALOGUE_OPCODE:
			DialogueManager.next(player);
			break;
		}
	}
}
