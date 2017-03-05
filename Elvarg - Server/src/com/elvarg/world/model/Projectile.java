package com.elvarg.world.model;

import com.elvarg.engine.task.Task;
import com.elvarg.engine.task.TaskManager;
import com.elvarg.world.World;
import com.elvarg.world.entity.Entity;
import com.elvarg.world.entity.impl.player.Player;

/**
 * A graphic propelled through the air by some sort of spell, weapon, or other
 * miscellaneous force.
 * 
 * @author lare96
 */
public final class Projectile {

	/** The starting position of the projectile. */
	private final Position start;

	/** The offset position of the projectile. */
	private final Position offset;

	/** The speed of the projectile. */
	private final int speed;

	/** The id of the projectile. */
	private final int projectileId;

	/** The starting height of the projectile. */
	private final int startHeight;

	/** The ending height of the projectile. */
	private final int endHeight;

	/** The lock on value of the projectile. */
	private final int lockon;

	/** The delay of the projectile. */
	private final int delay;

	/** The curve angle of the projectile. */
	private final int curve;

	/**
	 * Create a new {@link Projectile}.
	 * 
	 * @param start
	 *            the starting position of the projectile.
	 * @param end
	 *            the ending position of the projectile.
	 * @param lockon
	 *            the lock on value of the projectile.
	 * @param projectileId
	 *            the id of the projectile.
	 * @param speed
	 *            the speed of the projectile.
	 * @param delay
	 *            the delay of the projectile.
	 * @param startHeight
	 *            the starting height of the projectile.
	 * @param endHeight
	 *            the ending height of the projectile.
	 * @param curve
	 *            the curve angle of the projectile.
	 */
	public Projectile(Position start, Position end, int lockon, int projectileId, int speed, int delay, int startHeight,
			int endHeight, int curve) {
		this.start = start;
		this.offset = new Position((end.getX() - start.getX()), (end.getY() - start.getY()));
		this.lockon = lockon;
		this.projectileId = projectileId;
		this.delay = delay;
		this.speed = speed;
		this.startHeight = startHeight;
		this.endHeight = endHeight;
		this.curve = curve;
	}

	/**
	 * Create a new {@link Projectile}.
	 * 
	 * @param source
	 *            the entity that is firing this projectile.
	 * @param victim
	 *            the victim that this projectile is being fired at.
	 * @param projectileId
	 *            the id of the projectile.
	 * @param speed
	 *            the speed of the projectile.
	 * @param delay
	 *            the delay of the projectile.
	 * @param startHeight
	 *            the starting height of the projectile.
	 * @param endHeight
	 *            the ending height of the projectile.
	 * @param curve
	 *            the curve angle of the projectile.
	 */
	public Projectile(Entity source, Entity victim, int projectileId, int delay, int speed, int startHeight,
			int endHeight, int curve) {
		this(source.getPosition(), victim.getPosition(),
				(victim.isPlayer() ? -victim.getIndex() - 1 : victim.getIndex() + 1), projectileId, delay, speed,
				startHeight, endHeight, curve);
	}

	/**
	 * Sends one projectiles using the values set when the {@link Projectile}
	 * was constructed.
	 */
	public void sendProjectile() {
		for (Player player : World.getPlayers()) {
			if (player == null) {
				continue;
			}

			if (start.isViewableFrom(player.getPosition())) {
				player.getPacketSender().sendProjectile(start, offset, 0, speed, projectileId, startHeight, endHeight,
						lockon, delay);

			}
		}
	}

	/**
	 * Sends <code>count</code> projectiles using the values set when the
	 * {@link Projectile} was constructed.
	 * 
	 * @param count
	 *            the amount of projectiles to send.
	 */
	public void sendProjectiles(int count) {
		for (int i = 0; i < count; i++) {
			TaskManager.submit(new Task(1, false) {
				@Override
				public void execute() {
					sendProjectile();
					this.stop();
				}
			});
		}
	}

	/**
	 * Gets the starting position of the projectile.
	 * 
	 * @return the starting position of the projectile.
	 */
	public Position getStart() {
		return start;
	}

	/**
	 * Gets the offset position of the projectile.
	 * 
	 * @return the offset position of the projectile.
	 */
	public Position getOffset() {
		return offset;
	}

	/**
	 * Gets the speed of the projectile.
	 * 
	 * @return the speed of the projectile.
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Gets the id of the projectile.
	 * 
	 * @return the id of the projectile.
	 */
	public int getProjectileId() {
		return projectileId;
	}

	/**
	 * Gets the starting height of the projectile.
	 * 
	 * @return the starting height of the projectile.
	 */
	public int getStartHeight() {
		return startHeight;
	}

	/**
	 * Gets the ending height of the projectile.
	 * 
	 * @return the ending height of the projectile
	 */
	public int getEndHeight() {
		return endHeight;
	}

	/**
	 * Gets the lock on value of the projectile.
	 * 
	 * @return the lock on value of the projectile.
	 */
	public int getLockon() {
		return lockon;
	}

	/**
	 * Gets the delay of the projectile.
	 * 
	 * @return the delay of the projectile.
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Gets the curve angle of the projectile.
	 * 
	 * @return the curve angle of the projectile.
	 */
	public int getCurve() {
		return curve;
	}
}
