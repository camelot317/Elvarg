package com.elvarg.net.packet.impl;

import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.entity.impl.player.Player;

public class PlayerInactivePacketListener implements PacketListener {

	// CALLED EVERY 3 MINUTES OF INACTIVITY

	@Override
	public void handleMessage(Player player, Packet packet) {

	}
}
