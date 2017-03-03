package com.elvarg.world.regions;

import com.elvarg.world.entity.impl.player.Player;

/**
 * This abstracted class constructs how the classes that extends this abstract
 * class will be formated as, each abstract void giving it's own special
 * function for the player to conduct with.
 * 
 * @author Dennis
 *
 */
public interface Areas {
	/**
	 * This abstract void represents the first click option of a object in which
	 * the player interacts with, an Integer is placed inside the parameter so
	 * that the abstraction can use the switch(Integer){} function.
	 * 
	 * @param player
	 * @param object
	 */
	public void sendFirstClickObject(Player player, int object);

	/**
	 * This abstract void represents the second click option of a object in
	 * which the player interacts with, an Integer is placed inside the
	 * parameter so that the abstraction can use the switch(Integer){} function.
	 * 
	 * @param player
	 * @param object
	 */
	public void sendSecondClickObject(Player player, int object);

	/**
	 * This abstract void represents the third click option of a object in which
	 * the player interacts with, an Integer is placed inside the parameter so
	 * that the abstraction can use the switch(Integer){} function.
	 * 
	 * @param player
	 * @param object
	 */
	public void sendThirdClickObject(Player player, int object);

	/**
	 * This abstract void represents the first click option of a npc in which
	 * the player interacts with, an Integer is placed inside the parameter so
	 * that the abstraction can use the switch(Integer){} function.
	 * 
	 * @param player
	 * @param npc
	 */
	public void sendFirstClickNpc(Player player, int npc);

	/**
	 * This abstract void represents the second click option of a npc in which
	 * the player interacts with, an Integer is placed inside the parameter so
	 * that the abstraction can use the switch(Integer){} function.
	 * 
	 * @param player
	 * @param npc
	 */
	public void sendSecondClickNpc(Player player, int npc);

	/**
	 * This abstract void represents the third click option of a npc in which
	 * the player interacts with, an Integer is placed inside the parameter so
	 * that the abstraction can use the switch(Integer){} function.
	 * 
	 * @param player
	 * @param npc
	 */
	public void sendThirdClickNpc(Player player, int npc);
}