package com.elvarg.net.packet.impl;

import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketConstants;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.content.Consumables;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.teleportation.tabs.TabHandler;


public class ItemActionPacketListener implements PacketListener {

	private static void firstAction(final Player player, Packet packet) {
		int interfaceId = packet.readUnsignedShort();
		int itemId = packet.readShort();
		int slot = packet.readShort();
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
	
		if (Consumables.isFood(player, itemId, slot)) {
			return;
		}	
		TabHandler.onClick(player, itemId);
	}

	public static void secondAction(Player player, Packet packet) {
		int interfaceId = packet.readLEShortA();
		int slot = packet.readLEShort();
		int itemId = packet.readShortA();
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
		switch(itemId) {
		
		}
	}

	public void thirdClickAction(Player player, Packet packet) {
		int itemId = packet.readShortA();
		int slot = packet.readLEShortA();
		int interfaceId = packet.readLEShortA();
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
		
		switch(itemId) {
		
		}
	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getHitpoints() <= 0)
			return;
		switch (packet.getOpcode()) {
		case PacketConstants.SECOND_ITEM_ACTION_OPCODE:
			secondAction(player, packet);
			break;
		case PacketConstants.FIRST_ITEM_ACTION_OPCODE:
			firstAction(player, packet);
			break;
		case PacketConstants.THIRD_ITEM_ACTION_OPCODE:
			thirdClickAction(player, packet);
			break;
		}
	}

}