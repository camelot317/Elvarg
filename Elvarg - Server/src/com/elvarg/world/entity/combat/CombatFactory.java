package com.elvarg.world.entity.combat;

import java.util.Optional;

import com.elvarg.cache.impl.definitions.WeaponInterfaces.WeaponInterface;
import com.elvarg.engine.task.Task;
import com.elvarg.engine.task.TaskManager;
import com.elvarg.engine.task.impl.CombatPoisonEffect;
import com.elvarg.engine.task.impl.CombatPoisonEffect.CombatPoisonData;
import com.elvarg.engine.task.impl.CombatPoisonEffect.PoisonType;
import com.elvarg.net.SessionState;
import com.elvarg.util.Misc;
import com.elvarg.world.collision.region.RegionClipping;
import com.elvarg.world.content.PrayerHandler;
import com.elvarg.world.entity.combat.formula.DamageFormulas;
import com.elvarg.world.entity.combat.hit.HitDamage;
import com.elvarg.world.entity.combat.hit.HitMask;
import com.elvarg.world.entity.combat.hit.QueueableHit;
import com.elvarg.world.entity.combat.method.CombatMethod;
import com.elvarg.world.entity.combat.method.impl.MagicCombatMethod;
import com.elvarg.world.entity.combat.method.impl.MeleeCombatMethod;
import com.elvarg.world.entity.combat.method.impl.RangedCombatMethod;
import com.elvarg.world.entity.combat.ranged.RangedData;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.npc.NPCMovementCoordinator.CoordinateState;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.EffectTimer;
import com.elvarg.world.model.Flag;
import com.elvarg.world.model.Graphic;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.Locations.Location;
import com.elvarg.world.model.Skill;
import com.elvarg.world.model.container.impl.Equipment;
import com.elvarg.world.model.movement.RS317PathFinder;

/**
 * "The Combat Factory" Contains a bunch of methods and other things needed for
 * combat.
 * 
 * @author Swiffy
 */
public class CombatFactory {

	/**
	 * The default melee combat method.
	 */
	public static final MeleeCombatMethod MELEE_COMBAT = new MeleeCombatMethod();

	/**
	 * The default ranged combat method
	 */
	public static final RangedCombatMethod RANGED_COMBAT = new RangedCombatMethod();

	/**
	 * The default magic combat method
	 */
	public static final MagicCombatMethod MAGIC_COMBAT = new MagicCombatMethod();

	/**
	 * Generates a random {@link Hit} based on the argued entity's stats.
	 * 
	 * @param entity
	 *            the entity to generate the random hit for.
	 * @param victim
	 *            the victim being attacked.
	 * @param type
	 *            the combat type being used.
	 * @return the HitDamage.
	 */
	public static HitDamage getHitDamage(Character entity, Character victim, CombatType type) {

		int damage = 0;

		if (type == CombatType.MELEE) {
			damage = Misc.inclusive(1, DamageFormulas.calculateMaxMeleeHit(entity));

			// Do melee effects with the calculated damage..

		} else if (type == CombatType.RANGED) {
			damage = Misc.inclusive(1, DamageFormulas.calculateMaxRangedHit(entity));

			// Do ranged effects with the calculated damage..
			if (entity.isPlayer()) {

				Player player = entity.getAsPlayer();

				// Check if player is using dark bow and set damage to minimum
				// 8, maxmimum 48 if that's the case...
				if (player.getAsPlayer().isSpecialActivated()
						&& player.getAsPlayer().getCombatSpecial() == CombatSpecial.DARK_BOW) {
					if (damage < 8) {
						damage = 8;
					} else if (damage > 48) {
						damage = 48;
					}
				}

				// Handle bolt special effects for a player whose using crossbow
				if (player.getCombat().getWeapon() == WeaponInterface.CROSSBOW && Misc.getRandom(10) <= 2) {
					double multiplier = RangedData.getSpecialEffectsMultiplier(player, victim, damage);
					damage *= multiplier;
				}
			}

		} else if (type == CombatType.MAGIC) {
			damage = Misc.inclusive(1, DamageFormulas.getMagicMaxhit(entity));

			// Do magic effects with the calculated damage..
		}

		// We've got our damage. We can now create a HitDamage
		// instance.
		HitDamage hitDamage = new HitDamage(damage, HitMask.RED);

		/**
		 * Prayers decreasing damage.
		 */

		// Decrease damage if victim is a player and has prayers active..
		if (victim.isPlayer() && (!CombatFactory.fullVeracs(entity) || Misc.getRandom(4) == 1)) {

			// Check if victim is is using correct protection prayer
			if (PrayerHandler.isActivated(victim.getAsPlayer(), PrayerHandler.getProtectingPrayer(type))) {

				// Apply the damage reduction mod
				if (entity.isNpc()) {
					hitDamage.multiplyDamage(CombatConstants.PRAYER_DAMAGE_REDUCTION_AGAINST_NPCS);
				} else {
					hitDamage.multiplyDamage(CombatConstants.PRAYER_DAMAGE_REDUCTION_AGAINST_PLAYERS);
				}
			}
		}

		// Return our hitDamage that may have been modified slightly.
		return hitDamage;
	}

	/**
	 * Checks if an entity is a valid target.
	 * 
	 * @param attacker
	 * @param target
	 * @return
	 */
	public static boolean validTarget(Character attacker, Character target) {
		if (!target.isRegistered() || !attacker.isRegistered() || attacker.getHitpoints() <= 0
				|| target.getHitpoints() <= 0) {
			attacker.getCombat().reset();
			return false;
		}

		// Check if teleporting away/teleported away
		if (target.isNeedsPlacement() || attacker.isNeedsPlacement()
				|| attacker.getPosition().getDistance(target.getPosition()) >= 40) {
			attacker.getCombat().reset();
			return false;
		}

		// Check if any of the two have requested a proper logout.
		if (target.isPlayer()) {
			if (target.getAsPlayer().getSession().getState() == SessionState.REQUESTED_LOG_OUT) {
				return false;
			}
		}
		if (attacker.isPlayer()) {
			if (attacker.getAsPlayer().getSession().getState() == SessionState.REQUESTED_LOG_OUT) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if an entity can reach a target.
	 * 
	 * @param attacker
	 *            The entity which wants to attack.
	 * @param cb_type
	 *            The combat type the attacker is using.
	 * @param target
	 *            The victim.
	 * @return True if attacker has the proper distance to attack, otherwise
	 *         false.
	 */
	public static boolean canReach(Character attacker, CombatMethod method, Character target) {
		if (!validTarget(attacker, target)) {
			return false;
		}

		// Walk back if npc is too far away from spawn position.
		if (attacker.isNpc()) {
			NPC npc = attacker.getAsNpc();
			if (npc.getMovementCoordinator().getCoordinateState() == CoordinateState.RETREATING) {
				npc.getCombat().reset();
				return false;
			}
			if (npc.getPosition().getDistance(npc.getSpawnPosition()) >= npc.getDefinition()
					.getCombatFollowDistance()) {
				npc.getCombat().reset();
				npc.getMovementCoordinator().setCoordinateState(CoordinateState.RETREATING);
				return false;
			}
		}

		int distance = method.getAttackDistance(attacker);

		if (attacker.isPlayer() && method.getCombatType() != CombatType.MELEE) {
			if (target.getSize() >= 2) {
				distance += target.getSize() - 1;
			}
		}

		// We're moving so increase the distance by 2.
		if (attacker.getMovementQueue().isMoving() && target.getMovementQueue().isMoving()) {
			distance += 2;
		}

		// Check good distance?
		if (!(attacker.getPosition().isWithinDistance(target.getPosition(), distance))) {
			return false;
		} else {
			// Stop running forward if we're in distance.
			attacker.getMovementQueue().reset();
		}

		// Check blocked projectiles
		if (method.getCombatType() != CombatType.MELEE) {
			/*
			 * if(!RegionClipping.canProjectileAttack(attacker, target)) {
			 * return false; }
			 */
		} else {
			// Check diagonal block
			if (RegionClipping.isInDiagonalBlock(attacker, target)) {
				RS317PathFinder.solveDiagonalBlock(attacker, target);
				return false;
			}
		}

		// Check same spot
		if (attacker.getPosition().equals(target.getPosition())) {

			if (!attacker.getCombat().getFreezeTimer().finished()) {
				return false;
			}

			RS317PathFinder.solveDiagonalBlock(attacker, target);
		}

		return true;
	}

	/**
	 * Checks if an entity can attack a target.
	 * 
	 * @param attacker
	 *            The entity which wants to attack.
	 * @param cb_type
	 *            The combat type the attacker is using.
	 * @param target
	 *            The victim.
	 * @return True if attacker has the requirements to attack, otherwise false.
	 */
	public static boolean canAttack(Character attacker, CombatMethod method, Character target) {
		if (!validTarget(attacker, target)) {
			return false;
		}

		// Here we check if we are already in combat with another entity.
		// Only check if we aren't in multi.
		if (!(Location.inMulti(attacker) && Location.inMulti(target))) {
			if (isBeingAttacked(attacker) && attacker.getCombat().getAttacker() != target
					&& attacker.getCombat().getAttacker().getHitpoints() > 0) {
				if (attacker.isPlayer()) {
					attacker.getAsPlayer().getPacketSender().sendMessage("You are already under attack!");
				}
				attacker.getCombat().reset();
				return false;
			}

			// Here we check if we are already in combat with another entity.
			if (isBeingAttacked(target) && target.getCombat().getAttacker() != attacker) {
				if (attacker.isPlayer()) {
					attacker.getAsPlayer().getPacketSender().sendMessage("They are already under attack!");
				}
				attacker.getCombat().reset();
				return false;
			}
		}

		// Check if they can attack in their location instance
		if (attacker.isPlayer() && target.isPlayer()) {
			if (!attacker.getLocation().canAttack(attacker.getAsPlayer(), target.getAsPlayer())) {
				attacker.getCombat().reset();
				return false;
			}
		}

		if (!method.canAttack(attacker, target)) {
			return false;
		}

		// Check special attack
		if (attacker.isPlayer()) {
			Player p = attacker.getAsPlayer();

			// Check if we're using a special attack..
			if (p.isSpecialActivated() && p.getCombatSpecial() != null) {

				// Check if we have enough special attack percentage.
				// If not, reset special attack.
				if (p.getSpecialPercentage() < p.getCombatSpecial().getDrainAmount()) {
					p.getPacketSender().sendMessage("You do not have enough special attack energy left!");
					p.setSpecialActivated(false);
					CombatSpecial.updateBar(p);
					p.getCombat().reset();
					return false;
				}
			}

		}

		return true;
	}

	public static void queueHit(QueueableHit qHit) {
		Character attacker = qHit.getAttacker();
		Character target = qHit.getTarget();
		HitDamage[] damage = qHit.getHits();
		if (damage == null || target.getHitpoints() <= 0) {
			return;
		}

		if (attacker.isPlayer()) {
			rewardExp(attacker.getAsPlayer(), qHit);

			if (target.isPlayer()) {
				checkSkull(attacker.getAsPlayer(), target.getAsPlayer());
			}

		}

		target.getCombat().getHitQueue().append(qHit);
	}

	public static void handleQueuedHit(QueueableHit qHit) {
		final Character attacker = qHit.getAttacker();
		final Character target = qHit.getTarget();
		final CombatMethod method = qHit.getCombatMethod();

		if (target.getHitpoints() <= 0) {
			return;
		}

		// Do block animation
		target.performAnimation(new Animation(target.getBlockAnim()));

		// Here, we take the damage.
		// BUT, don't take damage if the attack was a magic splash.
		boolean magic_splash = method.getCombatType() == CombatType.MAGIC && !qHit.isAccurate();
		if (!magic_splash) {
			qHit.dealDamage();
		}

		// Make sure to let the combat method know we finished the attack
		// Only if this isn't custom hit (handleAfterHitEffects() will be false
		// then)
		if (qHit.handleAfterHitEffects()) {
			if (method != null) {
				method.handleAfterHitEffects(qHit);
			}
		}

		// Check for poisonous weapons..
		// And do other effects, such as barrows effects..
		if (attacker.isPlayer()) {

			Player p_ = attacker.getAsPlayer();

			// Randomly apply poison if poisonous weapon is equipped.
			if (Misc.getRandom(100) >= 90) {

				Optional<PoisonType> poison = Optional.empty();

				if (method.getCombatType() == CombatType.MELEE || p_.getCombat().getWeapon() == WeaponInterface.DART
						|| p_.getCombat().getWeapon() == WeaponInterface.KNIFE
						|| p_.getCombat().getWeapon() == WeaponInterface.THROWNAXE
						|| p_.getCombat().getWeapon() == WeaponInterface.JAVELIN) {
					poison = CombatPoisonData.getPoisonType(p_.getEquipment().get(Equipment.WEAPON_SLOT));
				} else if (method.getCombatType() == CombatType.RANGED) {
					poison = CombatPoisonData.getPoisonType(p_.getEquipment().get(Equipment.AMMUNITION_SLOT));
				}

				if (poison.isPresent()) {
					CombatFactory.poisonEntity(target, poison.get());
				}
			}

			// Handle barrows effects if damage is more than zero.
			if (qHit.getTotalDamage() > 0) {
				if (Misc.getRandom(10) >= 8) {

					// Apply Guthan's effect..
					checkGuthans(p_, target, qHit.getTotalDamage());

					// Other barrows effects here..
				}
			}
		}

		if (target.isPlayer()) {
			final Player p_ = target.getAsPlayer();

			// Handle ring of recoil for target
			// Also handle vengeance for target
			if (qHit.getTotalDamage() > 0) {
				checkRecoil(p_, attacker, qHit.getTotalDamage());
				checkVengeance(p_, attacker, qHit.getTotalDamage());
			}

			// Prayer effects
			checkPrayerEffects(attacker, p_, qHit.getTotalDamage(), method.getCombatType());
		}

		// Auto retaliate if needed
		checkAutoretaliate(attacker, target);

		// Set under attack
		target.getCombat().setUnderAttack(attacker);

		// Add damage to target damage map
		target.getCombat().addDamage(attacker, qHit.getTotalDamage());
	}

	/**
	 * Rewards a player with experience in respective skills based on how much
	 * damage they've dealt.
	 * 
	 * @param player
	 *            The player.
	 * @param hit
	 *            The damage dealt.
	 */
	public static void rewardExp(Player player, QueueableHit hit) {

		// Add magic exp, even if total damage is 0.
		// Since spells have a base exp reward
		if (hit.getCombatMethod().getCombatType() == CombatType.MAGIC) {
			if (player.getCombat().getPreviousCast() != null) {
				player.getSkillManager().addExperience(Skill.MAGIC,
						hit.getTotalDamage() + player.getCombat().getPreviousCast().baseExperience());
			}
		}

		// Don't add any exp to other skills if total damage is 0.
		if (hit.getTotalDamage() <= 0) {
			return;
		}

		// Add hp xp
		player.getSkillManager().addExperience(Skill.HITPOINTS, (int) (hit.getTotalDamage() * .70));

		// Magic xp was already added
		if (hit.getCombatMethod().getCombatType() == CombatType.MAGIC) {
			return;
		}

		// Add all other skills xp
		final int[] exp = hit.getSkills();
		for (int i : exp) {
			Skill skill = Skill.forId(i);
			player.getSkillManager().addExperience(skill, ((hit.getTotalDamage()) / exp.length));
		}
	}

	public static CombatMethod getMethod(Character attacker) {

		if (attacker.isPlayer()) {

			Player p = attacker.getAsPlayer();

			// Check if player is maging..
			if (p.getCombat().getCastSpell() != null || p.getCombat().getAutocastSpell() != null) {
				return MAGIC_COMBAT;
			}

			// Check special attacks..
			if (p.getCombatSpecial() != null) {
				if (p.isSpecialActivated()) {
					return p.getCombatSpecial().getCombatMethod();
				}
			}

			// Check if player is ranging..
			if (p.getCombat().getRangedWeaponData() != null) {
				return RANGED_COMBAT;
			}

		}

		// Return melee by default
		return MELEE_COMBAT;
	}

	public static boolean isAttacking(Character character) {
		return character.getCombat().getTarget() != null;
	}

	public static boolean isBeingAttacked(Character character) {
		return character.getCombat().getAttacker() != null;
	}

	public static boolean inCombat(Character character) {
		return isAttacking(character) || isBeingAttacked(character);
	}

	/**
	 * Attempts to poison the argued {@link Character} with the argued
	 * {@link PoisonType}. This method will have no effect if the entity is
	 * already poisoned.
	 * 
	 * @param entity
	 *            the entity that will be poisoned, if not already.
	 * @param poisonType
	 *            the poison type that this entity is being inflicted with.
	 */
	public static void poisonEntity(Character entity, PoisonType poisonType) {

		// We are already poisoned or the poison type is invalid, do nothing.
		if (entity.isPoisoned()) {
			return;
		}

		// If the entity is a player, we check for poison immunity. If they have
		// no immunity then we send them a message telling them that they are
		// poisoned.
		if (entity.isPlayer()) {
			Player player = (Player) entity;
			if (!player.getCombat().getPoisonImmunityTimer().finished()) {
				return;
			}
			player.getPacketSender().sendMessage("You have been poisoned!");
		}

		entity.setPoisonDamage(poisonType.getDamage());
		TaskManager.submit(new CombatPoisonEffect(entity));
	}

	public static void disableProtectionPrayers(Player player) {

		// Player has already been prayer-disabled
		if (!player.getCombat().getPrayerBlockTimer().finished()) {
			return;
		}

		player.getCombat().getPrayerBlockTimer().start(200);
		PrayerHandler.resetPrayers(player, PrayerHandler.PROTECTION_PRAYERS);
		player.getPacketSender().sendMessage("You have been disabled and can no longer use protection prayers.");
	}

	public static void checkRecoil(Player player, Character attacker, int damage) {
		if (player.getEquipment().get(Equipment.RING_SLOT).getId() == CombatConstants.RING_OF_RECOIL_ID) {
			int returnDmg = (int) Math.ceil(damage * 0.1D);
			if (returnDmg > 0) {

				attacker.dealDamage(new HitDamage(returnDmg, HitMask.RED));

				player.setRecoilDamage(player.getRecoilDamage() + damage);
				if (player.getRecoilDamage() >= 80 || Misc.getRandom(200) >= 195) {

					player.getEquipment().set(Equipment.RING_SLOT, new Item(-1));
					player.getEquipment().refreshItems();
					player.getPacketSender().sendMessage("Your ring of recoil has degraded.");

					player.setRecoilDamage(0);
				}
			}
		}
	}

	public static void checkVengeance(Player player, Character attacker, int damage) {
		if (player.hasVengeance()) {
			int returnDmg = (int) (damage * 0.75);
			if (returnDmg > 0) {

				attacker.dealDamage(new HitDamage(returnDmg, HitMask.RED));
				player.forceChat("Taste Vengeance!");
				player.setHasVengeance(false);

			}
		}
	}

	public static void checkGuthans(Player player, Character target, int damage) {
		if (fullGuthans(player)) {
			target.performGraphic(new Graphic(398));
			player.heal(damage);
		}
	}

	public static void checkSkull(Player attacker, Player target) {

		if (attacker.isSkulled()) {
			return;
		}

		// We've probably already been skulled by this player.
		if (target.getCombat().damageMapContains(attacker) || attacker.getCombat().damageMapContains(target)) {
			return;
		}

		if (target.getCombat().getAttacker() != null && target.getCombat().getAttacker() == attacker) {
			return;
		}

		if (attacker.getCombat().getAttacker() != null && attacker.getCombat().getAttacker() == target) {
			return;
		}

		attacker.getPacketSender().sendMessage("@red@You have been skulled!");
		attacker.setSkullTimer(600); // 6 minutes exactly.
		attacker.getUpdateFlag().flag(Flag.APPEARANCE);

	}

	public static void checkAutoretaliate(Character attacker, Character target) {
		if (!CombatFactory.isAttacking(target)) {

			boolean auto_ret;

			if (target.isPlayer()) {
				auto_ret = target.getCombat().autoRetaliate() && target.getMovementQueue().isMovementDone();
			} else {
				auto_ret = target.getAsNpc().getMovementCoordinator().getCoordinateState() == CoordinateState.HOME;
			}

			if (!auto_ret) {
				return;
			}

			// Start a task, don't autoretaliate immediately
			TaskManager.submit(new Task(1, attacker, false) {
				@Override
				protected void execute() {

					// Double check reqs again
					target.getCombat().attack(attacker);

					stop();
				}
			});
		}
	}

	public static void freeze(Character character, int seconds) {
		// Don't allow freeze more than once
		if (!character.getCombat().getFreezeTimer().finished()) {
			return;
		}

		// Add check for npc: Only small npcs should be freeze-able
		if (character.isNpc()) {
			// if(size > 1) {
			// return;
			// }
		}

		character.getCombat().getFreezeTimer().start(seconds);
		character.getMovementQueue().reset();

		if (character.isPlayer()) {

			// Send message and effect timer to client
			character.getAsPlayer().getPacketSender().sendMessage("You have been frozen!").sendEffectTimer(seconds,
					EffectTimer.FREEZE);

			// Actually reset combat too
			// I think it's that way on osrs
			character.getCombat().reset();
		}
	}

	public static void checkPrayerEffects(Character attacker, Player victim, int damage, CombatType type) {

		// Handle redemption here
		if (PrayerHandler.isActivated(victim, PrayerHandler.REDEMPTION)
				&& victim.getHitpoints() <= (victim.getSkillManager().getMaxLevel(Skill.HITPOINTS) / 10)) {
			int amountToHeal = (int) (victim.getSkillManager().getMaxLevel(Skill.PRAYER) * .25);
			victim.performGraphic(new Graphic(436));
			victim.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
			victim.getSkillManager().updateSkill(Skill.PRAYER);
			victim.getSkillManager().setCurrentLevel(Skill.HITPOINTS, victim.getHitpoints() + amountToHeal);
			victim.getSkillManager().updateSkill(Skill.HITPOINTS);
			victim.getPacketSender().sendMessage("You've run out of prayer points!");
			PrayerHandler.deactivatePrayers(victim);
			return;
		}

		if (attacker.isPlayer()) {

			Player p = (Player) attacker;

			// The retribution prayer effect.
			if (PrayerHandler.isActivated(victim, PrayerHandler.RETRIBUTION) && victim.getHitpoints() < 1) {
				victim.performGraphic(new Graphic(437));
				if (p.getPosition().isWithinDistance(victim.getPosition(), CombatConstants.RETRIBUTION_RADIUS)) {
					p.dealDamage(
							new HitDamage(Misc.getRandom(CombatConstants.MAXIMUM_RETRIBUTION_DAMAGE), HitMask.RED));
				}
			}

			// Handle smite effect here
			if (PrayerHandler.isActivated((Player) attacker, PrayerHandler.SMITE)) {
				victim.getSkillManager().setCurrentLevel(Skill.PRAYER,
						victim.getSkillManager().getCurrentLevel(Skill.PRAYER) - damage / 4);
				if (victim.getSkillManager().getCurrentLevel(Skill.PRAYER) < 0)
					victim.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
				victim.getSkillManager().updateSkill(Skill.PRAYER);
			}
		}
	}

	/**
	 * Determines if the entity is wearing full veracs.
	 * 
	 * @param entity
	 *            the entity to determine this for.
	 * @return true if the player is wearing full veracs.
	 */
	public static boolean fullVeracs(Character entity) {
		return entity.isNpc() ? entity.getAsNpc().getDefinition().getName().equals("Verac the Defiled")
				: entity.getAsPlayer().getEquipment().containsAll(4753, 4757, 4759, 4755);
	}

	/**
	 * Determines if the entity is wearing full dharoks.
	 * 
	 * @param entity
	 *            the entity to determine this for.
	 * @return true if the player is wearing full dharoks.
	 */
	public static boolean fullDharoks(Character entity) {
		return entity.isNpc() ? entity.getAsNpc().getDefinition().getName().equals("Dharok the Wretched")
				: entity.getAsPlayer().getEquipment().containsAll(4716, 4720, 4722, 4718);
	}

	/**
	 * Determines if the entity is wearing full karils.
	 * 
	 * @param entity
	 *            the entity to determine this for.
	 * @return true if the player is wearing full karils.
	 */
	public static boolean fullKarils(Character entity) {
		return entity.isNpc() ? entity.getAsNpc().getDefinition().getName().equals("Karil the Tainted")
				: entity.getAsPlayer().getEquipment().containsAll(4732, 4736, 4738, 4734);
	}

	/**
	 * Determines if the entity is wearing full ahrims.
	 * 
	 * @param entity
	 *            the entity to determine this for.
	 * @return true if the player is wearing full ahrims.
	 */
	public static boolean fullAhrims(Character entity) {
		return entity.isNpc() ? entity.getAsNpc().getDefinition().getName().equals("Ahrim the Blighted")
				: entity.getAsPlayer().getEquipment().containsAll(4708, 4712, 4714, 4710);
	}

	/**
	 * Determines if the entity is wearing full torags.
	 * 
	 * @param entity
	 *            the entity to determine this for.
	 * @return true if the player is wearing full torags.
	 */
	public static boolean fullTorags(Character entity) {
		return entity.isNpc() ? entity.getAsNpc().getDefinition().getName().equals("Torag the Corrupted")
				: entity.getAsPlayer().getEquipment().containsAll(4745, 4749, 4751, 4747);
	}

	/**
	 * Determines if the entity is wearing full guthans.
	 * 
	 * @param entity
	 *            the entity to determine this for.
	 * @return true if the player is wearing full guthans.
	 */
	public static boolean fullGuthans(Character entity) {
		return entity.isNpc() ? entity.getAsNpc().getDefinition().getName().equals("Guthan the Infested")
				: entity.getAsPlayer().getEquipment().containsAll(4724, 4728, 4730, 4726);
	}

	/**
	 * Calculates the combat level difference for wilderness player vs. player
	 * combat.
	 * 
	 * @param combatLevel
	 *            the combat level of the first person.
	 * @param otherCombatLevel
	 *            the combat level of the other person.
	 * @return the combat level difference.
	 */
	public static int combatLevelDifference(int combatLevel, int otherCombatLevel) {
		if (combatLevel > otherCombatLevel) {
			return (combatLevel - otherCombatLevel);
		} else if (otherCombatLevel > combatLevel) {
			return (otherCombatLevel - combatLevel);
		} else {
			return 0;
		}
	}
}
