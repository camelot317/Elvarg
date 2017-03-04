package com.elvarg.net.packet.impl;

import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.grounditems.GroundItemManager;
import com.elvarg.world.model.GroundItem;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.container.impl.Inventory;

/**
 * This packet listener is called when a player drops an item they have placed
 * in their inventory.
 * 
 * @author relex lawl
 */

public class DropItemPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int id = packet.readUnsignedShortA();
		int interface_id = packet.readUnsignedShort();
		int itemSlot = packet.readUnsignedShortA();

		if (interface_id != Inventory.INTERFACE_ID) {
			return;
		}

		if (player.getHitpoints() <= 0 || player.getInterfaceId() > 0)
			return;
		if (itemSlot < 0 || itemSlot > player.getInventory().capacity())
			return;

		if (player.busy()) {
			player.getPacketSender().sendMessage("You cannot do this right now.");
			return;
		}

		Item item = player.getInventory().getItems()[itemSlot];
		if (item == null)
			return;
		if (item.getId() != id || item.getAmount() <= 0) {
			return;
		}
		player.getPacketSender().sendInterfaceRemoval();

		if (item.getDefinition().isDropable()) {

			player.getInventory().setItem(itemSlot, new Item(-1, 0)).refreshItems();
			GroundItemManager.spawnGroundItem(player,
					new GroundItem(item, player.getPosition().copy(), player.getUsername(), player.getHostAddress(),
							false, 80,
							player.getPosition().getZ() >= 0 && player.getPosition().getZ() < 4 ? true : false, 80));

		} else {
			destroyItemInterface(player, item);
		}
	}

	public static void destroyItemInterface(Player player, Item item) {// Destroy
																		// item
																		// created
																		// by
																		// Remco
		player.setDestroyItem(item.getId());
		String[][] info = { // The info the dialogue gives
				{ "Are you sure you want to discard this item?", "14174" }, { "Yes.", "14175" }, { "No.", "14176" },
				{ "", "14177" }, { "This item will vanish once it hits the floor.", "14182" },
				{ "You cannot get it back if discarded.", "14183" }, { item.getDefinition().getName(), "14184" } };
		player.getPacketSender().sendItemOnInterface(14171, item.getId(), 0, item.getAmount());
		for (int i = 0; i < info.length; i++)
			player.getPacketSender().sendString(Integer.parseInt(info[i][1]), info[i][0]);
		player.getPacketSender().sendChatboxInterface(14170);
	}
}
