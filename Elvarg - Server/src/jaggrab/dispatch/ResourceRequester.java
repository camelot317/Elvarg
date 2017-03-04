package jaggrab.dispatch;

import java.io.IOException;

import com.elvarg.Elvarg;
import com.elvarg.cache.CacheLoader;
import com.elvarg.cache.impl.CacheConstants;

import io.netty.buffer.ByteBuf;

public class ResourceRequester {

	public static ByteBuf request(String path) throws IOException {

		final CacheLoader cache = Elvarg.getCache();

		if (path.startsWith("/crc")) {
			return cache.getCrcTable();
		} else if (path.startsWith("/title")) {
			return cache.getFile(0, 1);
		} else if (path.startsWith("/config")) {
			return cache.getFile(0, 2);
		} else if (path.startsWith("/interface")) {
			return cache.getFile(0, 3);
		} else if (path.startsWith("/media")) {
			return cache.getFile(0, 4);
		} else if (path.startsWith("/versionlist")) {
			return cache.getFile(0, 5);
		} else if (path.startsWith("/textures")) {
			return cache.getFile(0, 6);
		} else if (path.startsWith("/wordenc")) {
			return cache.getFile(0, 7);
		} else if (path.startsWith("/sounds")) {
			return cache.getFile(0, 8);
		} else if (path.startsWith("/sounds")) {
			return cache.getFile(0, 8);
		}

		/**
		 * Preloadable files
		 */
		if (path.startsWith("/preload")) {
			String file = path.substring(path.lastIndexOf("/") + 1);
			for (int i = 0; i < CacheConstants.PRELOAD_FILES.length; i++) {
				if (CacheConstants.PRELOAD_FILES[i].equals(file)) {
					return cache.getPreloadFile(i);
				}
			}
		}

		return null;
	}
}
