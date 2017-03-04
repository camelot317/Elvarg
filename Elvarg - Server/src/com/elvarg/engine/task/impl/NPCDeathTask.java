package com.elvarg.engine.task.impl;

import com.elvarg.engine.task.Task;
import com.elvarg.engine.task.TaskManager;
import com.elvarg.world.World;
import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.DamageDealer;
import com.elvarg.world.model.movement.MovementStatus;

/**
 * Represents an npc's death task, which handles everything an npc does before
 * and after their death animation (including it), such as dropping their drop
 * table items.
 * 
 * @author relex lawl
 */

public class NPCDeathTask extends Task {

	/**
	 * The NPCDeathTask constructor.
	 * 
	 * @param npc
	 *            The npc being killed.
	 */
	public NPCDeathTask(NPC npc) {
		super(2);
		this.npc = npc;
		this.ticks = 2;
	}

	/**
	 * The npc setting off the death task.
	 */
	private final NPC npc;

	/**
	 * The amount of ticks on the task.
	 */
	private int ticks = 2;

	/**
	 * The player who killed the NPC
	 */
	private Player killer = null;

	@Override
	public void execute() {
		try {

			switch (ticks) {
			case 2:
				npc.getMovementQueue().setMovementStatus(MovementStatus.DISABLED).reset();

				DamageDealer damageDealer = npc.getCombat().getTopDamageDealer(true, null);
				killer = damageDealer == null ? null : damageDealer.getPlayer();

				npc.performAnimation(new Animation(npc.getDefinition().getDeathAnim()));

				npc.getCombat().reset();

				if (npc.getInteractingEntity() != null) {
					npc.setEntityInteraction(null);
				}

				break;
			case 0:
				if (killer != null) {

					/** LOCATION KILLS **/
					if (npc.getLocation().handleKilledNPC(killer, npc)) {
						stop();
						return;
					}

					/** PARSE DROPS **/
					// NPCDrops.dropItems(killer, npc);

				}
				stop();
				break;
			}

			ticks--;
		} catch (Exception e) {
			e.printStackTrace();
			stop();
		}
	}

	@Override
	public void stop() {
		setEventRunning(false);

		npc.setDying(false);

		// respawn
		if (npc.getDefinition().getRespawn() > 0) {
			TaskManager.submit(new NPCRespawnTask(npc, npc.getDefinition().getRespawn()));
		}

		World.getNpcRemoveQueue().add(npc);

	}
}
