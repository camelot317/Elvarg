package com.elvarg.world.model.teleportation.tabs;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.Position;

/**
 * Teleport Tablet data storage.
 * 
 * @author Dennis
 *
 */
public enum TabData {

	HOME(new Item(1), new Position(3222, 3222, 0)),

	LUMBRIDGE(new Item(8008), new Position(3222, 3218, 0)),

	FALADOR(new Item(8009), new Position(2965, 3379, 0)),

	CAMELOT(new Item(8010), new Position(2757, 3477, 0)),

	ARDY(new Item(8011), new Position(2661, 3305, 0)),

	WATCH(new Item(8012), new Position(2549, 3112, 0)),

	VARROCK(new Item(8007), new Position(3213, 3424, 0));

	/**
	 * The {@link Item} of the teleport tablet.
	 */
	private final Item tablet;

	/**
	 * Gets the {@link #tablet} and returns as its initial value.
	 * 
	 * @return tabId
	 */
	public Item getTab() {
		return tablet;
	}

	/**
	 * The specified {@link Position} that the teleport tablet will send the
	 * {@link Player} upon interaction.
	 */
	private final Position location;

	/**
	 * Gets the {@link #Position} and returns as its initial value.
	 * 
	 * @return location
	 */
	public Position location() {
		return location;
	}

	/**
	 * TabData constructor
	 * 
	 * @param tablet
	 * @param location
	 */
	private TabData(final Item tablet, final Position location) {
		this.tablet = tablet;
		this.location = location;
	}

	/**
	 * The {@value #tab_set} storing
	 */
	private static Set<TabData> tab_set = Collections.unmodifiableSet(EnumSet.allOf(TabData.class));

	/**
	 * Gets the teleport tablet from the {@value #tab_set} stream.
	 * 
	 * @param tablet
	 * @return tablet
	 */
	public static Optional<TabData> getTab(Item tablet) {
		return tab_set.stream().filter(Objects::nonNull).filter(tabs -> tabs.getTab().getId() == tablet.getId())
				.findAny();
	}
}