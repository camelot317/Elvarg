package com.elvarg.world.entity.combat.method.impl.specials;

import com.elvarg.world.entity.combat.CombatSpecial;
import com.elvarg.world.entity.combat.CombatType;
import com.elvarg.world.entity.combat.hit.QueueableHit;
import com.elvarg.world.entity.combat.method.CombatMethod;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.Graphic;
import com.elvarg.world.model.GraphicHeight;
import com.elvarg.world.model.Priority;

public class AbyssalWhipCombatMethod implements CombatMethod {

	private static final Animation ANIMATION = new Animation(1658, Priority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(341, GraphicHeight.HIGH, Priority.HIGH);

	@Override
	public CombatType getCombatType() {
		return CombatType.MELEE;
	}

	@Override
	public QueueableHit[] fetchDamage(Character character, Character target) {
		return new QueueableHit[] { new QueueableHit(character, target, this, true, 0) };
	}

	@Override
	public boolean canAttack(Character character, Character target) {
		return true;
	}

	@Override
	public void onQueueAdd(Character character, Character target) {
		CombatSpecial.drain(character.getAsPlayer(), CombatSpecial.ABYSSAL_WHIP.getDrainAmount());
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
	}

	@Override
	public void finished(Character character) {

	}

	@Override
	public void handleAfterHitEffects(QueueableHit hit) {
		Character target = hit.getTarget();

		if (target.getHitpoints() <= 0) {
			return;
		}

		target.performGraphic(GRAPHIC);
		if (target.isPlayer()) {
			Player p = (Player) target;
			int totalRunEnergy = p.getRunEnergy() - 25;
			if (totalRunEnergy < 0) {
				totalRunEnergy = 0;
			}
			p.setRunEnergy(totalRunEnergy);
			p.getPacketSender().sendRunEnergy(totalRunEnergy);
			if (totalRunEnergy == 0) {
				p.setRunning(false);
				p.getPacketSender().sendRunStatus();
			}
		}
	}
}