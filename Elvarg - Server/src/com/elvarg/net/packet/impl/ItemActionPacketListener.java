package com.elvarg.net.packet.impl;

import com.elvarg.cache.impl.definitions.ItemDefinition;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketConstants;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.content.Consumables;
import com.elvarg.world.content.skills.herblore.HerbIdentification;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.teleportation.tabs.TabHandler;

@SuppressWarnings("unused")
public class ItemActionPacketListener implements PacketListener {

	private static void firstAction(final Player player, Packet packet) {
		int interfaceId = packet.readUnsignedShort();
		int itemId = packet.readShort();
		int slot = packet.readShort();
		final Item interacted = player.getInventory().forSlot(slot);
		if (interacted == null || interacted.getId() != itemId || interacted.getSlot() != slot) {
			return;
		}
		if (Consumables.isFood(player, interacted)) {
			return;
		}
		if (ItemDefinition.forId(interacted.getId()).getName().contains("Grimy")) {
			HerbIdentification.cleanHerb(player, interacted);
			return;
		}
		TabHandler.onClick(player, interacted);
		switch (interacted.getId()) {

		}
	}

	public static void secondAction(Player player, Packet packet) {
		int interfaceId = packet.readLEShortA();
		int slot = packet.readLEShort();
		int itemId = packet.readShortA();
		final Item interacted = player.getInventory().forSlot(slot);
		if (interacted == null || interacted.getId() != itemId || interacted.getSlot() != slot) {
			return;
		}
		switch (interacted.getId()) {

		}
	}

	public void thirdClickAction(Player player, Packet packet) {
		int itemId = packet.readShortA();
		int slot = packet.readLEShortA();
		int interfaceId = packet.readLEShortA();
		final Item interacted = player.getInventory().forSlot(slot);
		if (interacted == null || interacted.getId() != itemId || interacted.getSlot() != slot) {
			return;
		}
		switch (interacted.getId()) {

		}
	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getHitpoints() <= 0) {
			return;
		}
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