package com.elvarg.world.model.teleportation.operational;

import com.elvarg.world.entity.impl.player.Player;

/**
 * Illustrates any specified item operations within the equipment interface.
 * 
 * @author Dennis
 *
 */
public interface Operationable {

	/**
	 * Defines the Item identification, to later be checked if the item being
	 * operated is equivalent to {@link #itemId()}.
	 * 
	 * @return itemId
	 */
	public int itemId();

	/**
	 * Handles the executional event when the player operations the specified
	 * {@link #itemId()}.
	 * 
	 * @param player
	 */
	public void executeOperationalItem(Player player);
}