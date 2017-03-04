package com.elvarg.world.entity.combat.ranged;

import java.util.HashMap;
import java.util.Map;

import com.elvarg.cache.impl.definitions.WeaponInterfaces;
import com.elvarg.cache.impl.definitions.WeaponInterfaces.WeaponInterface;
import com.elvarg.engine.task.impl.CombatPoisonEffect.PoisonType;
import com.elvarg.util.Misc;
import com.elvarg.world.entity.combat.CombatFactory;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.grounditems.GroundItemManager;
import com.elvarg.world.model.Flag;
import com.elvarg.world.model.Graphic;
import com.elvarg.world.model.GraphicHeight;
import com.elvarg.world.model.GroundItem;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.Position;
import com.elvarg.world.model.Skill;
import com.elvarg.world.model.container.impl.Equipment;

/**
 * A table of constants that hold data for all ranged ammo.
 * 
 * @author Swiffy96
 */
public class RangedData {

	/** A map of items and their respective interfaces. */
	private static Map<Integer, RangedWeaponData> range_wep_data = new HashMap<>();
	private static Map<Integer, AmmunitionData> range_ammo_data = new HashMap<>();

	public enum RangedWeaponData {

		LONGBOW(new int[] { 839 }, new AmmunitionData[] { AmmunitionData.BRONZE_ARROW },
				RangedWeaponType.LONGBOW), SHORTBOW(new int[] { 841 },
						new AmmunitionData[] { AmmunitionData.BRONZE_ARROW },
						RangedWeaponType.SHORTBOW), OAK_LONGBOW(new int[] { 845 },
								new AmmunitionData[] { AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW,
										AmmunitionData.STEEL_ARROW },
								RangedWeaponType.LONGBOW), OAK_SHORTBOW(new int[] { 843 },
										new AmmunitionData[] { AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW,
												AmmunitionData.STEEL_ARROW },
										RangedWeaponType.SHORTBOW), WILLOW_LONGBOW(new int[] { 847 },
												new AmmunitionData[] { AmmunitionData.BRONZE_ARROW,
														AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW,
														AmmunitionData.MITHRIL_ARROW },
												RangedWeaponType.LONGBOW), WILLOW_SHORTBOW(new int[] { 849 },
														new AmmunitionData[] { AmmunitionData.BRONZE_ARROW,
																AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW,
																AmmunitionData.MITHRIL_ARROW },
														RangedWeaponType.SHORTBOW), MAPLE_LONGBOW(new int[] { 851 },
																new AmmunitionData[] { AmmunitionData.BRONZE_ARROW,
																		AmmunitionData.IRON_ARROW,
																		AmmunitionData.STEEL_ARROW,
																		AmmunitionData.MITHRIL_ARROW,
																		AmmunitionData.ADAMANT_ARROW },
																RangedWeaponType.LONGBOW), MAPLE_SHORTBOW(
																		new int[] { 853 },
																		new AmmunitionData[] {
																				AmmunitionData.BRONZE_ARROW,
																				AmmunitionData.IRON_ARROW,
																				AmmunitionData.STEEL_ARROW,
																				AmmunitionData.MITHRIL_ARROW,
																				AmmunitionData.ADAMANT_ARROW },
																		RangedWeaponType.SHORTBOW), YEW_LONGBOW(
																				new int[] { 855 },
																				new AmmunitionData[] {
																						AmmunitionData.BRONZE_ARROW,
																						AmmunitionData.IRON_ARROW,
																						AmmunitionData.STEEL_ARROW,
																						AmmunitionData.MITHRIL_ARROW,
																						AmmunitionData.ADAMANT_ARROW,
																						AmmunitionData.RUNE_ARROW,
																						AmmunitionData.ICE_ARROW },
																				RangedWeaponType.LONGBOW), YEW_SHORTBOW(
																						new int[] { 857 },
																						new AmmunitionData[] {
																								AmmunitionData.BRONZE_ARROW,
																								AmmunitionData.IRON_ARROW,
																								AmmunitionData.STEEL_ARROW,
																								AmmunitionData.MITHRIL_ARROW,
																								AmmunitionData.ADAMANT_ARROW,
																								AmmunitionData.RUNE_ARROW,
																								AmmunitionData.ICE_ARROW },
																						RangedWeaponType.SHORTBOW), MAGIC_LONGBOW(
																								new int[] { 859 },
																								new AmmunitionData[] {
																										AmmunitionData.BRONZE_ARROW,
																										AmmunitionData.IRON_ARROW,
																										AmmunitionData.STEEL_ARROW,
																										AmmunitionData.MITHRIL_ARROW,
																										AmmunitionData.ADAMANT_ARROW,
																										AmmunitionData.RUNE_ARROW,
																										AmmunitionData.ICE_ARROW,
																										AmmunitionData.BROAD_ARROW },
																								RangedWeaponType.LONGBOW), MAGIC_SHORTBOW(
																										new int[] { 861,
																												6724 },
																										new AmmunitionData[] {
																												AmmunitionData.BRONZE_ARROW,
																												AmmunitionData.IRON_ARROW,
																												AmmunitionData.STEEL_ARROW,
																												AmmunitionData.MITHRIL_ARROW,
																												AmmunitionData.ADAMANT_ARROW,
																												AmmunitionData.RUNE_ARROW,
																												AmmunitionData.ICE_ARROW,
																												AmmunitionData.BROAD_ARROW },
																										RangedWeaponType.SHORTBOW), GODBOW(
																												new int[] {
																														19143,
																														19149,
																														19146 },
																												new AmmunitionData[] {
																														AmmunitionData.BRONZE_ARROW,
																														AmmunitionData.IRON_ARROW,
																														AmmunitionData.STEEL_ARROW,
																														AmmunitionData.MITHRIL_ARROW,
																														AmmunitionData.ADAMANT_ARROW,
																														AmmunitionData.RUNE_ARROW,
																														AmmunitionData.BROAD_ARROW,
																														AmmunitionData.DRAGON_ARROW },
																												RangedWeaponType.SHORTBOW), ZARYTE_BOW(
																														new int[] {
																																20171 },
																														new AmmunitionData[] {
																																AmmunitionData.BRONZE_ARROW,
																																AmmunitionData.IRON_ARROW,
																																AmmunitionData.STEEL_ARROW,
																																AmmunitionData.MITHRIL_ARROW,
																																AmmunitionData.ADAMANT_ARROW,
																																AmmunitionData.RUNE_ARROW,
																																AmmunitionData.BROAD_ARROW,
																																AmmunitionData.DRAGON_ARROW },
																														RangedWeaponType.SHORTBOW),

		DARK_BOW(new int[] { 11235, 13405, 15701, 15702, 15703, 15704 },
				new AmmunitionData[] { AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW,
						AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW,
						AmmunitionData.RUNE_ARROW, AmmunitionData.DRAGON_ARROW },
				RangedWeaponType.DARK_BOW),

		BRONZE_CROSSBOW(new int[] { 9174 }, new AmmunitionData[] { AmmunitionData.BRONZE_BOLT },
				RangedWeaponType.CROSSBOW), IRON_CROSSBOW(new int[] { 9177 },
						new AmmunitionData[] { AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT,
								AmmunitionData.ENCHANTED_OPAL_BOLT, AmmunitionData.IRON_BOLT },
						RangedWeaponType.CROSSBOW), STEEL_CROSSBOW(new int[] { 9179 },
								new AmmunitionData[] { AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT,
										AmmunitionData.ENCHANTED_OPAL_BOLT, AmmunitionData.IRON_BOLT,
										AmmunitionData.JADE_BOLT, AmmunitionData.ENCHANTED_JADE_BOLT,
										AmmunitionData.STEEL_BOLT, AmmunitionData.PEARL_BOLT,
										AmmunitionData.ENCHANTED_PEARL_BOLT },
								RangedWeaponType.CROSSBOW), MITHRIL_CROSSBOW(new int[] { 9181 },
										new AmmunitionData[] { AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT,
												AmmunitionData.ENCHANTED_OPAL_BOLT, AmmunitionData.IRON_BOLT,
												AmmunitionData.JADE_BOLT, AmmunitionData.ENCHANTED_JADE_BOLT,
												AmmunitionData.STEEL_BOLT, AmmunitionData.PEARL_BOLT,
												AmmunitionData.ENCHANTED_PEARL_BOLT, AmmunitionData.MITHRIL_BOLT,
												AmmunitionData.TOPAZ_BOLT, AmmunitionData.ENCHANTED_TOPAZ_BOLT },
										RangedWeaponType.CROSSBOW), ADAMANT_CROSSBOW(new int[] { 9183 },
												new AmmunitionData[] { AmmunitionData.BRONZE_BOLT,
														AmmunitionData.OPAL_BOLT, AmmunitionData.ENCHANTED_OPAL_BOLT,
														AmmunitionData.IRON_BOLT, AmmunitionData.JADE_BOLT,
														AmmunitionData.ENCHANTED_JADE_BOLT, AmmunitionData.STEEL_BOLT,
														AmmunitionData.PEARL_BOLT, AmmunitionData.ENCHANTED_PEARL_BOLT,
														AmmunitionData.MITHRIL_BOLT, AmmunitionData.TOPAZ_BOLT,
														AmmunitionData.ENCHANTED_TOPAZ_BOLT,
														AmmunitionData.ADAMANT_BOLT, AmmunitionData.SAPPHIRE_BOLT,
														AmmunitionData.ENCHANTED_SAPPHIRE_BOLT,
														AmmunitionData.EMERALD_BOLT,
														AmmunitionData.ENCHANTED_EMERALD_BOLT, AmmunitionData.RUBY_BOLT,
														AmmunitionData.ENCHANTED_RUBY_BOLT },
												RangedWeaponType.CROSSBOW), RUNE_CROSSBOW(new int[] { 9185 },
														new AmmunitionData[] { AmmunitionData.BRONZE_BOLT,
																AmmunitionData.OPAL_BOLT,
																AmmunitionData.ENCHANTED_OPAL_BOLT,
																AmmunitionData.IRON_BOLT, AmmunitionData.JADE_BOLT,
																AmmunitionData.ENCHANTED_JADE_BOLT,
																AmmunitionData.STEEL_BOLT, AmmunitionData.PEARL_BOLT,
																AmmunitionData.ENCHANTED_PEARL_BOLT,
																AmmunitionData.MITHRIL_BOLT, AmmunitionData.TOPAZ_BOLT,
																AmmunitionData.ENCHANTED_TOPAZ_BOLT,
																AmmunitionData.ADAMANT_BOLT,
																AmmunitionData.SAPPHIRE_BOLT,
																AmmunitionData.ENCHANTED_SAPPHIRE_BOLT,
																AmmunitionData.EMERALD_BOLT,
																AmmunitionData.ENCHANTED_EMERALD_BOLT,
																AmmunitionData.RUBY_BOLT,
																AmmunitionData.ENCHANTED_RUBY_BOLT,
																AmmunitionData.RUNITE_BOLT, AmmunitionData.BROAD_BOLT,
																AmmunitionData.DIAMOND_BOLT,
																AmmunitionData.ENCHANTED_DIAMOND_BOLT,
																AmmunitionData.ONYX_BOLT,
																AmmunitionData.ENCHANTED_ONYX_BOLT,
																AmmunitionData.DRAGON_BOLT,
																AmmunitionData.ENCHANTED_DRAGON_BOLT },
														RangedWeaponType.CROSSBOW), ARMADYL_CROSSBOW(
																new int[] { 11785 },
																new AmmunitionData[] { AmmunitionData.BRONZE_BOLT,
																		AmmunitionData.OPAL_BOLT,
																		AmmunitionData.ENCHANTED_OPAL_BOLT,
																		AmmunitionData.IRON_BOLT,
																		AmmunitionData.JADE_BOLT,
																		AmmunitionData.ENCHANTED_JADE_BOLT,
																		AmmunitionData.STEEL_BOLT,
																		AmmunitionData.PEARL_BOLT,
																		AmmunitionData.ENCHANTED_PEARL_BOLT,
																		AmmunitionData.MITHRIL_BOLT,
																		AmmunitionData.TOPAZ_BOLT,
																		AmmunitionData.ENCHANTED_TOPAZ_BOLT,
																		AmmunitionData.ADAMANT_BOLT,
																		AmmunitionData.SAPPHIRE_BOLT,
																		AmmunitionData.ENCHANTED_SAPPHIRE_BOLT,
																		AmmunitionData.EMERALD_BOLT,
																		AmmunitionData.ENCHANTED_EMERALD_BOLT,
																		AmmunitionData.RUBY_BOLT,
																		AmmunitionData.ENCHANTED_RUBY_BOLT,
																		AmmunitionData.RUNITE_BOLT,
																		AmmunitionData.BROAD_BOLT,
																		AmmunitionData.DIAMOND_BOLT,
																		AmmunitionData.ENCHANTED_DIAMOND_BOLT,
																		AmmunitionData.ONYX_BOLT,
																		AmmunitionData.ENCHANTED_ONYX_BOLT,
																		AmmunitionData.DRAGON_BOLT,
																		AmmunitionData.ENCHANTED_DRAGON_BOLT },
																RangedWeaponType.CROSSBOW),

		BRONZE_DART(new int[] { 806 }, new AmmunitionData[] { AmmunitionData.BRONZE_DART },
				RangedWeaponType.THROW), IRON_DART(new int[] { 807 }, new AmmunitionData[] { AmmunitionData.IRON_DART },
						RangedWeaponType.THROW), STEEL_DART(new int[] { 808 },
								new AmmunitionData[] { AmmunitionData.STEEL_DART },
								RangedWeaponType.THROW), MITHRIL_DART(new int[] { 809 },
										new AmmunitionData[] { AmmunitionData.MITHRIL_DART },
										RangedWeaponType.THROW), ADAMANT_DART(new int[] { 810 },
												new AmmunitionData[] { AmmunitionData.ADAMANT_DART },
												RangedWeaponType.THROW), RUNE_DART(new int[] { 811 },
														new AmmunitionData[] { AmmunitionData.RUNE_DART },
														RangedWeaponType.THROW), DRAGON_DART(new int[] { 11230 },
																new AmmunitionData[] { AmmunitionData.DRAGON_DART },
																RangedWeaponType.THROW),

		BRONZE_KNIFE(new int[] { 864, 870, 5654 }, new AmmunitionData[] { AmmunitionData.BRONZE_KNIFE },
				RangedWeaponType.THROW), IRON_KNIFE(new int[] { 863, 871, 5655 },
						new AmmunitionData[] { AmmunitionData.IRON_KNIFE }, RangedWeaponType.THROW), STEEL_KNIFE(
								new int[] { 865, 872, 5656 }, new AmmunitionData[] { AmmunitionData.STEEL_KNIFE },
								RangedWeaponType.THROW), BLACK_KNIFE(new int[] { 869, 874, 5658 },
										new AmmunitionData[] { AmmunitionData.BLACK_KNIFE },
										RangedWeaponType.THROW), MITHRIL_KNIFE(new int[] { 866, 873, 5657 },
												new AmmunitionData[] { AmmunitionData.MITHRIL_KNIFE },
												RangedWeaponType.THROW), ADAMANT_KNIFE(new int[] { 867, 875, 5659 },
														new AmmunitionData[] { AmmunitionData.ADAMANT_KNIFE },
														RangedWeaponType.THROW), RUNE_KNIFE(
																new int[] { 868, 876, 5660, 5667 },
																new AmmunitionData[] { AmmunitionData.RUNE_KNIFE },
																RangedWeaponType.THROW),

		BRONZE_THROWNAXE(new int[] { 800 }, new AmmunitionData[] { AmmunitionData.BRONZE_THROWNAXE },
				RangedWeaponType.THROW), IRON_THROWNAXE(new int[] { 801 },
						new AmmunitionData[] { AmmunitionData.IRON_THROWNAXE },
						RangedWeaponType.THROW), STEEL_THROWNAXE(new int[] { 802 },
								new AmmunitionData[] { AmmunitionData.STEEL_THROWNAXE },
								RangedWeaponType.THROW), MITHRIL_THROWNAXE(new int[] { 803 },
										new AmmunitionData[] { AmmunitionData.MITHRIL_THROWNAXE },
										RangedWeaponType.THROW), ADAMANT_THROWNAXE(new int[] { 804 },
												new AmmunitionData[] { AmmunitionData.ADAMANT_THROWNAXE },
												RangedWeaponType.THROW), RUNE_THROWNAXE(new int[] { 805 },
														new AmmunitionData[] { AmmunitionData.RUNE_THROWNAXE },
														RangedWeaponType.THROW),

		TOKTZ_XIL_UL(new int[] { 6522 }, new AmmunitionData[] { AmmunitionData.TOKTZ_XIL_UL }, RangedWeaponType.THROW),

		BRONZE_JAVELIN(new int[] { 825 }, new AmmunitionData[] { AmmunitionData.BRONZE_JAVELIN },
				RangedWeaponType.THROW), IRON_JAVELIN(new int[] { 826 },
						new AmmunitionData[] { AmmunitionData.IRON_JAVELIN }, RangedWeaponType.THROW), STEEL_JAVELIN(
								new int[] { 827 }, new AmmunitionData[] { AmmunitionData.STEEL_JAVELIN },
								RangedWeaponType.THROW), MITHRIL_JAVELIN(new int[] { 828 },
										new AmmunitionData[] { AmmunitionData.MITHRIL_JAVELIN },
										RangedWeaponType.THROW), ADAMANT_JAVELIN(new int[] { 829 },
												new AmmunitionData[] { AmmunitionData.ADAMANT_JAVELIN },
												RangedWeaponType.THROW), RUNE_JAVELIN(new int[] { 830 },
														new AmmunitionData[] { AmmunitionData.RUNE_JAVELIN },
														RangedWeaponType.THROW),

		CHINCHOMPA(new int[] { 10033 }, new AmmunitionData[] { AmmunitionData.CHINCHOMPA },
				RangedWeaponType.THROW), RED_CHINCHOMPA(new int[] { 10034 },
						new AmmunitionData[] { AmmunitionData.RED_CHINCHOMPA }, RangedWeaponType.THROW),

		KARILS_CROSSBOW(new int[] { 4734 }, new AmmunitionData[] { AmmunitionData.BOLT_RACK },
				RangedWeaponType.CROSSBOW),

		BALLISTA(new int[] { 19478, 19481 },
				new AmmunitionData[] { AmmunitionData.BRONZE_JAVELIN, AmmunitionData.IRON_JAVELIN,
						AmmunitionData.STEEL_JAVELIN, AmmunitionData.MITHRIL_JAVELIN, AmmunitionData.ADAMANT_JAVELIN,
						AmmunitionData.RUNE_JAVELIN },
				RangedWeaponType.BALLISTA), TOXIC_BLOWPIPE(new int[] { 12926 },
						new AmmunitionData[] { AmmunitionData.ZULRAH_SCALES }, RangedWeaponType.BLOWPIPE);

		RangedWeaponData(int[] weaponIds, AmmunitionData[] ammunitionData, RangedWeaponType type) {
			this.weaponIds = weaponIds;
			this.ammunitionData = ammunitionData;
			this.type = type;
		}

		private int[] weaponIds;
		private AmmunitionData[] ammunitionData;
		private RangedWeaponType type;

		public int[] getWeaponIds() {
			return weaponIds;
		}

		public AmmunitionData[] getAmmunitionData() {
			return ammunitionData;
		}

		public RangedWeaponType getType() {
			return type;
		}

		public static RangedWeaponData getFor(Player p) {
			int weapon = p.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
			return range_wep_data.get(weapon);
		}

		static {
			for (RangedWeaponData data : RangedWeaponData.values()) {
				for (int i : data.getWeaponIds()) {
					range_wep_data.put(i, data);
				}
			}
		}
	}

	public enum AmmunitionData {

		BRONZE_ARROW(882, 19, 10, 3, 44, 7, 43, 31), IRON_ARROW(884, 18, 9, 3, 44, 10, 43, 31), STEEL_ARROW(886, 20, 11,
				3, 44, 16, 43, 31), MITHRIL_ARROW(888, 21, 12, 3, 44, 22, 43, 31), ADAMANT_ARROW(890, 22, 13, 3, 44, 31,
						43, 31), RUNE_ARROW(892, 24, 15, 3, 44, 50, 43, 31), ICE_ARROW(78, 25, 16, 3, 44, 58, 34,
								31), BROAD_ARROW(4160, 20, 11, 3, 44, 58, 43,
										31), DRAGON_ARROW(11212, 1111, 1120, 3, 44, 65, 43, 31),

		BRONZE_BOLT(877, -1, 27, 3, 44, 13, 43, 31), OPAL_BOLT(879, -1, 27, 3, 44, 20, 43, 31), ENCHANTED_OPAL_BOLT(
				9236, -1, 27, 3, 44, 20, 43, 31), IRON_BOLT(9140, -1, 27, 3, 44, 28, 43, 31), JADE_BOLT(9335, -1, 27, 3,
						44, 31, 43, 31), ENCHANTED_JADE_BOLT(9237, -1, 27, 3, 44, 31, 43, 31), STEEL_BOLT(9141, -1, 27,
								3, 44, 35, 43, 31), PEARL_BOLT(880, -1, 27, 3, 44, 38, 43, 31), ENCHANTED_PEARL_BOLT(
										9238, -1, 27, 3, 44, 38, 43, 31), MITHRIL_BOLT(9142, -1, 27, 3, 44, 40, 43,
												31), TOPAZ_BOLT(9336, -1, 27, 3, 44, 50, 43, 31), ENCHANTED_TOPAZ_BOLT(
														9239, -1, 27, 3, 44, 50, 43, 31), ADAMANT_BOLT(9143, -1, 27, 3,
																44, 60, 43, 31), SAPPHIRE_BOLT(9337, -1, 27, 3, 44, 65,
																		43, 31), ENCHANTED_SAPPHIRE_BOLT(9240, -1, 27,
																				3, 44, 65, 43, 31), EMERALD_BOLT(9338,
																						-1, 27, 3, 44, 70, 43,
																						31), ENCHANTED_EMERALD_BOLT(
																								9241, -1, 27, 3, 44, 70,
																								43, 31), RUBY_BOLT(9339,
																										-1, 27, 3, 44,
																										75, 43,
																										31), ENCHANTED_RUBY_BOLT(
																												9242,
																												-1, 27,
																												3, 44,
																												75, 43,
																												31), RUNITE_BOLT(
																														9144,
																														-1,
																														27,
																														3,
																														44,
																														84,
																														43,
																														31), BROAD_BOLT(
																																13280,
																																-1,
																																27,
																																3,
																																44,
																																88,
																																43,
																																31), DIAMOND_BOLT(
																																		9340,
																																		-1,
																																		27,
																																		3,
																																		44,
																																		88,
																																		43,
																																		31), ENCHANTED_DIAMOND_BOLT(
																																				9243,
																																				-1,
																																				27,
																																				3,
																																				44,
																																				88,
																																				43,
																																				31), ONYX_BOLT(
																																						9342,
																																						-1,
																																						27,
																																						3,
																																						44,
																																						90,
																																						43,
																																						31), ENCHANTED_ONYX_BOLT(
																																								9245,
																																								-1,
																																								27,
																																								3,
																																								44,
																																								90,
																																								43,
																																								31), DRAGON_BOLT(
																																										9341,
																																										-1,
																																										27,
																																										3,
																																										44,
																																										90,
																																										43,
																																										31), ENCHANTED_DRAGON_BOLT(
																																												9244,
																																												-1,
																																												27,
																																												3,
																																												44,
																																												90,
																																												43,
																																												31),

		BRONZE_DART(806, 1234, 226, 4, 33, 2, 45, 37), IRON_DART(807, 1235, 227, 4, 33, 5, 45, 37), STEEL_DART(808,
				1236, 228, 4, 33, 8, 45, 37), MITHRIL_DART(809, 1238, 229, 4, 33, 10, 45, 37), ADAMANT_DART(810, 1239,
						230, 4, 33, 15, 45, 37), RUNE_DART(811, 1240, 231, 4, 33, 20, 45,
								37), DRAGON_DART(11230, 1123, 226, 4, 33, 25, 49, 37),

		BRONZE_KNIFE(864, 219, 212, 4, 33, 8, 45, 37), BRONZE_KNIFE_P1(870, 219, 212, 4, 33, 8, 45,
				37), BRONZE_KNIFE_P2(5654, 219, 212, 4, 33, 8, 45,
						37), BRONZE_KNIFE_P3(5661, 219, 212, 4, 33, 8, 45, 37),

		IRON_KNIFE(863, 220, 213, 4, 33, 12, 45, 37), IRON_KNIFE_P1(871, 220, 213, 4, 33, 12, 45,
				37), IRON_KNIFE_P2(5655, 220, 213, 4, 33, 12, 45, 37), IRON_KNIFE_P3(5662, 220, 213, 4, 33, 12, 45, 37),

		STEEL_KNIFE(865, 221, 214, 4, 33, 15, 45, 37), STEEL_KNIFE_P1(872, 221, 214, 4, 33, 15, 45, 37), STEEL_KNIFE_P2(
				5656, 221, 214, 4, 33, 15, 45, 37), STEEL_KNIFE_P3(5663, 221, 214, 4, 33, 15, 45, 37),

		BLACK_KNIFE(869, 222, 215, 4, 33, 17, 45, 37), BLACK_KNIFE_P1(874, 222, 215, 4, 33, 17, 45, 37), BLACK_KNIFE_P2(
				5658, 222, 215, 4, 33, 17, 45, 37), BLACK_KNIFE_P3(5665, 222, 215, 4, 33, 17, 45, 37),

		MITHRIL_KNIFE(866, 223, 215, 4, 33, 19, 45, 37), MITHRIL_KNIFE_P1(873, 223, 215, 4, 33, 19, 45,
				37), MITHRIL_KNIFE_P2(5657, 223, 215, 4, 33, 19, 45,
						37), MITHRIL_KNIFE_P3(5664, 223, 215, 4, 33, 19, 45, 37),

		ADAMANT_KNIFE(867, 224, 217, 4, 33, 24, 45, 37), ADAMANT_KNIFE_P1(875, 224, 217, 4, 33, 24, 45,
				37), ADAMANT_KNIFE_P2(5659, 224, 217, 4, 33, 24, 45,
						37), ADAMANT_KNIFE_P3(5666, 224, 217, 4, 33, 24, 45, 37),

		RUNE_KNIFE(868, 225, 218, 4, 33, 30, 48, 37), RUNE_KNIFE_P1(876, 225, 218, 4, 33, 30, 48,
				37), RUNE_KNIFE_P2(5660, 225, 218, 4, 33, 30, 48, 37), RUNE_KNIFE_P3(5667, 225, 218, 4, 33, 30, 48, 37),

		BRONZE_THROWNAXE(800, 43, 36, 3, 44, 7, 43, 31), IRON_THROWNAXE(801, 42, 35, 3, 44, 9, 43, 31), STEEL_THROWNAXE(
				802, 44, 37, 3, 44, 11, 43, 31), MITHRIL_THROWNAXE(803, 45, 38, 3, 44, 13, 43, 31), ADAMANT_THROWNAXE(
						804, 46, 39, 3, 44, 15, 43, 31), RUNE_THROWNAXE(805, 48, 41, 3, 44, 17, 43, 31),

		BRONZE_JAVELIN(825, 206, 200, 2, 40, 7, 45, 37), IRON_JAVELIN(826, 207, 201, 2, 40, 9, 45, 37), STEEL_JAVELIN(
				827, 208, 202, 2, 40, 11, 45, 37), MITHRIL_JAVELIN(828, 209, 203, 2, 40, 13, 45, 37), ADAMANT_JAVELIN(
						829, 210, 204, 2, 40, 15, 45, 37), RUNE_JAVELIN(830, 211, 205, 2, 40, 17, 45, 37),

		TOKTZ_XIL_UL(6522, -1, 442, 2, 40, 58, 51, 37),

		CHINCHOMPA(10033, -1, -1, 17, 8, 50, 45, 37), RED_CHINCHOMPA(10034, -1, -1, 17, 8, 80, 45, 37),

		BOLT_RACK(4740, -1, 27, 3, 33, 70, 43, 31),

		ZULRAH_SCALES(12934, -1, 27, 3, 33, 115, 43, 31);

		;

		AmmunitionData(int itemId, int startGfxId, int projectileId, int projectileSpeed, int projectileDelay,
				int strength, int startHeight, int endHeight) {
			this.itemId = itemId;
			this.startGfxId = startGfxId;
			this.projectileId = projectileId;
			this.projectileSpeed = projectileSpeed;
			this.projectileDelay = projectileDelay;
			this.strength = strength;
			this.startHeight = startHeight;
			this.endHeight = endHeight;
		}

		private int itemId;
		private int startGfxId;
		private int projectileId;
		private int projectileSpeed;
		private int projectileDelay;
		private int strength;
		private int startHeight;
		private int endHeight;

		public int getItemId() {
			return itemId;
		}

		public int getStartGfxId() {
			return startGfxId;
		}

		public int getProjectileId() {
			return projectileId;
		}

		public int getProjectileSpeed() {
			return projectileSpeed;
		}

		public int getProjectileDelay() {
			return projectileDelay;
		}

		public int getStrength() {
			return strength;
		}

		public int getStartHeight() {
			return startHeight;
		}

		public int getEndHeight() {
			return endHeight;
		}

		public static AmmunitionData getFor(Player p) {
			AmmunitionData arrows = range_ammo_data.get(p.getEquipment().getItems()[Equipment.AMMUNITION_SLOT].getId());
			if (arrows == null) {

				// Player has no arrows.
				// Maybe they have a throw weapon though? Knife/dart/javelin
				return range_ammo_data.get(p.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId());

			}
			return arrows;
		}

		static {
			for (AmmunitionData data : AmmunitionData.values()) {
				range_ammo_data.put(data.getItemId(), data);
			}
		}
	}

	public enum RangedWeaponType {

		LONGBOW(5, 5), SHORTBOW(5, 4), CROSSBOW(5, 5), THROW(4, 3), DARK_BOW(5, 5), BALLISTA(5, 6), BLOWPIPE(5, 3);

		RangedWeaponType(int distanceRequired, int attackDelay) {
			this.distanceRequired = distanceRequired;
			this.attackDelay = attackDelay;
		}

		private int distanceRequired;
		private int attackDelay;

		public int getDistanceRequired() {
			return distanceRequired;
		}

		public int getAttackDelay() {
			return attackDelay;
		}
	}

	/**
	 * Updates ranged data for a player
	 * 
	 * @param player
	 */
	public static void updateDataFor(Player player) {
		// Update ammunition data for ranged
		player.getCombat().setAmmunition(AmmunitionData.getFor(player));

		// Update weapon data for ranged
		player.getCombat().setRangedWeaponData(RangedWeaponData.getFor(player));
	}

	/**
	 * Checks if a player has enough ammo to perform a ranged attack
	 * 
	 * @param player
	 *            The player to run the check for
	 * @return True if player has ammo, false otherwise
	 */
	public static boolean checkAmmo(Player player) {
		// Get the ranged weapon data
		RangedWeaponData rangedWeapon = player.getCombat().getRangedWeaponData();
		if (rangedWeapon == null) {
			player.getCombat().reset();
			return false;
		}

		// Get the ranged ammo data
		final AmmunitionData ammoData = player.getCombat().getAmmunition();
		if (ammoData == null) {
			player.getPacketSender().sendMessage("You don't have any arrows to fire.");
			player.getCombat().reset();
			return false;
		}

		if (rangedWeapon.getType() == RangedWeaponType.THROW) {
			return true;
		}

		int requiredArrows = 1;

		// Dbow or msb special attacks - both require 2 arrows to fire.
		if (rangedWeapon == RangedWeaponData.DARK_BOW
				|| rangedWeapon == RangedWeaponData.MAGIC_SHORTBOW && player.isSpecialActivated()) {
			requiredArrows = 2;
		}

		Item ammoSlotItem = player.getEquipment().getItems()[Equipment.AMMUNITION_SLOT];
		if (ammoSlotItem.getId() == -1 || ammoSlotItem.getAmount() < requiredArrows) {
			player.getPacketSender().sendMessage("You don't have the required amount of ammunition to fire that.");
			player.getCombat().reset();
			return false;
		}

		boolean properReq = false;

		// BAD LOOP
		for (AmmunitionData d : rangedWeapon.getAmmunitionData()) {
			if (d == ammoData) {
				if (d.getItemId() == ammoSlotItem.getId()) {
					properReq = true;
					break;
				}
			}
		}

		if (!properReq) {
			String ammoName = ammoSlotItem.getDefinition().getName(),
					weaponName = player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getDefinition().getName(),
					add = !ammoName.endsWith("s") && !ammoName.endsWith("(e)") ? "s" : "";
			player.getPacketSender().sendMessage("You can not use " + ammoName + "" + add + " with "
					+ Misc.anOrA(weaponName) + " " + weaponName + ".");
			player.getCombat().reset();
			return false;
		}

		return true;
	}

	/**
	 * Decrements the amount ammo the {@link Player} currently has equipped.
	 * 
	 * @param player
	 *            the player to decrement ammo for.
	 */
	public static void decrementAmmo(Player player, Position pos) {

		// Determine which slot we are decrementing ammo from.
		int slot = player.getCombat().getWeapon() == WeaponInterface.SHORTBOW
				|| player.getCombat().getWeapon() == WeaponInterface.LONGBOW
				|| player.getCombat().getWeapon() == WeaponInterface.CROSSBOW ? Equipment.AMMUNITION_SLOT
						: Equipment.WEAPON_SLOT;

		boolean accumalator = player.getEquipment().get(Equipment.CAPE_SLOT).getId() == 10499;

		if (accumalator) {
			if (Misc.getRandom(11) <= 9) {
				return;
			}
		}

		// Decrement the ammo in the selected slot.
		player.getEquipment().get(slot).decrementAmount();
		if (!accumalator) {
			GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(player.getEquipment().get(slot).getId()),
					pos, player.getUsername(), false, 120, true, 120));
		}

		// If we are at 0 ammo remove the item from the equipment completely.
		if (player.getEquipment().get(slot).getAmount() == 0) {
			player.getPacketSender().sendMessage("You have run out of ammunition!");
			player.getEquipment().set(slot, new Item(-1));

			if (slot == Equipment.WEAPON_SLOT) {
				WeaponInterfaces.assign(player);
				player.getUpdateFlag().flag(Flag.APPEARANCE);
			}

		}

		// Refresh the equipment interface.
		player.getEquipment().refreshItems();
	}

	@SuppressWarnings("incomplete-switch")
	public static double getSpecialEffectsMultiplier(Player p, Character target, int damage) {

		double multiplier = 1.0;

		// Todo: ENCHANTED_RUBY_BOLT
		switch (p.getCombat().getAmmunition()) {

		case ENCHANTED_DIAMOND_BOLT:

			target.performGraphic(new Graphic(758, GraphicHeight.MIDDLE));
			multiplier = 1.15;

			break;

		case ENCHANTED_DRAGON_BOLT:

			target.performGraphic(new Graphic(756));

			boolean multiply = true;
			if (target.isPlayer()) {
				Player t = target.getAsPlayer();
				multiply = !(!t.getCombat().getFireImmunityTimer().finished()
						|| t.getEquipment().get(Equipment.SHIELD_SLOT).getId() == 1540
						|| t.getEquipment().get(Equipment.SHIELD_SLOT).getId() == 11283);
			}

			if (multiply) {
				multiplier = 1.31;
			}

			break;
		case ENCHANTED_EMERALD_BOLT:

			target.performGraphic(new Graphic(752));
			CombatFactory.poisonEntity(target, PoisonType.MILD);

			break;
		case ENCHANTED_JADE_BOLT:

			target.performGraphic(new Graphic(755));
			multiplier = 1.05;

			break;
		case ENCHANTED_ONYX_BOLT:

			target.performGraphic(new Graphic(753));
			multiplier = 1.26;
			int heal = (int) (damage * 0.25) + 10;
			p.getSkillManager().setCurrentLevel(Skill.HITPOINTS,
					p.getSkillManager().getCurrentLevel(Skill.HITPOINTS) + heal);
			if (p.getSkillManager().getCurrentLevel(Skill.HITPOINTS) >= 1120) {
				p.getSkillManager().setCurrentLevel(Skill.HITPOINTS, 1120);
			}
			p.getSkillManager().updateSkill(Skill.HITPOINTS);
			if (damage < 250 && Misc.getRandom(3) <= 1) {
				damage += 150 + Misc.getRandom(80);
			}

			break;

		case ENCHANTED_PEARL_BOLT:

			target.performGraphic(new Graphic(750));
			multiplier = 1.1;

			break;

		case ENCHANTED_RUBY_BOLT:

			break;
		case ENCHANTED_SAPPHIRE_BOLT:

			target.performGraphic(new Graphic(751));
			if (target.isPlayer()) {
				Player t = target.getAsPlayer();
				t.getSkillManager().setCurrentLevel(Skill.PRAYER,
						t.getSkillManager().getCurrentLevel(Skill.PRAYER) - 20);
				if (t.getSkillManager().getCurrentLevel(Skill.PRAYER) < 0) {
					t.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
				}
				t.getPacketSender().sendMessage("Your Prayer level has been leeched.");

				p.getSkillManager().setCurrentLevel(Skill.PRAYER,
						t.getSkillManager().getCurrentLevel(Skill.PRAYER) + 20);
				if (p.getSkillManager().getCurrentLevel(Skill.PRAYER) > p.getSkillManager().getMaxLevel(Skill.PRAYER)) {
					p.getSkillManager().setCurrentLevel(Skill.PRAYER, p.getSkillManager().getMaxLevel(Skill.PRAYER));
				} else {
					p.getPacketSender()
							.sendMessage("Your enchanced bolts leech some Prayer points from your opponent..");
				}
			}

			break;
		case ENCHANTED_TOPAZ_BOLT:

			target.performGraphic(new Graphic(757));
			if (target.isPlayer()) {
				Player t = target.getAsPlayer();
				t.getSkillManager().setCurrentLevel(Skill.MAGIC, t.getSkillManager().getCurrentLevel(Skill.MAGIC) - 3);
				t.getPacketSender().sendMessage("Your Magic level has been reduced.");
			}

			break;
		case ENCHANTED_OPAL_BOLT:

			target.performGraphic(new Graphic(749));
			multiplier = 1.3;

			break;

		}

		return multiplier;
	}

}
