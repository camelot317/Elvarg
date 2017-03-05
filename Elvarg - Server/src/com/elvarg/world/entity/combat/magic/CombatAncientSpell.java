package com.elvarg.world.entity.combat.magic;

import java.util.Iterator;
import java.util.Optional;

import com.elvarg.world.World;
import com.elvarg.world.entity.combat.CombatFactory;
import com.elvarg.world.entity.combat.hit.QueueableHit;
import com.elvarg.world.entity.combat.method.impl.MagicCombatMethod;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.Locations.Location;
import com.elvarg.world.model.MagicSpellbook;

/**
 * A {@link CombatSpell} implementation that is primarily used for spells that
 * are a part of the ancients spellbook.
 * 
 * @author lare96
 */
public abstract class CombatAncientSpell extends CombatSpell {

	@Override
	public MagicSpellbook getSpellbook() {
		return MagicSpellbook.ANCIENT;
	}

	@Override
	public void finishCast(Character cast, Character castOn, boolean accurate, int damage) {

		// The spell wasn't accurate, so do nothing.
		if (!accurate || damage <= 0) {
			return;
		}

		// Do the spell effect here.
		spellEffect(cast, castOn, damage);

		// The spell doesn't support multiple targets or we aren't in a
		// multicombat zone, so do nothing.
		if (spellRadius() == 0/* || !Locations.Location.inMulti(castOn) */) {
			return;
		}

		// We passed the checks, so now we do multiple target stuff.
		Iterator<? extends Character> it = null;
		if (cast.isPlayer() && castOn.isPlayer()) {
			it = ((Player) cast).getLocalPlayers().iterator();
		} else if (cast.isPlayer() && castOn.isNpc()) {
			it = ((Player) cast).getLocalNpcs().iterator();
		} else if (cast.isNpc() && castOn.isNpc()) {
			it = World.getNpcs().iterator();
		} else if (cast.isNpc() && castOn.isPlayer()) {
			it = World.getPlayers().iterator();
		}

		for (Iterator<? extends Character> $it = it; $it.hasNext();) {
			Character next = $it.next();

			if (next == null) {
				continue;
			}

			if (next.isNpc()) {
				NPC n = (NPC) next;
				if (!n.getDefinition().isAttackable()) {
					continue;
				}
			} else {
				Player p = (Player) next;
				if (p.getLocation() != Location.WILDERNESS || !Location.inMulti(p)) {
					continue;
				}
			}

			if (next.getPosition().isWithinDistance(castOn.getPosition(), spellRadius()) && !next.equals(cast)
					&& !next.equals(castOn) && next.getHitpoints() > 0 && next.getHitpoints() > 0) {

				QueueableHit qH = new QueueableHit(cast, next, CombatFactory.MAGIC_COMBAT, true, 0)
						.setHandleAfterHitEffects(false);

				if (qH.isAccurate()) {

					// Successful hit, send graphics and do spell effects.
					endGraphic().ifPresent(next::performGraphic);
					spellEffect(cast, next, qH.getTotalDamage());

				} else {

					// Unsuccessful hit. Send splash graphics for the spell
					// because it wasn't accurate
					next.performGraphic(MagicCombatMethod.SPLASH_GRAPHIC);
				}

				CombatFactory.queueHit(qH);

			}
		}
	}

	@Override
	public Optional<Item[]> equipmentRequired(Player player) {

		// Ancient spells never require any equipment, although the method can
		// still be overridden if by some chance a spell does.
		return Optional.empty();
	}

	/**
	 * The effect this spell has on the target.
	 * 
	 * @param cast
	 *            the entity casting this spell.
	 * @param castOn
	 *            the person being hit by this spell.
	 * @param damage
	 *            the damage inflicted.
	 */
	public abstract void spellEffect(Character cast, Character castOn, int damage);

	/**
	 * The radius of this spell, only comes in effect when the victim is hit in
	 * a multicombat area.
	 * 
	 * @return how far from the target this spell can hit when targeting
	 *         multiple entities.
	 */
	public abstract int spellRadius();
}
