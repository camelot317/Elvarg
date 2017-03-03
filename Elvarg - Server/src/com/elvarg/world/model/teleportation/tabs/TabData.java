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
 * @author Dennis
 *
 */
public enum TabData 
{
	HOME(1, new Position(3222, 3222, 0)),
	LUMBRIDGE(8008, new Position(3222, 3218, 0)),
	FALADOR(8009, new Position(2965, 3379, 0)),
	CAMELOT(8010, new Position(2757, 3477, 0)),
	ARDY(8011, new Position(2661, 3305, 0)),
	WATCH(8012, new Position(2549, 3112, 0)),
	VARROCK(8007, new Position(3213, 3424, 0));
	
	/**
	 * The {@link Item} id of the teleport tablet.
	 */
	private final int tabId;
	
	/**
	 * Gets the {@link #tabId} and returns as its initial value.
	 * @return tabId
	 */
	public int getTab()
	{
		return tabId;
	}
	
	/**
	 * The specified {@link Position} that the teleport tablet will send the {@link Player} upon interaction.
	 */
	private final Position location;
	
	/**
	 * Gets the {@link #tabId} and returns as its initial value.
	 * @return location
	 */
	public Position location()
	{
		return location;
	}
	
	/**
	 * TabData constructor
	 * @param tabId
	 * @param location
	 */
	private TabData(int tabId, Position location)
	{
		this.tabId = tabId;
		this.location = location;
	}

	/**
	 * The {@value #tab_set} storing
	 */
	private static Set<TabData> tab_set = Collections.unmodifiableSet(EnumSet.allOf(TabData.class));
	
	/**
	 * Gets the teleport tablet from the {@value #tab_set} stream.
	 * @param tabId
	 * @return tabId
	 */
	public static Optional<TabData> getTab(int tabId)
	{
		return tab_set.stream().filter(Objects::nonNull).filter(tabs -> tabs.getTab() == tabId).findAny();
	}
}