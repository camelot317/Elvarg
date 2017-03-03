package com.elvarg.world.entity;

import com.elvarg.GameConstants;
import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.object.GameObject;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.Graphic;
import com.elvarg.world.model.Position;

public class Entity {

	/**
	 * The Entity constructor.
	 * @param position	The position the entity is currently in.
	 */
	public Entity(Position position) {
		setPosition(position);
		lastKnownRegion = position;
	}
	
	/**
	 * The entity's unique index.
	 */
	private int index;

	/**
	 * The entity's tile size.
	 */
	private int size = 1;

	/**
	 * The default position the entity is in.
	 */
	private Position position = GameConstants.DEFAULT_POSITION.copy();

	/**	
	 * The entity's first position in current map region.
	 */
	private Position lastKnownRegion;

	/**
	 * Gets the entity's unique index.
	 * @return	The entity's index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the entity's index.
	 * @param index		The value the entity's index will contain.
	 * @return			The Entity instance.
	 */
	public Entity setIndex(int index) {
		this.index = index;
		return this;
	}

	/**
	 * Gets this entity's first position upon entering their
	 * current map region.
	 * @return	The lastKnownRegion instance.
	 */
	public Position getLastKnownRegion() {
		return lastKnownRegion;
	}

	/**
	 * Sets the entity's current region's position.
	 * @param lastKnownRegion	The position in which the player first entered the current region.
	 * @return					The Entity instance.
	 */
	public Entity setLastKnownRegion(Position lastKnownRegion) {
		this.lastKnownRegion = lastKnownRegion;
		return this;
	}

	/**
	 * Sets the entity position
	 * @param position the world position
	 */
	public Entity setPosition(Position position) {
		this.position = position;
		return this;
	}

	/**
	 * Gets the entity position.
	 * @return the entity's world position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Performs an animation.
	 * @param animation	The animation to perform.
	 */
	public void performAnimation(Animation animation) {

	}

	/**
	 * Performs a graphic.
	 * @param graphic	The graphic to perform.
	 */
	public void performGraphic(Graphic graphic) {

	}

	/**
	 * gets the entity's tile size.
	 * @return	The size the entity occupies in the world.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the entity's tile size
	 * @return	The Entity instance.
	 */
	public Entity setSize(int size) {
		this.size = size;
		return this;
	}

	public boolean isNpc() {
		return this instanceof NPC;
	}

	public boolean isPlayer() {
		return this instanceof Player;
	}

	public boolean isGameObject() {
		return this instanceof GameObject;
	}
}
