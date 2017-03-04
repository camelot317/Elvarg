package com.elvarg.world.entity.combat.magic;

import java.util.Arrays;
import java.util.Optional;

import com.elvarg.world.entity.Entity;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.MagicSpellbook;
import com.elvarg.world.model.Skill;

/**
 * A parent class represented by any generic spell able to be cast by an
 * {@link Entity}.
 * 
 * @author lare96
 */
public abstract class Spell {

	/**
	 * Determines if this spell is able to be cast by the argued {@link Player}.
	 * We do not include {@link Npc}s here since no checks need to be made for
	 * them when they cast a spell.
	 * 
	 * @param player
	 *            the player casting the spell.
	 * @return <code>true</code> if the spell can be cast by the player,
	 *         <code>false</code> otherwise.
	 */
	public boolean canCast(Player player, boolean delete) {

		// We first check the level required.
		if (player.getSkillManager().getCurrentLevel(Skill.MAGIC) < levelRequired()) {
			player.getPacketSender()
					.sendMessage("You need a Magic level of " + levelRequired() + " to cast this spell.");
			player.getCombat().reset();
			return false;
		}

		// Secondly we check if they have proper magic spellbook
		// If not, reset all magic attributes such as current spell
		// Aswell as autocast spell
		if (!player.getSpellbook().equals(getSpellbook())) {
			Autocasting.setAutocast(player, null);
			player.getCombat().setCastSpell(null);
			player.getCombat().reset();
			return false;
		}

		// Then we check the items required.
		if (itemsRequired(player).isPresent()) {

			// Suppress the runes based on the staff, we then use the new array
			// of items that don't include suppressed runes.
			Item[] items = PlayerMagicStaff.suppressRunes(player, itemsRequired(player).get());

			// Now check if we have all of the runes.
			if (!player.getInventory().containsAll(items)) {

				// We don't, so we can't cast.
				player.getPacketSender().sendMessage("You do not have the required items to cast this spell.");
				player.getCombat().setCastSpell(null);
				player.getCombat().reset();
				return false;
			}

			// Finally, we check the equipment required.
			if (equipmentRequired(player).isPresent()) {
				if (!player.getEquipment().containsAll(equipmentRequired(player).get())) {
					player.getPacketSender().sendMessage("You do not have the required equipment to cast this spell.");
					player.getCombat().setCastSpell(null);
					player.getCombat().reset();
					return false;
				}
			}

			// We've made it through the checks, so we have the items and can
			// remove them now.
			if (delete) {
				for (Item it : Arrays.asList(items)) {
					if (it != null)
						player.getInventory().delete(it);
				}
			}
		}
		return true;
	}

	public abstract int spellId();

	/**
	 * The level required to cast this spell.
	 * 
	 * @return the level required to cast this spell.
	 */
	public abstract int levelRequired();

	/**
	 * The base experience given when this spell is cast.
	 * 
	 * @return the base experience given when this spell is cast.
	 */
	public abstract int baseExperience();

	/**
	 * The items required to cast this spell.
	 * 
	 * @param player
	 *            the player's inventory to check for these items.
	 * 
	 * @return the items required to cast this spell, or <code>null</code> if
	 *         there are no items required.
	 */
	public abstract Optional<Item[]> itemsRequired(Player player);

	/**
	 * The equipment required to cast this spell.
	 * 
	 * @param player
	 *            the player's equipment to check for these items.
	 * 
	 * @return the equipment required to cast this spell, or <code>null</code>
	 *         if there is no equipment required.
	 */
	public abstract Optional<Item[]> equipmentRequired(Player player);

	/**
	 * The method invoked when the spell is cast.
	 * 
	 * @param cast
	 *            the entity casting the spell.
	 * @param castOn
	 *            the target of the spell.
	 */
	public abstract void startCast(Character cast, Character castOn);

	/**
	 * Returns the spellbook in which this spell is.
	 * 
	 * @return
	 */
	public MagicSpellbook getSpellbook() {
		return MagicSpellbook.NORMAL;
	}
}
