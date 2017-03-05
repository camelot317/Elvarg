package com.elvarg.world.model;

import com.elvarg.util.Misc;

/**
 * This enum contains data used as constants for skill configurations such as
 * experience rates, string id's for interface updating.
 * 
 * @author Gabriel Hannason
 */
public enum Skill {

	ATTACK(6247, 1, 0), DEFENCE(6253, 1, 6), STRENGTH(6206, 1, 3), HITPOINTS(6216, 3, 1), RANGED(4443, 2, 9), PRAYER(
			6242, 6, 12), MAGIC(6211, 2, 15), COOKING(6226, 1, 11), WOODCUTTING(4272, 1, 17), FLETCHING(6231, 2,
					16), FISHING(6258, 1, 8), FIREMAKING(4282, 2, 14), CRAFTING(6263, 4, 13), SMITHING(6221, 7,
							5), MINING(4416, 3, 2), HERBLORE(6237, 4, 7), AGILITY(4277, 4, 4), THIEVING(4261, 1,
									10), SLAYER(12122, 6, 19), FARMING(5267, 4, 20), RUNECRAFTING(4267, 3,
											18), CONSTRUCTION(7267, 3, 21), HUNTER(8267, 3, 22);

	private Skill(int chatboxInterface, int prestigePoints, int prestigeId) {
		this.chatboxInterface = chatboxInterface;
		this.prestigePoints = prestigePoints;
		this.prestigeId = prestigeId;
	}

	/**
	 * The skill's chatbox interface The interface which will be sent on
	 * levelup.
	 */
	private int chatboxInterface;

	/**
	 * The amount of points the player will receive for prestiging the skill.
	 */
	private int prestigePoints;

	/**
	 * The button id for prestiging this skill.
	 */
	private int prestigeId;

	/**
	 * Gets the Skill's chatbox interface.
	 * 
	 * @return The interface which will be sent on levelup.
	 */
	public int getChatboxInterface() {
		return chatboxInterface;
	}

	/**
	 * Get's the amount of points the player will receive for prestiging the
	 * skill.
	 * 
	 * @return The prestige points reward.
	 */
	public int getPrestigePoints() {
		return prestigePoints;
	}

	/**
	 * Gets the Skill's name.
	 * 
	 * @return The skill's name in a lower case format.
	 */
	public String getName() {
		return toString().toLowerCase();
	}

	/**
	 * Gets the Skill's name.
	 * 
	 * @return The skill's name in a formatted way.
	 */
	public String getFormatName() {
		return Misc.formatText(getName());
	}

	/**
	 * Gets the Skill value which ordinal() matches {@code id}.
	 * 
	 * @param id
	 *            The index of the skill to fetch Skill instance for.
	 * @return The Skill instance.
	 */
	public static Skill forId(int id) {
		for (Skill skill : Skill.values()) {
			if (skill.ordinal() == id) {
				return skill;
			}
		}
		return null;
	}

	/**
	 * Gets the Skill value which prestigeId matches {@code id}.
	 * 
	 * @param id
	 *            The skill with matching prestigeId to fetch.
	 * @return The Skill instance.
	 */
	public static Skill forPrestigeId(int id) {
		for (Skill skill : Skill.values()) {
			if (skill.prestigeId == id) {
				return skill;
			}
		}
		return null;
	}

	/**
	 * Gets the Skill value which name matches {@code name}.
	 * 
	 * @param string
	 *            The name of the skill to fetch Skill instance for.
	 * @return The Skill instance.
	 */
	public static Skill forName(String name) {
		for (Skill skill : Skill.values()) {
			if (skill.toString().equalsIgnoreCase(name)) {
				return skill;
			}
		}
		return null;
	}
}