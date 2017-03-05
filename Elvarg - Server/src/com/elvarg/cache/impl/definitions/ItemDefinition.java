package com.elvarg.cache.impl.definitions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.elvarg.GameConstants;
import com.elvarg.world.content.skills.SkillManager;
import com.elvarg.world.model.container.impl.Equipment;

/**
 * This file manages every item definition, which includes their name,
 * description, value, skill requirements, etc.
 * 
 * @author relex lawl
 */

public class ItemDefinition {

	/**
	 * The max amount of items that will be loaded.
	 */
	private static final int MAX_AMOUNT_OF_ITEMS = 21500;

	/**
	 * ItemDefinition array containing all items' definition values.
	 */
	private static ItemDefinition[] definitions = new ItemDefinition[MAX_AMOUNT_OF_ITEMS];

	/**
	 * Loading all item definitions
	 */
	public static void init() {
		ItemDefinition definition = definitions[0];
		try {
			File file = new File(GameConstants.DEFINITIONS_DIRECTORY + "items.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("inish")) {
					definitions[definition.id] = definition;
					continue;
				}
				String[] args = line.split(": ");
				if (args.length <= 1)
					continue;
				String token = args[0], value = args[1];
				if (line.contains("Bonus[")) {
					String[] other = line.split("]");
					int index = Integer.valueOf(line.substring(6, other[0].length()));
					double bonus = Double.valueOf(value);
					definition.bonus[index] = bonus;
					continue;
				}
				if (line.contains("Requirement[")) {
					String[] other = line.split("]");
					int index = Integer.valueOf(line.substring(12, other[0].length()));
					int requirement = Integer.valueOf(value);
					definition.requirement[index] = requirement;
					continue;
				}
				switch (token.toLowerCase()) {
				case "item id":
					int id = Integer.valueOf(value);
					definition = new ItemDefinition();
					definition.id = id;
					break;
				case "name":
					if (value == null)
						continue;
					definition.name = value;
					break;
				case "examine":
					definition.examine = value;
					break;
				case "value":
					int price = Integer.valueOf(value);
					definition.value = price;
					break;
				case "stackable":
					definition.stackable = Boolean.valueOf(value);
					break;
				case "tradeable":
					definition.tradeable = Boolean.valueOf(value);
					break;
				case "sellable":
					definition.sellable = Boolean.valueOf(value);
					break;
				case "dropable":
					definition.dropable = Boolean.valueOf(value);
					break;
				case "noted":
					definition.noted = Boolean.valueOf(value);
					break;
				case "noteid":
					definition.noteId = Integer.parseInt(value);
					break;
				case "double-handed":
					definition.doubleHanded = Boolean.valueOf(value);
					break;
				case "equipmenttype":
					definition.equipmentType = EquipmentType.valueOf(value);
					break;
				case "blockanim":
					definition.blockAnim = Integer.parseInt(value);
					break;
				case "standanim":
					definition.standAnim = Integer.parseInt(value);
					break;
				case "walkanim":
					definition.walkAnim = Integer.parseInt(value);
					break;
				case "runanim":
					definition.runAnim = Integer.parseInt(value);
					break;
				case "standturnanim":
					definition.standTurnAnim = Integer.parseInt(value);
					break;
				case "turn180anim":
					definition.turn180Anim = Integer.parseInt(value);
					break;
				case "turn90cwanim":
					definition.turn90CWAnim = Integer.parseInt(value);
					break;
				case "turn90ccwanim":
					definition.turn90CCWAnim = Integer.parseInt(value);
					break;
				case "interfaceid":
					definition.interfaceId = Integer.parseInt(value);
					break;
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ItemDefinition[] getDefinitions() {
		return definitions;
	}

	/**
	 * Gets the item definition correspondent to the id.
	 * 
	 * @param id
	 *            The id of the item to fetch definition for.
	 * @return definitions[id].
	 */
	public static ItemDefinition forId(int id) {
		return (id < 0 || id > definitions.length || definitions[id] == null) ? create(id) : definitions[id];
	}

	/**
	 * Gets the max amount of items that will be loaded in Niobe.
	 * 
	 * @return The maximum amount of item definitions loaded.
	 */
	public static int getMaxAmountOfItems() {
		return MAX_AMOUNT_OF_ITEMS;
	}

	public static ItemDefinition UNARMED_ITEM_DEFINITION;

	public static ItemDefinition create(int item) {
		if (item == -1) {
			if (UNARMED_ITEM_DEFINITION != null) {
				return UNARMED_ITEM_DEFINITION;
			}
			UNARMED_ITEM_DEFINITION = new ItemDefinition();
			UNARMED_ITEM_DEFINITION.name = "Unarmed";
			return UNARMED_ITEM_DEFINITION;
		}
		return new ItemDefinition();
	}

	private int id;
	private String name = "";
	private String examine = "";
	private int value;
	private boolean stackable, tradeable, sellable, dropable, noted;
	private int noteId;
	private boolean doubleHanded;
	private int blockAnim = 424, standAnim = 808, walkAnim = 819, runAnim = 824, standTurnAnim = 823, turn180Anim = 820,
			turn90CWAnim = 821, turn90CCWAnim = 821, interfaceId;
	private double[] bonus = new double[18];
	private int[] requirement = new int[SkillManager.AMOUNT_OF_SKILLS];
	private EquipmentType equipmentType = EquipmentType.NONE;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getExamine() {
		return examine;
	}

	public int getValue() {
		return value;
	}

	public boolean isStackable() {
		return stackable;
	}

	public boolean isTradeable() {
		return tradeable;
	}

	public boolean isSellable() {
		return sellable;
	}

	public boolean isDropable() {
		return dropable;
	}

	public boolean isNoted() {
		return noted;
	}

	public int getNoteId() {
		return noteId;
	}

	public boolean isDoubleHanded() {
		return doubleHanded;
	}

	public int getBlockAnim() {
		return blockAnim;
	}

	public int getStandAnim() {
		return standAnim;
	}

	public int getWalkAnim() {
		return walkAnim;
	}

	public int getRunAnim() {
		return runAnim;
	}

	public int getStandTurnAnim() {
		return standTurnAnim;
	}

	public int getTurn180Anim() {
		return turn180Anim;
	}

	public int getTurn90CWAnim() {
		return turn90CWAnim;
	}

	public int getTurn90CCWAnim() {
		return turn90CCWAnim;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public int[] getRequirement() {
		return requirement;
	}

	public double[] getBonus() {
		return bonus;
	}

	public int getEquipmentSlot() {
		return equipmentType.slot;
	}

	/**
	 * Checks if item is a full torso replacer.
	 */
	public boolean isFullBody() {
		return equipmentType.equals(EquipmentType.PLATEBODY);
	}

	/**
	 * Checks if item is a full head replacer.
	 */
	public boolean isFullHelm() {
		return equipmentType.equals(EquipmentType.FULL_HELMET);
	}

	/**
	 * Checks if item is a head replacer and keeps facial hair.
	 */
	public boolean isMedHelm() {
		return equipmentType.equals(EquipmentType.MED_HELMET);
	}

	/**
	 * Checks if item is a hooded cape/cloak.
	 */
	public boolean isHoodedCape() {
		return equipmentType.equals(EquipmentType.HOODED_CAPE);
	}

	/**
	 * Checks if item is a mask/replaces facial hair.
	 */
	public boolean isMask() {
		return equipmentType.equals(EquipmentType.MASK);
	}

	private enum EquipmentType {
		HOODED_CAPE(Equipment.CAPE_SLOT), CAPE(Equipment.CAPE_SLOT),

		SHIELD(Equipment.SHIELD_SLOT),

		GLOVES(Equipment.HANDS_SLOT),

		BOOTS(Equipment.FEET_SLOT),

		AMULET(Equipment.AMULET_SLOT),

		RING(Equipment.RING_SLOT),

		ARROWS(Equipment.AMMUNITION_SLOT),

		HAT(Equipment.HEAD_SLOT), // DOESNT CHANGE HAIR CLIPPING/JUST ADDS TO
									// HEAD
		MED_HELMET(Equipment.HEAD_SLOT), MASK(Equipment.HEAD_SLOT), FULL_MASK(Equipment.HEAD_SLOT), // TODO
																									// DOES
																									// NOTHING,
																									// PROBABALY
																									// WILL
																									// ALWAYS
																									// HAVE
																									// NO
																									// USE,
																									// currently
																									// unued
		FULL_HELMET(Equipment.HEAD_SLOT),

		BODY(Equipment.BODY_SLOT), // TORSO REMOVAL
		PLATEBODY(Equipment.BODY_SLOT),

		LEGS(Equipment.LEG_SLOT), // REMOVES BOTTOM HALF OF BODY TO FEET IF ITEM
									// HAS NO LEG DATA

		WEAPON(Equipment.WEAPON_SLOT),

		NONE(-1);// DEFAULT/NOTHING IN SLOT

		private EquipmentType(int slot) {
			this.slot = slot;
		}

		private int slot;
	}

	@Override
	public String toString() {
		return "[ItemDefinition(" + id + ")] - Name: " + name + "; equipment slot: " + getEquipmentSlot() + "; value: "
				+ value + "; stackable ? " + Boolean.toString(stackable) + "; noted ? " + Boolean.toString(noted)
				+ "; 2h ? " + doubleHanded;
	}

	public static int getItemId(String itemName) {
		for (int i = 0; i < MAX_AMOUNT_OF_ITEMS; i++) {
			if (definitions[i] != null) {
				if (definitions[i].getName().equalsIgnoreCase(itemName)) {
					return definitions[i].getId();
				}
			}
		}
		return -1;
	}
}
