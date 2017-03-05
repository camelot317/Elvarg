package com.elvarg.world.entity.combat.hit;

import com.elvarg.world.entity.combat.CombatFactory;
import com.elvarg.world.entity.combat.formula.AccuracyFormulas;
import com.elvarg.world.entity.combat.method.CombatMethod;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.player.Player;

public class QueueableHit {

	/** The attacker instance. */
	private Character attacker;

	/** The victim instance. */
	private Character target;

	/** The combat method that was used in this hit **/
	private CombatMethod method;

	/** The damage which will be dealt **/
	private HitDamage[] hits;

	/** The total damage this hit will deal **/
	private int totalDamage;

	/** The delay of this hit **/
	private int delay;

	/** Check accuracy of the hit? **/
	private boolean checkAccuracy;

	/** Was the hit accurate? **/
	private boolean accurate;

	/** Should processedHit be executed after processing this hit? **/
	/** Used for custom queueable hits **/
	private boolean handleAfterHitEffects;

	/** Constructs a QueueableHit with a total of 1 hit. **/
	public QueueableHit(Character attacker, Character target, CombatMethod method, boolean checkAccuracy, int delay) {
		this.attacker = attacker;
		this.target = target;
		this.method = method;
		this.checkAccuracy = checkAccuracy;
		this.hits = prepareHits(1);
		this.delay = delay;
		this.handleAfterHitEffects = true;
	}

	/** Constructs a QueueableHit with a total of {hitAmount} hits. **/
	public QueueableHit(Character attacker, Character target, CombatMethod method, boolean checkAccuracy, int hitAmount,
			int delay) {
		this.attacker = attacker;
		this.target = target;
		this.method = method;
		this.checkAccuracy = checkAccuracy;
		this.hits = prepareHits(hitAmount);
		this.delay = delay;
		this.handleAfterHitEffects = true;
	}

	public Character getAttacker() {
		return attacker;
	}

	public Character getTarget() {
		return target;
	}

	public CombatMethod getCombatMethod() {
		return method;
	}

	public HitDamage[] getHits() {
		return hits;
	}

	public int decrementAndGetDelay() {
		return delay--;
	}

	public int getTotalDamage() {
		return totalDamage;
	}

	public boolean isAccurate() {
		return accurate;
	}

	public QueueableHit setHandleAfterHitEffects(boolean handleAfterHitEffects) {
		this.handleAfterHitEffects = handleAfterHitEffects;
		return this;
	}

	public boolean handleAfterHitEffects() {
		return handleAfterHitEffects;
	}

	private HitDamage[] prepareHits(int hitAmount) {
		// Check the hit amounts.
		if (hitAmount > 4) {
			throw new IllegalArgumentException("Illegal number of hits! The maximum number of hits per turn is 4.");
		} else if (hitAmount < 0) {
			throw new IllegalArgumentException("Illegal number of hits! The minimum number of hits per turn is 0.");
		}

		if (attacker == null || target == null) {
			return null;
		}

		HitDamage[] hits = new HitDamage[hitAmount];

		for (int i = 0; i < hits.length; i++) {

			// Was the hit accurate?
			accurate = checkAccuracy ? AccuracyFormulas.rollAccuracy(attacker, target, method.getCombatType()) : true;

			HitDamage damage;
			if (!accurate) {

				// The hit wasn't accurate. Blocked by defence. Don't do any
				// damage.
				damage = new HitDamage(0, HitMask.BLUE);
			} else {

				// The hit was accurate. Getting random damage..
				damage = CombatFactory.getHitDamage(attacker, target, method.getCombatType());
			}

			// Update total damage
			totalDamage += damage.getDamage(); // The total damage this
												// QueueableHit will deal, for
												// calculating amount of
												// experience to give the
												// attacker.
			hits[i] = damage;

		}
		return hits;
	}

	public void updateTotalDamage() {
		totalDamage = 0;
		for (int i = 0; i < hits.length; i++) {
			totalDamage += hits[i].getDamage();
		}
	}

	public void dealDamage() {
		if (hits.length == 1) {
			target.dealDamage(hits[0]);
		} else if (hits.length == 2) {
			target.dealDoubleDamage(hits[0], hits[1]);
		} else if (hits.length == 3) {
			target.dealTripleDamage(hits[0], hits[1], hits[2]);
		} else if (hits.length == 4) {
			target.dealQuadrupleDamage(hits[0], hits[1], hits[2], hits[3]);
		}
	}

	public int[] getSkills() {
		if (attacker.isNpc()) {
			return new int[] {};
		}
		return ((Player) attacker).getCombat().getFightType().getStyle().skill(method.getCombatType());
	}
}
