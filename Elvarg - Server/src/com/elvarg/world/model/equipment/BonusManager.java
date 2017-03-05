package com.elvarg.world.model.equipment;

import com.elvarg.cache.impl.definitions.ItemDefinition;
import com.elvarg.world.entity.combat.formula.DamageFormulas;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Item;

public class BonusManager {

	public static void open(Player player) {
		BonusManager.update(player);
		player.getPacketSender().sendInterface(INTERFACE_ID);
	}

	public static void update(Player player) {
		double[] bonuses = new double[18];
		for (Item item : player.getEquipment().getItems()) {
			ItemDefinition definition = ItemDefinition.forId(item.getId());
			for (int i = 0; i < definition.getBonus().length; i++) {
				bonuses[i] += definition.getBonus()[i];
			}
		}
		for (int i = 0; i < STRING_ID.length; i++) {
			if (i <= 4) {
				player.getBonusManager().attackBonus[i] = bonuses[i];
			} else if (i <= 9) {
				int index = i - 5;
				player.getBonusManager().defenceBonus[index] = bonuses[i];
				/*
				 * if(player.getEquipment().getItems()[Equipment.SHIELD_SLOT].
				 * getId() == 11283 && !STRING_ID[i][1].contains("Magic")) {
				 * if(player.getDfsCharges() > 0) {
				 * player.getBonusManager().defenceBonus[index] +=
				 * player.getDfsCharges(); bonuses[i] += player.getDfsCharges();
				 * } }
				 */
			} else {
				int index = i - 10;
				player.getBonusManager().otherBonus[index] = bonuses[i];
			}
			player.getPacketSender().sendString(Integer.valueOf(STRING_ID[i][0]), STRING_ID[i][1] + ": " + bonuses[i]);
		}

		// if(player.getInterfaceId() == INTERFACE_ID) {
		player.getPacketSender().sendString(MELEE_MAXHIT_FRAME,
				"Melee maxhit: " + getDamageString(DamageFormulas.calculateMaxMeleeHit(player)));
		player.getPacketSender().sendString(RANGED_MAXHIT_FRAME,
				"Ranged maxhit: " + getDamageString(DamageFormulas.calculateMaxRangedHit(player)));
		player.getPacketSender().sendString(MAGIC_MAXHIT_FRAME,
				"Magic maxhit: " + getDamageString(DamageFormulas.getMagicMaxhit(player)));
		// }
	}

	private static String getDamageString(int damage) {
		if (damage == 0) {
			return "---";
		}
		if (damage <= 10) {
			return "@red@" + damage;
		}
		if (damage <= 25) {
			return "@yel@" + damage;
		}
		return "@gre@" + damage;
	}

	public double[] getAttackBonus() {
		return attackBonus;
	}

	public double[] getDefenceBonus() {
		return defenceBonus;
	}

	public double[] getOtherBonus() {
		return otherBonus;
	}

	private double[] attackBonus = new double[5];

	private double[] defenceBonus = new double[5];

	private double[] otherBonus = new double[2];

	private static final String[][] STRING_ID = { { "1675", "Stab" }, { "1676", "Slash" }, { "1677", "Crush" },
			{ "1678", "Magic" }, { "1679", "Range" },

			{ "1680", "Stab" }, { "1681", "Slash" }, { "1682", "Crush" }, { "1683", "Magic" }, { "1684", "Range" },
			/*
			 * {"16522", "Summoning"}, {"16523", "Absorb Melee"}, {"16524",
			 * "Absorb Magic"}, {"16525", "Absorb Ranged"},
			 */

			{ "1686", "Strength" },
			// {"16526", "Ranged Strength"},
			{ "1687", "Prayer" },
			// {"16527", "Magic Damage"}
	};

	public static final int ATTACK_STAB = 0, ATTACK_SLASH = 1, ATTACK_CRUSH = 2, ATTACK_MAGIC = 3, ATTACK_RANGE = 4,

			DEFENCE_STAB = 0, DEFENCE_SLASH = 1, DEFENCE_CRUSH = 2, DEFENCE_MAGIC = 3, DEFENCE_RANGE = 4,
			DEFENCE_SUMMONING = 5, ABSORB_MELEE = 6, ABSORB_MAGIC = 7, ABSORB_RANGED = 8,

			BONUS_STRENGTH = 0, RANGED_STRENGTH = 1, BONUS_PRAYER = 2, MAGIC_DAMAGE = 3;

	private static final int MELEE_MAXHIT_FRAME = 15115;
	private static final int RANGED_MAXHIT_FRAME = 15116;
	private static final int MAGIC_MAXHIT_FRAME = 15117;

	private static final int INTERFACE_ID = 15106;

}
