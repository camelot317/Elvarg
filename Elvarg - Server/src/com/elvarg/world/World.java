package com.elvarg.world;

import java.util.Iterator;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

import com.elvarg.Elvarg;
import com.elvarg.GameConstants;
import com.elvarg.util.Misc;
import com.elvarg.world.content.ServerFeed;
import com.elvarg.world.entity.impl.CharacterList;
import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.object.ObjectHandler;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.entity.updating.NpcUpdateSequence;
import com.elvarg.world.entity.updating.PlayerUpdateSequence;
import com.elvarg.world.entity.updating.UpdateSequence;
import com.elvarg.world.model.PlayerRights;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author Swiffy96 Thanks to lare96 for help with parallel updating system
 */
public class World {

	/** All of the registered players. */
	private static CharacterList<Player> players = new CharacterList<>(1000);

	/** All of the registered NPCs. */
	private static CharacterList<NPC> npcs = new CharacterList<>(2027);

	/** Used to block the game thread until updating has completed. */
	private static Phaser synchronizer = new Phaser(1);

	/** A thread pool that will update players in parallel. */
	private static ExecutorService updateExecutor = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors(),
			new ThreadFactoryBuilder().setNameFormat("UpdateThread").setPriority(Thread.MAX_PRIORITY).build());

	/** The queue of {@link Player}s waiting to be logged in. **/
	private static Queue<Player> logins = new ConcurrentLinkedQueue<>();

	/** The queue of {@link Player}s waiting to be logged out. **/
	private static Queue<Player> logouts = new ConcurrentLinkedQueue<>();

	/** The queue of {@link Player}s waiting to be added to the game. **/
	private static Queue<Player> playerAddQueue = new ConcurrentLinkedQueue<>();

	/** The queue of {@link Player}s waiting to be removed from the game. **/
	private static Queue<Player> playerRemoveQueue = new ConcurrentLinkedQueue<>();

	/** The queue of {@link NPC}s waiting to be added to the game. **/
	private static Queue<NPC> npcAddQueue = new ConcurrentLinkedQueue<>();

	/** The queue of {@link NPC}s waiting to be removed from the game. **/
	private static Queue<NPC> npcRemoveQueue = new ConcurrentLinkedQueue<>();

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The world's players.
	 * 
	 * @return All players this world holds.
	 */
	public static CharacterList<Player> getPlayers() {
		return players;
	}

	/**
	 * The world's npcs.
	 * 
	 * @return All npcs this world holds.
	 */
	public static CharacterList<NPC> getNpcs() {
		return npcs;
	}

	public static Queue<Player> getLoginQueue() {
		return logins;
	}

	public static Queue<Player> getLogoutQueue() {
		return logouts;
	}

	public static Queue<Player> getPlayerAddQueue() {
		return playerAddQueue;
	}

	public static Queue<Player> getPlayerRemoveQueue() {
		return playerRemoveQueue;
	}

	public static Queue<NPC> getNpcAddQueue() {
		return npcAddQueue;
	}

	public static Queue<NPC> getNpcRemoveQueue() {
		return npcRemoveQueue;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static Player getPlayerByName(String username) {
		Optional<Player> op = players.search(p -> p != null && p.getUsername().equals(Misc.formatText(username)));
		return op.isPresent() ? op.get() : null;
	}

	public static Player getPlayerByLong(long encodedName) {
		Optional<Player> op = players.search(p -> p != null && p.getLongUsername().equals(encodedName));
		return op.isPresent() ? op.get() : null;
	}

	public static void sendMessage(String message) {
		players.forEach(p -> p.getPacketSender().sendMessage(message));
	}

	public static void sendStaffMessage(String message) {
		players.stream()
				.filter(p -> p != null && (p.getRights() == PlayerRights.OWNER
						|| p.getRights() == PlayerRights.DEVELOPER || p.getRights() == PlayerRights.ADMINISTRATOR
						|| p.getRights() == PlayerRights.MODERATOR || p.getRights() == PlayerRights.SUPPORT))
				.forEach(p -> p.getPacketSender().sendMessage(message));
	}

	public static void updateServerTime() {
		players.forEach(p -> p.getPacketSender().sendString(39161,
				"@or2@Server time: @or2@[ @yel@" + Misc.getCurrentServerTime() + "@or2@ ]"));
	}

	public static void savePlayers() {
		players.forEach(p -> p.save());
	}

	public static void sequence() {

		// Handle queued logins.
		for (int amount = 0; amount < GameConstants.QUEUED_LOOP_THRESHOLD; amount++) {
			Player player = logins.poll();
			if (player == null)
				break;
			player.onLogin();
		}

		// Handle queued logouts.
		int amount = 0;
		Iterator<Player> $it = logouts.iterator();
		while ($it.hasNext()) {
			Player player = $it.next();
			if (player == null || amount >= GameConstants.QUEUED_LOOP_THRESHOLD) {
				break;
			}

			boolean force_logout = Elvarg.isUpdating()
					|| World.getLogoutQueue().contains(player) && player.getLogoutTimer().elapsed(90000);

			if (player.canLogout() || force_logout) {
				player.onLogout();
				$it.remove();
				amount++;
			}
		}

		// Register queued players
		for (amount = 0; amount < GameConstants.QUEUED_LOOP_THRESHOLD; amount++) {
			Player player = playerAddQueue.poll();
			if (player == null)
				break;
			getPlayers().add(player);
		}

		// Deregister queued players
		for (amount = 0; amount < GameConstants.QUEUED_LOOP_THRESHOLD; amount++) {
			Player player = playerRemoveQueue.poll();
			if (player == null)
				break;
			getPlayers().remove(player);
		}

		// Register queued npcs
		for (amount = 0; amount < GameConstants.QUEUED_LOOP_THRESHOLD; amount++) {
			NPC npc = npcAddQueue.poll();
			if (npc == null)
				break;
			getNpcs().add(npc);
		}

		// Deregister queued npcs
		for (amount = 0; amount < GameConstants.QUEUED_LOOP_THRESHOLD; amount++) {
			NPC npc = npcRemoveQueue.poll();
			if (npc == null)
				break;
			getNpcs().remove(npc);
		}

		// Shuffle them
		players.shuffle();
		npcs.shuffle();

		// Update them
		// .....

		// First we construct the update sequences.
		UpdateSequence<Player> playerUpdate = new PlayerUpdateSequence(synchronizer, updateExecutor);
		UpdateSequence<NPC> npcUpdate = new NpcUpdateSequence();

		// Then we execute pre-updating code.
		players.forEach(playerUpdate::executePreUpdate);
		npcs.forEach(npcUpdate::executePreUpdate);

		// Then we execute parallelized updating code.
		synchronizer.bulkRegister(players.size());
		players.forEach(playerUpdate::executeUpdate);
		synchronizer.arriveAndAwaitAdvance();

		// Then we execute post-updating code.
		players.forEach(playerUpdate::executePostUpdate);
		npcs.forEach(npcUpdate::executePostUpdate);

		// Objects updating
		ObjectHandler.process();

		// Misc updating
		ServerFeed.updateEntries();
	}
}
