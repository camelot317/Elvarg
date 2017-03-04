package com.elvarg.net.packet.impl;

import com.elvarg.engine.task.impl.WalkToTask;
import com.elvarg.engine.task.impl.WalkToTask.FinalizedMovementTask;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketConstants;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.World;
import com.elvarg.world.entity.combat.magic.CombatSpell;
import com.elvarg.world.entity.combat.magic.CombatSpells;
import com.elvarg.world.entity.combat.pvp.BountyHunter;
import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Flag;
import com.elvarg.world.model.PlayerRights;
import com.elvarg.world.model.Skill;
import com.elvarg.world.model.container.impl.Shop;
import com.elvarg.world.model.dialogue.DialogueManager;
import com.elvarg.world.model.dialogue.DialogueOptions;

public class NPCOptionPacketListener implements PacketListener {

	private static void firstClick(Player player, Packet packet) {
		int index = packet.readLEShort();
		if (index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		player.setEntityInteraction(npc);
		if (player.getRights() == PlayerRights.ADMINISTRATOR)
			player.getPacketSender().sendMessage("First click npc id: " + npc.getId());
		player.setWalkToTask(new WalkToTask(player, npc.getPosition(), npc.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				switch (npc.getId()) {

				case SHOP_KEEPER:
					Shop.open(player, 0);
					break;

				case JEWELRY_TRADER:
					Shop.open(player, 7);
					break;

				case MAKE_OVER_MAGE:
					player.getPacketSender().sendInterfaceRemoval().sendInterface(3559);
					player.getAppearance().setCanChangeAppearance(true);
					break;

				case SURGEON_GENERAL_TIFANI:
					Shop.open(player, 8);
					break;

				case PKER:
					Shop.open(player, 9);
					break;

				case WARRIOR:
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption1(Player player) {
							Shop.open(player, 5);
						}

						@Override
						public void handleOption2(Player player) {
							Shop.open(player, 6);
						}

						@Override
						public void handleOption3(Player player) {
							player.getPacketSender().sendInterfaceRemoval();
						}
					});
					DialogueManager.start(player, 8);
					break;

				case MAGE:
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption1(Player player) {
							Shop.open(player, 2);
						}

						@Override
						public void handleOption2(Player player) {
							Shop.open(player, 1);
						}

						@Override
						public void handleOption3(Player player) {
							player.getPacketSender().sendInterfaceRemoval();
						}
					});
					DialogueManager.start(player, 6);
					break;

				case RANGER:
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption1(Player player) {
							Shop.open(player, 4);
						}

						@Override
						public void handleOption2(Player player) {
							Shop.open(player, 3);
						}

						@Override
						public void handleOption3(Player player) {
							player.getPacketSender().sendInterfaceRemoval();
						}
					});
					DialogueManager.start(player, 7);
					break;

				case EMBLEM_TRADER:

					// Set dialogue options
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption1(Player player) {
							// Open pkp shop
						}

						@Override
						public void handleOption2(Player player) {
							// Sell emblems option
							if (!BountyHunter.exchangeEmblems(player)) {
								DialogueManager.start(player, 5);
							} else {
								DialogueManager.start(player, 4);
							}
						}

						@Override
						public void handleOption3(Player player) {
							// Skull me option
							if (player.getSkullTimer() > 0) {
								DialogueManager.start(player, 3);
							} else {
								DialogueManager.start(player, 2);
								player.getPacketSender().sendMessage("@red@You have been skulled!");
								player.setSkullTimer(600); // 6 minutes exactly.
								player.getUpdateFlag().flag(Flag.APPEARANCE);
							}
						}

						@Override
						public void handleOption4(Player player) {
							player.getPacketSender().sendInterfaceRemoval();
						}
					});

					// And then start dialogue
					DialogueManager.start(player, 0);

					break;

				}
				npc.setPositionToFace(player.getPosition());
				player.setPositionToFace(npc.getPosition());
			}
		}));
	}

	public void handleSecondClick(Player player, Packet packet) {
		int index = packet.readLEShortA();
		if (index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		player.setEntityInteraction(npc);
		final int npcId = npc.getId();
		if (player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Second click npc id: " + npcId);
		player.setWalkToTask(new WalkToTask(player, npc.getPosition(), npc.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				switch (npc.getId()) {

				case MAGE:
					Shop.open(player, 2);
					break;

				case RANGER:
					Shop.open(player, 4);
					break;

				case WARRIOR:
					Shop.open(player, 5);
					break;

				case SURGEON_GENERAL_TIFANI:
					player.getSkillManager().setCurrentLevel(Skill.HITPOINTS,
							player.getSkillManager().getMaxLevel(Skill.HITPOINTS));
					player.getPacketSender().sendMessage("You've been healed by the surgeon.");
					player.getPacketSender().sendInterfaceRemoval();
					break;

				case EMBLEM_TRADER:

					break;

				}
				npc.setPositionToFace(player.getPosition());
				player.setPositionToFace(npc.getPosition());
			}
		}));
	}

	public void handleThirdClick(Player player, Packet packet) {
		int index = packet.readShort();
		if (index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		player.setEntityInteraction(npc).setPositionToFace(npc.getPosition().copy());
		npc.setPositionToFace(player.getPosition());
		if (player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Third click npc id: " + npc.getId());
		player.setWalkToTask(new WalkToTask(player, npc.getPosition(), npc.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				switch (npc.getId()) {

				case MAGE:
					Shop.open(player, 1);
					break;

				case RANGER:
					Shop.open(player, 3);
					break;

				case WARRIOR:
					Shop.open(player, 6);
					break;

				case EMBLEM_TRADER:
					if (!BountyHunter.exchangeEmblems(player)) {
						DialogueManager.start(player, 5);
					} else {
						DialogueManager.start(player, 4);
					}
					break;
				}

				npc.setPositionToFace(player.getPosition());
				player.setPositionToFace(npc.getPosition());
			}
		}));
	}

	public void handleFourthClick(Player player, Packet packet) {
		int index = packet.readLEShort();
		if (index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		player.setEntityInteraction(npc);
		if (player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Fourth click npc id: " + npc.getId());
		player.setWalkToTask(new WalkToTask(player, npc.getPosition(), npc.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				switch (npc.getId()) {
				case EMBLEM_TRADER:
					if (player.getSkullTimer() > 0) {
						DialogueManager.start(player, 3);
					} else {
						DialogueManager.start(player, 2);
						player.getPacketSender().sendMessage("@red@You have been skulled!");
						player.setSkullTimer(600); // 6 minutes exactly.
						player.getUpdateFlag().flag(Flag.APPEARANCE);
					}
					break;
				}
				npc.setPositionToFace(player.getPosition());
				player.setPositionToFace(npc.getPosition());
			}
		}));
	}

	private static void attackNPC(Player player, Packet packet) {
		int index = packet.readShortA();
		if (index < 0 || index > World.getNpcs().capacity())
			return;
		final NPC interact = World.getNpcs().get(index);

		if (interact == null || interact.getDefinition() == null) {
			return;
		}

		if (!interact.getDefinition().isAttackable()) {
			return;
		}

		if (interact == null || interact.getHitpoints() <= 0) {
			player.getMovementQueue().reset();
			return;
		}

		player.getCombat().attack(interact);
	}

	private static void mageNpc(Player player, Packet packet) {
		int npcIndex = packet.readLEShortA();
		int spellId = packet.readShortA();

		if (npcIndex < 0 || spellId < 0 || npcIndex > World.getNpcs().capacity()) {
			return;
		}

		final NPC interact = World.getNpcs().get(npcIndex);

		if (interact == null || interact.getDefinition() == null) {
			return;
		}

		if (!interact.getDefinition().isAttackable()) {
			return;
		}

		if (interact == null || interact.getHitpoints() <= 0) {
			player.getMovementQueue().reset();
			return;
		}

		CombatSpell spell = CombatSpells.getCombatSpell(spellId);

		if (spell == null) {
			player.getMovementQueue().reset();
			return;
		}

		player.setPositionToFace(interact.getPosition());
		player.getCombat().setCastSpell(spell);

		player.getCombat().attack(interact);
	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.busy()) {
			return;
		}
		switch (packet.getOpcode()) {
		case PacketConstants.ATTACK_NPC_OPCODE:
			attackNPC(player, packet);
			break;
		case PacketConstants.FIRST_CLICK_OPCODE:
			firstClick(player, packet);
			break;
		case PacketConstants.SECOND_CLICK_OPCODE:
			handleSecondClick(player, packet);
			break;
		case PacketConstants.THIRD_CLICK_OPCODE:
			handleThirdClick(player, packet);
			break;
		case PacketConstants.FOURTH_CLICK_OPCODE:
			handleFourthClick(player, packet);
			break;
		case PacketConstants.MAGE_NPC_OPCODE:
			mageNpc(player, packet);
			break;
		}
	}

	/** NPCS **/
	private static final int EMBLEM_TRADER = 315;
	private static final int WARRIOR = 1158;
	private static final int RANGER = 1576;
	private static final int MAGE = 3309;
	private static final int MAKE_OVER_MAGE = 1306;
	private static final int SHOP_KEEPER = 506;
	private static final int JEWELRY_TRADER = 526;
	private static final int SURGEON_GENERAL_TIFANI = 3343;
	private static final int PKER = 2660;
}
