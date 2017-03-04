package com.elvarg.world.model;

/**
 * Represents a player's magic spellbook.
 * 
 * @author relex lawl
 */

public enum MagicSpellbook {

	NORMAL(39000, TeleportType.NORMAL), ANCIENT(39100, TeleportType.ANCIENT), LUNAR(29999, TeleportType.LUNAR);

	/**
	 * The MagicSpellBook constructor.
	 * 
	 * @param interfaceId
	 *            The spellbook's interface id.
	 * @param message
	 *            The message received upon switching to said spellbook.
	 */
	private MagicSpellbook(int interfaceId, TeleportType teleportType) {
		this.interfaceId = interfaceId;
		this.teleportType = teleportType;
	}

	/**
	 * The spellbook's interface id
	 */
	private final int interfaceId;

	/**
	 * The spellbook's teleport type
	 */
	private TeleportType teleportType;

	/**
	 * Gets the interface to switch tab interface to.
	 * 
	 * @return The interface id of said spellbook.
	 */
	public int getInterfaceId() {
		return interfaceId;
	}

	/**
	 * Gets the spellbook's teleport type
	 * 
	 * @return The teleport type of said spellbook.
	 */
	public TeleportType getTeleportType() {
		return teleportType;
	}

	/**
	 * Gets the MagicSpellBook for said id.
	 * 
	 * @param id
	 *            The ordinal of the SpellBook to fetch.
	 * @return The MagicSpellBook who's ordinal is equal to id.
	 */
	public static MagicSpellbook forId(int id) {
		for (MagicSpellbook book : MagicSpellbook.values()) {
			if (book.ordinal() == id) {
				return book;
			}
		}
		return NORMAL;
	}
}
