package com.elvarg.net.packet.impl;

import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.movement.MovementStatus;

/**
 * Cheap fix for the rare height exploit..
 * 
 * @author Gabriel Hannason
 */

public class HeightCheckPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int plane = packet.readByte();
		if (player.getPosition().getZ() >= 0 && player.getPosition().getZ() < 4) { // Only
																					// check
																					// for
																					// normal
																					// height
																					// levels,
																					// not
																					// minigames
																					// etc

			if (plane != player.getPosition().getZ()) { // Player might have
														// used a
														// third-party-software
														// to change their
														// height level

				if (!player.isNeedsPlacement()
						&& player.getMovementQueue().getMovementStatus() != MovementStatus.DISABLED) { // Only
																										// check
																										// if
																										// player
																										// isn't
																										// being
																										// blocked

					// Player used a third-party-software to change their height
					// level, fix it
					player.getMovementQueue().reset(); // Reset movement
					player.setNeedsPlacement(true); // Block upcoming movement
													// and actions
					player.getPacketSender().sendHeight(); // Send the proper
															// height level
					player.getSkillManager().stopSkilling(); // Stop skilling &
																// walkto tasks
					player.getPacketSender().sendInterfaceRemoval(); // Send
																		// interface
																		// removal
					player.setWalkToTask(null); // Stop walk to tasks
				}
			}
		}
	}
}
