package com.elvarg.world.entity.impl.npc.combat;

import java.util.ArrayList;
import java.util.List;

import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.npc.combat.impl.Goblin;
import com.elvarg.world.entity.impl.player.Player;

public class MobCombatHandler {

	public static MobCombatHandler mobCombatHandler = new MobCombatHandler();

	private static final List<SpecialMob> mob_list = new ArrayList<>();

	static {
		mob_list.add(new Goblin());
	}

	public int getAttackAnimation(int id) {
		return mob_list.get(id).getAttackAnimation();
	}

	public int getDefenceAnimation(int id) {
		return mob_list.get(id).getDefenceAnimation();
	}

	public int getDeathAnimation(int id) {
		return mob_list.get(id).getDeathAnimation();
	}

	public int getMaxHit(int id) {
		return mob_list.get(id).getMaxHit();
	}

	public static MobCombatHandler executeCombat(Player player, NPC mob, int id) {
		return mob_list.get(id).executeCombat(mob);
	}
}