package com.elvarg.cache.impl;

import java.nio.ByteBuffer;

import com.elvarg.net.ByteBufUtils;
import com.google.common.base.Preconditions;

/**
 * Represents an index within some {@link Cache}.
 * 
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public final class CacheIndex {

	/**
	 * The length of the index.
	 */
	private final int length;

	/**
	 * The id of the index.
	 */
	private final int id;

	/**
	 * Constructs a new {@link CacheIndex} with the expected length and id. This
	 * constructor is marked {@code private} and should not be modified to be
	 * invoked directly, use {@link CacheIndex#decode(ByteBuffer)} instead.
	 * 
	 * @param length
	 *            The length of the index.
	 * @param id
	 *            The id of the index.
	 */
	private CacheIndex(int length, int id) {
		this.length = length;
		this.id = id;
	}

	/**
	 * Decodes an {@link CacheIndex} from the specified {@link ByteBuffer}.
	 * 
	 * @param buffer
	 *            The {@link ByteBuffer} to get the index from.
	 * @return The decoded index.
	 */
	public static CacheIndex decode(ByteBuffer buffer) {
		int length = ByteBufUtils.getMedium(buffer);
		int id = ByteBufUtils.getMedium(buffer);
		return new CacheIndex(length, id);
	}

	/**
	 * Returns the id of this index.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the length of this index.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Tests whether or not this index is valid.
	 */
	public void check() {
		Preconditions.checkArgument(length > 0);
	}

}