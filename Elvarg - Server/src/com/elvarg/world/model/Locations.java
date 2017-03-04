package com.elvarg.world.model;

import com.elvarg.world.entity.Entity;
import com.elvarg.world.entity.combat.CombatFactory;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.player.Player;

public class Locations {

	public static void login(Player player) {
		player.setLocation(Location.getLocation(player));
		player.getLocation().login(player);
		player.getLocation().enter(player);
	}

	public static void logout(Player player) {
		player.getLocation().logout(player);
		player.getLocation().leave(player);
	}

	public static int PLAYERS_IN_WILD;
	public static int PLAYERS_IN_DUEL_ARENA;

	public enum Location {
		VARROCK(new int[] { 3167, 3272 }, new int[] { 3263, 3504 }, false, true, true, true, true, true) {
		},
		BANK(new int[] { 3090, 3099, 3089, 3090, 3248, 3258, 3179, 3191, 2944, 2948, 2942, 2948, 2944, 2950, 3008, 3019,
				3017, 3022, 3203, 3213, 3212, 3215, 3215, 3220, 3220, 3227, 3227, 3230, 3226, 3228, 3227, 3229 },
				new int[] { 3487, 3500, 3492, 3498, 3413, 3428, 3432, 3448, 3365, 3374, 3367, 3374, 3365, 3370, 3352,
						3359, 3352, 3357, 3200, 3237, 3200, 3235, 3202, 3235, 3202, 3229, 3208, 3226, 3230, 3211, 3208,
						3226 },
				false, true, true, false, false, true) {
		},
		EDGEVILLE(new int[] { 3073, 3134 }, new int[] { 3457, 3518 }, false, false, true, false, false, true) {
		},
		LUMBRIDGE(new int[] { 3175, 3238 }, new int[] { 3179, 3302 }, false, true, true, true, true, true) {
		},
		KING_BLACK_DRAGON(new int[] { 2251, 2292 }, new int[] { 4673, 4717 }, true, true, true, true, true, true) {
		},
		ROCK_CRABS(new int[] { 2689, 2727 }, new int[] { 3691, 3730 }, true, true, true, true, true, true) {
		},
		BANDIT_CAMP(new int[] { 3020, 3150, 3055, 3195 }, new int[] { 3684, 3711, 2958, 3003 }, true, true, true, true,
				true, true) {
		},
		WILDERNESS(new int[] { 2940, 3392, 2986, 3012, 3653, 3720, 3650, 3653, 3150, 3199, 2994, 3041 },
				new int[] { 3523, 3968, 10338, 10366, 3441, 3538, 3457, 3472, 3796, 3869, 3733, 3790 }, false, true,
				true, true, true, true) {
			@Override
			public void process(Player player) {
				int x = player.getPosition().getX();
				int y = player.getPosition().getY();
				player.setWildernessLevel(((((y > 6400 ? y - 6400 : y) - 3520) / 8) + 1));
				player.getPacketSender().sendString(23321, "Level: " + player.getWildernessLevel());
			}

			@Override
			public void logout(Player player) {
				player.getBountyHunter().logout();
			}

			@Override
			public void leave(Player player) {
				player.getBountyHunter().leave();
			}

			@Override
			public void login(Player player) {
				enter(player);
			}

			@Override
			public void enter(Player player) {
				player.getBountyHunter().enter();
			}

			@Override
			public boolean canTeleport(Player player) {
				if (player.getWildernessLevel() > 20) {
					player.getPacketSender().sendMessage("Teleport spells are blocked in this level of Wilderness.");
					player.getPacketSender()
							.sendMessage("You must be below level 20 of Wilderness to use teleportation spells.");
					return false;
				}
				return true;
			}

			@Override
			public boolean canAttack(Player player, Player target) {
				int combatDifference = CombatFactory.combatLevelDifference(player.getSkillManager().getCombatLevel(),
						target.getSkillManager().getCombatLevel());
				if (combatDifference > player.getWildernessLevel() + 5
						|| combatDifference > target.getWildernessLevel() + 5) {
					// player.getPacketSender().sendMessage("Your combat level
					// difference is too great to attack that player
					// here.").sendMessage("Move deeper into the wilderness
					// first.");
					// player.getMovementQueue().reset();
					// return false;
				}
				if (target.getLocation() != Location.WILDERNESS) {
					player.getPacketSender()
							.sendMessage("That player cannot be attacked, because they are not in the Wilderness.");
					player.getMovementQueue().reset();
					return false;
				}
				/*
				 * if(Misc.getMinutesPlayed(player) < 20) {
				 * player.getPacketSender().
				 * sendMessage("You must have played for at least 20 minutes in order to attack someone."
				 * ); return false; } if(Misc.getMinutesPlayed(target) < 20) {
				 * player.getPacketSender().
				 * sendMessage("This player is a new player and can therefore not be attacked yet."
				 * ); return false; }
				 */
				return true;
			}
		},
		DEFAULT(null, null, false, true, true, true, true, true) {
		};

		Location(int[] x, int[] y, boolean multi, boolean summonAllowed, boolean followingAllowed,
				boolean cannonAllowed, boolean firemakingAllowed, boolean aidingAllowed) {
			this.x = x;
			this.y = y;
			this.multi = multi;
			this.summonAllowed = summonAllowed;
			this.followingAllowed = followingAllowed;
			this.cannonAllowed = cannonAllowed;
			this.firemakingAllowed = firemakingAllowed;
			this.aidingAllowed = aidingAllowed;
		}

		private int[] x, y;
		private boolean multi;
		private boolean summonAllowed;
		private boolean followingAllowed;
		private boolean cannonAllowed;
		private boolean firemakingAllowed;
		private boolean aidingAllowed;

		public int[] getX() {
			return x;
		}

		public int[] getY() {
			return y;
		}

		public static boolean inMulti(Character gc) {
			if (gc.getLocation() == WILDERNESS) {
				int x = gc.getPosition().getX(), y = gc.getPosition().getY();
				if (x >= 3250 && x <= 3302 && y >= 3905 && y <= 3925 || x >= 3020 && x <= 3055 && y >= 3684 && y <= 3711
						|| x >= 3150 && x <= 3195 && y >= 2958 && y <= 3003
						|| x >= 3645 && x <= 3715 && y >= 3454 && y <= 3550
						|| x >= 3150 && x <= 3199 && y >= 3796 && y <= 3869
						|| x >= 2994 && x <= 3041 && y >= 3733 && y <= 3790)
					return true;
			}
			return gc.getLocation().multi;
		}

		public boolean isSummoningAllowed() {
			return summonAllowed;
		}

		public boolean isFollowingAllowed() {
			return followingAllowed;
		}

		public boolean isCannonAllowed() {
			return cannonAllowed;
		}

		public boolean isFiremakingAllowed() {
			return firemakingAllowed;
		}

		public boolean isAidingAllowed() {
			return aidingAllowed;
		}

		public static Location getLocation(Entity gc) {
			for (Location location : Location.values()) {
				if (location != Location.DEFAULT)
					if (inLocation(gc, location))
						return location;
			}
			return Location.DEFAULT;
		}

		public static boolean inLocation(Entity gc, Location location) {
			if (location == Location.DEFAULT) {
				if (getLocation(gc) == Location.DEFAULT)
					return true;
				else
					return false;
			}
			/*
			 * if(gc instanceof Player) { Player p = (Player)gc; if(location ==
			 * Location.TRAWLER_GAME) { String state =
			 * FishingTrawler.getState(p); return (state != null &&
			 * state.equals("PLAYING")); } else if(location ==
			 * FIGHT_PITS_WAIT_ROOM || location == FIGHT_PITS) { String state =
			 * FightPit.getState(p), needed = (location == FIGHT_PITS_WAIT_ROOM)
			 * ? "WAITING" : "PLAYING"; return (state != null &&
			 * state.equals(needed)); } else if(location == Location.SOULWARS) {
			 * return (SoulWars.redTeam.contains(p) ||
			 * SoulWars.blueTeam.contains(p) && SoulWars.gameRunning); } else
			 * if(location == Location.SOULWARS_WAIT) { return
			 * SoulWars.isWithin(SoulWars.BLUE_LOBBY, p) ||
			 * SoulWars.isWithin(SoulWars.RED_LOBBY, p); } }
			 */
			return inLocation(gc.getPosition().getX(), gc.getPosition().getY(), location);
		}

		public static boolean inLocation(int absX, int absY, Location location) {
			int checks = location.getX().length - 1;
			for (int i = 0; i <= checks; i += 2) {
				if (absX >= location.getX()[i] && absX <= location.getX()[i + 1]) {
					if (absY >= location.getY()[i] && absY <= location.getY()[i + 1]) {
						return true;
					}
				}
			}
			return false;
		}

		public void process(Player player) {

		}

		public boolean canTeleport(Player player) {
			return true;
		}

		public void login(Player player) {

		}

		public void enter(Player player) {

		}

		public void leave(Player player) {

		}

		public void logout(Player player) {

		}

		public void onDeath(Player player) {

		}

		public boolean handleKilledNPC(Player killer, NPC npc) {
			return false;
		}

		public boolean canAttack(Player player, Player target) {
			return false;
		}
	}

	public static void process(Character gc) {
		Location newLocation = Location.getLocation(gc);
		if (gc.getLocation() == newLocation) {
			if (gc.isPlayer()) {
				Player player = (Player) gc;
				gc.getLocation().process(player);
				if (Location.inMulti(player)) {
					if (player.getMultiIcon() != 1)
						player.getPacketSender().sendMultiIcon(1);
				} else if (player.getMultiIcon() == 1)
					player.getPacketSender().sendMultiIcon(0);
			}
		} else {
			Location prev = gc.getLocation();
			if (gc.isPlayer()) {
				Player player = (Player) gc;
				if (player.getMultiIcon() > 0)
					player.getPacketSender().sendMultiIcon(0);
				if (player.getWalkableInterfaceId() > 0)
					player.getPacketSender().sendWalkableInterface(-1);
				if (player.getPlayerInteractingOption() != PlayerInteractingOption.NONE)
					player.getPacketSender().sendInteractionOption("null", 2, true);
			}
			gc.setLocation(newLocation);
			if (gc.isPlayer()) {
				prev.leave(((Player) gc));
				gc.getLocation().enter(((Player) gc));
			}
		}
	}

	public static boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		if (playerX == objectX && playerY == objectY)
			return true;
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == playerX
						&& ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if ((objectX - i) == playerX
						&& ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if (objectX == playerX
						&& ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean goodDistance(Position pos1, Position pos2, int distanceReq) {
		if (pos1.getZ() != pos2.getZ())
			return false;
		return goodDistance(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY(), distanceReq);
	}

	public static int distanceTo(Position position, Position destination, int size) {
		final int x = position.getX();
		final int y = position.getY();
		final int otherX = destination.getX();
		final int otherY = destination.getY();
		int distX, distY;
		if (x < otherX)
			distX = otherX - x;
		else if (x > otherX + size)
			distX = x - (otherX + size);
		else
			distX = 0;
		if (y < otherY)
			distY = otherY - y;
		else if (y > otherY + size)
			distY = y - (otherY + size);
		else
			distY = 0;
		if (distX == distY)
			return distX + 1;
		return distX > distY ? distX : distY;
	}
}
