package com.elvarg.cache.impl;

import com.elvarg.cache.CacheLoader;

import io.netty.buffer.ByteBuf;

/**
 * Represents a sector within an {@link CacheArchive}.
 * <p>
 * A archive sector contains a hashed name and compressed data, this data
 * represents files and information within the {@link CacheLoader}.
 * </p>
 * 
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class CacheArchiveSector {

	/**
	 * The data within this sector.
	 */
	private final ByteBuf data;

	/**
	 * The hashed name of this sector.
	 */
	private final int hash;

	/**
	 * Constructs a new {@link CacheArchiveSector} with the specified data and
	 * hashed name.
	 * 
	 * @param data
	 *            The data within this sector.
	 * @param hash
	 *            The hashed name of this sector.
	 */
	protected CacheArchiveSector(ByteBuf data, int hash) {
		this.data = data;
		this.hash = hash;
	}

	/**
	 * Returns the data within this sector.
	 */
	public ByteBuf getData() {
		return data;
	}

	/**
	 * Returns the hashed name of this sector.
	 */
	public int getHash() {
		return hash;
	}

}