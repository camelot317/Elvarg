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
import com.elvarg.world.model.Graphic;
import com.elvarg.world.model.GraphicHeight;
import com.elvarg.world.model.Priority;
import com.elvarg.world.model.Projectile;

public class DarkBowCombatMethod implements CombatMethod {

	private static final Animation ANIMATION = new Animation(426, Priority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(1100, GraphicHeight.HIGH, Priority.HIGH);

	@Override
	public CombatType getCombatType() {
		return CombatType.RANGED;
	}

	@Override
	public QueueableHit[] fetchDamage(Character character, Character target) {
		return new QueueableHit[] { new QueueableHit(character, target, this, true, 3),
				new QueueableHit(character, target, this, true, 2) };
	}

	@Override
	public boolean canAttack(Character character, Character target) {
		Player player = character.getAsPlayer();

		// Check if current player's ranged weapon data is dark bow.
		if (!(player.getCombat().getRangedWeaponData() != null
				&& player.getCombat().getRangedWeaponData() == RangedWeaponData.DARK_BOW)) {
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

		CombatSpecial.drain(player, CombatSpecial.DARK_BOW.getDrainAmount());

		new Projectile(player, target, 1099, 70, 30, 43, 31, 0).sendProjectile();
		RangedData.decrementAmmo(player, target.getPosition());
		// And again.. Two arrows..
		new Projectile(player, target, 1099, 95, 30, 43, 31, 0).sendProjectile();
		RangedData.decrementAmmo(player, target.getPosition());
	}

	@Override
	public int getAttackSpeed(Character character) {
		return character.getBaseAttackSpeed() + 1;
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
		hit.getTarget().performGraphic(GRAPHIC);
	}
}