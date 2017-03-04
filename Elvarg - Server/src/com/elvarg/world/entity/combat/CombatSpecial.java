package com.elvarg.world.entity.combat;

import java.util.Arrays;

import com.elvarg.cache.impl.definitions.WeaponInterfaces.WeaponInterface;
import com.elvarg.engine.task.TaskManager;
import com.elvarg.engine.task.impl.PlayerSpecialAmountTask;
import com.elvarg.world.entity.combat.method.CombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.AbyssalTentacleCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.AbyssalWhipCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.ArmadylCrossbowCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.ArmadylGodswordCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.BandosGodswordCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.BarrelchestAnchorCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.DarkBowCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.DragonClawCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.DragonDaggerCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.DragonHalberdCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.DragonLongswordCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.DragonMaceCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.DragonScimitarCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.GraniteMaulCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.MagicShortbowCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.SaradominGodswordCombatMethod;
import com.elvarg.world.entity.combat.method.impl.specials.ZamorakGodswordCombatMethod;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.container.impl.Equipment;
import com.elvarg.world.model.equipment.BonusManager;

/**
 * Holds constants that hold data for all of the special attacks that can be
 * used.
 * 
 * @author lare96
 */
public enum CombatSpecial {

	// Melee
	ABYSSAL_WHIP(new int[] { 4151, 21371, 15441, 15442, 15443, 15444 }, 50, 1.04, 1, new AbyssalWhipCombatMethod(),
			WeaponInterface.WHIP), ABYSSAL_TENTACLE(new int[] { 12006 }, 50, 1.04, 1, new AbyssalTentacleCombatMethod(),
					WeaponInterface.WHIP),

	BARRELSCHEST_ANCHOR(new int[] { 10887 }, 50, 1.22, 1.35, new BarrelchestAnchorCombatMethod(),
			WeaponInterface.WARHAMMER), DRAGON_SCIMITAR(new int[] { 4587 }, 55, 1.08, 1.1,
					new DragonScimitarCombatMethod(), WeaponInterface.SCIMITAR), DRAGON_LONGSWORD(new int[] { 1305 },
							25, 1.31, 1.33, new DragonLongswordCombatMethod(),
							WeaponInterface.LONGSWORD), DRAGON_MACE(new int[] { 1434 }, 25, 1.61, 1.25,
									new DragonMaceCombatMethod(), WeaponInterface.MACE),

	ARMADYL_GODSWORD(new int[] { 11802 }, 50, 1.57, 1.63, new ArmadylGodswordCombatMethod(),
			WeaponInterface.TWO_HANDED_SWORD), SARADOMIN_GODSWORD(new int[] { 11806 }, 50, 1.27, 1.5,
					new SaradominGodswordCombatMethod(), WeaponInterface.TWO_HANDED_SWORD), BANDOS_GODSWORD(
							new int[] { 11804 }, 100, 1.22, 1.4, new BandosGodswordCombatMethod(),
							WeaponInterface.TWO_HANDED_SWORD), ZAMORAK_GODSWORD(new int[] { 11808 }, 50, 1.15, 1.4,
									new ZamorakGodswordCombatMethod(), WeaponInterface.TWO_HANDED_SWORD),

	// Multiple hits
	DRAGON_HALBERD(new int[] { 3204 }, 30, 1, 1.19, new DragonHalberdCombatMethod(),
			WeaponInterface.HALBERD), DRAGON_DAGGER(new int[] { 1215, 1231, 5680, 5698 }, 25, 1.35, 1.25,
					new DragonDaggerCombatMethod(), WeaponInterface.DAGGER), GRANITE_MAUL(new int[] { 4153 }, 50, 1.12,
							1.18, new GraniteMaulCombatMethod(),
							WeaponInterface.GRANITE_MAUL), DRAGON_CLAWS(new int[] { 13652 }, 50, 1.18, 1.5,
									new DragonClawCombatMethod(), WeaponInterface.CLAWS),

	// Ranged
	MAGIC_SHORTBOW(new int[] { 861 }, 55, 1.03, 1.2, new MagicShortbowCombatMethod(),
			WeaponInterface.SHORTBOW), DARK_BOW(new int[] { 11235 }, 55, 1.29, 1.22, new DarkBowCombatMethod(),
					WeaponInterface.LONGBOW), ARMADYL_CROSSBOW(new int[] { 11785 }, 40, 1.30, 2.0,
							new ArmadylCrossbowCombatMethod(), WeaponInterface.CROSSBOW),

	;

	/** The weapon ID's that perform this special when activated. */
	private int[] identifiers;

	/** The amount of special energy this attack will drain. */
	private int drainAmount;

	/** The strength bonus when performing this special attack. */
	private double strengthBonus;

	/** The accuracy bonus when performing this special attack. */
	private double accuracyBonus;

	/** The combat type used when performing this special attack. */
	private CombatMethod combatMethod;

	/** The weapon interface used by the identifiers. */
	private WeaponInterface weaponType;

	/**
	 * Create a new {@link CombatSpecial}.
	 * 
	 * @param identifers
	 *            the weapon ID's that perform this special when activated.
	 * @param drainAmount
	 *            the amount of special energy this attack will drain.
	 * @param strengthBonus
	 *            the strength bonus when performing this special attack.
	 * @param accuracyBonus
	 *            the accuracy bonus when performing this special attack.
	 * @param combatMethod
	 *            the combat type used when performing this special attack.
	 * @param weaponType
	 *            the weapon interface used by the identifiers.
	 */
	private CombatSpecial(int[] identifiers, int drainAmount, double strengthBonus, double accuracyBonus,
			CombatMethod combatMethod, WeaponInterface weaponType) {
		this.identifiers = identifiers;
		this.drainAmount = drainAmount;
		this.strengthBonus = strengthBonus;
		this.accuracyBonus = accuracyBonus;
		this.combatMethod = combatMethod;
		this.weaponType = weaponType;
	}

	/**
	 * Checks if a player has the reqs to perform the special attack
	 * 
	 * @param player
	 * @param special
	 * @return
	 */
	public static boolean checkSpecial(Player player, CombatSpecial special) {
		return (player.getCombatSpecial() != null && player.getCombatSpecial() == special && player.isSpecialActivated()
				&& player.getSpecialPercentage() >= special.getDrainAmount());
	}

	/**
	 * Drains the special bar for the argued {@link Player}.
	 * 
	 * @param player
	 *            the player who's special bar will be drained.
	 * @param amount
	 *            the amount of energy to drain from the special bar.
	 */
	public static void drain(Player player, int amount) {
		player.decrementSpecialPercentage(amount);
		player.setSpecialActivated(false);
		CombatSpecial.updateBar(player);
		if (!player.isRecoveringSpecialAttack()) {
			TaskManager.submit(new PlayerSpecialAmountTask(player));
		}
	}

	/**
	 * Updates the special bar with the amount of special energy the argued
	 * {@link Player} has.
	 * 
	 * @param player
	 *            the player who's special bar will be updated.
	 */
	public static void updateBar(Player player) {
		if (player.getCombat().getWeapon().getSpecialBar() == -1
				|| player.getCombat().getWeapon().getSpecialMeter() == -1) {
			return;
		}
		int specialCheck = 10;
		int specialBar = player.getCombat().getWeapon().getSpecialMeter();
		int specialAmount = player.getSpecialPercentage() / 10;

		for (int i = 0; i < 10; i++) {
			player.getPacketSender().sendInterfaceComponentMoval(specialAmount >= specialCheck ? 500 : 0, 0,
					--specialBar);
			specialCheck--;
		}
		player.getPacketSender().updateSpecialAttackOrb().sendString(player.getCombat().getWeapon().getSpecialMeter(),
				player.isSpecialActivated() ? ("@yel@ Special Attack (" + player.getSpecialPercentage() + "%)")
						: ("@bla@ Special Attack (" + player.getSpecialPercentage() + "%"));

	}

	/**
	 * Assigns special bars to the attack style interface if needed.
	 * 
	 * @param player
	 *            the player to assign the special bar for.
	 */
	public static void assign(Player player) {
		if (player.getCombat().getWeapon().getSpecialBar() == -1) {
			player.setSpecialActivated(false);
			player.setCombatSpecial(null);
			CombatSpecial.updateBar(player);
			return;
		}

		for (CombatSpecial c : CombatSpecial.values()) {
			if (player.getCombat().getWeapon() == c.getWeaponType()) {
				if (Arrays.stream(c.getIdentifiers())
						.anyMatch(id -> player.getEquipment().get(Equipment.WEAPON_SLOT).getId() == id)) {
					player.getPacketSender().sendInterfaceDisplayState(player.getCombat().getWeapon().getSpecialBar(),
							false);
					player.setCombatSpecial(c);
					return;
				}
			}
		}

		player.getPacketSender().sendInterfaceDisplayState(player.getCombat().getWeapon().getSpecialBar(), true);
		player.setCombatSpecial(null);
		player.setSpecialActivated(false);
	}

	public static void activate(Player player) {
		/*
		 * if(Dueling.checkRule(player, DuelRule.NO_SPECIAL_ATTACKS)) {
		 * player.getPacketSender().
		 * sendMessage("Special Attacks have been turned off in this duel.");
		 * return; }
		 */

		if (player.getCombatSpecial() == null) {
			return;
		}

		if (player.isSpecialActivated()) {
			player.setSpecialActivated(false);
			CombatSpecial.updateBar(player);
		} else {

			final CombatSpecial spec = player.getCombatSpecial();

			player.setSpecialActivated(true);

			// Handle instant special attacks here.
			// Example: Granite Maul, Dragon battleaxe...
			if (spec == CombatSpecial.GRANITE_MAUL) {

				if (player.getSpecialPercentage() < player.getCombatSpecial().getDrainAmount()) {
					player.getPacketSender().sendMessage("You do not have enough special attack energy left!");
					player.setSpecialActivated(false);
					CombatSpecial.updateBar(player);
					return;
				}

				if (CombatFactory.isAttacking(player)) {

					// Handle an immediate attack by indicating
					// that we should disregard delay...
					player.getCombat().setDisregardDelay(true);
					player.getCombat().doCombat();

					return;

				} else {

					// Uninformed player using gmaul without being in combat..
					// Teach them a lesson!
					player.getPacketSender()
							.sendMessage("Although not required, the Granite maul special attack should be used during")
							.sendMessage("combat for maximum effect.");

				}
			} /*
				 * else if(spec == CombatSpecial.DRAGON_BATTLEAXE) {
				 * 
				 * }
				 */
			CombatSpecial.updateBar(player);
		}
		BonusManager.update(player);
	}

	/**
	 * Gets the weapon ID's that perform this special when activated.
	 * 
	 * @return the weapon ID's that perform this special when activated.
	 */
	public int[] getIdentifiers() {
		return identifiers;
	}

	/**
	 * Gets the amount of special energy this attack will drain.
	 * 
	 * @return the amount of special energy this attack will drain.
	 */
	public int getDrainAmount() {
		return drainAmount;
	}

	/**
	 * Gets the strength bonus when performing this special attack.
	 * 
	 * @return the strength bonus when performing this special attack.
	 */
	public double getStrengthBonus() {
		return strengthBonus;
	}

	/**
	 * Gets the accuracy bonus when performing this special attack.
	 * 
	 * @return the accuracy bonus when performing this special attack.
	 */
	public double getAccuracyBonus() {
		return accuracyBonus;
	}

	/**
	 * Gets the combat type used when performing this special attack.
	 * 
	 * @return the combat type used when performing this special attack.
	 */
	public CombatMethod getCombatMethod() {
		return combatMethod;
	}

	/**
	 * Gets the weapon interface used by the identifiers.
	 * 
	 * @return the weapon interface used by the identifiers.
	 */
	public WeaponInterface getWeaponType() {
		return weaponType;
	}
}
