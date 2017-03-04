package com.elvarg.net.packet.impl;

import com.elvarg.cache.impl.definitions.NpcDefinition;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.entity.impl.player.Player;

public class ExamineNpcPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int npc = packet.readShort();
		if (npc <= 0) {
			return;
		}
		System.out.println("NPC: " + npc);
		NpcDefinition npcDef = NpcDefinition.forId(npc);
		if (npcDef != null) {
			player.getPacketSender().sendMessage(npcDef.getExamine());
		}
	}

}
