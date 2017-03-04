package com.elvarg.engine.task.impl;

import com.elvarg.engine.task.Task;
import com.elvarg.world.World;
import com.elvarg.world.entity.impl.npc.NPC;

public class NPCRespawnTask extends Task {

	public NPCRespawnTask(NPC npc, int respawn) {
		super(respawn);
		this.npc = npc;
	}

	private final NPC npc;

	@Override
	public void execute() {

		NPC npc_ = new NPC(npc.getId(), npc.getSpawnPosition());
		npc_.setHitpoints(npc.getDefinition().getHitpoints());
		World.getNpcAddQueue().add(npc_);

		stop();
	}

}
