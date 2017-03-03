package com.elvarg.world.entity.updating;

import com.elvarg.world.World;
import com.elvarg.world.entity.impl.npc.NPC;

/**
 * A {@link WorldUpdateSequence} implementation for {@link Npc}s that provides
 * code for each of the updating stages. The actual updating stage is not
 * supported by this implementation because npc's are updated for players.
 *
 * @author lare96
 */
public class NpcUpdateSequence implements UpdateSequence<NPC> {
	
	@Override
	public void executePreUpdate(NPC t) {
		try {
			t.onTick();
		} catch (Exception e) {
			e.printStackTrace();
			World.getNpcRemoveQueue().add(t);
		}
	}
	@Override
	public void executeUpdate(NPC t) {
		throw new UnsupportedOperationException(
				"NPCs cannot be updated for NPCs!");
	}
	
	@Override
	public void executePostUpdate(NPC t) {
		try {
			NPCUpdating.resetFlags(t);
		} catch (Exception e) {
			e.printStackTrace();
			World.getNpcRemoveQueue().add(t);
		}
	}
}