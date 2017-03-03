package com.elvarg.world.entity.impl.npc;

import com.elvarg.util.Misc;
import com.elvarg.world.entity.combat.CombatFactory;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Locations.Location;
import com.elvarg.world.model.container.impl.Equipment;

/**
 * Handles the behavior of aggressive {@link Npc}s around players within the
 * <code>NPC_TARGET_DISTANCE</code> radius.
 * 
 * @author lare96
 */
public final class NpcAggression {

	/**
	 * Time that has to be spent in a region before npcs stop acting aggressive
	 * toward a specific player.
	 */
	public static final int NPC_TOLERANCE_SECONDS = 300; //5 mins

	public static void target(Player player) {

		if(CombatFactory.inCombat(player) && !Location.inMulti(player)) {
			return;
		}


		// Loop through all of the aggressive npcs.
		for (NPC npc : player.getLocalNpcs()) {

			if(npc == null || npc.getHitpoints() <= 0) {
				continue;
			}

			//	NPCFacing.updateFacing(player, npc);

			if(npc.getDefinition().getAggressionDistance() <= 0) {
				continue;
			}


			//if(!npc.findNewTarget()) {
			if(CombatFactory.inCombat(npc)) {
				continue;
			}
			//}


			boolean bandits = npc.getId() == 690;

			if(!bandits) {
				if (player.getTolerance().elapsed() > (NPC_TOLERANCE_SECONDS * 1000)) {
					break;
				}

				if (player.getSkillManager().getCombatLevel() > (npc.getDefinition().getCombatLevel() * 2) && player.getLocation() != Location.WILDERNESS) {
					continue;
				}				
			}

			int distance = npc.getSpawnPosition().getDistance(player.getPosition());
			if(distance < npc.getDefinition().getAggressionDistance() &&
					distance < npc.getDefinition().getCombatFollowDistance()) {
				if(CombatFactory.canAttack(npc, CombatFactory.getMethod(npc), player)) {

					//Bandits
					if(bandits) {
						int zammy = Equipment.getItemCount(player, "Zamorak", true);
						int sara = Equipment.getItemCount(player, "Saradomin", true);

						if(!(zammy > 0 || sara > 0)) {
							continue;
						}

						if(Misc.getRandom(2) == 1) {
							String s = zammy > 0 ? "Zamorak" : "Saradomin";
							if(Misc.getRandom(2) == 1) {
								npc.forceChat("Filthy "+s+" follower scum!");
							} else {
								npc.forceChat(""+s+" scum! You will regret coming here!");
							}
						}
					}

					npc.getCombat().attack(player);
					break;
				}
			}
		}
	}

}
