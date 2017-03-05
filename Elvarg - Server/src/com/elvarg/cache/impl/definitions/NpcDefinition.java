package com.elvarg.cache.impl.definitions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.elvarg.GameConstants;
import com.elvarg.world.World;
import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.npc.combat.MobCombatHandler;
import com.elvarg.world.model.Position;

/**
 * A single npc definition.
 * 
 * @author lare96
 */
public class NpcDefinition {

	/**
	 * The amount of definitions.
	 */
	private static final int DEFINITION_AMOUNT = 14500;

	public static void init() {

		definitions = new NpcDefinition[DEFINITION_AMOUNT];

		NpcDefinition definition = definitions[0];
		try {
			File file = new File(GameConstants.DEFINITIONS_DIRECTORY + "npcs.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;

			Position spawnPos = null;

			while ((line = reader.readLine()) != null) {

				if (line.contains("inish")) {
					definitions[definition.id] = definition;

					// Loaded the definitions for this npc.
					// Let's spawn the npc if needed.
					if (definition != null) {
						if (spawnPos != null) {
							NPC npc = new NPC(definition.getId(), spawnPos);
							if (definition.getHitpoints() > 0) {
								npc.setHitpoints(definition.getHitpoints());
							}
							World.getNpcAddQueue().add(npc);
							spawnPos = null;
						}
					}

					continue;
				}

				String[] args = line.split(": ");
				if (args.length <= 1)
					continue;
				String token = args[0], value = args[1];
				switch (token.toLowerCase()) {
				case "npc":
					int id = Integer.valueOf(value);

					// If definitions already exist -
					// We're just spawning a new npc.
					// ONLY create new definition if there isn't one already.
					if (forId(id) == null) {
						definition = new NpcDefinition();
						definition.id = id;
					}

					break;
				case "name":
					if (value == null)
						continue;
					definition.name = value;
					break;
				case "examine":
					definition.examine = value;
					break;
				case "spawn-position":
					String[] coordinates = value.split(":");
					int x = Integer.parseInt(coordinates[0]);
					int y = Integer.parseInt(coordinates[1]);
					int z = coordinates.length == 3 ? Integer.parseInt(coordinates[2]) : 0;
					spawnPos = new Position(x, y, z);
					break;
				case "size":
					definition.size = Integer.parseInt(value);
					break;
				case "walk-radius":
					definition.walkRadius = Integer.parseInt(value);
					break;
				case "combat-follow":
					definition.combatFollowDistance = Integer.parseInt(value);
					break;
				case "aggressive-distance":
					definition.aggressionDistance = Integer.parseInt(value);
					break;
				case "attackable":
					definition.attackable = Boolean.parseBoolean(value);
					break;
				case "retreats":
					definition.retreats = Boolean.parseBoolean(value);
					break;
				case "poisonous":
					definition.poisonous = Boolean.parseBoolean(value);
					break;
				case "respawn":
					definition.respawn = Integer.parseInt(value);
					break;
				case "maxhit":
					definition.maxHit = Integer.parseInt(value);
					break;
				case "hitpoints":
					definition.hitpoints = Integer.parseInt(value);
					break;
				case "attackspeed":
					definition.attackSpeed = Integer.parseInt(value);
					break;
				case "attackanim":
					definition.attackAnim = Integer.parseInt(value);
					break;
				case "defenceanim":
					definition.defenceAnim = Integer.parseInt(value);
					break;
				case "deathanim":
					definition.deathAnim = Integer.parseInt(value);
					break;
				case "combatlevel":
					definition.combatLevel = Integer.parseInt(value);
					break;
				case "attacklevel":
					definition.attackLevel = Integer.parseInt(value);
					break;
				case "strengthlevel":
					definition.strengthLevel = Integer.parseInt(value);
					break;
				case "rangedlevel":
					definition.rangedLevel = Integer.parseInt(value);
					break;
				case "magiclevel":
					definition.magicLevel = Integer.parseInt(value);
					break;
				case "defencemelee":
					definition.defenceMelee = Integer.parseInt(value);
					break;
				case "defencerange":
					definition.defenceRange = Integer.parseInt(value);
					break;
				case "defencemage":
					definition.defenceMage = Integer.parseInt(value);
					break;
				case "slayerlevel":
					definition.slayerLevel = Integer.parseInt(value);
					break;
				}
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** An array containing all of the npc definitions. */
	private static NpcDefinition[] definitions;

	public static NpcDefinition forId(int id) {
		return id > definitions.length ? null : definitions[id];
	}

	/** The id of the npc. */
	private int id;

	/** The name of the npc. */
	private String name;

	/** The examine of the npc. */
	private String examine;

	/** The npc size. */
	private int size = 1;

	/** Does the npc randomly walk? */
	private int walkRadius;

	/** If the npc is attackable. */
	private boolean attackable;

	/** If the npc is aggressive. */
	private boolean aggressive;

	/** If the npc retreats. */
	private boolean retreats;

	/** If the npc poisons. */
	private boolean poisonous;

	/** Time it takes for this npc to respawn. */
	private int respawn;

	/** The max hit of this npc. */
	private int maxHit;

	/** The amount of hp this npc has. */
	private int hitpoints;

	/** The attack speed of this npc. */
	private int attackSpeed;

	/** The attack animation of this npc. */
	private int attackAnim;

	/** The defence animation of this npc. */
	private int defenceAnim;

	/** The death animation of this npc. */
	private int deathAnim;

	/** This npc's combat level */
	private int combatLevel;

	/** This npc's attack bonus. */
	private int attackLevel;
	private int strengthLevel;
	private int rangedLevel;
	private int magicLevel;

	/** This npc's melee resistance. */
	private int defenceMelee;

	/** This npc's range resistance. */
	private int defenceRange;

	/** This npc's defence resistance. */
	private int defenceMage;

	/** This npc's slayer level required to attack. */
	private int slayerLevel;

	/** This npc's aggression distance */
	private int aggressionDistance;

	/** This npc's maximum follow distance in combat **/
	private int combatFollowDistance = 7; // Default is 7

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExamine() {
		return examine;
	}

	public void setExamine(String examine) {
		this.examine = examine;
	}

	public boolean isAttackable() {
		return attackable;
	}

	public void setAttackable(boolean attackable) {
		this.attackable = attackable;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean doesRetreat() {
		return retreats;
	}

	public void setRetreats(boolean retreats) {
		this.retreats = retreats;
	}

	public boolean isPoisonous() {
		return poisonous;
	}

	public void setPoisonous(boolean poisonous) {
		this.poisonous = poisonous;
	}

	public int getRespawn() {
		return respawn;
	}

	public void setRespawn(int respawn) {
		this.respawn = respawn;
	}

	public int getMaxHit() {
		MobCombatHandler.mobCombatHandler.getMaxHit(maxHit);
		return maxHit;
	}

	public void setMaxHit(int maxHit) {
		this.maxHit = maxHit;
	}

	public int getHitpoints() {
		return hitpoints;
	}

	public void setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
	}

	public int getAttackSpeed() {
		return attackSpeed;
	}

	public void setAttackSpeed(int attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	public int getAttackAnim() {
		return attackAnim;
	}

	public void setAttackAnim(int attackAnim) {
		this.attackAnim = attackAnim;
	}

	public int getDefenceAnim() {
		return defenceAnim;
	}

	public void setDefenceAnim(int defenceAnim) {
		this.defenceAnim = defenceAnim;
	}

	public int getDeathAnim() {
		return deathAnim;
	}

	public void setDeathAnim(int deathAnim) {
		this.deathAnim = deathAnim;
	}

	public int getAttackLevel() {
		return attackLevel;
	}

	public void setAttackLevel(int attackLevel) {
		this.attackLevel = attackLevel;
	}

	public int getStrengthLevel() {
		return strengthLevel;
	}

	public void setStrengthLevel(int strengthLevel) {
		this.strengthLevel = strengthLevel;
	}

	public int getRangedLevel() {
		return rangedLevel;
	}

	public void setRangedLevel(int rangedLevel) {
		this.rangedLevel = rangedLevel;
	}

	public int getMagicLevel() {
		return magicLevel;
	}

	public void setMagicLevel(int magicLevel) {
		this.magicLevel = magicLevel;
	}

	public int getDefenceMelee() {
		return defenceMelee;
	}

	public void setDefenceMelee(int defenceMelee) {
		this.defenceMelee = defenceMelee;
	}

	public int getDefenceRange() {
		return defenceRange;
	}

	public void setDefenceRange(int defenceRange) {
		this.defenceRange = defenceRange;
	}

	public int getDefenceMage() {
		return defenceMage;
	}

	public void setDefenceMage(int defenceMage) {
		this.defenceMage = defenceMage;
	}

	public int getSlayerLevel() {
		return slayerLevel;
	}

	public void setSlayerLevel(int slayerLevel) {
		this.slayerLevel = slayerLevel;
	}

	public int getWalkRadius() {
		return walkRadius;
	}

	public void setWalkRadius(int walkRadius) {
		this.walkRadius = walkRadius;
	}

	public int getCombatLevel() {
		return combatLevel;
	}

	public void setCombatLevel(int combatLevel) {
		this.combatLevel = combatLevel;
	}

	public int getAggressionDistance() {
		return aggressionDistance;
	}

	public void setAggressionDistance(int aggressionDistance) {
		this.aggressionDistance = aggressionDistance;
	}

	public int getCombatFollowDistance() {
		return combatFollowDistance;
	}

	public void setCombatFollowDistance(int combatFollowDistance) {
		this.combatFollowDistance = combatFollowDistance;
	}

}
