package com.elvarg.cache.impl.definitions;

import com.elvarg.Elvarg;
import com.elvarg.GameConstants;
import com.elvarg.cache.impl.CacheArchive;
import com.elvarg.cache.impl.CacheConstants;
import com.elvarg.util.JsonLoader;
import com.elvarg.world.collision.buffer.ByteStreamExt;
import com.elvarg.world.entity.impl.object.GameObject;
import com.elvarg.world.entity.impl.object.ObjectHandler;
import com.elvarg.world.model.Position;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public final class ObjectDefinition {

	public static JsonLoader parseObjects() {

		return new JsonLoader() {
			@Override
			public void load(JsonObject reader, Gson builder) {
				int id = reader.get("id").getAsInt();
				int x = reader.get("x").getAsInt();
				int y = reader.get("y").getAsInt();
				int z = 0;

				if (reader.has("z")) {
					z = reader.get("z").getAsInt();
				}

				int face = reader.get("face").getAsInt();

				int type = 10;
				if (reader.has("type")) {
					type = reader.get("type").getAsInt();
				}

				int seconds = -1;
				if (reader.has("seconds")) {
					seconds = reader.get("seconds").getAsInt();
				}

				GameObject object = new GameObject(id, new Position(x, y, z), type, face, seconds);
				ObjectHandler.spawnGlobalObject(object);
			}

			@Override
			public String filePath() {
				return GameConstants.DEFINITIONS_DIRECTORY + "world_objects.json";
			}
		};
	}

	public boolean obstructsGround;
	public byte ambientLighting;
	public int translateX;
	public String name;
	public int scaleZ;
	public byte lightDiffusion;
	public int objectSizeX;
	public int translateY;
	public int minimapFunction;
	public int[] originalModelColors;
	public int scaleX;
	public int varp;
	public boolean inverted;
	public static boolean lowMemory;
	public int type;
	public static int[] streamIndices;
	public boolean impenetrable;
	public int mapscene;
	public int childrenIDs[];
	public int supportItems;
	public int objectSizeY;
	public boolean contouredGround;
	public boolean occludes;
	public boolean hollow;
	public boolean solid;
	public int surroundings;
	public boolean delayShading;
	public static int cacheIndex;
	public int scaleY;
	public int[] modelIds;
	public int varbit;
	public int decorDisplacement;
	public int[] modelTypes;
	public byte[] description;
	public boolean isInteractive;
	public boolean castsShadow;
	public int animation;
	public static ObjectDefinition[] cache;
	public int translateZ;
	public int[] modifiedModelColors;
	public String interactions[];

	public ObjectDefinition() {
		type = -1;
	}

	private static ByteStreamExt stream;

	public static ObjectDefinition forId(int id) {
		if (id > streamIndices.length)
			id = streamIndices.length - 1;
		for (int index = 0; index < 20; index++)
			if (cache[index].type == id)
				return cache[index];
		cacheIndex = (cacheIndex + 1) % 20;
		ObjectDefinition objectDef = cache[cacheIndex];
		stream.currentOffset = streamIndices[id];
		objectDef.type = id;
		objectDef.reset();
		objectDef.readValues(stream);
		// System.out.println("Object name: "+objectDef.name+", id: "+id);
		boolean removeObject = id == 5244 || id == 2623 || id == 2956 || id == 463 || id == 462 || id == 10527
				|| id == 10529 || id == 40257 || id == 296 || id == 300 || id == 1747 || id == 7332 || id == 7326
				|| id == 7325 || id == 7385 || id == 7331 || id == 7385 || id == 7320 || id == 7317 || id == 7323
				|| id == 7354 || id == 1536 || id == 1537 || id == 5126 || id == 1551 || id == 1553 || id == 1516
				|| id == 1519 || id == 1557 || id == 1558 || id == 7126 || id == 733 || id == 14233 || id == 14235
				|| id == 1596 || id == 1597 || id == 14751 || id == 14752 || id == 14923 || id == 36844 || id == 30864
				|| id == 2514 || id == 1805 || id == 15536 || id == 2399 || id == 14749 || id == 29315 || id == 29316
				|| id == 29319 || id == 29320 || id == 29360 || id == 1528 || id == 36913 || id == 36915 || id == 15516
				|| id == 35549 || id == 35551 || id == 26808 || id == 26910 || id == 26913 || id == 24381 || id == 15514
				|| id == 25891 || id == 26082 || id == 26081 || id == 1530 || id == 16776 || id == 16778 || id == 28589
				|| id == 1533 || id == 17089 || id == 1600 || id == 1601 || id == 11707 || id == 24376 || id == 24378
				|| id == 40108 || id == 59 || id == 2069 || id == 36846;
		if (objectDef.name != null) {
			if (objectDef.name.toLowerCase().contains(("door")) || objectDef.name.toLowerCase().contains(("gate"))) {
				removeObject = true;
			}
		}
		if (removeObject) {
			objectDef.modelIds = null;
			objectDef.isInteractive = false;
			objectDef.solid = false;
			return objectDef;
		}

		if (id == 6552) {
			objectDef.interactions = new String[5];
			objectDef.interactions[0] = "Toggle-spells";
			objectDef.name = "Ancient altar";
		}

		if (id == 14911) {
			objectDef.interactions = new String[5];
			objectDef.interactions[0] = "Toggle-spells";
			objectDef.name = "Lunar altar";
		}

		if (id == 7149 || id == 7147) {
			objectDef.isInteractive = true;
			objectDef.interactions = new String[5];
			objectDef.interactions[0] = "Squeeze-Through";
			objectDef.name = "Gap";
		}
		if (id == 7152 || id == 7144) {
			objectDef.isInteractive = true;
			objectDef.interactions = new String[5];
			objectDef.interactions[0] = "Distract";
			objectDef.name = "Eyes";
		}
		if (id == 2164) {
			objectDef.isInteractive = true;
			objectDef.interactions = new String[5];
			objectDef.interactions[0] = "Fix";
			objectDef.interactions[1] = null;
			objectDef.name = "Trawler Net";
		}
		if (id == 1293) {
			objectDef.isInteractive = true;
			objectDef.interactions = new String[5];
			objectDef.interactions[0] = "Teleport";
			objectDef.interactions[1] = null;
			objectDef.name = "Spirit Tree";
		}
		if (id == 7152 || id == 7144) {
			objectDef.isInteractive = true;
			objectDef.interactions = new String[5];
			objectDef.interactions[0] = "Burn Down";
			objectDef.name = "Boil";
		}
		if (id == 7152 || id == 7144) {
			objectDef.isInteractive = true;
			objectDef.interactions = new String[5];
			objectDef.interactions[0] = "Chop";
			objectDef.name = "Tendrils";
		}
		if (id == 2452) {
			objectDef.isInteractive = true;
			objectDef.interactions = new String[5];
			objectDef.interactions[0] = "Go Through";
			objectDef.name = "Passage";
		}
		if (id == 7153) {
			objectDef.isInteractive = true;
			objectDef.interactions = new String[5];
			objectDef.interactions[0] = "Mine";
			objectDef.name = "Rock";
		}
		if (id == 2452 || id == 2455 || id == 2456 || id == 2454 || id == 2453 || id == 2461 || id == 2457 || id == 2461
				|| id == 2459 || id == 2460) {
			objectDef.isInteractive = true;
			objectDef.name = "Mysterious Ruins";
		}
		switch (id) {
		case 10638:
			objectDef.isInteractive = true;
			return objectDef;
		}

		return objectDef;
	}

	public void reset() {
		modelIds = null;
		modelTypes = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		objectSizeX = 1;
		objectSizeY = 1;
		solid = true;
		impenetrable = true;
		isInteractive = false;
		contouredGround = false;
		delayShading = false;
		occludes = false;
		animation = -1;
		decorDisplacement = 16;
		ambientLighting = 0;
		lightDiffusion = 0;
		interactions = null;
		minimapFunction = -1;
		mapscene = -1;
		inverted = false;
		castsShadow = true;
		scaleX = 128;
		scaleY = 128;
		scaleZ = 128;
		surroundings = 0;
		translateX = 0;
		translateY = 0;
		translateZ = 0;
		obstructsGround = false;
		hollow = false;
		supportItems = -1;
		varbit = -1;
		varp = -1;
		childrenIDs = null;
	}

	public static void init() {
		// long startup = System.currentTimeMillis();
		// System.out.println("Loading cache game object definitions...");

		CacheArchive objectDefs = Elvarg.getCache().getArchive(CacheConstants.CONFIG_ARCHIVE);
		stream = new ByteStreamExt(objectDefs.getData("loc.dat").array());
		ByteStreamExt idxBuffer525 = new ByteStreamExt(objectDefs.getData("loc.idx").array());

		int totalObjects525 = idxBuffer525.readUnsignedWord();
		streamIndices = new int[totalObjects525];
		int i = 2;
		for (int j = 0; j < totalObjects525; j++) {
			streamIndices[j] = i;
			i += idxBuffer525.readUnsignedWord();
		}

		cache = new ObjectDefinition[20];
		for (int k = 0; k < 20; k++) {
			cache[k] = new ObjectDefinition();
		}

		// System.out.println("Loaded " + totalObjects525 + " cache object
		// definitions #525 ");
		// + totalObjects667 + " cache object definitions #667 in " +
		// (System.currentTimeMillis() - startup) + "ms");
	}

	public void readValues(ByteStreamExt buffer) {
		int interactive = -1;
		do {
			int opCode = buffer.readUnsignedByte();
			if (opCode == 0)
				break;
			if (opCode == 1) {
				int count = buffer.readUnsignedByte();
				if (count > 0) {
					if (modelIds == null || lowMemory) {
						modelTypes = new int[count];
						modelIds = new int[count];
						for (int index = 0; index < count; index++) {
							modelIds[index] = buffer.readUnsignedWord();
							modelTypes[index] = buffer.readUnsignedByte();
						}
					} else {
						buffer.currentOffset += count * 3;
					}
				}
			} else if (opCode == 2)
				name = buffer.readString();
			else if (opCode == 3)
				description = buffer.readBytes();
			else if (opCode == 5) {
				int count = buffer.readUnsignedByte();
				if (count > 0) {
					if (modelIds == null || lowMemory) {
						modelTypes = null;
						modelIds = new int[count];
						for (int index = 0; index < count; index++)
							modelIds[index] = buffer.readUnsignedWord();
					} else {
						buffer.currentOffset += count * 2;
					}
				}
			} else if (opCode == 14)
				objectSizeX = buffer.readUnsignedByte();
			else if (opCode == 15)
				objectSizeY = buffer.readUnsignedByte();
			else if (opCode == 17)
				solid = false;
			else if (opCode == 18)
				impenetrable = false;
			else if (opCode == 19)
				isInteractive = (buffer.readUnsignedByte() == 1);
			else if (opCode == 21)
				contouredGround = true;
			else if (opCode == 22)
				delayShading = true;
			else if (opCode == 23)
				occludes = true;
			else if (opCode == 24) {
				animation = buffer.readUnsignedWord();
				if (animation == 65535)
					animation = -1;
			} else if (opCode == 28)
				decorDisplacement = buffer.readUnsignedByte();
			else if (opCode == 29)
				ambientLighting = buffer.readSignedByte();
			else if (opCode == 39)
				lightDiffusion = buffer.readSignedByte();
			else if (opCode >= 30 && opCode < 39) {
				if (interactions == null)
					interactions = new String[5];
				interactions[opCode - 30] = buffer.readString();
				if (interactions[opCode - 30].equalsIgnoreCase("hidden"))
					interactions[opCode - 30] = null;
			} else if (opCode == 40) {
				int i1 = buffer.readUnsignedByte();
				modifiedModelColors = new int[i1];
				originalModelColors = new int[i1];
				for (int i2 = 0; i2 < i1; i2++) {
					modifiedModelColors[i2] = buffer.readUnsignedWord();
					originalModelColors[i2] = buffer.readUnsignedWord();
				}

			} else if (opCode == 60)
				minimapFunction = buffer.readUnsignedWord();
			else if (opCode == 62)
				inverted = true;
			else if (opCode == 64)
				castsShadow = false;
			else if (opCode == 65)
				scaleX = buffer.readUnsignedWord();
			else if (opCode == 66)
				scaleY = buffer.readUnsignedWord();
			else if (opCode == 67)
				scaleZ = buffer.readUnsignedWord();
			else if (opCode == 68)
				mapscene = buffer.readUnsignedWord();
			else if (opCode == 69)
				surroundings = buffer.readUnsignedByte();
			else if (opCode == 70)
				translateX = buffer.readSignedWord();
			else if (opCode == 71)
				translateY = buffer.readSignedWord();
			else if (opCode == 72)
				translateZ = buffer.readSignedWord();
			else if (opCode == 73)
				obstructsGround = true;
			else if (opCode == 74)
				hollow = true;
			else if (opCode == 75)
				supportItems = buffer.readUnsignedByte();
			else if (opCode == 77) {
				varbit = buffer.readUnsignedWord();
				if (varbit == 65535)
					varbit = -1;
				varp = buffer.readUnsignedWord();
				if (varp == 65535)
					varp = -1;
				int count = buffer.readUnsignedByte();
				childrenIDs = new int[count + 1];
				for (int index = 0; index <= count; index++) {
					childrenIDs[index] = buffer.readUnsignedWord();
					if (childrenIDs[index] == 65535)
						childrenIDs[index] = -1;
				}
			}
		} while (true);
		if (interactive == -1 && name != "null" && name != null) {
			isInteractive = modelIds != null && (modelTypes == null || modelTypes[0] == 10);
			if (interactions != null)
				isInteractive = true;
		}
		if (hollow) {
			solid = false;
			impenetrable = false;
		}
		if (supportItems == -1)
			supportItems = solid ? 1 : 0;
	}

	public String getName() {
		return name;
	}

	public int getSizeX() {
		return objectSizeX;
	}

	public int getSizeY() {
		return objectSizeY;
	}

	public boolean hasActions() {
		return isInteractive;
	}
}