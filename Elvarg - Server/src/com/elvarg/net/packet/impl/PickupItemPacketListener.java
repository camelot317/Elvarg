package com.elvarg.net.packet.impl;

import com.elvarg.cache.impl.definitions.ItemDefinition;
import com.elvarg.engine.task.impl.WalkToTask;
import com.elvarg.engine.task.impl.WalkToTask.FinalizedMovementTask;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.grounditems.GroundItemManager;
import com.elvarg.world.model.GroundItem;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.Position;

/**
 * This packet listener is used to pick up ground items that exist in the world.
 * 
 * @author relex lawl
 */

public class PickupItemPacketListener implements PacketListener {

	@Override
	public void handleMessage(final Player player, Packet packet) {
		final int y = packet.readLEShort();
		final int itemId = packet.readShort();
		final int x = packet.readLEShort();
		final Position position = new Position(x, y, player.getPosition().getZ());
		if (!player.getLastItemPickup().elapsed(500))
			return;
		if (player.busy())
			return;
		player.setWalkToTask(new WalkToTask(player, position, 1, new FinalizedMovementTask() {
			@Override
			public void execute() {
				if (Math.abs(player.getPosition().getX() - x) > 25 || Math.abs(player.getPosition().getY() - y) > 25) {
					player.getMovementQueue().reset();
					return;
				}
				boolean canPickup = player.getInventory().getFreeSlots() > 0
						|| (player.getInventory().getFreeSlots() == 0 && ItemDefinition.forId(itemId).isStackable()
								&& player.getInventory().contains(itemId));
				if (!canPickup) {
					player.getInventory().full();
					return;
				}
				GroundItem gItem = GroundItemManager.getGroundItem(player, new Item(itemId), position);
				if (gItem != null) {
					if (player.getInventory().getAmount(gItem.getItem().getId())
							+ gItem.getItem().getAmount() > Integer.MAX_VALUE
							|| player.getInventory().getAmount(gItem.getItem().getId())
									+ gItem.getItem().getAmount() <= 0) {
						player.getPacketSender()
								.sendMessage("You cannot hold that amount of this item. Clear your inventory!");
						return;
					}
					GroundItemManager.pickupGroundItem(player, new Item(itemId),
							new Position(x, y, player.getPosition().getZ()));
				}
			}
		}));
	}
}
