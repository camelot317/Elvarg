package com.elvarg.world.model.teleportation.tabs;

import java.util.Optional;

import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.teleportation.TeleportHandler;
import com.elvarg.world.model.teleportation.TeleportType;

/**
 * Handles the Teleport Tablet event processing
 * 
 * @author Dennis
 *
 */
public class TabHandler {

	/**
	 * Reaction method that handles the initial tablet functions if applicable.
	 * 
	 * @param player
	 * @param tabId
	 */
	public static void onClick(Player player, int tabId) {
		Optional<TabData> tab = TabData.getTab(tabId);

		// Checks if the tab isn't present, if not perform nothing
		if (!tab.isPresent()) {
			return;
		}
		
		// If the player is dead, performs nothing
		if (player == null) {
			return;
		}
		// Teleport the player to the proper Teleport Tablet location
		TeleportHandler.teleport(player, tab.get().location(), TeleportType.TELE_TAB);
		
		// Removes the Teleport Tablet item from inventory 
		player.getInventory().delete(new Item(tab.get().getTab()));
	}
}