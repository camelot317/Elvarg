package com.elvarg.net.packet.impl;

import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketConstants;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.World;
import com.elvarg.world.entity.impl.player.Player;

/**
 * This packet listener is called when a player has clicked on another player's
 * menu actions.
 * 
 * @author relex lawl
 */

public class PlayerOptionPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.busy()) {
			return;
		}
		switch (packet.getOpcode()) {
		case PacketConstants.ATTACK_PLAYER_OPCODE:
			attack(player, packet);
			break;
		case 128:
			option1(player, packet);
			break;
		case 37:
			option2(player, packet);
			break;
		case 227:
			option3(player, packet);
			break;
		}
	}

	private static void attack(Player player, Packet packet) {
		int index = packet.readLEShort();
		if (index > World.getPlayers().capacity() || index < 0)
			return;
		final Player attacked = World.getPlayers().get(index);

		if (attacked == null || attacked.getHitpoints() <= 0 || attacked.equals(player)) {
			player.getMovementQueue().reset();
			return;
		}

		/*
		 * if(player.getLocation() == Location.DUEL_ARENA &&
		 * player.getDueling().duelingStatus == 0) {
		 * player.getDueling().challengePlayer(attacked); return; }
		 */

		player.getCombat().attack(attacked);
	}

	/**
	 * Manages the first option click on a player option menu.
	 * 
	 * @param player
	 *            The player clicking the other entity.
	 * @param packet
	 *            The packet to read values from.
	 */
	private static void option1(Player player, Packet packet) {
		int id = packet.readShort() & 0xFFFF;
		if (id < 0 || id > World.getPlayers().capacity())
			return;
		Player victim = World.getPlayers().get(id);
		if (victim == null)
			return;
		/*
		 * GameServer.getTaskScheduler().schedule(new WalkToTask(player,
		 * victim.getPosition(), new FinalizedMovementTask() {
		 * 
		 * @Override public void execute() { //do first option here } }));
		 */
	}

	/**
	 * Manages the second option click on a player option menu.
	 * 
	 * @param player
	 *            The player clicking the other entity.
	 * @param packet
	 *            The packet to read values from.
	 */
	private static void option2(Player player, Packet packet) {
		int id = packet.readShort() & 0xFFFF;
		if (id < 0 || id > World.getPlayers().capacity())
			return;
		Player victim = World.getPlayers().get(id);
		if (victim == null)
			return;
		/*
		 * GameServer.getTaskScheduler().schedule(new WalkToTask(player,
		 * victim.getPosition(), new FinalizedMovementTask() {
		 * 
		 * @Override public void execute() { //do second option here } }));
		 */
	}

	/**
	 * Manages the third option click on a player option menu.
	 * 
	 * @param player
	 *            The player clicking the other entity.
	 * @param packet
	 *            The packet to read values from.
	 */
	private static void option3(Player player, Packet packet) {
		int id = packet.readLEShortA() & 0xFFFF;
		if (id < 0 || id > World.getPlayers().capacity())
			return;
		Player victim = World.getPlayers().get(id);
		if (victim == null)
			return;
		/*
		 * GameServer.getTaskScheduler().schedule(new WalkToTask(player,
		 * victim.getPosition(), new FinalizedMovementTask() {
		 * 
		 * @Override public void execute() { //do third option here } }));
		 */
	}
}
