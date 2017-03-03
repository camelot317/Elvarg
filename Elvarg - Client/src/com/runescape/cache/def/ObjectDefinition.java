package com.runescape.cache.def;

import java.io.IOException;
import com.runescape.Client;
import com.runescape.cache.FileArchive;
import com.runescape.cache.anim.Frame;
import com.runescape.cache.config.VariableBits;
import com.runescape.collection.ReferenceCache;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import com.runescape.net.requester.ResourceProvider;

public final class ObjectDefinition {

	public boolean obstructsGround;
	public byte ambientLighting;
	public int translateX;
	public String name;
	public int scaleZ;
	public static final Model[] aModelArray741s = new Model[4];
	public byte lightDiffusion;
	public int objectSizeX;
	public int translateY;
	public int minimapFunction;
	public int[] originalModelColors;
	public int scaleX;
	public int varp;
	public boolean inverted;
	public static boolean lowMemory;
	public static Buffer stream;
	public int type;
	public static int[] streamIndices;
	public boolean impenetrable;
	public int mapscene;
	public int childrenIDs[];
	public int supportItems;
	public int objectSizeY;
	public boolean contouredGround;
	public boolean occludes;
	public static Client clientInstance;
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
	public static ReferenceCache models = new ReferenceCache(30);
	public int animation;
	public static ObjectDefinition[] cache;
	public int translateZ;
	public int[] modifiedModelColors;
	public static ReferenceCache baseModels = new ReferenceCache(500);
	public String interactions[];

	public ObjectDefinition() {
		type = -1;
	}

	public static ObjectDefinition lookup(int id) {
		if (id > streamIndices.length)
			id = streamIndices.length - 1;
		for (int index = 0; index < 20; index++)
			if (cache[index].type == id)
				return cache[index];
		
		if(id == 25913)
			id = 15552;
		if(id == 25916 || id == 25917)
			id = 15552;
		
		cacheIndex = (cacheIndex + 1) % 20;
		ObjectDefinition objectDef = cache[cacheIndex];
		stream.currentPosition = streamIndices[id];
		objectDef.type = id;
		objectDef.reset();
		objectDef.readValues(stream);
		if(objectDef.type > 15000) {
			objectDef.delayShading = false;
		}
		//	System.out.println("Object name: "+objectDef.name+", id: "+id);
		boolean removeObject = id == 5244 || id == 2623 || id == 2956 || id == 463 || id == 462 || id == 10527 || id == 10529 || id == 40257 || id == 296 || id == 300 || id == 1747 || id == 7332 || id == 7326 || id == 7325 || id == 7385 || id == 7331 || id == 7385 || id == 7320 || id == 7317 || id == 7323 || id == 7354 || id == 1536 || id == 1537 || id == 5126 || id == 1551 || id == 1553 || id == 1516 || id == 1519 || id == 1557 || id == 1558 || id == 7126 || id == 733 || id == 14233 || id == 14235 || id == 1596 || id == 1597 || id == 14751 || id == 14752 || id == 14923 || id == 36844 || id == 30864 || id == 2514 || id == 1805 || id == 15536 || id == 2399 || id == 14749 || id == 29315 || id == 29316 || id == 29319 || id == 29320 || id == 29360 || id == 1528 || id == 36913 || id == 36915 || id == 15516 || id == 35549 || id == 35551 || id == 26808 || id == 26910 || id == 26913 || id == 24381 || id == 15514 || id == 25891 || id == 26082 || id == 26081 || id == 1530 || id == 16776 || id == 16778 || id == 28589 || id == 1533 || id == 17089 || id == 1600 || id == 1601 || id == 11707 || id == 24376 || id == 24378 || id == 40108 || id == 59 || id == 2069 || id == 36846;
		if(objectDef.name != null) {
			if(objectDef.name.toLowerCase().contains(("door")) || objectDef.name.toLowerCase().contains(("gate")) || objectDef.name.toLowerCase().contains(("ditch"))) {
				removeObject = true;
			}
		}

		if(removeObject) {
			objectDef.modelIds = null;
			objectDef.isInteractive = false;
			objectDef.solid = false;
			return objectDef;
		}

		if(id == 6552) {
			objectDef.interactions = new String[5];
			objectDef.interactions[0] = "Toggle-spells";
			objectDef.name = "Ancient altar";
		}

		if(id == 14911) {
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
		if (id == 2452 || id == 2455 || id == 2456 || id == 2454 || id == 2453
				|| id == 2461 || id == 2457 || id == 2461 || id == 2459
				|| id == 2460) {
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

	/** HARDCODING OBJECT TEXTURES **/
	public void applyTexture(Model model) {

		//Venenatis texture fix
		if(type == 26764) {
			model.setTexture(26);
		}
	}

	public void loadModels(ResourceProvider archive) {
		if (modelIds == null)
			return;
		for (int index = 0; index < modelIds.length; index++)
			archive.loadExtra(modelIds[index] & 0xffff, 0);
	}

	public static void clear() {
		baseModels = null;
		models = null;
		streamIndices = null;
		cache = null;
		stream = null;
	}

	public static void init(FileArchive streamLoader) throws IOException {
		stream = new Buffer(streamLoader.readFile("loc.dat"));
		Buffer stream = new Buffer(streamLoader.readFile("loc.idx"));
		int totalObjects = stream.readUShort();
		streamIndices = new int[totalObjects];
		int offset = 2;
		for (int index = 0; index < totalObjects; index++) {
			streamIndices[index] = offset;
			offset += stream.readUShort();
		}
		cache = new ObjectDefinition[20];
		for (int index = 0; index < 20; index++)
			cache[index] = new ObjectDefinition();

		System.out.println("Loaded: " + totalObjects + " Objects");
	}

	public boolean method577(int i) {
		if (modelTypes == null) {
			if (modelIds == null)
				return true;
			if (i != 10)
				return true;
			boolean flag1 = true;
			for (int k = 0; k < modelIds.length; k++)
				flag1 &= Model.isCached(modelIds[k] & 0xffff);

			return flag1;
		}
		for (int j = 0; j < modelTypes.length; j++)
			if (modelTypes[j] == i)
				return Model.isCached(modelIds[j] & 0xffff);

		return true;
	}

	public Model modelAt(int type, int orientation, int aY, int bY, int cY, int dY, int frameId) {
		Model model = model(type, frameId, orientation);
		if (model == null)
			return null;
		if (contouredGround || delayShading)
			model = new Model(contouredGround, delayShading, model);
		if (contouredGround) {
			int y = (aY + bY + cY + dY) / 4;
			for (int vertex = 0; vertex < model.numVertices; vertex++) {
				int x = model.vertexX[vertex];
				int z = model.vertexZ[vertex];
				int l2 = aY + ((bY - aY) * (x + 64)) / 128;
				int i3 = dY + ((cY - dY) * (x + 64)) / 128;
				int j3 = l2 + ((i3 - l2) * (z + 64)) / 128;
				model.vertexY[vertex] += j3 - y;
			}

			model.computeSphericalBounds();
		}
		
		applyTexture(model);
		return model;
	}

	public boolean method579() {
		if (modelIds == null)
			return true;
		boolean flag1 = true;
		for (int i = 0; i < modelIds.length; i++)
			flag1 &= Model.isCached(modelIds[i] & 0xffff);
		return flag1;
	}

	public ObjectDefinition method580() {
		int i = -1;
		if (varbit != -1) {
			VariableBits varBit = VariableBits.varbits[varbit];
			int j = varBit.getSetting();
			int k = varBit.getLow();
			int l = varBit.getHigh();
			int i1 = Client.BIT_MASKS[l - k];
			i = clientInstance.settings[j] >> k & i1;
		} else if (varp != -1)
			i = clientInstance.settings[varp];
		if (i < 0 || i >= childrenIDs.length || childrenIDs[i] == -1)
			return null;
		else
			return lookup(childrenIDs[i]);
	}

	public Model model(int j, int k, int l) {
		Model model = null;
		long l1;
		if (modelTypes == null) {
			if (j != 10)
				return null;
			l1 = (long) ((type << 6) + l) + ((long) (k + 1) << 32);
			Model model_1 = (Model) models.get(l1);
			if (model_1 != null) {
				applyTexture(model_1);
				return model_1;
			}
			if (modelIds == null)
				return null;
			boolean flag1 = inverted ^ (l > 3);
			int k1 = modelIds.length;
			for (int i2 = 0; i2 < k1; i2++) {
				int l2 = modelIds[i2];
				if (flag1)
					l2 += 0x10000;
				model = (Model) baseModels.get(l2);
				if (model == null) {
					model = Model.getModel(l2 & 0xffff);
					if (model == null)
						return null;
					if (flag1)
						model.method477();
					baseModels.put(model, l2);
				}
				if (k1 > 1)
					aModelArray741s[i2] = model;
			}

			if (k1 > 1)
				model = new Model(k1, aModelArray741s);
		} else {
			int i1 = -1;
			for (int j1 = 0; j1 < modelTypes.length; j1++) {
				if (modelTypes[j1] != j)
					continue;
				i1 = j1;
				break;
			}

			if (i1 == -1)
				return null;
			l1 = (long) ((type << 8) + (i1 << 3) + l) + ((long) (k + 1) << 32);
			Model model_2 = (Model) models.get(l1);
			if (model_2 != null) {
				applyTexture(model_2);
				return model_2;
			}
			if(modelIds == null) {
				return null;
			}
			int j2 = modelIds[i1];
			boolean flag3 = inverted ^ (l > 3);
			if (flag3)
				j2 += 0x10000;
			model = (Model) baseModels.get(j2);
			if (model == null) {
				model = Model.getModel(j2 & 0xffff);
				if (model == null)
					return null;
				if (flag3)
					model.method477();
				baseModels.put(model, j2);
			}
		}
		boolean flag;
		flag = scaleX != 128 || scaleY != 128 || scaleZ != 128;
		boolean flag2;
		flag2 = translateX != 0 || translateY != 0 || translateZ != 0;
		Model model_3 = new Model(modifiedModelColors == null,
				Frame.noAnimationInProgress(k), l == 0 && k == -1 && !flag
				&& !flag2, model);
		if (k != -1) {
			model_3.skin();
			model_3.applyTransform(k);
			model_3.faceGroups = null;
			model_3.vertexGroups = null;
		}
		while (l-- > 0)
			model_3.rotate90Degrees();
		if (modifiedModelColors != null) {
			for (int k2 = 0; k2 < modifiedModelColors.length; k2++)
				model_3.recolor(modifiedModelColors[k2],
						originalModelColors[k2]);

		}
		if (flag)
			model_3.scale(scaleX, scaleZ, scaleY);
		if (flag2)
			model_3.translate(translateX, translateY, translateZ);
		model_3.light(84, 1500, -90, -280, -70, !delayShading);
		if (supportItems == 1)
			model_3.itemDropHeight = model_3.modelBaseY;
		models.put(model_3, l1);
		applyTexture(model_3);
		return model_3;
	}

	private void readValues(Buffer buffer) {
		int i = -1;
		label0: do {
			int opcode;
			do {
				opcode = buffer.readUnsignedByte();
				if (opcode == 0)
					break label0;
				if (opcode == 1) {
					int count = buffer.readUnsignedByte();
					if (modelIds == null || lowMemory) {
						modelTypes = new int[count];
						modelIds = new int[count];
						for (int index = 0; index < count; index++) {
							modelIds[index] = buffer.readUShort();
							modelTypes[index] = buffer.readUnsignedByte();
						}
					} else {
						buffer.currentPosition += count * 3;
					}
				} else if (opcode == 2)
					name = buffer.readString();
				else if (opcode == 3)
					description = buffer.readString().getBytes();
				else if (opcode == 5) {
					int count = buffer.readUnsignedByte();
					if (modelIds == null || lowMemory) {
						modelTypes = null;
						modelIds = new int[count];
						for (int index = 0; index < count; index++)
							modelIds[index] = buffer.readUShort();
					} else {
						buffer.currentPosition += count * 2;
					}
				} else if (opcode == 14)
					objectSizeX = buffer.readUnsignedByte();
				else if (opcode == 15)
					objectSizeY = buffer.readUnsignedByte();
				else if (opcode == 17)
					solid = false;
				else if (opcode == 18)
					impenetrable = false;
				else if (opcode == 19) {
					i = buffer.readUnsignedByte();
					if (i == 1)
						isInteractive = true;
				} else if (opcode == 21)
					contouredGround = true;
				else if (opcode == 22)
					delayShading = true;
				else if (opcode == 23)
					occludes = true;
				else if (opcode == 24) {
					animation = buffer.readUShort();
					if (animation == 65535)
						animation = -1;
				} else if (opcode == 28)
					decorDisplacement = buffer.readUnsignedByte();
				else if (opcode == 29)
					ambientLighting = buffer.readSignedByte();
				else if (opcode == 39)
					lightDiffusion = buffer.readSignedByte();
				else if (opcode >= 30 && opcode < 39) {
					if (interactions == null)
						interactions = new String[10];
					interactions[opcode - 30] = buffer.readString();
					if (interactions[opcode - 30].equalsIgnoreCase("hidden"))
						interactions[opcode - 30] = null;
				} else if (opcode == 40) {
					int i1 = buffer.readUnsignedByte();
					originalModelColors = new int[i1];
					modifiedModelColors = new int[i1];
					for (int i2 = 0; i2 < i1; i2++) {
						originalModelColors[i2] = buffer.readUShort();
						modifiedModelColors[i2] = buffer.readUShort();
					}
				} else if (opcode == 41) {
					int length = buffer.readUnsignedByte();
					//originalTexture = new int[length];
					//modifiedTexture = new int[length];
					for (int textureId = 0; textureId < length; textureId++) {
						/*	originalTexture[textureId] = */buffer.readUShort();
						/*	modifiedTexture[textureId] = */buffer.readUShort();
					}
				} else if (opcode == 60)
					minimapFunction = buffer.readUShort();
				else if (opcode == 62)
					inverted = true;
				else if (opcode == 64)
					castsShadow = false;
				else if (opcode == 65)
					scaleX = buffer.readUShort();
				else if (opcode == 66)
					scaleY = buffer.readUShort();
				else if (opcode == 67)
					scaleZ = buffer.readUShort();
				else if (opcode == 68)
					mapscene = buffer.readUShort();
				else if (opcode == 69)
					surroundings = buffer.readUnsignedByte();
				else if (opcode == 70)
					translateX = buffer.readShort();
				else if (opcode == 71)
					translateY = buffer.readShort();
				else if (opcode == 72)
					translateZ = buffer.readShort();
				else if (opcode == 73)
					obstructsGround = true;
				else if (opcode == 74) {
					hollow = true;
				} else {
					if (opcode != 75)
						continue;
					supportItems = buffer.readUnsignedByte();
				}
				continue label0;
			} while (opcode != 77);
			varbit = buffer.readUShort();
			if (varbit == 65535)
				varbit = -1;
			varp = buffer.readUShort();
			if (varp == 65535)
				varp = -1;
			int j1 = buffer.readUnsignedByte();
			childrenIDs = new int[j1 + 1];
			for (int j2 = 0; j2 <= j1; j2++) {
				childrenIDs[j2] = buffer.readUShort();
				if (childrenIDs[j2] == 65535)
					childrenIDs[j2] = -1;
			}

		} while (true);
		if (i == -1 && name != "null" && name != null) {
			isInteractive = modelIds != null
					&& (modelTypes == null || modelTypes[0] == 10);
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
}