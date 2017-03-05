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
	 * @param tablet
	 */
	public static void onClick(Player player, Item tablet) {
		final Optional<TabData> tab = TabData.getTab(tablet);

		// Checks if the tab isn't present, if not perform nothing
		if (!tab.isPresent()) {
			return;
		}

		// Teleport the player to the proper Teleport Tablet location
		if (TeleportHandler.checkReqs(player, tab.get().location())) {

			TeleportHandler.teleport(player, tab.get().location(), TeleportType.TELE_TAB);

			// Removes the Teleport Tablet item from inventory
			player.getInventory().delete(tab.get().getTab());

		}
	}
}