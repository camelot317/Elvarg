package com.elvarg;

import com.elvarg.cache.impl.CacheConstants;
import com.elvarg.world.model.Position;

/**
 * A class containing different attributes which affect the game in different
 * ways.
 * 
 * @author Swiffy
 */
public class GameConstants {

	/**
	 * Is JAGGRAB enabled?
	 */
	public static final boolean JAGGRAB_ENABLED = true;

	/**
	 * The directory of the cache files.
	 */
	public static final String DEFINITIONS_DIRECTORY = CacheConstants.CACHE_BASE_DIR + "definitions/";

	/**
	 * The game engine rate
	 */
	public static final int ENGINE_PROCESSING_CYCLE_RATE = 300;

	/**
	 * The maximum amount of iterations that should occur per queue.
	 */
	public static final int QUEUED_LOOP_THRESHOLD = 50;

	/**
	 * The game version
	 */
	public static final int GAME_VERSION = 1;

	/**
	 * The secure game UID /Unique Identifier/
	 */
	public static final int GAME_UID = 23;

	/**
	 * The default position in game.
	 */
	public static final Position DEFAULT_POSITION = new Position(3091, 3503);

	/**
	 * Should the inventory be refreshed immediately on switching items or
	 * should it be delayed until next game cycle?
	 */
	public static final boolean QUEUE_SWITCHING_REFRESH = true;

	/**
	 * The tab interfaces in game. {Gameframe} [0] = tab Id, [1] = tab interface
	 * Id
	 */
	public static final int TAB_INTERFACES[][] = { { 0, 2423 }, { 1, 3917 }, { 2, 639 }, { 3, 3213 }, { 4, 1644 },
			{ 5, 5608 }, { 6, -1 }, // Row 1

			{ 7, 37128 }, { 8, 5065 }, { 9, 5715 }, { 10, 2449 }, { 11, 42500 }, { 12, 147 } };
	public static final int[][] startKit = { { 995, 500000000 }, { 4151, 1 }, { 4153, 1 }, { 1215, 1 }, { 9185, 1 },
			{ 11235, 1 }, { 9244, 5000 }, { 11212, 1000 }, { 386, 500 }, { 2441, 100 }, { 2437, 100 }, { 2443, 100 },
			{ 2445, 100 }, { 2435, 100 }, { 3041, 100 }, { 2431, 100 }, { 1163, 1 }, { 1127, 1 }, { 1079, 1 },
			{ 12954, 1 }, { 4089, 1 }, { 4091, 1 }, { 4093, 1 }, { 4095, 1 }, { 4097, 1 }, { 2503, 1 }, { 2497, 1 },
			{ 2491, 1 } };
}
