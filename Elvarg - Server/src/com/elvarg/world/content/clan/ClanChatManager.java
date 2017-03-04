package com.elvarg.world.content.clan;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;

import com.elvarg.util.Misc;
import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.dialogue.DialogueManager;
import com.elvarg.world.model.dialogue.DialogueOptions;
import com.elvarg.world.model.syntax.impl.JoinClanChat;

/**
 * 100% Runescape clanchat system.
 * 
 * @author Gabriel Hannason
 */
public class ClanChatManager {

	private static final String FILE_DIRECTORY = "./data/saves/clans/";

	private static ClanChat[] clans = new ClanChat[3000];

	public static ClanChat[] getClans() {
		return clans;
	}

	public static ClanChat getClanChat(int index) {
		return clans[index];
	}

	public static ClanChat getClanChatChannel(Player player) {
		for (ClanChat clan : clans) {
			if (clan == null || clan.getOwnerName() == null)
				continue;
			if (clan.getOwnerName().equals(player.getUsername())) {
				return clan;
			}
		}
		return null;
	}

	public static void init() {
		try {
			for (File file : (new File(FILE_DIRECTORY)).listFiles()) {
				if (!file.exists())
					continue;
				DataInputStream input = new DataInputStream(new FileInputStream(file));
				String name = input.readUTF();
				String owner = input.readUTF();
				int index = input.readShort();
				ClanChat clan = new ClanChat(owner, name, index);
				clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_ENTER, ClanChatRank.forId(input.read()));
				clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_KICK, ClanChatRank.forId(input.read()));
				clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_TALK, ClanChatRank.forId(input.read()));
				int totalRanks = input.readShort();
				for (int i = 0; i < totalRanks; i++) {
					clan.getRankedNames().put(input.readUTF(), ClanChatRank.forId(input.read()));
				}
				int totalBans = input.readShort();
				for (int i = 0; i < totalBans; i++) {
					clan.addBannedName(input.readUTF());
				}
				clans[index] = clan;
				input.close();
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public static void writeFile(ClanChat clan) {
		try {
			File file = new File(FILE_DIRECTORY + clan.getName());
			if (file.exists())
				file.createNewFile();
			DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
			output.writeUTF(clan.getName());
			output.writeUTF(clan.getOwnerName());
			output.writeShort(clan.getIndex());
			output.write(clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER] != null
					? clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER].ordinal() : -1);
			output.write(clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK] != null
					? clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK].ordinal() : -1);
			output.write(clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK] != null
					? clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK].ordinal() : -1);
			output.writeShort(clan.getRankedNames().size());
			for (Entry<String, ClanChatRank> iterator : clan.getRankedNames().entrySet()) {
				String name = iterator.getKey();
				int rank = iterator.getValue().ordinal();
				output.writeUTF(name);
				output.write(rank);
			}
			output.writeShort(clan.getBannedNames().size());
			for (BannedMember ban : clan.getBannedNames()) {
				output.writeUTF(ban.getName());
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		for (ClanChat clan : clans) {
			if (clan != null) {
				writeFile(clan);
			}
		}
	}

	public static void createClan(Player player) {
		player.getPacketSender().sendInterfaceRemoval();
		if (getClanChatChannel(player) != null) {
			player.getPacketSender().sendMessage("You have already created a clanchat channel.");
			return;
		}
		File file = new File(FILE_DIRECTORY + player.getUsername());
		if (file.exists())
			file.delete();
		ClanChat createdCc = create(player);
		if (createdCc != null) {
			if (player.getCurrentClanChat() == null) {
				join(player, createdCc);
			}
			player.getPacketSender().sendMessage(
					"You now have a clanchat channel. To enter the chat, simply use your name as keyword.");
		}
	}

	public static void deleteClan(Player player) {
		player.getPacketSender().sendInterfaceRemoval();
		if (player.getCurrentClanChat() != null) {
			player.getPacketSender()
					.sendMessage("Please leave the clanchat channel you are currently in before doing this.");
			return;
		}
		if (getClanChatChannel(player) == null) {
			player.getPacketSender().sendMessage("You have not created a clanchat channel yet.");
			return;
		}
		delete(player);
	}

	public static ClanChat create(Player player) {
		File file = new File(FILE_DIRECTORY + player.getUsername());
		if (file.exists()) {
			player.getPacketSender().sendMessage("Your clan channel is already public!");
			return null;
		}
		int index = getIndex();
		if (index == -1) { // Too many clans
			player.getPacketSender().sendMessage("An error occured! Please contact an administrator and report this.");
			return null;
		}

		clans[index] = new ClanChat(player, player.getUsername(), index);
		clans[index].getRankedNames().put(player.getUsername(), ClanChatRank.OWNER);

		if (player.getCurrentClanChat() == null) {
			join(player, clans[index]);
		} else {
			player.getPacketSender()
					.sendMessage("You have created a clan chat channel. To join it, simply enter your username as")
					.sendMessage("keyword.");
		}

		writeFile(clans[index]);
		return clans[index];
	}

	public static void join(Player player, String channel) {
		if (channel == null || channel.equals("") || channel.equals("null")) {
			return;
		}
		if (player.getCurrentClanChat() != null) {
			player.getPacketSender().sendMessage("You are already in a clan channel.");
			return;
		}
		channel = channel.toLowerCase();
		for (ClanChat clan : clans) {
			if (clan == null) {
				continue;
			}
			if (clan.getName().toLowerCase().equals(channel) || clan.getOwnerName().toLowerCase().equals(channel)) {

				join(player, clan);
				return;
			}
		}

		player.getPacketSender().sendMessage("That channel does not exist.");
	}

	public static void updateList(ClanChat clan) {
		Collections.sort(clan.getMembers(), new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				ClanChatRank rank1 = clan.getRank(o1);
				ClanChatRank rank2 = clan.getRank(o2);
				if (rank1 == null && rank2 == null) {
					return 1;
				}
				if (rank1 == null && rank2 != null) {
					return 1;
				} else if (rank1 != null && rank2 == null) {
					return -1;
				}
				if (rank1.ordinal() == rank2.ordinal()) {
					return 1;
				}
				if (rank1 == ClanChatRank.OWNER) {
					return -1;
				} else if (rank2 == ClanChatRank.OWNER) {
					return 1;
				}
				if (rank1.ordinal() > rank2.ordinal()) {
					return -1;
				}
				return 1;
			}
		});
		for (Player member : clan.getMembers()) {
			if (member != null) {
				int childId = 37144;
				for (Player others : clan.getMembers()) {
					if (others != null) {
						ClanChatRank rank = clan.getRank(others);

						int image = -1;
						if (rank != null) {
							image = rank.getSpriteId();
						}

						String prefix = image >= 0 ? ("<img=" + (image) + ">") : "";
						member.getPacketSender().sendString(childId, prefix + others.getUsername());
						childId++;
					}
				}
				for (int i = childId; i < 37244; i++) {
					member.getPacketSender().sendString(i, "");
				}
				ClanChatRank rank = clan.getRank(member);

				if (rank != null) {
					if (rank == ClanChatRank.OWNER || rank == ClanChatRank.STAFF
							|| clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK] != null && rank
									.ordinal() >= clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK].ordinal()) {

						member.getPacketSender().sendShowClanChatOptions(true);

					} else {
						member.getPacketSender().sendShowClanChatOptions(false);
					}
				} else {
					member.getPacketSender().sendShowClanChatOptions(false);
				}

			}
		}
	}

	public static void sendMessage(Player player, String message) {
		ClanChat clan = player.getCurrentClanChat();
		if (clan == null) {
			player.getPacketSender().sendMessage("You're not in a clanchat channel.");
			return;
		}
		ClanChatRank rank = clan.getRank(player);
		if (clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK] != null) {
			if (rank == null || rank.ordinal() < clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK].ordinal()) {
				player.getPacketSender().sendMessage("You do not have the required rank to speak in this channel.");
				return;
			}
		}
		String bracketColor = "<col=16777215>";
		String clanNameColor = "<col=255>";
		String nameColor = "@bla@";
		String chatColor = "<col=993D00>";
		for (Player memberPlayer : clan.getMembers()) {
			if (memberPlayer != null) {
				if (memberPlayer.getRelations().getIgnoreList().contains(player.getLongUsername()))
					continue;
				int img = player.getRights().ordinal();

				String rankImg = img > 0 ? "<img=" + img + "> " : " ";
				memberPlayer.getPacketSender()
						.sendClanMessage("" + bracketColor + "[" + clanNameColor + clan.getName() + bracketColor + "]"
								+ nameColor + "" + rankImg + "" + Misc.capitalizeWords(player.getUsername()) + ": "
								+ chatColor + Misc.capitalize(message));
			}
		}
	}

	public static void sendMessage(ClanChat clan, String message) {
		for (Player member : clan.getMembers()) {
			if (member != null) {
				member.getPacketSender().sendMessage(message);
			}
		}
	}

	public static void leave(Player player, boolean kicked) {
		final ClanChat clan = player.getCurrentClanChat();
		if (clan == null) {
			return;
		}
		player.getPacketSender().sendString(37139, "Talking in: N/A");
		player.getPacketSender().sendString(37140, "Owner: N/A");
		// player.getPacketSender().sendString(29454, "Lootshare: N/A");
		player.getPacketSender().sendString(37135, "Join Chat");
		player.setCurrentClanChat(null);
		clan.removeMember(player.getUsername());
		for (int i = 37144; i < 37244; i++) {
			player.getPacketSender().sendString(i, "");
		}
		player.getPacketSender().sendShowClanChatOptions(false);
		updateList(clan);
		if (kicked) {
			player.setClanChatName("");
		}
		player.getPacketSender()
				.sendMessage(kicked ? "You have been kicked from the channel." : "You have left the channel.");
	}

	private static void join(Player player, ClanChat clan) {
		if (clan.getOwnerName().equals(player.getUsername())) {
			if (clan.getOwner() == null) {
				clan.setOwner(player);
			}
			clan.giveRank(player, ClanChatRank.OWNER);
		}
		player.getPacketSender().sendMessage("Attempting to join channel...");
		if (clan.getMembers().size() >= 100) {
			player.getPacketSender().sendMessage("This clan channel is currently full.");
			return;
		}
		if (clan.isBanned(player.getUsername())) {
			player.getPacketSender()
					.sendMessage("You're currently banned from using this channel. Bans expire after 30 minutes.");
			return;
		}
		ClanChatRank rank = clan.getRank(player);

		// Give or Remove friends rank if needed.
		if (rank == null) {
			if (clan.getOwner() != null && clan.getOwner().getRelations().isFriendWith(player.getUsername())) {
				clan.giveRank(player, ClanChatRank.FRIEND);
			}
		} else {
			if (rank == ClanChatRank.FRIEND && clan.getOwner() != null
					&& !clan.getOwner().getRelations().isFriendWith(player.getUsername())) {
				clan.giveRank(player, null);
			}
		}

		// Give or Remove staff rank if needed.
		/*
		 * if (player.getRights().isStaff()) { if(rank == null || rank !=
		 * ClanChatRank.OWNER) { rank = ClanChatRank.STAFF;
		 * clan.giveRank(player, ClanChatRank.STAFF); } } else { if(rank != null
		 * && rank == ClanChatRank.STAFF) { clan.giveRank(player, null); } }
		 */

		if (clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER] != null) {
			if (rank == null || clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER].ordinal() > rank.ordinal()) {
				player.getPacketSender().sendMessage("Your rank is not high enough to enter this channel.");
				return;
			}
		}
		player.setCurrentClanChat(clan);
		player.setClanChatName(clan.getName());
		String clanName = Misc.capitalizeWords(clan.getName());
		clan.addMember(player);
		player.getPacketSender().sendString(37139, "Talking in: @whi@" + clanName);
		player.getPacketSender().sendString(37140, "Owner: " + Misc.capitalizeWords(clan.getOwnerName()));
		player.getPacketSender().sendString(37135, "Leave Chat");

		// player.getPacketSender().sendString(29454, "Lootshare:
		// "+getLootshareStatus(clan));
		player.getPacketSender().sendMessage("Now talking in " + clan.getOwnerName() + "'s channel.");
		player.getPacketSender().sendMessage("To talk start each line of chat with the / symbol.");
		updateList(clan);
	}

	public static void delete(Player player) {
		ClanChat clan = getClanChatChannel(player);
		File file = new File(FILE_DIRECTORY + clan.getName());
		for (Player member : clan.getMembers()) {
			if (member != null) {
				leave(member, true);
			}
		}
		if (player.getClanChatName().equalsIgnoreCase(clan.getName())) {
			player.setClanChatName("");
		}
		player.getPacketSender().sendMessage("Your clanchat channel was successfully deleted.");
		clans[clan.getIndex()] = null;
		file.delete();
	}

	public static void setName(Player player, String newName) {
		final ClanChat clan = getClanChatChannel(player);
		if (clan == null) {
			player.getPacketSender().sendMessage("You need to have a clan channel to do this.");
			return;
		}
		if (newName.length() == 0)
			return;
		if (newName.length() > 12)
			newName = newName.substring(0, 11);
		if (new File(FILE_DIRECTORY + newName).exists()) {
			player.getPacketSender().sendMessage("That clanchat name is already taken.");
			return;
		}
		if (clan.getLastAction().elapsed(5000) || player.getRights().isStaff()) {
			new File(FILE_DIRECTORY + clan.getName()).delete();
			clan.setName(Misc.capitalizeWords(newName));
			for (Player member : clan.getMembers()) {
				if (member == null)
					continue;
				member.setClanChatName(clan.getName());
				member.getPacketSender().sendString(37139, "Talking in: @whi@" + clan.getName());
			}
			clanChatSetupInterface(player, false);
			writeFile(clan);
			clan.getLastAction().reset();
		} else {
			player.getPacketSender().sendMessage("You need to wait a few seconds between every clanchat action.");
		}
	}

	public static void kick(Player player, Player target) {
		ClanChat clan = player.getCurrentClanChat();
		if (clan == null) {
			player.getPacketSender().sendMessage("You're not in a clan channel.");
			return;
		}
		final ClanChatRank rank = clan.getRank(player);
		if (rank == null
				|| rank != ClanChatRank.STAFF && clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK] != null
						&& rank.ordinal() < clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK].ordinal()) {
			player.getPacketSender().sendMessage("You do not have the required rank to kick this player.");
			return;
		}
		for (Player member : clan.getMembers()) {
			if (member != null && member.equals(target)) {
				ClanChatRank memberRank = clan.getRank(member);
				if (memberRank != null) {
					if (memberRank == ClanChatRank.STAFF) {
						player.getPacketSender().sendMessage("That player cannot be kicked.");
						break;
					}
					if (rank.ordinal() < memberRank.ordinal()) {
						player.getPacketSender()
								.sendMessage("You cannot kick a player who has a higher rank than you!");
						break;
					}
				}
				clan.addBannedName(member.getUsername());
				leave(member, true);
				sendMessage(player.getCurrentClanChat(),
						"<col=16777215>[<col=255>" + clan.getName() + "<col=16777215>]<col=3300CC> "
								+ member.getUsername() + " has been kicked from the channel by " + player.getUsername()
								+ ".");
				break;
			}
		}
	}

	public static boolean handleButton(Player player, int button, int menuId) {

		// Member options..
		if (button >= 37144 && button < 37244) {
			final int index = button - 37144;

			if (index > 0) {
				if ((player.getCurrentClanChat() == null
						|| !player.getCurrentClanChat().getOwnerName().equals(player.getUsername())) && menuId != 1) {
					player.getPacketSender().sendMessage("Only the clanchat owner can do that.");
					return true;
				}
				Player target = getPlayer(index, player.getCurrentClanChat());
				if (target == null || target.equals(player)) {
					return true;
				}

				switch (menuId) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					ClanChatRank rank = ClanChatRank.forMenuId(menuId);
					ClanChatRank targetRank = player.getCurrentClanChat().getRank(target);
					if (targetRank != null) {
						if (targetRank == rank) {
							player.getPacketSender().sendMessage("That player already has that rank.");
							return true;
						}
						if (targetRank == ClanChatRank.STAFF) {
							player.getPacketSender().sendMessage("That player cannot be promoted or demoted.");
							return true;
						}
					}
					if (player.getCurrentClanChat().getLastAction().elapsed(5000) || player.getRights().isStaff()) {
						player.getCurrentClanChat().giveRank(target, rank);
						updateList(player.getCurrentClanChat());
						sendMessage(player.getCurrentClanChat(),
								"<col=16777215>[<col=255>" + player.getCurrentClanChat().getName()
										+ "<col=16777215>]<col=3300CC> " + target.getUsername()
										+ " has been given the rank: " + Misc.formatText(rank.name().toLowerCase())
										+ ".");
						player.getCurrentClanChat().getLastAction().reset();
					} else {
						player.getPacketSender()
								.sendMessage("You need to wait a few seconds between every clanchat action.");
					}
					break;
				case 6:
					targetRank = player.getCurrentClanChat().getRank(target);
					if (targetRank == null) {
						player.getPacketSender().sendMessage("That player has no rank.");
						return true;
					}
					if (targetRank == ClanChatRank.STAFF) {
						player.getPacketSender().sendMessage("That player cannot be promoted or demoted.");
						return true;
					}
					if (player.getCurrentClanChat().getLastAction().elapsed(5000) || player.getRights().isStaff()) {
						player.getCurrentClanChat().getRankedNames().remove(target.getUsername());
						updateList(player.getCurrentClanChat());
						sendMessage(player.getCurrentClanChat(),
								"<col=16777215>[<col=255>" + player.getCurrentClanChat().getName()
										+ "<col=16777215>]<col=3300CC> " + target.getUsername()
										+ " has been demoted from their rank.");
						player.getCurrentClanChat().getLastAction().reset();
					} else {
						player.getPacketSender()
								.sendMessage("You need to wait a few seconds between every clanchat action.");
					}
					break;
				case 7:
					kick(player, target);
					break;
				}
			}

			return true;
		}

		// Other buttons..
		switch (button) {
		case 37132: // CC Setup

			if (!player.busy()) {
				if (getClanChatChannel(player) == null) {

					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption1(Player player) {
							ClanChatManager.create(player);
							player.getPacketSender().sendInterfaceRemoval();
						}

						@Override
						public void handleOption2(Player player) {
							player.getPacketSender().sendInterfaceRemoval();
						}
					});

					DialogueManager.start(player, 9);

					return true;
				}
			} else {
				player.getPacketSender().sendMessage("You cannot do that right now.");
			}

			return true;

		case 37129: // Join / Leave clan
			if (player.getCurrentClanChat() == null) {
				player.setEnterSyntax(new JoinClanChat());
				player.getPacketSender().sendEnterInputPrompt("Which channel would you like to join?");
			} else {
				leave(player, false);
				player.setClanChatName("");
			}
			return true;
		}
		return false;
	}

	public static boolean dropShareLoot(Player player, NPC npc, Item itemDropped) {
		/*
		 * ClanChat clan = player.getFields().getClanChat(); if (clan != null) {
		 * boolean received = false; List<Player> players =
		 * getPlayersWithinPosition(clan, npc.getPosition()); String green =
		 * "<col=" +
		 * ClanChatMessageColor.GREEN.getRGB()[player.getFields().rgbIndex] +
		 * ">"; if (clan.isItemSharing() && itemDropped.getId() != 995) { Player
		 * rewarded = players.size() > 0 ?
		 * players.get(MathUtils.random(players.size() - 1)) : null; if
		 * (rewarded != null) { rewarded.getPacketSender().sendMessage(green +
		 * "You have received " + itemDropped.getAmount() + "x " +
		 * itemDropped.getDefinition().getName() + "."); received = true; } } if
		 * (clan.isCoinSharing() && itemDropped.getId() == 995) { for (Item drop
		 * : npc.getDrops()) { if ((drop.getDefinition().getValue() *
		 * drop.getAmount()) < 50000) { GroundItem groundItem = new
		 * GroundItem(drop, npc.getPosition().copy());
		 * GameServer.getWorld().register(groundItem, player); continue; } int
		 * amount = (int) (ItemDefinition.forId(drop.getId()).getValue() /
		 * players.size()); Item split = new Item(995, amount); for (Player
		 * member : players) { GroundItem groundItem = new
		 * GroundItem(split.copy(), npc.getPosition().copy());
		 * GameServer.getWorld().register(groundItem, member);
		 * member.getPacketSender().sendMessage(green + "You have received " +
		 * amount + "x " + split.getDefinition().getName() +
		 * " as part of a split drop."); } } } else if(!clan.isItemSharing() &&
		 * !clan.isCoinSharing() || !received) return false; } else return
		 * false;
		 */
		return false;
	}

	public static void toggleLootShare(Player player) {
		final ClanChat clan = player.getCurrentClanChat();
		if (clan == null) {
			player.getPacketSender().sendMessage("You're not in a clan channel.");
			return;
		}
		if (!player.getRights().isStaff()) {
			if (clan.getOwner() == null)
				return;
			if (!clan.getOwner().equals(player)) {
				player.getPacketSender().sendMessage("Only the owner of the channel has the power to do this.");
				return;
			}
		}
		if (clan.getLastAction().elapsed(5000) || player.getRights().isStaff()) {
			clan.setLootShare(!clan.getLootShare());
			sendMessage(clan, "<col=16777215>[<col=255>" + clan.getName() + "<col=16777215>] <col=3300CC>"
					+ player.getUsername() + " has " + (clan.getLootShare() ? "enabled" : "disabled") + " Lootshare.");
			for (Player member : clan.getMembers()) {
				if (member != null) {
					// member.getPacketSender().sendString(29454, "Lootshare:
					// "+getLootshareStatus(clan));
				}
			}
			clan.getLastAction().reset();
		} else {
			player.getPacketSender().sendMessage("You need to wait a few seconds between every clanchat action.");
		}
	}

	private static String getLootshareStatus(ClanChat clan) {
		return clan.getLootShare() ? "@gre@On" : "Off";
	}

	private static int getIndex() {
		for (int i = 0; i < clans.length; i++) {
			if (clans[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public static boolean handleClanChatSetupButton(Player player, int id) {
		if (player.getInterfaceId() == 40172) {
			final ClanChat clan = getClanChatChannel(player);
			if (clan == null) {
				return true;
			}
			switch (id) {
			case -18281:
			case -17529:
			case -17530:
			case -17531:
			case -17532:
			case -17533:
			case -17534:
			case -17535:
			case -17536:
				int l = -17529 - id;
				clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_ENTER, id == -18281 ? null : ClanChatRank.forId(l));
				player.getPacketSender().sendMessage("You have changed your clanchat channel's settings.");
				if (clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER] != null) {
					for (Player member : clan.getMembers()) {
						if (member == null)
							continue;
						ClanChatRank rank = clan.getRank(member);
						if (rank == null
								|| clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER].ordinal() > rank.ordinal()
										&& rank != ClanChatRank.STAFF) {
							member.getPacketSender().sendMessage("Your rank is not high enough to be in this channel.");
							leave(member, false);
							player.getPacketSender()
									.sendMessage("@red@Warning! Changing that setting kicked the player "
											+ member.getUsername() + " from the chat because")
									.sendMessage("@red@ they do not have the required rank to be in the chat.");
							;
						}
					}
				}
				clanChatSetupInterface(player, false);
				writeFile(clan);
				return true;
			case -18278:
			case -17519:
			case -17520:
			case -17521:
			case -17522:
			case -17523:
			case -17524:
			case -17525:
			case -17526:
				l = -17519 - id;
				clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_TALK, id == -18278 ? null : ClanChatRank.forId(l));
				player.getPacketSender().sendMessage("You have changed your clanchat channel's settings.");
				clanChatSetupInterface(player, false);
				writeFile(clan);
				return true;
			case -18275:
			case -17510:
			case -17511:
			case -17512:
			case -17513:
			case -17514:
			case -17515:
				l = (-17510 - id) + 1;
				clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_KICK, id == -18275 ? null : ClanChatRank.forId(l));
				player.getPacketSender().sendMessage("You have changed your clanchat channel's settings.");
				clanChatSetupInterface(player, false);
				updateList(clan);
				writeFile(clan);
				return true;
			}
		}
		return false;
	}

	public static void clanChatSetupInterface(Player player, boolean check) {
		player.getPacketSender().sendInterfaceRemoval();
		ClanChat channel = getClanChatChannel(player);
		if (check) {
			if (channel == null) {
				player.getPacketSender().sendMessage("You have not created a clanchat channel yet.");
				return;
			}
		}
		player.getPacketSender().sendString(47814, channel.getName());
		if (channel.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER] == null) {
			player.getPacketSender().sendString(47815, "Anyone");
		} else {
			player.getPacketSender().sendString(47815,
					Misc.formatText(channel.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER].name().toLowerCase())
							+ "+");
		}

		if (channel.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK] == null) {
			player.getPacketSender().sendString(47816, "Anyone");
		} else {
			player.getPacketSender().sendString(47816,
					Misc.formatText(channel.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK].name().toLowerCase())
							+ "+");
		}

		if (channel.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK] == null) {
			player.getPacketSender().sendString(47817, "Only me");
		} else {
			player.getPacketSender().sendString(47817,
					Misc.formatText(channel.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_KICK].name().toLowerCase())
							+ "+");
		}

		player.getPacketSender().sendInterface(40172);
	}

	public static void onLogin(Player player) {
		resetInterface(player);
		ClanChatManager.join(player, player.getClanChatName());
	}

	public static void resetInterface(Player player) {
		player.getPacketSender().sendString(37139, "Talking in: N/A");
		player.getPacketSender().sendString(37140, "Owner: N/A");
		player.getPacketSender().sendString(37135, "Join Chat");
		// player.getPacketSender().sendString(29454, "Lootshare: N/A");
		for (int i = 37144; i < 37244; i++) {
			player.getPacketSender().sendString(i, "");
		}
	}

	public static Player getPlayer(int index, ClanChat clan) {
		int clanIndex = 0;
		for (Player members : clan.getMembers()) {
			if (members != null) {
				if (clanIndex == index) {
					return members;
				}
				clanIndex++;
			}
		}
		return null;
	}
}
