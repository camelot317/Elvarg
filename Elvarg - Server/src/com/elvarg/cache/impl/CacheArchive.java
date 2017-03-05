package com.elvarg.cache.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.elvarg.net.ByteBufUtils;
import com.elvarg.util.CompressionUtil;
import com.elvarg.util.Misc;
import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Represents an archive within the {@link Cache}.
 * <p>
 * An archive contains varied amounts of {@link CacheArchiveSector}s which
 * contain compressed file system data.
 * </p>
 * 
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class CacheArchive {

	/**
	 * A {@link Map} of {@link CacheArchiveSector} hashes to
	 * {@link CacheArchiveSector}s.
	 */
	private final Map<Integer, CacheArchiveSector> sectors;

	/**
	 * Constructs a new {@link CacheArchive} with the specified {@link Map} of
	 * {@link CacheArchiveSector}s.
	 * 
	 * @param sectors
	 *            The {@link Map} of sectors within this archive.
	 */
	private CacheArchive(Map<Integer, CacheArchiveSector> sectors) {
		this.sectors = sectors;
	}

	/**
	 * Decodes the data within this {@link CacheArchive}.
	 * 
	 * @param data
	 *            The encoded data within this archive.
	 * @return Returns an {@link CacheArchive} with the decoded data, never
	 *         {@code null}.
	 * @throws IOException
	 *             If some I/O exception occurs.
	 */
	public static CacheArchive decode(ByteBuf data) throws IOException {
		int length = ByteBufUtils.getMedium(data);
		int compressedLength = ByteBufUtils.getMedium(data);

		byte[] uncompressedData = data.array();

		if (compressedLength != length) {
			uncompressedData = CompressionUtil.unbzip2Headerless(data.array(), Cache.INDEX_SIZE, compressedLength);
			data = Unpooled.wrappedBuffer(uncompressedData);
		}

		int total = data.readShort() & 0xFF;
		int offset = data.readerIndex() + total * 10;

		Map<Integer, CacheArchiveSector> sectors = new HashMap<>(total);
		for (int i = 0; i < total; i++) {
			int hash = data.readInt();
			length = ByteBufUtils.getMedium(data);
			compressedLength = ByteBufUtils.getMedium(data);

			byte[] sectorData = new byte[length];

			if (length != compressedLength) {
				sectorData = CompressionUtil.unbzip2Headerless(uncompressedData, offset, compressedLength);
			} else {
				System.arraycopy(uncompressedData, offset, sectorData, 0, length);
			}

			sectors.put(hash, new CacheArchiveSector(Unpooled.wrappedBuffer(sectorData), hash));
			offset += compressedLength;
		}

		return new CacheArchive(sectors);
	}

	/**
	 * Retrieves an {@link Optional<ArchiveSector>} for the specified hash.
	 * 
	 * @param hash
	 *            The archive sectors hash.
	 * @return The optional container.
	 */
	private Optional<CacheArchiveSector> getSector(int hash) {
		return Optional.ofNullable(sectors.get(hash));
	}

	/**
	 * Retrieves an {@link Optional<ArchiveSector>} for the specified name.
	 * 
	 * @param name
	 *            The archive sectors name.
	 * @return The optional container.
	 */
	private Optional<CacheArchiveSector> getSector(String name) {
		int hash = Misc.hash(name);
		return getSector(hash);
	}

	/**
	 * Returns the data within the {@link CacheArchiveSector} for the specified
	 * {@code String} name.
	 * 
	 * @param name
	 *            The name of the {@link CacheArchiveSector}.
	 * @return The data within the {@link CacheArchiveSector} or nothing, this
	 *         method fails-fast if no {@link CacheArchiveSector} exists for the
	 *         specified {@code name}.
	 */
	public ByteBuf getData(String name) {
		Optional<CacheArchiveSector> optionalData = getSector(name);
		Preconditions.checkArgument(optionalData.isPresent());
		CacheArchiveSector dataSector = optionalData.get();
		return dataSector.getData();
	}

}