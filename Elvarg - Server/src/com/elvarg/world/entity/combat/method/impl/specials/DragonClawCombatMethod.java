package com.elvarg.world.entity.combat.method.impl.specials;

import com.elvarg.world.entity.combat.CombatSpecial;
import com.elvarg.world.entity.combat.CombatType;
import com.elvarg.world.entity.combat.hit.QueueableHit;
import com.elvarg.world.entity.combat.method.CombatMethod;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.Graphic;
import com.elvarg.world.model.Priority;

public class DragonClawCombatMethod implements CombatMethod {

	private static final Animation ANIMATION = new Animation(7527, Priority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(1171, Priority.HIGH);

	@Override
	public CombatType getCombatType() {
		return CombatType.MELEE;
	}

	@Override
	public QueueableHit[] fetchDamage(Character character, Character target) {
		QueueableHit qHit = new QueueableHit(character, target, this, true, 4, 1);

		/* DRAGON CLAWS SPECIAL FORMULA */

		int first = qHit.getHits()[0].getDamage();
		int second = first <= 0 ? qHit.getHits()[1].getDamage() : (first / 2);
		int third = second <= 0 ? second : (second / 2);
		int fourth = second <= 0 ? second : (second / 2);

		qHit.getHits()[0].setDamage(first);
		qHit.getHits()[1].setDamage(second);
		qHit.getHits()[2].setDamage(third);
		qHit.getHits()[3].setDamage(fourth);
		qHit.updateTotalDamage();

		return new QueueableHit[] { qHit };
	}

	@Override
	public boolean canAttack(Character character, Character target) {
		return true;
	}

	@Override
	public void onQueueAdd(Character character, Character target) {
		CombatSpecial.drain(character.getAsPlayer(), CombatSpecial.DRAGON_CLAWS.getDrainAmount());
	}

	@Override
	public int getAttackSpeed(Character character) {
		return character.getBaseAttackSpeed();
	}

	@Override
	public int getAttackDistance(Character character) {
		return 1;
	}

	@Override
	public void startAnimation(Character character) {
		character.performAnimation(ANIMATION);
		character.performGraphic(GRAPHIC);
	}

	@Override
	public void finished(Character character) {

	}

	@Override
	public void handleAfterHitEffects(QueueableHit hit) {

	}
}