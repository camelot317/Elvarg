package com.elvarg.net.packet.impl;

import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketConstants;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Position;
import com.elvarg.world.model.movement.MovementStatus;

/**
 * This packet listener is called when a player has clicked on either the
 * mini-map or the actual game map to move around.
 * 
 * @author Gabriel Hannason
 */

public class MovementPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int size = packet.getSize();

		/*
		 * if (packet.getOpcode() == 248) size -= 14;
		 */
		player.getMovementQueue().setFollowCharacter(null);
		player.getSkillManager().stopSkilling();

		if (player.getInteractingEntity() != null) {
			player.setEntityInteraction(null);
		}

		player.setWalkToTask(null);

		player.getCombat().setCastSpell(null);
		player.getCombat().reset();

		if (packet.getOpcode() != PacketConstants.COMMAND_MOVEMENT_OPCODE) {

		}

		if (!checkReqs(player, packet.getOpcode()))
			return;

		player.getPacketSender().sendInterfaceRemoval();

		final int steps = (size - 5) / 2;
		if (steps < 0)
			return;
		final int firstStepX = packet.readLEShortA();
		final int[][] path = new int[steps][2];
		for (int i = 0; i < steps; i++) {
			path[i][0] = packet.readByte();
			path[i][1] = packet.readByte();
		}
		final int firstStepY = packet.readLEShort();
		final Position[] positions = new Position[steps + 1];
		positions[0] = new Position(firstStepX, firstStepY, player.getPosition().getZ());

		boolean invalidStep = false;

		if (player.getPosition().getDistance(positions[0]) >= 22) {
			invalidStep = true;
		} else {
			for (int i = 0; i < steps; i++) {
				positions[i + 1] = new Position(path[i][0] + firstStepX, path[i][1] + firstStepY,
						player.getPosition().getZ());
				if (!positions[i + 1].isWithinDistance(player.getPosition(), 40)) {
					invalidStep = true;
					break;
				}
			}
		}

		if (invalidStep) {
			player.getMovementQueue().reset();
			// System.out.println(""+player.getUsername()+" invalid step. Steps:
			// "+steps+" Position0: "+positions[0]);
			return;
		}

		// System.out.println("Walk to: "+positions[0]+", steps: "+steps);
		if (player.getMovementQueue().addFirstStep(positions[0])) {
			for (int i = 1; i < positions.length; i++) {
				player.getMovementQueue().addStep(positions[i]);
			}
		}
	}

	public boolean checkReqs(Player player, int opcode) {
		if (!player.getCombat().getFreezeTimer().finished()) {
			if (opcode != PacketConstants.COMMAND_MOVEMENT_OPCODE)
				player.getPacketSender().sendMessage("A magical spell has made you unable to move.");
			return false;
		}
		/*
		 * if(player.getTrading().inTrade() && System.currentTimeMillis() -
		 * player.getTrading().lastAction <= 1000) { return false; }
		 * if(Dueling.checkRule(player, DuelRule.NO_MOVEMENT)) { if(opcode !=
		 * COMMAND_MOVEMENT_OPCODE) player.getPacketSender().
		 * sendMessage("Movement has been turned off in this duel!"); return
		 * false; } if(player.isPlayerLocked() || player.isCrossingObstacle())
		 * return false;
		 */
		if (player.isNeedsPlacement()) {
			return false;
		}
		return player.getMovementQueue().getMovementStatus() != MovementStatus.DISABLED;
	}
}