package com.elvarg.cache.impl.definitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elvarg.util.JsonLoader;
import com.elvarg.util.Misc;
import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.grounditems.GroundItemManager;
import com.elvarg.world.model.GroundItem;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.Position;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Controls the npc drops
 * 
 * @author 2012 <http://www.rune-server.org/members/dexter+morgan/>, Gabbe &
 *         Samy
 * 
 */
public class NPCDrops {

	/**
	 * The map containing all the npc drops.
	 */
	private static Map<Integer, NPCDrops> dropControllers = new HashMap<Integer, NPCDrops>();

	public static JsonLoader parseDrops() {

		ItemDropAnnouncer.init();

		return new JsonLoader() {

			@Override
			public void load(JsonObject reader, Gson builder) {
				int[] npcIds = builder.fromJson(reader.get("npcIds"), int[].class);
				NpcDropItem[] drops = builder.fromJson(reader.get("drops"), NpcDropItem[].class);

				NPCDrops d = new NPCDrops();
				d.npcIds = npcIds;
				d.drops = drops;
				for (int id : npcIds) {
					dropControllers.put(id, d);
				}
			}

			@Override
			public String filePath() {
				return "./data/def/json/drops.json";
			}
		};
	}

	/**
	 * The id's of the NPC's that "owns" this class.
	 */
	private int[] npcIds;

	/**
	 * All the drops that belongs to this class.
	 */
	private NpcDropItem[] drops;

	/**
	 * Gets the NPC drop controller by an id.
	 * 
	 * @return The NPC drops associated with this id.
	 */
	public static NPCDrops forId(int id) {
		return dropControllers.get(id);
	}

	public static Map<Integer, NPCDrops> getDrops() {
		return dropControllers;
	}

	/**
	 * Gets the drop list
	 * 
	 * @return the list
	 */
	public NpcDropItem[] getDropList() {
		return drops;
	}

	/**
	 * Gets the npcIds
	 * 
	 * @return the npcIds
	 */
	public int[] getNpcIds() {
		return npcIds;
	}

	/**
	 * Represents a npc drop item
	 */
	public static class NpcDropItem {

		/**
		 * The id.
		 */
		private final int id;

		/**
		 * Array holding all the amounts of this item.
		 */
		private final int[] count;

		/**
		 * The chance of getting this item.
		 */
		private final int chance;

		/**
		 * New npc drop item
		 * 
		 * @param id
		 *            the item
		 * @param count
		 *            the count
		 * @param chance
		 *            the chance
		 */
		public NpcDropItem(int id, int[] count, int chance) {
			this.id = id;
			this.count = count;
			this.chance = chance;
		}

		/**
		 * Gets the item id.
		 * 
		 * @return The item id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the chance.
		 * 
		 * @return The chance.
		 */
		public int[] getCount() {
			return count;
		}

		/**
		 * Gets the chance.
		 * 
		 * @return The chance.
		 */
		public DropChance getChance() {
			switch (chance) {
			case 1:
				return DropChance.ALMOST_ALWAYS; // 50% <-> 1/2
			case 2:
				return DropChance.VERY_COMMON; // 20% <-> 1/5
			case 3:
				return DropChance.COMMON; // 5% <-> 1/20
			case 4:
				return DropChance.UNCOMMON; // 2% <-> 1/50
			case 5:
				return DropChance.RARE; // 0.5% <-> 1/200
			case 6:
				return DropChance.LEGENDARY; // 0.2% <-> 1/500
			case 7:
				return DropChance.LEGENDARY_2;
			case 8:
				return DropChance.LEGENDARY_3;
			case 9:
				return DropChance.LEGENDARY_4;
			case 10:
				return DropChance.LEGENDARY_5;
			case 11:
				return DropChance.LEGENDARY_6;
			case 12:
				return DropChance.LEGENDARY_7;
			default:
				return DropChance.ALWAYS; // 100% <-> 1/1
			}
		}

		/**
		 * Gets the item
		 * 
		 * @return the item
		 */
		public Item getItem() {
			int amount = 0;
			for (int i = 0; i < count.length; i++)
				amount += count[i];
			if (amount > count[0])
				amount = count[0] + Misc.getRandom(count[1]);
			return new Item(id, amount);
		}
	}

	public enum DropChance {
		ALWAYS(0), ALMOST_ALWAYS(2), VERY_COMMON(5), COMMON(15), UNCOMMON(40), NOTTHATRARE(100), RARE(155), LEGENDARY(
				320), LEGENDARY_2(
						410), LEGENDARY_3(485), LEGENDARY_4(680), LEGENDARY_5(810), LEGENDARY_6(950), LEGENDARY_7(1100);

		DropChance(int randomModifier) {
			this.random = randomModifier;
		}

		private int random;

		public int getRandom() {
			return this.random;
		}
	}

	/**
	 * Drops items for a player after killing an npc. A player can max receive
	 * one item per drop chance.
	 * 
	 * @param p
	 *            Player to receive drop.
	 * @param npc
	 *            NPC to receive drop FROM.
	 */
	public static void dropItems(Player p, NPC npc) {
	}

	public static boolean shouldDrop(boolean[] b, DropChance chance, boolean ringOfWealth) {
		int random = chance.getRandom();
		if (ringOfWealth && random >= 60) {
			random -= (random / 10);
		}
		return !b[chance.ordinal()] && Misc.getRandom(random) == 1;
	}

	public static void drop(Player player, Item item, NPC npc, Position pos, boolean goGlobal) {
	}

	public static void casketDrop(Player player, int combat, Position pos) {
		int chance = 1 + combat;
		if (Misc.getRandom(combat <= 50 ? 1300 : 1000) < chance) {
			GroundItemManager.spawnGroundItem(player,
					new GroundItem(new Item(7956), pos, player.getUsername(), false, 150, true, 200));
		}
	}

	public static class ItemDropAnnouncer {

		private static List<Integer> ITEM_LIST;

		private static final int[] TO_ANNOUNCE = new int[] { 14484, 4224, 11702, 11704, 11706, 11708, 11704, 11724,
				11726, 11728, 11718, 11720, 11722, 11730, 11716, 14876, 11286, 13427, 6731, 6737, 6735, 4151, 2513,
				15259, 13902, 13890, 13884, 13861, 13858, 13864, 13905, 13887, 13893, 13899, 13873, 13879, 13876, 13870,
				6571, 14008, 14009, 14010, 14011, 14012, 14013, 14014, 14015, 14016, 13750, 13748, 13746, 13752, 11335,
				15486, 13870, 13873, 13876, 13884, 13890, 13896, 13902, 13858, 13861, 13864, 13867, 11995, 11996, 11997,
				11978, 12001, 12002, 12003, 12004, 12005, 12006, 11990, 11991, 11992, 11993, 11994, 11989, 11988, 11987,
				11986, 11985, 11984, 11983, 11982, 11981, 11979, 13659, 11235, 20000, 20001, 20002, 15103, 15104, 15105,
				15106, 12603, 12601, 12605, 19908 };

		private static void init() {
			ITEM_LIST = new ArrayList<Integer>();
			for (int items : TO_ANNOUNCE) {
				ITEM_LIST.add(items);
			}
		}

		public static boolean announce(int item) {
			return ITEM_LIST.contains(item);
		}
	}
}