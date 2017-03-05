package com.elvarg.net.packet.impl;

import com.elvarg.cache.impl.definitions.ItemDefinition;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.entity.impl.player.Player;

public class ExamineItemPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int item = packet.readShort();

		if (item == 995) {
			player.getPacketSender().sendMessage(player.getInventory().getAmount(995) + "x coins.");
			return;
		}

		ItemDefinition itemDef = ItemDefinition.forId(item);
		if (itemDef != null) {
			player.getPacketSender().sendMessage(itemDef.getExamine());
		}
	}

}
