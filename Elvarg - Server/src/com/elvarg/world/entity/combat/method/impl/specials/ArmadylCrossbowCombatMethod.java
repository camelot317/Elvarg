package com.elvarg.world.entity.combat.method.impl.specials;

import com.elvarg.world.entity.combat.CombatSpecial;
import com.elvarg.world.entity.combat.CombatType;
import com.elvarg.world.entity.combat.hit.QueueableHit;
import com.elvarg.world.entity.combat.method.CombatMethod;
import com.elvarg.world.entity.combat.ranged.RangedData;
import com.elvarg.world.entity.combat.ranged.RangedData.RangedWeaponData;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.Priority;
import com.elvarg.world.model.Projectile;

public class ArmadylCrossbowCombatMethod implements CombatMethod {

	private static final Animation ANIMATION = new Animation(4230, Priority.HIGH);

	@Override
	public CombatType getCombatType() {
		return CombatType.RANGED;
	}

	@Override
	public QueueableHit[] fetchDamage(Character character, Character target) {
		return new QueueableHit[] { new QueueableHit(character, target, this, true, 2) };
	}

	@Override
	public boolean canAttack(Character character, Character target) {
		Player player = character.getAsPlayer();

		// Check if current player's ranged weapon data is armadyl crossbow
		if (!(player.getCombat().getRangedWeaponData() != null
				&& player.getCombat().getRangedWeaponData() == RangedWeaponData.ARMADYL_CROSSBOW)) {
			return false;
		}

		// Check if player has enough ammunition to fire.
		if (!RangedData.checkAmmo(player)) {
			return false;
		}

		return true;
	}

	@Override
	public void onQueueAdd(Character character, Character target) {
		final Player player = character.getAsPlayer();

		CombatSpecial.drain(player, CombatSpecial.ARMADYL_CROSSBOW.getDrainAmount());

		new Projectile(player, target, 301, 70, 30, 43, 31, 0).sendProjectile();

		RangedData.decrementAmmo(player, target.getPosition());
	}

	@Override
	public int getAttackSpeed(Character character) {
		return character.getBaseAttackSpeed();
	}

	@Override
	public int getAttackDistance(Character character) {
		return 6;
	}

	@Override
	public void startAnimation(Character character) {
		character.performAnimation(ANIMATION);
	}

	@Override
	public void finished(Character character) {

	}

	@Override
	public void handleAfterHitEffects(QueueableHit hit) {

	}
}