package com.elvarg.cache;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.CRC32;

import com.elvarg.cache.impl.Cache;
import com.elvarg.cache.impl.CacheArchive;
import com.elvarg.cache.impl.CacheConstants;
import com.elvarg.util.CompressionUtil;
import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jaggrab.net.FileDescriptor;

/**
 * Represents a file system of {@link Cache}s and {@link CacheArchive}s.
 * 
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 * @author Artem Batutin <artembatutin@gmail.com>
 * @editor Swiffy96
 */
public class CacheLoader {

	/**
	 * All of the {@link Cache}s within this {@link CacheLoader}.
	 */
	public Cache[] CACHES;

	/**
	 * All of the {@link CacheArchive}s within this {@link CacheLoader}.
	 */
	public CacheArchive[] ARCHIVES;

	/**
	 * The cached archive hashes.
	 */
	public ByteBuf CRC_TABLE;

	/**
	 * The preloadable-files table.
	 */
	public ByteBuf[] PRELOAD_FILES = new ByteBuf[CacheConstants.PRELOAD_FILES.length];

	/**
	 * Constructs and initializes a {@link CacheLoader} from the specified
	 * {@code directory}.
	 * 
	 * @param directory
	 *            The directory of the {@link CacheLoader}.
	 * @return The constructed {@link CacheLoader} instance.
	 * @throws Exception
	 */
	public void init() throws Exception {
		Path root = Paths.get(CacheConstants.CACHE_BASE_DIR);
		Preconditions.checkArgument(Files.isDirectory(root), "Supplied path must be a directory!");

		Path data = root.resolve(CacheConstants.DATA_PREFIX);
		Preconditions.checkArgument(Files.exists(data), "No data file found in the specified path!");

		// Load cache files...
		SeekableByteChannel dataChannel = Files.newByteChannel(data, READ, WRITE);
		CACHES = new Cache[CacheConstants.MAXIMUM_INDICES];
		ARCHIVES = new CacheArchive[CacheConstants.MAXIMUM_ARCHIVES];
		for (int index = 0; index < CACHES.length; index++) {
			Path path = root.resolve(CacheConstants.INDEX_PREFIX + index);
			if (Files.exists(path)) {
				SeekableByteChannel indexChannel = Files.newByteChannel(path, READ, WRITE);
				CACHES[index] = new Cache(dataChannel, indexChannel, index);
			}
		}

		// Load the archives from the cache files...
		// We don't use index 0
		for (int id = 1; id < ARCHIVES.length; id++) {
			Cache cache = Objects.requireNonNull(CACHES[CacheConstants.CONFIG_INDEX],
					"Configuration cache is null - unable to decode archives");
			ARCHIVES[id] = CacheArchive.decode(cache.get(id));
		}

		// Load preload files...
		for (int i = 0; i < CacheConstants.PRELOAD_FILES.length; i++) {
			String fileName = CacheConstants.PRELOAD_FILES[i];
			File file = new File(CacheConstants.CACHE_BASE_DIR + fileName);
			if (!file.exists() || file.isDirectory()) {
				throw new Exception("Preload file " + fileName + " is not present!");
			}
			getPreloadFile(i);
		}
	}

	/**
	 * Gets an {@link CacheArchive} for the specified {@code id}, this method
	 * fails-fast if no archive can be found.
	 * 
	 * @param id
	 *            The id of the {@link CacheArchive} to fetch.
	 * @return The {@link CacheArchive} for the specified {@code id}.
	 * @throws NullPointerException
	 *             If the archive cannot be found.
	 */
	public CacheArchive getArchive(int id) {
		Preconditions.checkElementIndex(id, ARCHIVES.length);
		return Objects.requireNonNull(ARCHIVES[id]);
	}

	/**
	 * Gets a {@link Cache} for the specified {@code id}, this method fails-fast
	 * if no cache can be found.
	 * 
	 * @param id
	 *            The id of the {@link Cache} to fetch.
	 * @return The {@link Cache} for the specified {@code id}.
	 * @throws NullPointerException
	 *             If the cache cannot be found.
	 */
	public Cache getCache(int id) {
		Preconditions.checkElementIndex(id, CACHES.length);
		return Objects.requireNonNull(CACHES[id]);
	}

	/**
	 * Returns a {@link ByteBuffer} of file data for the specified index within
	 * the specified {@link Cache}.
	 * 
	 * @param cacheId
	 *            The id of the cache.
	 * @param indexId
	 *            The id of the index within the cache.
	 * @return A {@link ByteBuffer} of file data for the specified index.
	 * @throws IOException
	 *             If some I/O exception occurs.
	 */
	public ByteBuf getFile(int cacheId, int indexId) throws IOException {
		Cache cache = getCache(cacheId);
		synchronized (cache) {
			return cache.get(indexId);
		}
	}

	public ByteBuf getFile(FileDescriptor fs) throws IOException {
		return getFile(fs.getType(), fs.getFile());
	}

	public byte[] getDecompressedFile(int cacheId, int indexId) throws IOException {
		ByteBuf compressed = getFile(cacheId, indexId);
		ByteBuf decompressed = Unpooled.wrappedBuffer(CompressionUtil.gunzip(compressed.array()));
		return decompressed.array();
	}

	/**
	 * Gets a file.
	 * 
	 * @param file
	 *            The file to get.
	 * @return A byte array which contains the contents of the file.
	 */
	public byte[] getFile(File file) {
		try {
			int i = (int) file.length();
			byte data[] = new byte[i];
			DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			datainputstream.readFully(data, 0, i);
			datainputstream.close();
			return data;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the ByteBuf of a preloaded file.
	 * 
	 * @param index
	 *            The index of the preload file to return.
	 * @return A {@link ByteBuffer} which contains the contents of the sprite
	 *         file.
	 */
	public ByteBuf getPreloadFile(int index) {
		synchronized (this) {
			if (PRELOAD_FILES[index] != null) {
				return PRELOAD_FILES[index].slice();
			}
		}

		// Load the file data...
		byte[] data = getFile(new File(CacheConstants.CACHE_BASE_DIR + CacheConstants.PRELOAD_FILES[index]));

		// Create a copied buffer from the file data..
		ByteBuf buf = Unpooled.copiedBuffer(data);

		synchronized (this) {
			PRELOAD_FILES[index] = buf;
			return PRELOAD_FILES[index].slice();
		}
	}

	/**
	 * Returns the cached {@link #CRC_TABLE} if they exist, otherwise they are
	 * calculated and cached for future use.
	 * 
	 * @return The hashes of each {@link CacheArchive}.
	 * @throws IOException
	 *             If some I/O exception occurs.
	 */
	public ByteBuf getCrcTable() throws IOException {
		synchronized (this) {
			if (CRC_TABLE != null) {
				return CRC_TABLE.slice();
			}

			int[] crcs = new int[CacheConstants.MAXIMUM_ARCHIVES + PRELOAD_FILES.length];

			CRC32 crc32 = new CRC32();
			for (int file = 1; file < crcs.length; file++) {

				ByteBuf buffer;

				// Should we fetch the crc for a preloadable file or a cache
				// file?
				if (file >= CacheConstants.MAXIMUM_ARCHIVES) {
					buffer = getPreloadFile(file - CacheConstants.MAXIMUM_ARCHIVES);
				} else {
					buffer = getFile(CacheConstants.CONFIG_INDEX, file);
				}

				crc32.reset();
				byte[] bytes = new byte[buffer.readableBytes()];
				buffer.readBytes(bytes, 0, bytes.length);
				crc32.update(bytes, 0, bytes.length);
				crcs[file] = (int) crc32.getValue();
			}

			ByteBuf buffer = Unpooled.buffer(crcs.length * Integer.BYTES + 4);

			int hash = 1234;
			for (int crc : crcs) {
				hash = (hash << 1) + crc;
				buffer.writeInt(crc);
			}

			buffer.writeInt(hash);

			synchronized (this) {
				CRC_TABLE = buffer.asReadOnly();
				return CRC_TABLE.slice();
			}
		}
	}

}