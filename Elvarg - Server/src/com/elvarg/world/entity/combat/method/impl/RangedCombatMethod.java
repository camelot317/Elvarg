package com.elvarg.world.entity.combat.method.impl;

import com.elvarg.world.entity.combat.CombatType;
import com.elvarg.world.entity.combat.hit.QueueableHit;
import com.elvarg.world.entity.combat.method.CombatMethod;
import com.elvarg.world.entity.combat.ranged.RangedData;
import com.elvarg.world.entity.combat.ranged.RangedData.AmmunitionData;
import com.elvarg.world.entity.combat.ranged.RangedData.RangedWeaponData;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.Projectile;

/**
 * The ranged combat method.
 * 
 * @author Gabriel Hannason
 */
public class RangedCombatMethod implements CombatMethod {

	@Override
	public CombatType getCombatType() {
		return CombatType.RANGED;
	}

	@Override
	public QueueableHit[] fetchDamage(Character character, Character target) {

		// Darkbow is double hits.
		if (character.getCombat().getRangedWeaponData() != null
				&& character.getCombat().getRangedWeaponData() == RangedWeaponData.DARK_BOW) {
			return new QueueableHit[] { new QueueableHit(character, target, this, true, 2),
					new QueueableHit(character, target, this, true, 3) };
		}

		return new QueueableHit[] { new QueueableHit(character, target, this, true, 2) };
	}

	@Override
	public boolean canAttack(Character character, Character target) {

		if (character.isNpc()) {
			return true;
		}

		Player p = character.getAsPlayer();

		if (!RangedData.checkAmmo(p)) {
			return false;
		}

		return true;
	}

	@Override
	public void onQueueAdd(Character character, Character target) {
		if (character.isPlayer()) {
			AmmunitionData ammo = character.getAsPlayer().getCombat().getAmmunition();
			new Projectile(character, target, ammo.getProjectileId(), ammo.getProjectileDelay() + 16,
					ammo.getProjectileSpeed() + 28, ammo.getStartHeight(), ammo.getEndHeight(), 0).sendProjectile();

			RangedData.decrementAmmo(character.getAsPlayer(), target.getPosition());

			// Dark bow sends two arrows, so send another projectile and delete
			// another arrow.
			if (character.getCombat().getRangedWeaponData() == RangedWeaponData.DARK_BOW) {
				new Projectile(character, target, ammo.getProjectileId(), ammo.getProjectileDelay() + 35,
						ammo.getProjectileSpeed() + 28, ammo.getStartHeight(), ammo.getEndHeight(), 0).sendProjectile();
				RangedData.decrementAmmo(character.getAsPlayer(), target.getPosition());
			}
		}
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
		int animation = character.getAttackAnim();

		if (animation != -1) {
			character.performAnimation(new Animation(animation));
		}

		if (character.isPlayer()) {
			AmmunitionData ammo = character.getAsPlayer().getCombat().getAmmunition();
			// character.getAsPlayer().performGraphic(new
			// Graphic(ammo.getStartGfxId(), ammo.getStartGfxId() == 2138 ?
			// GraphicHeight.LOW : ammo.getStartHeight() >= 43 ?
			// GraphicHeight.HIGH : GraphicHeight.MIDDLE));
		}
	}

	@Override
	public void finished(Character character) {

	}

	@Override
	public void handleAfterHitEffects(QueueableHit hit) {

	}

}
