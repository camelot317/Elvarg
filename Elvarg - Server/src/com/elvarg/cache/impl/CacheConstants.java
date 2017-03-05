package com.elvarg.cache.impl;

import com.elvarg.cache.CacheLoader;

public class CacheConstants {

	/**
	 * Represents the id of the configurations cache.
	 */
	public static final int CONFIG_INDEX = 0;

	/**
	 * Represents the id of the model cache.
	 */
	public static final int MODEL_INDEX = 1;

	/**
	 * Represents the id of the animations cache.
	 */
	public static final int ANIMATION_INDEX = 2;

	/**
	 * Represents the id of the sounds and music cache.
	 */
	public static final int MIDI_INDEX = 3;

	/**
	 * Represents the id of the map and landscape cache.
	 */
	public static final int MAP_INDEX = 4;

	/**
	 * Represents the id of the title screen archive.
	 */
	public static final int TITLE_ARCHIVE = 1;

	/**
	 * Represents the id of the configurations archive.
	 */
	public static final int CONFIG_ARCHIVE = 2;

	/**
	 * Represents the id of the interface archive.
	 */
	public static final int INTERFACE_ARCHIVE = 3;

	/**
	 * Represents the id of the media and sprite archive.
	 */
	public static final int MEDIA_ARCHIVE = 4;

	/**
	 * Represents the id of the manifest archive.
	 */
	public static final int MANIFEST_ARCHIVE = 5;

	/**
	 * Represents the id of the textures archive.
	 */
	public static final int TEXTURES_ARCHIVE = 6;

	/**
	 * Represents the id of the word archive - user for storing profane or
	 * illegal words not allowed to be spoken in-game.
	 */
	public static final int WORD_ARCHIVE = 7;

	/**
	 * Represents the id of the sound and music archive.
	 */
	public static final int SOUND_ARCHIVE = 8;

	/**
	 * Represents the maximum amount of archives within this file system.
	 */
	public static final int MAXIMUM_ARCHIVES = 9;

	/**
	 * Represents the maximum amount of indices within this file system.
	 */
	public static final int MAXIMUM_INDICES = 256;

	/**
	 * Represents the prefix of this {@link CacheLoader}s main cache files.
	 */
	public static final String DATA_PREFIX = "main_file_cache.dat";

	/**
	 * Represents the prefix of this {@link CacheLoader}s index files.
	 */
	public static final String INDEX_PREFIX = "main_file_cache.idx";

	/**
	 * Represents the file directory path to the cache.
	 */
	public static final String CACHE_BASE_DIR = "./data/cache/";

	/**
	 * The preload files.
	 * 
	 * Client will download these after downloading crcs.
	 */
	public static final String[] PRELOAD_FILES = { "sprites.idx", "sprites.dat", "obj.idx", "obj.dat" };

}
