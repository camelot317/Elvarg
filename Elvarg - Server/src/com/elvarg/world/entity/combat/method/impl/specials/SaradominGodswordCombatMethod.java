package com.elvarg.world.entity.combat.method.impl.specials;

import com.elvarg.world.entity.combat.CombatSpecial;
import com.elvarg.world.entity.combat.CombatType;
import com.elvarg.world.entity.combat.hit.QueueableHit;
import com.elvarg.world.entity.combat.method.CombatMethod;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.Graphic;
import com.elvarg.world.model.Priority;
import com.elvarg.world.model.Skill;

public class SaradominGodswordCombatMethod implements CombatMethod {

	private static final Animation ANIMATION = new Animation(7058, Priority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(1209, Priority.HIGH);

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
		CombatSpecial.drain(character.getAsPlayer(), CombatSpecial.SARADOMIN_GODSWORD.getDrainAmount());
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
		Player player = hit.getAttacker().getAsPlayer();
		int damage = hit.getTotalDamage();
		int damageHeal = (int) (damage * 0.5);
		int damagePrayerHeal = (int) (damage * 0.25);
		if (player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) < player.getSkillManager()
				.getMaxLevel(Skill.HITPOINTS)) {
			int level = player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) + damageHeal > player
					.getSkillManager().getMaxLevel(Skill.HITPOINTS)
							? player.getSkillManager().getMaxLevel(Skill.HITPOINTS)
							: player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) + damageHeal;
			player.getSkillManager().setCurrentLevel(Skill.HITPOINTS, level);
		}
		if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
				.getMaxLevel(Skill.PRAYER)) {
			int level = player.getSkillManager().getCurrentLevel(Skill.PRAYER) + damagePrayerHeal > player
					.getSkillManager().getMaxLevel(Skill.PRAYER) ? player.getSkillManager().getMaxLevel(Skill.PRAYER)
							: player.getSkillManager().getCurrentLevel(Skill.PRAYER) + damagePrayerHeal;
			player.getSkillManager().setCurrentLevel(Skill.PRAYER, level);
		}
	}
}