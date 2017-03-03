package com.elvarg.cache.impl.definitions;

import java.util.HashMap;
import java.util.Map;

import com.elvarg.GameConstants;
import com.elvarg.util.JsonLoader;
import com.elvarg.world.entity.combat.CombatSpecial;
import com.elvarg.world.entity.combat.FightType;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.container.impl.Equipment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * A static utility class that displays holds and displays data for weapon
 * interfaces.
 * 
 * @author lare96
 */
public final class WeaponInterfaces {

	/** A map of items and their respective interfaces. */
	private static Map<Integer, WeaponInterface> interfaces = new HashMap<>(500);

	/**
	 * All of the interfaces for weapons and the data needed to display these
	 * interfaces properly.
	 * 
	 * @author lare96
	 */
	public enum WeaponInterface {
		STAFF(328, 355, 5, new FightType[] { FightType.STAFF_BASH, FightType.STAFF_POUND, FightType.STAFF_FOCUS }),
		WARHAMMER(425, 428, 5, new FightType[] { FightType.WARHAMMER_POUND,
				FightType.WARHAMMER_PUMMEL, FightType.WARHAMMER_BLOCK }, 7474, 7486),
		MAUL(425, 428, 5, new FightType[] { FightType.MAUL_POUND,
				FightType.MAUL_PUMMEL, FightType.MAUL_BLOCK }, 7474, 7486),
		GRANITE_MAUL(425, 428, 6, new FightType[] { FightType.GRANITE_MAUL_POUND,
				FightType.GRANITE_MAUL_PUMMEL, FightType.GRANITE_MAUL_BLOCK }, 7474, 7486),
		SCYTHE(776, 779, 4, new FightType[] { FightType.SCYTHE_REAP,
				FightType.SCYTHE_CHOP, FightType.SCYTHE_JAB,
				FightType.SCYTHE_BLOCK }),
		BATTLEAXE(1698, 1701, 5, new FightType[] { FightType.BATTLEAXE_CHOP,
				FightType.BATTLEAXE_HACK, FightType.BATTLEAXE_SMASH,
				FightType.BATTLEAXE_BLOCK }, 7499, 7511),
		GREATAXE(1698, 1701, 5, new FightType[] { FightType.GREATAXE_CHOP,
				FightType.GREATAXE_HACK, FightType.GREATAXE_SMASH,
				FightType.GREATAXE_BLOCK }, 7499, 7511),
		CROSSBOW(1764, 1767, 5, new FightType[] { FightType.CROSSBOW_ACCURATE,
				FightType.CROSSBOW_RAPID, FightType.CROSSBOW_LONGRANGE }, 7549, 7561),
		SHORTBOW(1764, 1767, 3, new FightType[] { FightType.SHORTBOW_ACCURATE,
				FightType.SHORTBOW_RAPID, FightType.SHORTBOW_LONGRANGE }, 7549, 7561),
		LONGBOW(1764, 1767, 6, new FightType[] { FightType.LONGBOW_ACCURATE,
				FightType.LONGBOW_RAPID, FightType.LONGBOW_LONGRANGE }, 7549, 7561),
		DAGGER(2276, 2279, 5, new FightType[] { FightType.DAGGER_STAB,
				FightType.DAGGER_LUNGE, FightType.DAGGER_SLASH,
				FightType.DAGGER_BLOCK }, 7574, 7586),
		SWORD(2276, 2279, 5, new FightType[] { FightType.SWORD_STAB,
				FightType.SWORD_LUNGE, FightType.SWORD_SLASH,
				FightType.SWORD_BLOCK }, 7574, 7586),
		SCIMITAR(2423, 2426, 5, new FightType[] { FightType.SCIMITAR_CHOP,
				FightType.SCIMITAR_SLASH, FightType.SCIMITAR_LUNGE,
				FightType.SCIMITAR_BLOCK }, 7599, 7611),
		LONGSWORD(2423, 2426, 5, new FightType[] { FightType.LONGSWORD_CHOP,
				FightType.LONGSWORD_SLASH, FightType.LONGSWORD_LUNGE,
				FightType.LONGSWORD_BLOCK }, 7599, 7611),
		MACE(3796, 3799, 5, new FightType[] { FightType.MACE_POUND,
				FightType.MACE_PUMMEL, FightType.MACE_SPIKE,
				FightType.MACE_BLOCK }, 7624, 7636),
		KNIFE(4446, 4449, 3, new FightType[] { FightType.KNIFE_ACCURATE,
				FightType.KNIFE_RAPID, FightType.KNIFE_LONGRANGE }, 7649, 7661),
		SPEAR(4679, 4682, 5, new FightType[] { FightType.SPEAR_LUNGE,
				FightType.SPEAR_SWIPE, FightType.SPEAR_POUND,
				FightType.SPEAR_BLOCK }, 7674, 7686),
		TWO_HANDED_SWORD(4705, 4708, 6, new FightType[] {
				FightType.TWOHANDEDSWORD_CHOP, FightType.TWOHANDEDSWORD_SLASH,
				FightType.TWOHANDEDSWORD_SMASH, FightType.TWOHANDEDSWORD_BLOCK }, 7699, 7711),
		PICKAXE(5570, 5573, 5, new FightType[] { FightType.PICKAXE_SPIKE,
				FightType.PICKAXE_IMPALE, FightType.PICKAXE_SMASH,
				FightType.PICKAXE_BLOCK }),
		CLAWS(7762, 7765, 5, new FightType[] { FightType.CLAWS_CHOP,
				FightType.CLAWS_SLASH, FightType.CLAWS_LUNGE,
				FightType.CLAWS_BLOCK }, 7800, 7812),
		HALBERD(8460, 8463, 5, new FightType[] { FightType.HALBERD_JAB,
				FightType.HALBERD_SWIPE, FightType.HALBERD_FEND }, 8493, 8505),
		UNARMED(5855, 5857, 5, new FightType[] { FightType.UNARMED_PUNCH,
				FightType.UNARMED_KICK, FightType.UNARMED_BLOCK }),
		WHIP(12290, 12293, 4, new FightType[] { FightType.WHIP_FLICK,
				FightType.WHIP_LASH, FightType.WHIP_DEFLECT }, 12323, 12335),
		THROWNAXE(4446, 4449, 4, new FightType[] {
				FightType.THROWNAXE_ACCURATE, FightType.THROWNAXE_RAPID,
				FightType.THROWNAXE_LONGRANGE }, 7649, 7661),
		DART(4446, 4449, 3, new FightType[] { FightType.DART_ACCURATE,
				FightType.DART_RAPID, FightType.DART_LONGRANGE }, 7649, 7661),
		JAVELIN(4446, 4449, 4, new FightType[] { FightType.JAVELIN_ACCURATE,
				FightType.JAVELIN_RAPID, FightType.JAVELIN_LONGRANGE }, 7649, 7661);

		/** The interface that will be displayed on the sidebar. */
		private int interfaceId;

		/** The line that the name of the item will be printed to. */
		private int nameLineId;

		/** The attack speed of weapons using this interface. */
		private int speed;

		/** The fight types that correspond with this interface. */
		private FightType[] fightType;

		/** The id of the special bar for this interface. */
		private int specialBar;

		/** The id of the special meter for this interface. */
		private int specialMeter;

		/**
		 * Creates a new weapon interface.
		 * 
		 * @param interfaceId
		 *            the interface that will be displayed on the sidebar.
		 * @param nameLineId
		 *            the line that the name of the item will be printed to.
		 * @param speed
		 *            the attack speed of weapons using this interface.
		 * @param fightType
		 *            the fight types that correspond with this interface.
		 * @param specialBar
		 *            the id of the special bar for this interface.
		 * @param specialMeter
		 *            the id of the special meter for this interface.
		 */
		private WeaponInterface(int interfaceId, int nameLineId, int speed,
				FightType[] fightType, int specialBar, int specialMeter) {
			this.interfaceId = interfaceId;
			this.nameLineId = nameLineId;
			this.speed = speed;
			this.fightType = fightType;
			this.specialBar = specialBar;
			this.specialMeter = specialMeter;
		}

		/**
		 * Creates a new weapon interface.
		 * 
		 * @param interfaceId
		 *            the interface that will be displayed on the sidebar.
		 * @param nameLineId
		 *            the line that the name of the item will be printed to.
		 * @param speed
		 *            the attack speed of weapons using this interface.
		 * @param fightType
		 *            the fight types that correspond with this interface.
		 */
		private WeaponInterface(int interfaceId, int nameLineId, int speed,
				FightType[] fightType) {
			this(interfaceId, nameLineId, speed, fightType, -1, -1);
		}

		/**
		 * Gets the interface that will be displayed on the sidebar.
		 * 
		 * @return the interface id.
		 */
		public int getInterfaceId() {
			return interfaceId;
		}

		/**
		 * Gets the line that the name of the item will be printed to.
		 * 
		 * @return the name line id.
		 */
		public int getNameLineId() {
			return nameLineId;
		}

		/**
		 * Gets the attack speed of weapons using this interface.
		 * 
		 * @return the attack speed of weapons using this interface.
		 */
		public int getSpeed() {
			return speed;
		}

		/**
		 * Gets the fight types that correspond with this interface.
		 * 
		 * @return the fight types that correspond with this interface.
		 */
		public FightType[] getFightType() {
			return fightType;
		}

		/**
		 * Gets the id of the special bar for this interface.
		 * 
		 * @return the id of the special bar for this interface.
		 */
		public int getSpecialBar() {
			return specialBar;
		}

		/**
		 * Gets the id of the special meter for this interface.
		 * 
		 * @return the id of the special meter for this interface.
		 */
		public int getSpecialMeter() {
			return specialMeter;
		}
	}

	/**
	 * Assigns an interface to the combat sidebar based on the argued weapon.
	 * 
	 * @param player
	 *            the player that the interface will be assigned for.
	 * @param item
	 *            the item that the interface will be chosen for.
	 */
	public static void assign(Player player) {
		int item = player.getEquipment().get(Equipment.WEAPON_SLOT).getId();
		WeaponInterface weapon;


		if(item == -1) {
			weapon = WeaponInterface.UNARMED;
		} else {
			weapon = interfaces.get(item);
		}


		if(weapon == null)
			weapon = WeaponInterface.UNARMED;

		if (weapon == WeaponInterface.UNARMED) {
			player.getPacketSender().sendTabInterface(0, weapon.getInterfaceId());
			player.getPacketSender().sendString(weapon.getNameLineId(), "Unarmed");
			player.getCombat().setWeapon(WeaponInterface.UNARMED);
		} else if (weapon == WeaponInterface.CROSSBOW) {
			player.getPacketSender().sendString(weapon.getNameLineId() - 1, "Weapon: ");
		} else if (weapon == WeaponInterface.WHIP) {
			player.getPacketSender().sendString(weapon.getNameLineId() - 1, "Weapon: ");
		}

		//player.getPacketSender().sendItemOnInterface(weapon.getInterfaceId() + 1, 200, item);
		//player.getPacketSender().sendItemOnInterface(weapon.getInterfaceId() + 1, item, 0, 1);
		
		player.getPacketSender().sendTabInterface(0,
				weapon.getInterfaceId());
		player.getPacketSender().sendString(
				weapon.getNameLineId(), ItemDefinition.forId(item).getName());
		player.getCombat().setWeapon(weapon);
		CombatSpecial.assign(player);
		CombatSpecial.updateBar(player);

		for (FightType type : weapon.getFightType()) {
			if (type.getStyle() == player.getCombat().getFightType().getStyle()) {
				player.getCombat().setFightType(type);
				player.getPacketSender().sendConfig(player.getCombat().getFightType().getParentId(), player.getCombat().getFightType().getChildId());
				return;
			}
		}

		player.getCombat().setFightType(player.getCombat().getWeapon().getFightType()[0]);
		player.getPacketSender().sendConfig(player.getCombat().getFightType().getParentId(), player.getCombat().getFightType().getChildId());
	}

	public static boolean changeCombatSettings(Player player, int button) {
		switch(button) {
		case 1772: // shortbow & longbow
			if (player.getCombat().getWeapon() == WeaponInterface.SHORTBOW) {
				player.getCombat().setFightType(FightType.SHORTBOW_ACCURATE);
			} else if (player.getCombat().getWeapon() == WeaponInterface.LONGBOW) {
				player.getCombat().setFightType(FightType.LONGBOW_ACCURATE);
			} else if (player.getCombat().getWeapon() == WeaponInterface.CROSSBOW) {
				player.getCombat().setFightType(FightType.CROSSBOW_ACCURATE);
			}
			return true;
		case 1771:
			if (player.getCombat().getWeapon() == WeaponInterface.SHORTBOW) {
				player.getCombat().setFightType(FightType.SHORTBOW_RAPID);
			} else if (player.getCombat().getWeapon() == WeaponInterface.LONGBOW) {
				player.getCombat().setFightType(FightType.LONGBOW_RAPID);
			} else if (player.getCombat().getWeapon() == WeaponInterface.CROSSBOW) {
				player.getCombat().setFightType(FightType.CROSSBOW_RAPID);
			}
			return true;
		case 1770:
			if (player.getCombat().getWeapon() == WeaponInterface.SHORTBOW) {
				player.getCombat().setFightType(FightType.SHORTBOW_LONGRANGE);
			} else if (player.getCombat().getWeapon() == WeaponInterface.LONGBOW) {
				player.getCombat().setFightType(FightType.LONGBOW_LONGRANGE);
			} else if (player.getCombat().getWeapon() == WeaponInterface.CROSSBOW) {
				player.getCombat().setFightType(FightType.CROSSBOW_LONGRANGE);
			}
			return true;
		case 2282: // dagger & sword
			if (player.getCombat().getWeapon() == WeaponInterface.DAGGER) {
				player.getCombat().setFightType(FightType.DAGGER_STAB);
			} else if (player.getCombat().getWeapon() == WeaponInterface.SWORD) {
				player.getCombat().setFightType(FightType.SWORD_STAB);
			}
			return true;
		case 2285:
			if (player.getCombat().getWeapon() == WeaponInterface.DAGGER) {
				player.getCombat().setFightType(FightType.DAGGER_LUNGE);
			} else if (player.getCombat().getWeapon() == WeaponInterface.SWORD) {
				player.getCombat().setFightType(FightType.SWORD_LUNGE);
			}
			return true;
		case 2284:
			if (player.getCombat().getWeapon() == WeaponInterface.DAGGER) {
				player.getCombat().setFightType(FightType.DAGGER_SLASH);
			} else if (player.getCombat().getWeapon() == WeaponInterface.SWORD) {
				player.getCombat().setFightType(FightType.SWORD_SLASH);
			}
			return true;
		case 2283:
			if (player.getCombat().getWeapon() == WeaponInterface.DAGGER) {
				player.getCombat().setFightType(FightType.DAGGER_BLOCK);
			} else if (player.getCombat().getWeapon() == WeaponInterface.SWORD) {
				player.getCombat().setFightType(FightType.SWORD_BLOCK);
			}
			return true;
		case 2429: // scimitar & longsword
			if (player.getCombat().getWeapon() == WeaponInterface.SCIMITAR) {
				player.getCombat().setFightType(FightType.SCIMITAR_CHOP);
			} else if (player.getCombat().getWeapon() == WeaponInterface.LONGSWORD) {
				player.getCombat().setFightType(FightType.LONGSWORD_CHOP);
			}
			return true;
		case 2432:
			if (player.getCombat().getWeapon() == WeaponInterface.SCIMITAR) {
				player.getCombat().setFightType(FightType.SCIMITAR_SLASH);
			} else if (player.getCombat().getWeapon() == WeaponInterface.LONGSWORD) {
				player.getCombat().setFightType(FightType.LONGSWORD_SLASH);
			}
			return true;
		case 2431:
			if (player.getCombat().getWeapon() == WeaponInterface.SCIMITAR) {
				player.getCombat().setFightType(FightType.SCIMITAR_LUNGE);
			} else if (player.getCombat().getWeapon() == WeaponInterface.LONGSWORD) {
				player.getCombat().setFightType(FightType.LONGSWORD_LUNGE);
			}
			return true;
		case 2430:
			if (player.getCombat().getWeapon() == WeaponInterface.SCIMITAR) {
				player.getCombat().setFightType(FightType.SCIMITAR_BLOCK);
			} else if (player.getCombat().getWeapon() == WeaponInterface.LONGSWORD) {
				player.getCombat().setFightType(FightType.LONGSWORD_BLOCK);
			}
			return true;
		case 3802: // mace
			player.getCombat().setFightType(FightType.MACE_POUND);
			return true;
		case 3805:
			player.getCombat().setFightType(FightType.MACE_PUMMEL);
			return true;
		case 3804:
			player.getCombat().setFightType(FightType.MACE_SPIKE);
			return true;
		case 3803:
			player.getCombat().setFightType(FightType.MACE_BLOCK);
			return true;
		case 4454: // knife, thrownaxe, dart & javelin
			if (player.getCombat().getWeapon() == WeaponInterface.KNIFE) {
				player.getCombat().setFightType(FightType.KNIFE_ACCURATE);
			} else if (player.getCombat().getWeapon() == WeaponInterface.THROWNAXE) {
				player.getCombat().setFightType(FightType.THROWNAXE_ACCURATE);
			} else if (player.getCombat().getWeapon() == WeaponInterface.DART) {
				player.getCombat().setFightType(FightType.DART_ACCURATE);
			} else if (player.getCombat().getWeapon() == WeaponInterface.JAVELIN) {
				player.getCombat().setFightType(FightType.JAVELIN_ACCURATE);
			}
			return true;
		case 4453:
			if (player.getCombat().getWeapon() == WeaponInterface.KNIFE) {
				player.getCombat().setFightType(FightType.KNIFE_RAPID);
			} else if (player.getCombat().getWeapon() == WeaponInterface.THROWNAXE) {
				player.getCombat().setFightType(FightType.THROWNAXE_RAPID);
			} else if (player.getCombat().getWeapon() == WeaponInterface.DART) {
				player.getCombat().setFightType(FightType.DART_RAPID);
			} else if (player.getCombat().getWeapon() == WeaponInterface.JAVELIN) {
				player.getCombat().setFightType(FightType.JAVELIN_RAPID);
			}
			return true;
		case 4452:
			if (player.getCombat().getWeapon() == WeaponInterface.KNIFE) {
				player.getCombat().setFightType(FightType.KNIFE_LONGRANGE);
			} else if (player.getCombat().getWeapon() == WeaponInterface.THROWNAXE) {
				player.getCombat().setFightType(FightType.THROWNAXE_LONGRANGE);
			} else if (player.getCombat().getWeapon() == WeaponInterface.DART) {
				player.getCombat().setFightType(FightType.DART_LONGRANGE);
			} else if (player.getCombat().getWeapon() == WeaponInterface.JAVELIN) {
				player.getCombat().setFightType(FightType.JAVELIN_LONGRANGE);
			}
			return true;
		case 4685: // spear
			player.getCombat().setFightType(FightType.SPEAR_LUNGE);
			return true;
		case 4688:
			player.getCombat().setFightType(FightType.SPEAR_SWIPE);
			return true;
		case 4687:
			player.getCombat().setFightType(FightType.SPEAR_POUND);
			return true;
		case 4686:
			player.getCombat().setFightType(FightType.SPEAR_BLOCK);
			return true;
		case 4711: // 2h sword
			player.getCombat().setFightType(FightType.TWOHANDEDSWORD_CHOP);
			return true;
		case 4714:
			player.getCombat().setFightType(FightType.TWOHANDEDSWORD_SLASH);
			return true;
		case 4713:
			player.getCombat().setFightType(FightType.TWOHANDEDSWORD_SMASH);
			return true;
		case 4712:
			player.getCombat().setFightType(FightType.TWOHANDEDSWORD_BLOCK);
			return true;
		case 5576: // pickaxe
			player.getCombat().setFightType(FightType.PICKAXE_SPIKE);
			return true;
		case 5579:
			player.getCombat().setFightType(FightType.PICKAXE_IMPALE);
			return true;
		case 5578:
			player.getCombat().setFightType(FightType.PICKAXE_SMASH);
			return true;
		case 5577:
			player.getCombat().setFightType(FightType.PICKAXE_BLOCK);
			return true;
		case 7768: // claws
			player.getCombat().setFightType(FightType.CLAWS_CHOP);
			return true;
		case 7771:
			player.getCombat().setFightType(FightType.CLAWS_SLASH);
			return true;
		case 7770:
			player.getCombat().setFightType(FightType.CLAWS_LUNGE);
			return true;
		case 7769:
			player.getCombat().setFightType(FightType.CLAWS_BLOCK);
			return true;
		case 8466: // halberd
			player.getCombat().setFightType(FightType.HALBERD_JAB);
			return true;
		case 8468:
			player.getCombat().setFightType(FightType.HALBERD_SWIPE);
			return true;
		case 8467:
			player.getCombat().setFightType(FightType.HALBERD_FEND);
			return true;
		case 5861: // unarmed
			player.getCombat().setFightType(FightType.UNARMED_BLOCK);
			return true;
		case 5862:
			player.getCombat().setFightType(FightType.UNARMED_KICK);
			return true;
		case 5860:
			player.getCombat().setFightType(FightType.UNARMED_PUNCH);
			return true;
		case 12298: // whip
			player.getCombat().setFightType(FightType.WHIP_FLICK);
			return true;
		case 12297:
			player.getCombat().setFightType(FightType.WHIP_LASH);
			return true;
		case 12296:
			player.getCombat().setFightType(FightType.WHIP_DEFLECT);
			return true;
		case 336: // staff
			player.getCombat().setFightType(FightType.STAFF_BASH);
			return true;
		case 335:
			player.getCombat().setFightType(FightType.STAFF_POUND);
			return true;
		case 334:
			player.getCombat().setFightType(FightType.STAFF_FOCUS);
			return true;
		case 433: // warhammer
			if(player.getCombat().getWeapon() == WeaponInterface.GRANITE_MAUL) {
				player.getCombat().setFightType(FightType.GRANITE_MAUL_POUND);
			} else if(player.getCombat().getWeapon() == WeaponInterface.WARHAMMER) {
				player.getCombat().setFightType(FightType.WARHAMMER_POUND);
			}
			return true;
		case 432:
			if(player.getCombat().getWeapon() == WeaponInterface.GRANITE_MAUL) {
				player.getCombat().setFightType(FightType.GRANITE_MAUL_PUMMEL);
			} else if(player.getCombat().getWeapon() == WeaponInterface.WARHAMMER) {
				player.getCombat().setFightType(FightType.WARHAMMER_PUMMEL);
			}
			return true;
		case 431:
			if(player.getCombat().getWeapon() == WeaponInterface.GRANITE_MAUL) {
				player.getCombat().setFightType(FightType.GRANITE_MAUL_BLOCK);
			} else if(player.getCombat().getWeapon() == WeaponInterface.WARHAMMER) {
				player.getCombat().setFightType(FightType.WARHAMMER_BLOCK);
			}
			return true;
		case 782: // scythe
			player.getCombat().setFightType(FightType.SCYTHE_REAP);
			return true;
		case 784:
			player.getCombat().setFightType(FightType.SCYTHE_CHOP);
			return true;
		case 785:
			player.getCombat().setFightType(FightType.SCYTHE_JAB);
			return true;
		case 783:
			player.getCombat().setFightType(FightType.SCYTHE_BLOCK);
			return true;
		case 1704: // battle axe
			player.getCombat().setFightType(FightType.BATTLEAXE_CHOP);
			return true;
		case 1707:
			player.getCombat().setFightType(FightType.BATTLEAXE_HACK);
			return true;
		case 1706:
			player.getCombat().setFightType(FightType.BATTLEAXE_SMASH);
			return true;
		case 1705:
			player.getCombat().setFightType(FightType.BATTLEAXE_BLOCK);
			return true;
		case 29138:
		case 29038:
		case 29063:
		case 29113:
		case 29163:
		case 29188:
		case 29213:
		case 29238:
		case 30007:
		case 48023:
		case 33033:
		case 30108:
		case 7473:
		case 7562:
		case 7487:
		case 7788:
		case 8481:
		case 7612:
		case 7587:
		case 7662:
		case 7462:
		case 7548:
		case 7687:
		case 7537:
		case 7623:
		case 12322:
		case 7637:
		case 12311:
		case 155:
			CombatSpecial.activate(player);
			break;
		}
		return false;
	}

	/**
	 * Prepares the dynamic json loader for loading weapon interfaces.
	 * 
	 * @return the dynamic json loader.
	 * @throws Exception
	 *             if any errors occur while preparing for load.
	 */
	public static JsonLoader parseInterfaces() {
		return new JsonLoader() {
			@Override
			public void load(JsonObject reader, Gson builder) {
				int id = reader.get("item-id").getAsInt();
				WeaponInterface animation = builder.fromJson(
						reader.get("interface"), WeaponInterface.class);
				interfaces.put(id, animation);
			}

			@Override
			public String filePath() {
				return GameConstants.DEFINITIONS_DIRECTORY + "weapon_interfaces.json";
			}
		};
	}
}
