package com.elvarg.world.model.container.impl;

import java.util.Optional;

import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.container.ItemContainer;
import com.elvarg.world.model.container.StackType;

/**
 * Represents a player's inventory item container.
 * 
 * @author relex lawl
 */

public class Inventory extends ItemContainer {

	/**
	 * The Inventory constructor.
	 * 
	 * @param player
	 *            The player who's inventory is being represented.
	 */
	public Inventory(Player player) {
		super(player);
	}

	@Override
	public int capacity() {
		return 28;
	}

	@Override
	public StackType stackType() {
		return StackType.DEFAULT;
	}

	@Override
	public Inventory refreshItems() {
		getPlayer().getPacketSender().sendItemContainer(this, INTERFACE_ID);
		return this;
	}

	@Override
	public Inventory full() {
		getPlayer().getPacketSender().sendMessage("Not enough space in your inventory.");
		return this;
	}

	/**
	 * Adds a set of items into the inventory.
	 *
	 * @param item
	 *            the set of items to add.
	 */
	@Override
	public void addItemSet(Item[] item) {
		for (Item addItem : item) {
			if (addItem == null) {
				continue;
			}
			add(addItem);
		}
	}

	/**
	 * Deletes a set of items from the inventory.
	 *
	 * @param optional
	 *            the set of items to delete.
	 */
	public void deleteItemSet(Optional<Item[]> optional) {
		if (optional.isPresent()) {
			for (Item deleteItem : optional.get()) {
				if (deleteItem == null) {
					continue;
				}

				delete(deleteItem);
			}
		}
	}

	public static final int INTERFACE_ID = 3214;
}
