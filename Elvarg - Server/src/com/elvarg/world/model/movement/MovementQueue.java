package com.elvarg.world.model.movement;

import java.util.ArrayDeque;
import java.util.Deque;

import com.elvarg.world.collision.region.RegionClipping;
import com.elvarg.world.entity.combat.CombatFactory;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Direction;
import com.elvarg.world.model.Position;

/**
 * A queue of {@link Direction}s which a {@link Character} will follow.
 * 
 * @author Graham Edgecombe Edited by Gabbe
 */
public final class MovementQueue {

	/**
	 * Represents a single point in the queue.
	 * 
	 * @author Graham Edgecombe
	 */
	public static final class Point {

		private final Position position;
		private final Direction direction;

		public Point(Position position, Direction direction) {
			this.position = position;
			this.direction = direction;
		}

		@Override
		public String toString() {
			return Point.class.getName() + " [direction=" + direction + ", position=" + position + "]";
		}

	}

	/**
	 * The maximum size of the queue. If any additional steps are added, they
	 * are discarded.
	 */
	private static final int MAXIMUM_SIZE = 100;

	/**
	 * The character whose walking queue this is.
	 */
	private final Character character;

	/**
	 * The queue of directions.
	 */
	private final Deque<Point> points = new ArrayDeque<Point>();

	/**
	 * The following task
	 */
	private Character followCharacter;

	/**
	 * Creates a walking queue for the specified character.
	 * 
	 * @param character
	 *            The character.
	 */
	public MovementQueue(Character character) {
		this.character = character;
		this.isPlayer = character.isPlayer();
	}

	private final boolean isPlayer;
	private MovementStatus status = MovementStatus.NONE;

	/**
	 * Sets a character to follow
	 */
	public void setFollowCharacter(Character followCharacter) {
		this.followCharacter = followCharacter;
	}

	public Character getFollowCharacter() {
		return followCharacter;
	}

	/**
	 * Adds the first step to the queue, attempting to connect the server and
	 * client position by looking at the previous queue.
	 * 
	 * @param clientConnectionPosition
	 *            The first step.
	 * @return {@code true} if the queues could be connected correctly,
	 *         {@code false} if not.
	 */
	public boolean addFirstStep(Position clientConnectionPosition) {
		reset();
		addStep(clientConnectionPosition);
		return true;
	}

	/**
	 * Adds a step to walk to the queue.
	 * 
	 * @param x
	 *            X to walk to
	 * @param y
	 *            Y to walk to
	 * @param clipped
	 *            Can the step walk through objects?
	 */
	public void walkStep(int x, int y) {
		Position position = character.getPosition().copy();
		position.setX(position.getX() + x);
		position.setY(position.getY() + y);
		addStep(position);
	}

	/**
	 * Adds a step.
	 * 
	 * @param x
	 *            The x coordinate of this step.
	 * @param y
	 *            The y coordinate of this step.
	 * @param heightLevel
	 * @param flag
	 */
	private void addStep(int x, int y, int heightLevel) {
		if (status == MovementStatus.DISABLED || !character.getCombat().getFreezeTimer().finished()) {
			return;
		}

		if (points.size() >= MAXIMUM_SIZE)
			return;

		final Point last = getLast();
		final int deltaX = x - last.position.getX();
		final int deltaY = y - last.position.getY();
		final Direction direction = Direction.fromDeltas(deltaX, deltaY);
		if (direction != Direction.NONE)
			points.add(new Point(new Position(x, y, heightLevel), direction));
	}

	/**
	 * Adds a step to the queue.
	 * 
	 * @param step
	 *            The step to add.
	 * @oaram flag
	 */
	public void addStep(Position step) {
		if (!character.getCombat().getFreezeTimer().finished() || status == MovementStatus.DISABLED)
			return;
		final Point last = getLast();
		final int x = step.getX();
		final int y = step.getY();
		int deltaX = x - last.position.getX();
		int deltaY = y - last.position.getY();
		final int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));
		for (int i = 0; i < max; i++) {
			if (deltaX < 0)
				deltaX++;
			else if (deltaX > 0)
				deltaX--;
			if (deltaY < 0)
				deltaY++;
			else if (deltaY > 0)
				deltaY--;
			addStep(x - deltaX, y - deltaY, step.getZ());
		}
	}

	public boolean canWalk(int deltaX, int deltaY) {
		final Position to = new Position(character.getPosition().getX() + deltaX,
				character.getPosition().getY() + deltaY, character.getPosition().getZ());
		if (character.getPosition().getZ() == -1 && to.getZ() == -1)
			return true;
		return canWalk(character.getPosition(), to, character.getSize());
	}

	public static boolean canWalk(Position from, Position to, int size) {
		return RegionClipping.canMove(from, to, size, size);
	}

	/*
	 * public boolean checkBarricade(int x, int y) { Position position =
	 * character.getPosition(); if(character.isPlayer()) {
	 * if(Locations.inSoulWars((Player)character)) {
	 * if(SoulWars.checkBarricade(position.getX() + x, position.getY()+ y,
	 * position.getZ())) { ((Player)character).getPacketSender().sendMessage(
	 * "The path is blocked by a Barricade."); reset(true); return true; } } }
	 * return false; }
	 */

	/**
	 * Gets the last point.
	 * 
	 * @return The last point.
	 */
	private Point getLast() {
		final Point last = points.peekLast();
		if (last == null)
			return new Point(character.getPosition(), Direction.NONE);
		return last;
	}

	/**
	 * @return true if the character is moving.
	 */
	public boolean isMoving() {
		return !points.isEmpty();
	}

	/**
	 * Called every 600ms, updates the queue.
	 */
	public void onTick() {

		if (character.isNeedsPlacement()) {
			return;
		}

		follow();

		boolean movement = status != MovementStatus.DISABLED && !!character.getCombat().getFreezeTimer().finished();

		if (movement) {
			Point walkPoint = null;
			Point runPoint = null;

			walkPoint = points.poll();

			if (isRunToggled()) {
				runPoint = points.poll();
			}

			if (walkPoint != null && walkPoint.direction != Direction.NONE) {

				if (followCharacter != null) {
					if (walkPoint.equals(followCharacter.getPosition())) {
						return;
					} else {
						if (!followCharacter.getMovementQueue().isRunToggled()) {
							if (character.getPosition().isWithinDistance(followCharacter.getPosition(), 2)) {
								runPoint = null;
							}
						}
					}
				}

				character.setPosition(walkPoint.position);
				character.setPrimaryDirection(walkPoint.direction);
				character.setLastDirection(walkPoint.direction);
			}

			if (runPoint != null && runPoint.direction != Direction.NONE) {

				if (followCharacter != null) {
					if (walkPoint.equals(followCharacter.getPosition())) {
						return;
					}
				}

				character.setPosition(runPoint.position);
				character.setSecondaryDirection(runPoint.direction);
				character.setLastDirection(runPoint.direction);
			}

			if (isPlayer) {
				handleRegionChange();
			}
		}
	}

	public boolean isMovementDone() {
		return points.size() == 0;
	}

	public void handleRegionChange() {
		final int diffX = character.getPosition().getX() - character.getLastKnownRegion().getRegionX() * 8;
		final int diffY = character.getPosition().getY() - character.getLastKnownRegion().getRegionY() * 8;
		boolean regionChanged = false;
		if (diffX < 16)
			regionChanged = true;
		else if (diffX >= 88)
			regionChanged = true;
		if (diffY < 16)
			regionChanged = true;
		else if (diffY >= 88)
			regionChanged = true;
		if (regionChanged) {
			((Player) character).getPacketSender().sendMapRegion();
		}
	}

	public void follow() {

		if (followCharacter == null) {
			return;
		}

		// Check if we can still follow the leader.
		if (followCharacter.getHitpoints() <= 0 || !followCharacter.isRegistered() || character.getHitpoints() <= 0
				|| !character.isRegistered()) {
			if (character.getInteractingEntity() != null) {
				character.setEntityInteraction(null);
			}
			setFollowCharacter(null);
			return;
		}

		// Block if our movement is locked.
		if (status == MovementStatus.DISABLED || !character.getCombat().getFreezeTimer().finished()) {
			return;
		}

		// If we are on the same position as the leader then move
		// away.
		if (character.getPosition().equals(followCharacter.getPosition())) {
			character.getMovementQueue().reset();
			if (followCharacter.getMovementQueue().isMovementDone()) {
				MovementQueue.stepAway(character);
			}
			return;
		}

		// If we are attacking someone and following them, make sure to stop
		// when at proper distance.
		boolean combatFollow = CombatFactory.isAttacking(character);
		if (combatFollow) {
			if (character.getCombat().getMethod() != null) {
				if (CombatFactory.canReach(character, character.getCombat().getMethod(),
						character.getCombat().getTarget())) {
					return;
				}
			}
		}

		if (character.getInteractingEntity() != followCharacter) {
			character.setEntityInteraction(followCharacter);
		}

		// If we are within 1 square we don't need to move.
		if (character.getPosition().getDistance(followCharacter.getPosition()) <= 1) {
			character.getMovementQueue().reset();
			return;
		}

		/**
		 * Much "smarter" following below than the standard
		 */

		int f_y = followCharacter.getPosition().getY();
		int f_x = followCharacter.getPosition().getX();
		int y = character.getPosition().getY();
		int x = character.getPosition().getX();

		if (y > f_y) {

			// If we have higher Y, we should move north of them
			RS317PathFinder.findPath(character, followCharacter.getPosition().getX(),
					followCharacter.getPosition().getY() + 1, true, 0, 0);

		} else if (f_y > y) {

			// If foe has higher Y, we should move south of them
			RS317PathFinder.findPath(character, followCharacter.getPosition().getX(),
					followCharacter.getPosition().getY() - 1, true, 0, 0);

		} else if (f_y == y) {

			// We have same Y as foe.
			// We shall move depending on our X coordinate.

			if (x > f_x) {
				RS317PathFinder.findPath(character, followCharacter.getPosition().getX() + 1,
						followCharacter.getPosition().getY(), true, 0, 0);
			} else if (f_x > x) {
				RS317PathFinder.findPath(character, followCharacter.getPosition().getX() - 1,
						followCharacter.getPosition().getY(), true, 0, 0);
			}

		}

	}

	public MovementQueue setMovementStatus(MovementStatus status) {
		this.status = status;
		return this;
	}

	public MovementStatus getMovementStatus() {
		return status;
	}

	/**
	 * Stops the movement.
	 */
	public MovementQueue reset() {
		points.clear();
		return this;
	}

	/**
	 * Gets the size of the queue.
	 * 
	 * @return The size of the queue.
	 */
	public int size() {
		return points.size();
	}

	/**
	 * Steps away from a Gamecharacter
	 * 
	 * @param character
	 *            The gamecharacter to step away from
	 */
	public static void stepAway(Character character) {
		if (character.getMovementQueue().canWalk(-1, 0))
			character.getMovementQueue().walkStep(-1, 0);
		else if (character.getMovementQueue().canWalk(1, 0))
			character.getMovementQueue().walkStep(1, 0);
		else if (character.getMovementQueue().canWalk(0, -1))
			character.getMovementQueue().walkStep(0, -1);
		else if (character.getMovementQueue().canWalk(0, 1))
			character.getMovementQueue().walkStep(0, 1);
	}

	public static int getMove(int x, int p2, int size) {
		if ((x - p2) == 0) {
			return 0;
		} else if ((x - p2) < 0) {
			return size;
		} else if ((x - p2) > 0) {
			return -size;
		}
		return 0;
	}

	public boolean isRunToggled() {
		return character.isPlayer() && ((Player) character).isRunning(); // &&
																			// !((Player)character).isCrossingObstacle();
	}
}