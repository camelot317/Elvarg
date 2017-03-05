package com.elvarg.world.entity.combat.pvp;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.elvarg.net.SessionState;
import com.elvarg.util.Misc;
import com.elvarg.util.Stopwatch;
import com.elvarg.world.content.ServerFeed;
import com.elvarg.world.entity.combat.CombatFactory;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.grounditems.GroundItemManager;
import com.elvarg.world.model.GroundItem;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.Locations.Location;

public class BountyHunter {

	// The owner of this instance
	private Player player;

	// The current target
	private Player target;

	// The stopwatch used for different kinds of things.
	// To keep track of last search for a target
	// To keep track of last help message sent to player
	private Stopwatch stopwatch = new Stopwatch().reset();

	// Amount of target kills
	private int targetKills;

	// Amount of normal kills
	private int normalKills;

	// Amount of deaths
	private int deaths;

	// The timer out of wilderness
	// When this hits 0, targets will be reset
	private int safingTimer = 180;

	// Called on every game tick
	public void onTick() {

		if (player.getLocation() == Location.WILDERNESS) {

			// We don't have target. Find one.
			if (target == null) {

				// Search for target every 30 seconds.
				if (stopwatch.elapsed(30000)) {

					// Check that we're valid
					if (player == null || player.getLocation() != Location.WILDERNESS
							|| player.getSession().getState() != SessionState.LOGGED_IN
							|| player.getWildernessLevel() <= 0) {
						return;
					}

					// Check the player isn't teleporting or that they have
					// died..
					if (player.getHitpoints() <= 0 || player.isNeedsPlacement()) {
						return;
					}

					for (Player other : PLAYERS_IN_WILD) {

						// Check that they're valid
						if (other == null || other.getLocation() != Location.WILDERNESS
								|| other.getSession().getState() != SessionState.LOGGED_IN
								|| other.getWildernessLevel() <= 0) {
							continue;
						}

						// Check the guy isn't teleporting or that they have
						// died..
						if (other.getHitpoints() <= 0 || other.isNeedsPlacement()) {
							continue;
						}

						// Check that they don't already have a target
						if (other.getBountyHunter().getTarget() != null) {
							continue;
						}

						// Check if we're looping ourselves..
						if (other.equals(player)) {
							continue;
						}

						int combatDifference = CombatFactory.combatLevelDifference(
								player.getSkillManager().getCombatLevel(), other.getSkillManager().getCombatLevel());

						if (combatDifference <= player.getWildernessLevel()
								&& combatDifference <= other.getWildernessLevel()) {
							assign(player, other);
							assign(other, player);
							break;
						}
					}

					stopwatch.reset();
				}

				return;

			} else {

				// We have a target!
				if (target.getLocation() == Location.WILDERNESS) {

					// Let player know where the target is every 20 seconds.
					// If they aren't fighting!
					if (stopwatch.elapsed(20000)) {

						// If player can't even see the target, let them know
						// where they're at.
						if (!player.getLocalPlayers().contains(target)) {
							player.getPacketSender().sendMessage(
									"Your target is in level " + target.getWildernessLevel() + " wilderness.");
						}

						stopwatch.reset();
					}
				}

			}
		} else {

			// Player is not in wilderness but has a target.
			// Handle reset timer...
			if (target != null) {

				if (safingTimer == 180 || safingTimer == 120 || safingTimer == 60) {
					player.getPacketSender().sendMessage("You have " + safingTimer
							+ " seconds to get back to the wilderness before you lose your target.");
					target.getPacketSender()
							.sendMessage("Your target has " + safingTimer
									+ " seconds to get back to the wilderness before they lose you as")
							.sendMessage("target.");

				}

				safingTimer--;

				// They didn't return... Reset target
				if (safingTimer == 0) {
					assign(target, null);
					assign(player, null);
				}

			}
		}
	}

	// Updates the interface
	public void updateInterface() {

		// Update target information
		if (target == null) {
			player.getPacketSender().sendString(TARGET_WEALTH_STRING, "---").sendString(TARGET_NAME_STRING, "None")
					.sendString(TARGET_LEVEL_STRING, "Combat: ------");

			showWealthType(WealthType.NO_TARGET);
		} else {

			WealthType type = target.getBountyHunter().getWealth();

			player.getPacketSender().sendString(TARGET_WEALTH_STRING, "Wealth: " + type.tooltip)
					.sendString(TARGET_NAME_STRING, target.getUsername())
					.sendString(TARGET_LEVEL_STRING, "Combat: " + target.getSkillManager().getCombatLevel());

			showWealthType(type);
		}

		// Update kda information
		player.getPacketSender().sendString(23323, "Targets killed: " + targetKills)
				.sendString(23324, "Players killed: " + normalKills).sendString(23325, "Deaths: " + deaths);
	}

	// Enters bounty hunter
	// Adds player to list and shows attack option.
	public void enter() {

		player.getPacketSender().sendInteractionOption("Attack", 2, true);
		player.getPacketSender().sendWalkableInterface(23300);

		updateInterface();

		if (!PLAYERS_IN_WILD.contains(player)) {
			PLAYERS_IN_WILD.add(player);
		}

	}

	// Leaves bounty hunter
	// Removes player from list.
	public void leave() {

		PLAYERS_IN_WILD.remove(player);

		player.setWildernessLevel(0);

	}

	// Player logged out!
	public void logout() {
		if (target != null) {

			assign(target, null);
			assign(player, null);

		}
	}

	// Killed a player.
	// Improve statistics and give reward if target.
	public void killedPlayer(Player killed) {
		if (target != null) {
			if (target.equals(killed)) {

				target.getPacketSender().sendMessage("You were defeated by your target!");
				assign(target, null);

				player.getPacketSender().sendMessage("Congratulations, you managed to defeat your target!");
				player.getBountyHunter().incrementTargetKills();
				assign(player, null);

				// Search for emblem in the player's inventory
				Emblem inventoryEmblem = null;
				for (Emblem e : Emblem.values()) {
					if (player.getInventory().contains(e.id)) {
						inventoryEmblem = e;
						break;
					}
				}
				// If we didn't find an emblem, there should be a small chance
				// Of receiving a tier 1 emblem since they successfully killed a
				// target
				if (inventoryEmblem == null) {
					if (true) {
						GroundItemManager.spawnGroundItem(player,
								new GroundItem(new Item(Emblem.MYSTERIOUS_EMBLEM_1.id, 1), killed.getPosition(),
										player.getUsername(), player.getHostAddress(), false, 150, false, -1));
						player.getPacketSender().sendMessage(
								"@red@You have been awarded with a mysterious emblem for successfully killing your target.");
					}
				} else {

					// This emblem can't be upgraded more..
					if (inventoryEmblem != Emblem.MYSTERIOUS_EMBLEM_10) {

						// We found an emblem. Upgrade it!
						// Double check that we have it inventory one more time
						if (player.getInventory().contains(inventoryEmblem.id)) {
							player.getInventory().delete(inventoryEmblem.id, 1);

							int nextEmblemId = 1;

							// Mysterious emblem tier 1 has a noted version
							// too...
							// So add 2 instead of 1 to skip it.
							if (inventoryEmblem == Emblem.MYSTERIOUS_EMBLEM_1) {
								nextEmblemId = 2;
							}

							player.getInventory().add(inventoryEmblem.id + nextEmblemId, 1);

							player.getPacketSender().sendMessage("@red@Your mysterious emblem has been upgraded!");
						}
					} else {

						// This emblem can't be upgraded more..
						player.getPacketSender().sendMessage(
								"@red@Your mysterious emblem is already tier 10 and cannot be upgraded further.");
					}
				}
			} else
				player.getBountyHunter().incrementKills();
		} else
			player.getBountyHunter().incrementKills();

		ServerFeed.submit("<img=" + ServerFeed.SKULL_SPRITE + ">@whi@[@gre@" + Misc.getCurrentServerTime()
				+ "@whi@] @or1@" + player.getUsername() + " @whi@has defeated @yel@" + killed.getUsername() + "@whi@!",
				15);

		killed.getBountyHunter().incrementDeaths();
		killed.getBountyHunter().updateInterface();
	}

	// Removes all wealth type sprites except for the specified type.
	public void showWealthType(WealthType type) {
		for (WealthType types : WealthType.values()) {
			int state = 0;
			if (types == type) {
				state = 1;
			}
			player.getPacketSender().sendConfig(types.configId, state);
		}
	}

	public WealthType getWealth() {
		int amount = 0;

		for (Item item : Misc.concat(player.getInventory().getItems(), player.getEquipment().getItems())) {
			if (item == null || item.getId() <= 0 || item.getAmount() <= 0 || !item.getDefinition().isDropable()
					|| !item.getDefinition().isTradeable()) {
				continue;
			}
			amount += item.getDefinition().getValue();
		}

		return WealthType.forWealth(amount);
	}

	public BountyHunter(Player player) {
		this.player = player;
	}

	public Player getTarget() {
		return target;
	}

	public BountyHunter setTarget(Player target) {
		this.target = target;
		return this;
	}

	public void setTargetKills(int targetKills) {
		this.targetKills = targetKills;
	}

	public void incrementTargetKills() {
		targetKills++;
	}

	public int getTargetKills() {
		return targetKills;
	}

	public void setNormalKills(int normalKills) {
		this.normalKills = normalKills;
	}

	public void incrementKills() {
		normalKills++;
	}

	public int getNormalKills() {
		return normalKills;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public void incrementDeaths() {
		deaths++;
	}

	public int getDeaths() {
		return deaths;
	}

	public void resetSafingTimer() {
		this.safingTimer = 180;
	}

	/** STATIC **/

	private static void assign(Player player, Player other) {
		player.getBountyHunter().setTarget(other).updateInterface();

		if (other == null) {
			player.getPacketSender().sendMessage("Your target has been reset.");
			player.getPacketSender().sendEntityHintRemoval(true);
		} else {
			player.getPacketSender().sendMessage("You have been assigned a target!");
			player.getPacketSender().sendEntityHint(other);
		}

		player.getBountyHunter().resetSafingTimer();
	}

	public static boolean exchangeEmblems(Player player) {
		ArrayList<Emblem> list = new ArrayList<Emblem>();
		for (Emblem emblem : Emblem.values()) {
			if (player.getInventory().contains(emblem.id)) {
				list.add(emblem);
			}
		}

		if (list.isEmpty()) {
			return false;
		}

		int pkp = 0;

		for (Emblem emblem : list) {
			int amount = player.getInventory().getAmount(emblem.id);
			if (amount > 0) {

				player.getInventory().delete(emblem.id, amount);

				int pkp_reward = (emblem.pkp * amount);
				player.incrementPkp(pkp_reward);
				pkp += pkp_reward;

			}
		}

		player.getPacketSender().sendMessage("@red@You have received " + pkp + " PKP for your emblem(s).");
		return true;
	}

	private static enum WealthType {
		NO_TARGET("N/A", 876), VERY_LOW("V. Low", 877), LOW("Low", 878), MEDIUM("Medium", 879), HIGH("High",
				880), VERY_HIGH("V. HIGH", 881);

		;

		WealthType(String tooltip, int configId) {
			this.tooltip = tooltip;
			this.configId = configId;
		}

		public String tooltip;
		public int configId;

		private static WealthType forWealth(int wealth) {
			/*
			 * if(wealth >= 10k) { ...Add wealthtypes for wealth }
			 */
			return WealthType.VERY_LOW;
		}
	}

	public static enum Emblem {
		MYSTERIOUS_EMBLEM_1(12746, 1, 5), MYSTERIOUS_EMBLEM_2(12748, 1, 10), MYSTERIOUS_EMBLEM_3(12749, 1,
				15), MYSTERIOUS_EMBLEM_4(12750, 1, 20), MYSTERIOUS_EMBLEM_5(12751, 1, 25), MYSTERIOUS_EMBLEM_6(12752, 1,
						30), MYSTERIOUS_EMBLEM_7(12753, 1, 35), MYSTERIOUS_EMBLEM_8(12754, 1,
								40), MYSTERIOUS_EMBLEM_9(12755, 1, 45), MYSTERIOUS_EMBLEM_10(12756, 1, 50),

		;

		Emblem(int id, int tier, int pkp) {
			this.id = id;
			this.tier = tier;
			this.pkp = pkp;
		}

		public int id;
		public int tier;
		public int pkp;
	}

	private static final int TARGET_WEALTH_STRING = 23305;
	private static final int TARGET_NAME_STRING = 23307;
	private static final int TARGET_LEVEL_STRING = 23308;

	public static CopyOnWriteArrayList<Player> PLAYERS_IN_WILD = new CopyOnWriteArrayList<>();
}
