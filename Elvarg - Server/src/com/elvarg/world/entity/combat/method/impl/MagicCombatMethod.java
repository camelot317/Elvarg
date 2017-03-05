package com.elvarg.world.entity.combat.method.impl;

import com.elvarg.world.entity.combat.CombatType;
import com.elvarg.world.entity.combat.hit.QueueableHit;
import com.elvarg.world.entity.combat.magic.CombatSpell;
import com.elvarg.world.entity.combat.method.CombatMethod;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.model.Graphic;
import com.elvarg.world.model.GraphicHeight;

/**
 * The magic combat method.
 * 
 * @author Gabriel Hannason
 */
public class MagicCombatMethod implements CombatMethod {

	public static final Graphic SPLASH_GRAPHIC = new Graphic(85, GraphicHeight.MIDDLE);

	@Override
	public CombatType getCombatType() {
		return CombatType.MAGIC;
	}

	@Override
	public QueueableHit[] fetchDamage(Character character, Character target) {
		return new QueueableHit[] { new QueueableHit(character, target, this, true, 3) };
	}

	@Override
	public boolean canAttack(Character character, Character target) {

		if (character.isNpc()) {
			return true;
		}

		// Set the current spell to the autocast spell if it's null.
		if (character.getCombat().getCastSpell() == null) {
			character.getCombat().setCastSpell(character.getCombat().getAutocastSpell());
		}

		// Character didn't have autocast spell either.
		if (character.getCombat().getCastSpell() == null) {
			return false;
		}

		return character.getCombat().getCastSpell().canCast(character.getAsPlayer(), true);
	}

	@Override
	public void onQueueAdd(Character character, Character target) {

		CombatSpell spell = character.getCombat().getCastSpell();

		if (spell != null) {
			spell.startCast(character, target);
		}

	}

	@Override
	public int getAttackSpeed(Character character) {

		if (character.getCombat().getPreviousCast() != null) {
			return character.getCombat().getPreviousCast().getAttackSpeed();
		}

		return character.getBaseAttackSpeed();
	}

	@Override
	public int getAttackDistance(Character character) {
		return 8;
	}

	@Override
	public void startAnimation(Character character) {
	}

	@Override
	public void finished(Character character) {

		// Reset the castSpell to autocastSpell
		// Update previousCastSpell so effects can be handled.

		final CombatSpell current = character.getCombat().getCastSpell();

		character.getCombat().setCastSpell(null);

		if (character.getCombat().getAutocastSpell() == null) {
			character.getCombat().reset();
		}

		character.getCombat().setPreviousCast(current);
	}

	@Override
	public void handleAfterHitEffects(QueueableHit hit) {
		Character attacker = hit.getAttacker();
		Character target = hit.getTarget();
		boolean accurate = hit.isAccurate();
		int damage = hit.getTotalDamage();

		if (attacker.getHitpoints() <= 0 || target.getHitpoints() <= 0) {
			return;
		}

		CombatSpell previousSpell = attacker.getCombat().getPreviousCast();

		if (previousSpell != null) {

			if (accurate) {

				// Send proper end graphics for the spell because it was
				// accurate
				previousSpell.endGraphic().ifPresent(target::performGraphic);

			} else {

				// Send splash graphics for the spell because it wasn't accurate
				target.performGraphic(SPLASH_GRAPHIC);
			}

			previousSpell.finishCast(attacker, target, accurate, damage);

		}
	}
}
