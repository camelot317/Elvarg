package com.elvarg.net.packet.impl;

import com.elvarg.GameConstants;
import com.elvarg.cache.impl.definitions.WeaponInterfaces;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.util.Misc;
import com.elvarg.world.entity.combat.CombatSpecial;
import com.elvarg.world.entity.combat.magic.Autocasting;
import com.elvarg.world.entity.combat.ranged.RangedData;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Flag;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.Skill;
import com.elvarg.world.model.container.impl.Equipment;
import com.elvarg.world.model.container.impl.Inventory;
import com.elvarg.world.model.equipment.BonusManager;

/**
 * This packet listener manages the equip action a player executes when wielding
 * or equipping an item.
 * 
 * @author relex lawl
 */

public class EquipPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getHitpoints() <= 0)
			return;

		int id = packet.readShort();
		int slot = packet.readShortA();
		int interfaceId = packet.readShortA();

		if (player.getInterfaceId() != Equipment.EQUIPMENT_SCREEN_INTERFACE_ID) {
			player.getPacketSender().sendInterfaceRemoval();
		}

		switch (interfaceId) {
		case Inventory.INTERFACE_ID:
			/*
			 * Making sure slot is valid.
			 */
			if (slot >= 0 && slot <= 28) {
				Item item = player.getInventory().getItems()[slot].copy();
				if (item.getId() != id) {
					return;
				}
				/*
				 * Making sure item exists and that id is consistent.
				 */
				if (item != null && id == item.getId()) {
					for (Skill skill : Skill.values()) {
						if (skill == Skill.CONSTRUCTION)
							continue;
						if (item.getDefinition().getRequirement()[skill.ordinal()] > player.getSkillManager()
								.getMaxLevel(skill)) {
							StringBuilder vowel = new StringBuilder();
							if (skill.getName().startsWith("a") || skill.getName().startsWith("e")
									|| skill.getName().startsWith("i") || skill.getName().startsWith("o")
									|| skill.getName().startsWith("u")) {
								vowel.append("an ");
							} else {
								vowel.append("a ");
							}
							player.getPacketSender().sendMessage("You need " + vowel.toString()
									+ Misc.formatText(skill.getName()) + " level of at least "
									+ item.getDefinition().getRequirement()[skill.ordinal()] + " to wear this.");
							return;
						}
					}

					// Check if it's a proper weapon!
					int equipmentSlot = item.getDefinition().getEquipmentSlot();

					// Weapon hasn't been added yet?
					if (equipmentSlot == -1) {
						return;
					}

					Item equipItem = player.getEquipment().forSlot(equipmentSlot).copy();
					/*
					 * if(player.getLocation() == Location.DUEL_ARENA) { for(int
					 * i = 10; i < player.getDueling().selectedDuelRules.length;
					 * i++) { if(player.getDueling().selectedDuelRules[i]) {
					 * DuelRule duelRule = DuelRule.forId(i); if(equipmentSlot
					 * == duelRule.getEquipmentSlot() || duelRule ==
					 * Dueling.DuelRule.NO_SHIELD &&
					 * item.getDefinition().isTwoHanded()) {
					 * player.getPacketSender().
					 * sendMessage("The rules that were set do not allow this item to be equipped."
					 * ); return; } } }
					 * if(player.getDueling().selectedDuelRules[DuelRule.
					 * LOCK_WEAPON.ordinal()]) { if(equipmentSlot ==
					 * Equipment.WEAPON_SLOT ||
					 * item.getDefinition().isTwoHanded()) {
					 * player.getPacketSender().
					 * sendMessage("Weapons have been locked during this duel!"
					 * ); return; } } }
					 */
					if (equipItem.getDefinition().isStackable() && equipItem.getId() == item.getId()) {
						int amount = equipItem.getAmount() + item.getAmount() <= Integer.MAX_VALUE
								? equipItem.getAmount() + item.getAmount() : Integer.MAX_VALUE;
						player.getInventory().delete(item, false);
						player.getEquipment().getItems()[equipmentSlot].setAmount(amount);
						equipItem.setAmount(amount);
					} else {
						if (item.getDefinition().isDoubleHanded()
								&& item.getDefinition().getEquipmentSlot() == Equipment.WEAPON_SLOT) {
							int slotsRequired = player.getEquipment().isSlotOccupied(Equipment.SHIELD_SLOT)
									&& player.getEquipment().isSlotOccupied(Equipment.WEAPON_SLOT) ? 1 : 0;
							if (player.getInventory().getFreeSlots() < slotsRequired) {
								player.getInventory().full();
								return;
							}

							Item shield = player.getEquipment().getItems()[Equipment.SHIELD_SLOT];
							Item weapon = player.getEquipment().getItems()[Equipment.WEAPON_SLOT];
							player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(-1, 0));
							player.getInventory().delete(item);
							player.getEquipment().set(equipmentSlot, item);
							if (shield.getId() != -1) {
								player.getInventory().add(shield);
							}
							if (weapon.getId() != -1) {
								player.getInventory().add(weapon);
							}

						} else if (equipmentSlot == Equipment.SHIELD_SLOT
								&& player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getDefinition()
										.isDoubleHanded()) {
							player.getInventory().setItem(slot,
									player.getEquipment().getItems()[Equipment.WEAPON_SLOT]);
							player.getEquipment().setItem(Equipment.WEAPON_SLOT, new Item(-1));
							player.getEquipment().setItem(Equipment.SHIELD_SLOT, item);
							resetWeapon(player);
						} else {
							if (item.getDefinition().getEquipmentSlot() == equipItem.getDefinition().getEquipmentSlot()
									&& equipItem.getId() != -1) {
								if (player.getInventory().contains(equipItem.getId())) {
									player.getInventory().delete(item, false);
									player.getInventory().add(equipItem, false);
								} else
									player.getInventory().setItem(slot, equipItem);
								player.getEquipment().setItem(equipmentSlot, item);
							} else {
								player.getInventory().setItem(slot, new Item(-1, 0));
								player.getEquipment().setItem(item.getDefinition().getEquipmentSlot(), item);
							}
						}
					}
					if (equipmentSlot == Equipment.WEAPON_SLOT) {
						resetWeapon(player);
					} else if (equipmentSlot == Equipment.RING_SLOT && item.getId() == 2570) {
						player.getPacketSender()
								.sendMessage(
										"<img=10> <col=996633>Warning! The Ring of Life special effect does not work in the Wilderness or")
								.sendMessage("<col=996633> Duel Arena.");
					}

					// Check if ranged update is needed!
					if (equipmentSlot == Equipment.AMMUNITION_SLOT || equipmentSlot == Equipment.WEAPON_SLOT) {
						RangedData.updateDataFor(player);
					}

					if (player.getEquipment().get(Equipment.WEAPON_SLOT).getId() != 4153) {
						player.getCombat().reset();
					}

					// player.setCastSpell(null);
					BonusManager.update(player);

					player.getEquipment().refreshItems();

					if (GameConstants.QUEUE_SWITCHING_REFRESH) {
						player.setUpdateInventory(true);
					} else {
						player.getInventory().refreshItems();
					}

					player.getUpdateFlag().flag(Flag.APPEARANCE);
					// Sounds.sendSound(player, Sound.EQUIP_ITEM);

				}
			}
			break;
		}
	}

	public static void resetWeapon(Player player) {
		WeaponInterfaces.assign(player);
		player.setSpecialActivated(false);
		CombatSpecial.updateBar(player);
		if (player.getCombat().getAutocastSpell() != null) {
			Autocasting.setAutocast(player, null);
			player.getPacketSender().sendMessage("Autocast spell cleared.");
		}
	}
}