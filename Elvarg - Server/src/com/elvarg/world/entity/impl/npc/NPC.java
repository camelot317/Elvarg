package com.elvarg.world.entity.impl.npc;

import com.elvarg.cache.impl.definitions.NpcDefinition;
import com.elvarg.engine.task.TaskManager;
import com.elvarg.engine.task.impl.NPCDeathTask;
import com.elvarg.world.entity.combat.CombatFactory;
import com.elvarg.world.entity.combat.CombatType;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.npc.NPCMovementCoordinator.CoordinateState;
import com.elvarg.world.model.Locations;
import com.elvarg.world.model.Position;

/**
 * Represents a non-playable character, which players can interact with.
 * 
 * @author Gabriel Hannason
 */

public class NPC extends Character {

	public NPC(int id, Position position) {
		super(position);

		this.id = id;
		this.spawnPosition = position;
	}

	public void onTick() {

		// Only process the npc if they have properly been added
		// to the game with a definition.
		if (getDefinition() != null) {

			// Handle combat
			getCombat().onTick();

			// Handles random walk and retreating from fights
			getMovementQueue().onTick();
			movementCoordinator.onTick();

			// Process locations
			Locations.process(this);

			// Regenerating health if needed, but not in combat!
			if (!CombatFactory.inCombat(this)
					|| movementCoordinator.getCoordinateState() == CoordinateState.RETREATING) {

				// We've been damaged.
				// Regenerate health.
				if (getDefinition().getHitpoints() > hitpoints) {
					setHitpoints(hitpoints + (int) (getDefinition().getHitpoints() * 0.1));
					if (hitpoints > getDefinition().getHitpoints()) {
						setHitpoints(getDefinition().getHitpoints());
					}
				}

			}

		}
	}

	@Override
	public void appendDeath() {
		if (!isDying) {
			TaskManager.submit(new NPCDeathTask(this));
			isDying = true;
		}
	}

	/**
	 * Called upon being registered to the world.
	 */
	@Override
	public void onRegister() {

	}

	@Override
	public int getHitpoints() {
		return hitpoints;
	}

	@Override
	public NPC setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
		if (this.hitpoints <= 0)
			appendDeath();
		return this;
	}

	@Override
	public void heal(int heal) {
		if ((this.hitpoints + heal) > getDefinition().getHitpoints()) {
			setHitpoints(getDefinition().getHitpoints());
			return;
		}
		setHitpoints(this.hitpoints + heal);
	}

	@Override
	public boolean isNpc() {
		return true;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof NPC && ((NPC) other).getIndex() == getIndex();
	}

	@Override
	public int getSize() {
		return getDefinition() == null ? 1 : getDefinition().getSize();
	}

	@Override
	public int getBaseAttack(CombatType type) {
		return getDefinition().getAttackLevel();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public int getBaseDefence(CombatType type) {
		int base = 0;
		switch (type) {
		case MAGIC:
			base = getDefinition().getDefenceMage();
			break;
		case MELEE:
			base = getDefinition().getDefenceMelee();
			break;
		case RANGED:
			base = getDefinition().getDefenceRange();
			break;
		}
		return base;
	}

	@Override
	public int getBaseAttackSpeed() {
		return getDefinition().getAttackSpeed();
	}

	@Override
	public int getAttackAnim() {
		return getDefinition().getAttackAnim();
	}

	@Override
	public int getBlockAnim() {
		return getDefinition().getDefenceAnim();
	}

	/*
	 * Fields
	 */

	private NPCMovementCoordinator movementCoordinator = new NPCMovementCoordinator(this);

	private final int id;
	private int hitpoints;
	private Position spawnPosition;

	private int transformationId = -1;
	private boolean isDying;
	private boolean visible = true;

	/*
	 * Getters and setters
	 */

	public int getId() {
		return id;
	}

	public int getTransformationId() {
		return transformationId;
	}

	public void setTransformationId(int transformationId) {
		this.transformationId = transformationId;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setDying(boolean isDying) {
		this.isDying = isDying;
	}

	public boolean isDying() {
		return isDying;
	}

	public NPCMovementCoordinator getMovementCoordinator() {
		return movementCoordinator;
	}

	public NpcDefinition getDefinition() {
		return NpcDefinition.forId(id);
	}

	public Position getSpawnPosition() {
		return spawnPosition;
	}
}
