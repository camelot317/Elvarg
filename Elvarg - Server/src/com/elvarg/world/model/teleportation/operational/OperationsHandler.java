package com.elvarg.world.model.teleportation.operational;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.teleportation.operational.impl.AmuletOfGlory;

/**
 * Handler class for Item Operations. If the item specified is equal to the
 * {@value #operational_list} value then execute the event.
 * 
 * @author Dennis
 *
 */
public class OperationsHandler
{

	/**
	 * Stores the implemented classes from {@link Operationable} interface and stores them for later use.
	 */
	private static final List<Operationable> operational_list = new ArrayList<>();
	
	/**
	 * The actual storing sequence for any implemented classes.
	 */
	static
	{
		operational_list.add(new AmuletOfGlory());
	}
	
	/**
	 * Gets the {@value #operational_list} data, filters any null objects, then searches for a matching itemId found within the {@link #operational_list}.
	 * @param itemId
	 * @return itemId
	 */
	private static Optional<Operationable> getOperationals(int itemId)
	{
		return operational_list.stream().filter(Objects::nonNull).filter(item -> item.itemId() == itemId).findAny();
	}
	
	/**
	 * Executes the Operational Item. checks to see if the itemId exists for use, checks to see if the itemId is equivalent to {@link #getOperationals(int)} itemId
	 * @param player
	 * @param itemId
	 */
	public static void executeOperation(Player player, int itemId)
	{
		Optional<Operationable> getOperations = getOperationals(itemId);
		
		if (!getOperations.isPresent())
		{
			return;
		}
		
		if (itemId == getOperations.get().itemId())
		{
			getOperations.get().executeOperationalItem(player);
		}
	}
}