package com.elvarg.world.content;

import java.util.HashMap;
import java.util.Map;

import com.elvarg.cache.impl.definitions.ItemDefinition;
import com.elvarg.engine.task.TaskManager;
import com.elvarg.engine.task.impl.OverloadPotionTask;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.EffectTimer;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.Skill;

/**
 * Consumables are items that players can use to restore stats/points.
 * Examples of Consumable items: Food, Potions
 * 
 * @author Swiffy
 */

public class Consumables {

	/**
	 * Checks if <code>item</code> is a valid consumable food type.
	 * @param player	The player clicking on <code>item</code>.
	 * @param item		The item being clicked upon.
	 * @param slot		The slot of the item.
	 * @return			If <code>true</code> player will proceed to eat said item.
	 */
	public static boolean isFood(Player player, int item, int slot) {
		FoodType food = FoodType.types.get(item);
		if (food != null) {
			eat(player, food, slot);
			return true;
		}
		return false;
	}

	/**
	 * The heal option on the Health Orb
	 * @param player		The player to heal
	 */
	public static void handleHealAction(Player player) {
		if(!player.getFoodTimer().elapsed(1300)) 
			return;
		for(Item item : player.getInventory().getItems()) {
			if(item != null) {
				if(isFood(player, item.getId(), player.getInventory().getSlot(item.getId()))) {
					return;
				}
			}
		}
		player.getPacketSender().sendMessage("You do not have any items that can heal you in your inventory.");
	}

	/**
	 * Handles the player eating said food type.
	 * @param player	The player eating the consumable.
	 * @param food		The food type being consumed.
	 * @param slot		The slot of the food being eaten.
	 */
	private static void eat(Player player, FoodType food, int slot) {
		if(player.getHitpoints() <= 0)
			return;
		/*	if(Dueling.checkRule(player, DuelRule.NO_FOOD)) {
			player.getPacketSender().sendMessage("Food has been disabled in this duel.");			
			return;
		}*/
		if (food != null && player.getFoodTimer().elapsed(1100) || food == FoodType.KARAMBWAN) {
			player.getCombat().reset();
			player.getCombat().setAttackTimer(player.getCombat().getAttackTimer() + 2);
			if(food != FoodType.KARAMBWAN) {
				player.getFoodTimer().reset();
			}
			//	player.getPotionTimer().reset();
			player.getPacketSender().sendInterfaceRemoval();
			player.performAnimation(new Animation(829));
			player.getInventory().delete(food.item, slot);
			int heal = food.heal;

			int max = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			if (heal + player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) > max) {
				heal = max - player.getSkillManager().getCurrentLevel(Skill.HITPOINTS);
				//Don't reset hp back to max level.. Player might have used brews
				if(heal < 0) {
					heal = 0;
				}
			}

			if(food == FoodType.CAKE || food == FoodType.SECOND_CAKE_SLICE) {
				player.getInventory().add(new Item(food.item.getId() + 2, 1));
			}
			String e = food.toString() == "BANDAGES" ? "use" : "eat";
			player.getPacketSender().sendMessage("You "+e+" the " + food.name + ".");
			player.setHitpoints(player.getHitpoints() + heal);
		}
	}

	/**
	 * Represents a valid consumable item.
	 * 
	 * @author relex lawl
	 */
	private enum FoodType {
		/*
		 * Fish food types players can get by fishing
		 * or purchasing from other entities.
		 */
		KEBAB(new Item(1971), 4),
		CHEESE(new Item(1985), 4),
		CAKE(new Item(1891), 5),
		SECOND_CAKE_SLICE(new Item(1893), 5),
		THIRD_CAKE_SLICE(new Item(1895), 5),
		BANDAGES(new Item(14640), 12),
		JANGERBERRIES(new Item(247), 2),
		WORM_CRUNCHIES(new Item(2205), 7),
		EDIBLE_SEAWEED(new Item(403), 4),
		ANCHOVIES(new Item(319), 1),
		SHRIMPS(new Item(315), 3),
		SARDINE(new Item(325), 4),
		COD(new Item(339), 7),
		TROUT(new Item(333), 7),
		PIKE(new Item(351), 8),
		SALMON(new Item(329), 9),
		TUNA(new Item(361), 10),
		LOBSTER(new Item(379), 12),
		BASS(new Item(365), 13),
		SWORDFISH(new Item(373), 14),
		MEAT_PIZZA(new Item(2293), 14),
		MONKFISH(new Item(7946), 16),
		SHARK(new Item(385), 20),
		SEA_TURTLE(new Item(397), 21),
		MANTA_RAY(new Item(391), 22),
		KARAMBWAN(new Item(3144), 18),

		/*
		 * Baked goods food types a player
		 * can make with the cooking skill.
		 */
		POTATO(new Item(1942), 1),
		BAKED_POTATO(new Item(6701), 4),
		POTATO_WITH_BUTTER(new Item(6703), 14),
		CHILLI_POTATO(new Item(7054), 14),
		EGG_POTATO(new Item(7056), 16),
		POTATO_WITH_CHEESE(new Item(6705), 16),
		MUSHROOM_POTATO(new Item(7058), 20),
		TUNA_POTATO(new Item(7060), 20),

		/*
		 * Fruit food types which a player can get
		 * by picking from certain trees or hand-making
		 * them (such as pineapple chunks/rings).
		 */
		SPINACH_ROLL(new Item(1969), 2),
		BANANA(new Item(1963), 2),
		BANANA_(new Item(18199), 2),
		CABBAGE(new Item(1965), 2),
		ORANGE(new Item(2108), 2),
		PINEAPPLE_CHUNKS(new Item(2116), 2),
		PINEAPPLE_RINGS(new Item(2118), 2),
		PEACH(new Item(6883), 8),

		/*
		 * Other food types.
		 */
		PURPLE_SWEETS(new Item(4561), 3);

		private FoodType(Item item, int heal) {
			this.item = item;
			this.heal = heal;
			this.name = (toString().toLowerCase().replaceAll("__", "-").replaceAll("_", " "));
		}

		private Item item;

		private int heal;

		private String name;

		private static Map<Integer, FoodType> types = new HashMap<Integer, FoodType>();

		static {
			for (FoodType type : FoodType.values()) {
				types.put(type.item.getId(), type);
			}
		}
	}
}
