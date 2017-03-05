package com.elvarg.world.entity.impl;

import com.elvarg.engine.task.Task;
import com.elvarg.engine.task.TaskManager;
import com.elvarg.util.Stopwatch;
import com.elvarg.world.entity.Entity;
import com.elvarg.world.entity.combat.Combat;
import com.elvarg.world.entity.combat.CombatType;
import com.elvarg.world.entity.combat.hit.HitDamage;
import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.Direction;
import com.elvarg.world.model.Flag;
import com.elvarg.world.model.Graphic;
import com.elvarg.world.model.Locations.Location;
import com.elvarg.world.model.Position;
import com.elvarg.world.model.UpdateFlag;
import com.elvarg.world.model.movement.MovementQueue;

/**
 * A player or NPC
 * 
 * @author Gabriel Hannason
 */

public abstract class Character extends Entity {

	public Character(Position position) {
		super(position);
	}

	public Character moveTo(Position teleportTarget) {
		getMovementQueue().reset();
		setPosition(teleportTarget.copy());
		setNeedsPlacement(true);
		setResetMovementQueue(true);
		if (isPlayer()) {
			getMovementQueue().handleRegionChange();
		}
		return this;
	}

	public Character forceChat(String message) {
		setForcedChat(message);
		getUpdateFlag().flag(Flag.FORCED_CHAT);
		return this;
	}

	public Character setEntityInteraction(Entity entity) {
		this.interactingEntity = entity;
		getUpdateFlag().flag(Flag.ENTITY_INTERACTION);
		return this;
	}

	@Override
	public void performAnimation(Animation animation) {
		setAnimation(animation);
	}

	@Override
	public void performGraphic(Graphic graphic) {
		setGraphic(graphic);
	}

	/*
	 * Fields
	 */

	private final Combat combat = new Combat(this);
	private final MovementQueue movementQueue = new MovementQueue(this);
	private String forcedChat;
	private Direction direction, primaryDirection = Direction.NONE, secondaryDirection = Direction.NONE,
			lastDirection = Direction.NONE;
	private Stopwatch lastCombat = new Stopwatch();
	private UpdateFlag updateFlag = new UpdateFlag();
	private Location location = Location.DEFAULT;
	private Position positionToFace;
	private Animation animation;
	private Graphic graphic;
	private Entity interactingEntity;
	public Position singlePlayerPositionFacing;
	private int npcTransformationId;
	private int poisonDamage;
	private boolean[] prayerActive = new boolean[30], curseActive = new boolean[20];
	private boolean resetMovementQueue;
	private boolean needsPlacement;

	private HitDamage primaryHit;
	private HitDamage secondaryHit;

	public abstract void onRegister();

	public abstract Character setHitpoints(int hitpoints);

	public abstract void appendDeath();

	public abstract void heal(int damage);

	public abstract int getHitpoints();

	public abstract int getBaseAttack(CombatType type);

	public abstract int getBaseDefence(CombatType type);

	public abstract int getBaseAttackSpeed();

	public abstract int getAttackAnim();

	public abstract int getBlockAnim();

	/**
	 * Is this entity registered.
	 */
	private boolean registered;

	/*
	 * Getters and setters Also contains methods.
	 */

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Character setGraphic(Graphic newGraphic) {

		/**
		 * Graphic priorities. This piece of code below will stop other graphics
		 * from being performed if there's already one with higher priority
		 * queued to be sent to the client via player updating.
		 */
		if (this.graphic != null && newGraphic != null) {
			if (this.graphic.getPriority().ordinal() > newGraphic.getPriority().ordinal()) {
				return this;
			}
		}

		this.graphic = newGraphic;

		if (newGraphic != null) {
			getUpdateFlag().flag(Flag.GRAPHIC);
		}

		return this;
	}

	public Character setAnimation(Animation newAnimation) {

		/**
		 * Animation priorities. This piece of code below will stop other
		 * animations from being performed if there's already one with higher
		 * priority queued to be sent to the client via player updating.
		 */
		if (this.animation != null && newAnimation != null) {
			if (this.animation.getPriority().ordinal() > newAnimation.getPriority().ordinal()) {
				return this;
			}
		}

		this.animation = newAnimation;

		if (animation != null) {
			getUpdateFlag().flag(Flag.ANIMATION);
		}

		return this;
	}

	public Graphic getGraphic() {
		return graphic;
	}

	public Animation getAnimation() {
		return animation;
	}

	/**
	 * @return the lastCombat
	 */
	public Stopwatch getLastCombat() {
		return lastCombat;
	}

	public int getAndDecrementPoisonDamage() {
		return poisonDamage--;
	}

	public int getPoisonDamage() {
		return poisonDamage;
	}

	public void setPoisonDamage(int poisonDamage) {
		this.poisonDamage = poisonDamage;
	}

	public boolean isPoisoned() {
		return poisonDamage > 0;
	}

	public Position getPositionToFace() {
		return positionToFace;
	}

	public Character setPositionToFace(Position positionToFace) {
		this.positionToFace = positionToFace;
		getUpdateFlag().flag(Flag.FACE_POSITION);
		return this;
	}

	public UpdateFlag getUpdateFlag() {
		return updateFlag;
	}

	public MovementQueue getMovementQueue() {
		return movementQueue;
	}

	public Combat getCombat() {
		return combat;
	}

	public Entity getInteractingEntity() {
		return interactingEntity;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
		int[] directionDeltas = direction.getDirectionDelta();
		setPositionToFace(getPosition().copy().add(directionDeltas[0], directionDeltas[1]));
	}

	/**
	 * Sets the value for {@link CharacterNode#secondaryDirection}.
	 *
	 * @param secondaryDirection
	 *            the new value to set.
	 */
	public final void setSecondaryDirection(Direction secondaryDirection) {
		this.secondaryDirection = secondaryDirection;
	}

	/**
	 * Gets the last direction this character was facing.
	 *
	 * @return the last direction.
	 */
	public final Direction getLastDirection() {
		return lastDirection;
	}

	/**
	 * Sets the value for {@link CharacterNode#lastDirection}.
	 *
	 * @param lastDirection
	 *            the new value to set.
	 */
	public final void setLastDirection(Direction lastDirection) {
		this.lastDirection = lastDirection;
	}

	public String getForcedChat() {
		return forcedChat;
	}

	public Character setForcedChat(String forcedChat) {
		this.forcedChat = forcedChat;
		return this;
	}

	public boolean[] getPrayerActive() {
		return prayerActive;
	}

	public boolean[] getCurseActive() {
		return curseActive;
	}

	public Character setPrayerActive(boolean[] prayerActive) {
		this.prayerActive = prayerActive;
		return this;
	}

	public Character setPrayerActive(int id, boolean prayerActive) {
		this.prayerActive[id] = prayerActive;
		return this;
	}

	public Character setCurseActive(boolean[] curseActive) {
		this.curseActive = curseActive;
		return this;
	}

	public Character setCurseActive(int id, boolean curseActive) {
		this.curseActive[id] = curseActive;
		return this;
	}

	public int getNpcTransformationId() {
		return npcTransformationId;
	}

	public Character setNpcTransformationId(int npcTransformationId) {
		this.npcTransformationId = npcTransformationId;
		return this;
	}

	/**
	 * Deals one damage to this entity.
	 * 
	 * @param hit
	 *            the damage to be dealt.
	 */
	public void dealDamage(HitDamage hit) {
		if (getUpdateFlag().flagged(Flag.SINGLE_HIT)) {
			dealSecondaryDamage(hit);
			return;
		}
		if (getHitpoints() <= 0)
			return;
		primaryHit = decrementHealth(hit);
		getUpdateFlag().flag(Flag.SINGLE_HIT);
	}

	public HitDamage decrementHealth(HitDamage hit) {
		if (getHitpoints() <= 0)
			return hit;
		if (hit.getDamage() > getHitpoints())
			hit.setDamage(getHitpoints());
		if (hit.getDamage() < 0)
			hit.setDamage(0);
		int outcome = getHitpoints() - hit.getDamage();
		if (outcome < 0)
			outcome = 0;
		setHitpoints(outcome);
		return hit;
	}

	/**
	 * Deal secondary damage to this entity.
	 * 
	 * @param hit
	 *            the damage to be dealt.
	 */
	private void dealSecondaryDamage(HitDamage hit) {
		secondaryHit = decrementHealth(hit);
		getUpdateFlag().flag(Flag.DOUBLE_HIT);
	}

	/**
	 * Deals two damage splats to this entity.
	 * 
	 * @param hit
	 *            the first hit.
	 * @param secondHit
	 *            the second hit.
	 */
	public void dealDoubleDamage(HitDamage hit, HitDamage secondHit) {
		dealDamage(hit);
		dealSecondaryDamage(secondHit);
	}

	/**
	 * Deals three damage splats to this entity.
	 * 
	 * @param hit
	 *            the first hit.
	 * @param secondHit
	 *            the second hit.
	 * @param thirdHit
	 *            the third hit.
	 */
	public void dealTripleDamage(HitDamage hit, HitDamage secondHit, final HitDamage thirdHit) {
		dealDoubleDamage(hit, secondHit);

		TaskManager.submit(new Task(1, this, false) {
			@Override
			public void execute() {
				if (!isRegistered()) {
					this.stop();
					return;
				}
				dealDamage(thirdHit);
				this.stop();
			}
		});
	}

	/**
	 * Deals four damage splats to this entity.
	 * 
	 * @param hit
	 *            the first hit.
	 * @param secondHit
	 *            the second hit.
	 * @param thirdHit
	 *            the third hit.
	 * @param fourthHit
	 *            the fourth hit.
	 */
	public void dealQuadrupleDamage(HitDamage hit, HitDamage secondHit, final HitDamage thirdHit,
			final HitDamage fourthHit) {
		dealDoubleDamage(hit, secondHit);

		TaskManager.submit(new Task(1, this, false) {
			@Override
			public void execute() {
				if (!isRegistered()) {
					this.stop();
					return;
				}
				dealDoubleDamage(thirdHit, fourthHit);
				this.stop();
			}
		});
	}

	/**
	 * Get the primary hit for this entity.
	 * 
	 * @return the primaryHit.
	 */
	public HitDamage getPrimaryHit() {
		return primaryHit;
	}

	/**
	 * Get the secondary hit for this entity.
	 * 
	 * @return the secondaryHit.
	 */
	public HitDamage getSecondaryHit() {
		return secondaryHit;
	}

	/*
	 * Movement queue
	 */

	public void setPrimaryDirection(Direction primaryDirection) {
		this.primaryDirection = primaryDirection;
	}

	public Direction getPrimaryDirection() {
		return primaryDirection;
	}

	public Direction getSecondaryDirection() {
		return secondaryDirection;
	}

	/**
	 * Determines if this character needs to reset their movement queue.
	 *
	 * @return {@code true} if this character needs to reset their movement
	 *         queue, {@code false} otherwise.
	 */
	public final boolean isResetMovementQueue() {
		return resetMovementQueue;
	}

	/**
	 * Gets if this entity is registered.
	 * 
	 * @return the unregistered.
	 */
	public boolean isRegistered() {
		return registered;
	}

	/**
	 * Sets if this entity is registered,
	 * 
	 * @param unregistered
	 *            the unregistered to set.
	 */
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	/**
	 * Sets the value for {@link CharacterNode#resetMovementQueue}.
	 *
	 * @param resetMovementQueue
	 *            the new value to set.
	 */
	public final void setResetMovementQueue(boolean resetMovementQueue) {
		this.resetMovementQueue = resetMovementQueue;
	}

	public void setNeedsPlacement(boolean needsPlacement) {
		this.needsPlacement = needsPlacement;
	}

	public boolean isNeedsPlacement() {
		return needsPlacement;
	}

	public Player getAsPlayer() {
		return ((Player) this);
	}

	public NPC getAsNpc() {
		return ((NPC) this);
	}
}