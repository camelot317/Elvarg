package com.elvarg.world.entity.combat.method;

import com.elvarg.world.entity.combat.CombatType;
import com.elvarg.world.entity.combat.hit.QueueableHit;
import com.elvarg.world.entity.impl.Character;

public interface CombatMethod {

	public abstract boolean canAttack(Character character, Character target);

	public abstract void onQueueAdd(Character character, Character target);

	public abstract int getAttackSpeed(Character character);

	public abstract int getAttackDistance(Character character);

	public abstract void startAnimation(Character character);

	public abstract CombatType getCombatType();

	public abstract QueueableHit[] fetchDamage(Character character, Character target);

	public abstract void finished(Character character);

	public abstract void handleAfterHitEffects(QueueableHit hit);

}
