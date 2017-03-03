package com.runescape.cache.graphics;

import com.runescape.Client;
import com.runescape.Configuration;
import com.runescape.cache.FileArchive;
import com.runescape.cache.anim.Frame;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.collection.ReferenceCache;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import com.runescape.util.StringUtils;

/**
 * Previously known as RSInterface, which is a class used to create and show
 * game interfaces.
 */
public final class Widget {

	public static final int OPTION_OK = 1;
	public static final int OPTION_USABLE = 2;
	public static final int OPTION_CLOSE = 3;
	public static final int OPTION_TOGGLE_SETTING = 4;
	public static final int OPTION_RESET_SETTING = 5;
	public static final int OPTION_CONTINUE = 6;

	public static final int TYPE_CONTAINER = 0;
	public static final int TYPE_MODEL_LIST = 1;
	public static final int TYPE_INVENTORY = 2;
	public static final int TYPE_RECTANGLE = 3;
	public static final int TYPE_TEXT = 4;
	public static final int TYPE_SPRITE = 5;
	public static final int TYPE_MODEL = 6;
	public static final int TYPE_ITEM_LIST = 7;

	private static final int[] SPECIAL_BARS =
		{
				7474,
				7499,
				7549,
				7574,
				7599,
				7624,
				7649,
				7674,
				7699,
				7800,
				8493,
				6117,
				7524,
				7749,
				7724,
				12323,
		};
	
	public void swapInventoryItems(int i, int j) {
		int id = inventoryItemId[i];
		inventoryItemId[i] = inventoryItemId[j];
		inventoryItemId[j] = id;
		id = inventoryAmounts[i];
		inventoryAmounts[i] = inventoryAmounts[j];
		inventoryAmounts[j] = id;
	}

	public static void load(FileArchive interfaces, GameFont textDrawingAreas[], FileArchive graphics) {
		spriteCache = new ReferenceCache(50000);
		Buffer buffer = new Buffer(interfaces.readFile("data"));
		int defaultParentId = -1;
		buffer.readUShort();
		interfaceCache = new Widget[52000];

		while (buffer.currentPosition < buffer.payload.length) {
			int interfaceId = buffer.readUShort();
			if (interfaceId == 65535) {
				defaultParentId = buffer.readUShort();
				interfaceId = buffer.readUShort();
			}

			Widget widget = interfaceCache[interfaceId] = new Widget();
			widget.id = interfaceId;
			widget.parent = defaultParentId;
			widget.type = buffer.readUnsignedByte();
			widget.atActionType = buffer.readUnsignedByte();
			widget.contentType = buffer.readUShort();
			widget.width = buffer.readUShort();
			widget.height = buffer.readUShort();
			widget.opacity = (byte) buffer.readUnsignedByte();
			widget.hoverType = buffer.readUnsignedByte();
			if (widget.hoverType != 0)
				widget.hoverType = (widget.hoverType - 1 << 8) + buffer.readUnsignedByte();
			else
				widget.hoverType = -1;
			int operators = buffer.readUnsignedByte();
			if (operators > 0) {
				widget.valueCompareType = new int[operators];
				widget.requiredValues = new int[operators];
				for (int index = 0; index < operators; index++) {
					widget.valueCompareType[index] = buffer.readUnsignedByte();
					widget.requiredValues[index] = buffer.readUShort();
				}

			}
			int scripts = buffer.readUnsignedByte();
			if (scripts > 0) {
				widget.valueIndexArray = new int[scripts][];
				for (int script = 0; script < scripts; script++) {
					int instructions = buffer.readUShort();
					widget.valueIndexArray[script] = new int[instructions];
					for (int instruction = 0; instruction < instructions; instruction++)
						widget.valueIndexArray[script][instruction] = buffer.readUShort();

				}

			}
			if (widget.type == TYPE_CONTAINER) {
				widget.drawsTransparent = false;
				widget.scrollMax = buffer.readUShort();
				widget.invisible = buffer.readUnsignedByte() == 1;
				int length = buffer.readUShort();

				if(widget.id == 5608) {

					widget.children = new int[PRAYER_INTERFACE_CHILDREN];
					widget.childX = new int[PRAYER_INTERFACE_CHILDREN];
					widget.childY = new int[PRAYER_INTERFACE_CHILDREN];

					for (int index = 0; index < length; index++) {
						widget.children[BEGIN_READING_PRAYER_INTERFACE+ index] = buffer.readUShort();
						widget.childX[BEGIN_READING_PRAYER_INTERFACE+ index] = buffer.readShort();
						widget.childY[BEGIN_READING_PRAYER_INTERFACE+ index] = buffer.readShort();
					}

				} else {


					widget.children = new int[length];
					widget.childX = new int[length];
					widget.childY = new int[length];

					for (int index = 0; index < length; index++) {
						widget.children[index] = buffer.readUShort();
						widget.childX[index] = buffer.readShort();
						widget.childY[index] = buffer.readShort();
					}
				}
			}
			if (widget.type == TYPE_MODEL_LIST) {
				buffer.readUShort();
				buffer.readUnsignedByte();
			}
			if (widget.type == TYPE_INVENTORY) {
				widget.inventoryItemId = new int[widget.width * widget.height];
				widget.inventoryAmounts = new int[widget.width * widget.height];
				widget.allowSwapItems = buffer.readUnsignedByte() == 1;
				widget.hasActions = buffer.readUnsignedByte() == 1;
				widget.usableItems = buffer.readUnsignedByte() == 1;
				widget.replaceItems = buffer.readUnsignedByte() == 1;
				widget.spritePaddingX = buffer.readUnsignedByte();
				widget.spritePaddingY = buffer.readUnsignedByte();
				widget.spritesX = new int[20];
				widget.spritesY = new int[20];
				widget.sprites = new Sprite[20];
				for (int j2 = 0; j2 < 20; j2++) {
					int k3 = buffer.readUnsignedByte();
					if (k3 == 1) {
						widget.spritesX[j2] = buffer.readShort();
						widget.spritesY[j2] = buffer.readShort();
						String s1 = buffer.readString();
						if (graphics != null && s1.length() > 0) {
							int i5 = s1.lastIndexOf(",");

							int index = Integer.parseInt(s1.substring(i5 + 1));

							String name = s1.substring(0, i5);

							widget.sprites[j2] = getSprite(index, graphics, name);
						}
					}
				}
				widget.actions = new String[5];
				for (int actionIndex = 0; actionIndex < 5; actionIndex++) {
					widget.actions[actionIndex] = buffer.readString();
					if (widget.actions[actionIndex].length() == 0)
						widget.actions[actionIndex] = null;
					if (widget.parent == 1644)
						widget.actions[2] = "Operate";
					if (widget.parent == 3824) {
						widget.actions[4] = "Buy X";
					}
					if (widget.parent == 3822) {
						widget.actions[4] = "Sell X";
					}
				}
			}
			if (widget.type == TYPE_RECTANGLE)
				widget.filled = buffer.readUnsignedByte() == 1;
			if (widget.type == TYPE_TEXT || widget.type == TYPE_MODEL_LIST) {
				widget.centerText = buffer.readUnsignedByte() == 1;
				int k2 = buffer.readUnsignedByte();
				if (textDrawingAreas != null)
					widget.textDrawingAreas = textDrawingAreas[k2];
				widget.textShadow = buffer.readUnsignedByte() == 1;
			}

			if (widget.type == TYPE_TEXT) {
				widget.defaultText = buffer.readString().replaceAll("RuneScape", Configuration.CLIENT_NAME);
				if (widget.id == 19209) {
					widget.defaultText.replaceAll("Total", "");
				}
				widget.secondaryText = buffer.readString();
			}

			if (widget.type == TYPE_MODEL_LIST || widget.type == TYPE_RECTANGLE || widget.type == TYPE_TEXT)
				widget.textColor = buffer.readInt();
			if (widget.type == TYPE_RECTANGLE || widget.type == TYPE_TEXT) {
				widget.secondaryColor = buffer.readInt();
				widget.defaultHoverColor = buffer.readInt();
				widget.secondaryHoverColor = buffer.readInt();
			}
			if (widget.type == TYPE_SPRITE) {
				widget.drawsTransparent = false;
				String name = buffer.readString();
				if (graphics != null && name.length() > 0) {
					int index = name.lastIndexOf(",");
					widget.disabledSprite = getSprite(Integer.parseInt(name.substring(index + 1)), graphics,
							name.substring(0, index));
				}
				name = buffer.readString();
				if (graphics != null && name.length() > 0) {
					int index = name.lastIndexOf(",");
					widget.enabledSprite = getSprite(Integer.parseInt(name.substring(index + 1)), graphics,
							name.substring(0, index));
				}
			}
			if (widget.type == TYPE_MODEL) {
				int content = buffer.readUnsignedByte();
				if (content != 0) {
					widget.defaultMediaType = 1;
					widget.defaultMedia = (content - 1 << 8) + buffer.readUnsignedByte();
				}
				content = buffer.readUnsignedByte();
				if (content != 0) {
					widget.anInt255 = 1;
					widget.anInt256 = (content - 1 << 8) + buffer.readUnsignedByte();
				}
				content = buffer.readUnsignedByte();
				if (content != 0)
					widget.defaultAnimationId = (content - 1 << 8) + buffer.readUnsignedByte();
				else
					widget.defaultAnimationId = -1;
				content = buffer.readUnsignedByte();
				if (content != 0)
					widget.secondaryAnimationId = (content - 1 << 8) + buffer.readUnsignedByte();
				else
					widget.secondaryAnimationId = -1;
				widget.modelZoom = buffer.readUShort();
				widget.modelRotation1 = buffer.readUShort();
				widget.modelRotation2 = buffer.readUShort();
			}
			if (widget.type == TYPE_ITEM_LIST) {
				widget.inventoryItemId = new int[widget.width * widget.height];
				widget.inventoryAmounts = new int[widget.width * widget.height];
				widget.centerText = buffer.readUnsignedByte() == 1;
				int l2 = buffer.readUnsignedByte();
				if (textDrawingAreas != null)
					widget.textDrawingAreas = textDrawingAreas[l2];
				widget.textShadow = buffer.readUnsignedByte() == 1;
				widget.textColor = buffer.readInt();
				widget.spritePaddingX = buffer.readShort();
				widget.spritePaddingY = buffer.readShort();
				widget.hasActions = buffer.readUnsignedByte() == 1;
				widget.actions = new String[5];
				for (int actionCount = 0; actionCount < 5; actionCount++) {
					widget.actions[actionCount] = buffer.readString();
					if (widget.actions[actionCount].length() == 0)
						widget.actions[actionCount] = null;
				}

			}
			if (widget.atActionType == OPTION_USABLE || widget.type == TYPE_INVENTORY) {
				widget.selectedActionName = buffer.readString();
				widget.spellName = buffer.readString();
				widget.spellUsableOn = buffer.readUShort();
			}

			if (widget.type == 8) {
				widget.defaultText = buffer.readString();
			}

			if (widget.atActionType == OPTION_OK || widget.atActionType == OPTION_TOGGLE_SETTING
					|| widget.atActionType == OPTION_RESET_SETTING || widget.atActionType == OPTION_CONTINUE) {
				widget.tooltip = buffer.readString();
				if (widget.tooltip.length() == 0) {
					// TODO
					if (widget.atActionType == OPTION_OK)
						widget.tooltip = "Ok";
					if (widget.atActionType == OPTION_TOGGLE_SETTING)
						widget.tooltip = "Select";
					if (widget.atActionType == OPTION_RESET_SETTING)
						widget.tooltip = "Select";
					if (widget.atActionType == OPTION_CONTINUE)
						widget.tooltip = "Continue";
				}
			}
		}
		interfaceLoader = interfaces;
		clanChatTab(textDrawingAreas);
		configureLunar(textDrawingAreas);
		quickPrayers(textDrawingAreas);
		equipmentScreen(textDrawingAreas);
		equipmentTab(textDrawingAreas);
		itemsKeptOnDeath(textDrawingAreas);
		bounty(textDrawingAreas);
		repositionModernSpells();
		shop();
		prayerBook();
		priceChecker(textDrawingAreas);
		
		bankInterface(textDrawingAreas);
		bankSettings(textDrawingAreas);
		
		killFeed(textDrawingAreas);
		teleportInterface(textDrawingAreas);

		mainTeleports();
		normalSpellbookEdit(textDrawingAreas);
		ancientSpellbookEdit(textDrawingAreas);
		settingsTab();

		//specialBars();
		
		spriteCache = null;
	}

	public static void settingsTab() {
		Widget p = addTabInterface(42500);

		//Removing adjust bars such as music/sounds
		int[] to_remove = {19131, 19149, 19157, 22635, 941, 942, 943, 944, 945, 19150, 19151, 19152, 19153, 19154, 19155};
		for(int i : to_remove) {
			removeSomething(i);
		}

		for(int i : new int[]{930, 931, 932, 933, 934, 22634}) {
			interfaceCache[i].tooltip
			= interfaceCache[i].defaultText = "Adjust Camera Zoom";
		}

		//Adding zoom image
		addSpriteLoader(42508, 189);

		//Adding key bindings image
		addSpriteLoader(42510, 190);
		addButton(42511, 42500, 30, 30, interfaceCache[19156].disabledSprite, interfaceCache[19156].enabledSprite, -1, "Adjust Key bindings");
		removeSomething(19156); //Removes house button

		//Adding screen sizes
		addButton(42501, 42500, 54, 46, 185, 186, 42502, "Fixed Screen");
		addHoveredButton_sprite_loader(42502, 186, 54, 46, 42503);

		addButton(42504, 42500, 54, 46, 187, 188, 42505, "Resized Screen");
		addHoveredButton_sprite_loader(42505, 188, 54, 46, 42506);

		p.totalChildren(8);

		//Screen sizes
		setBounds(42501, 30, 95, 0, p);
		setBounds(42502, 30, 95, 1, p);
		setBounds(42504, 110, 95, 2, p);
		setBounds(42505, 110, 95, 3, p);

		//Camera zoom image
		setBounds(42508, 10, 49, 4, p);

		//key bindings images
		setBounds(42511, 132, 212, 5, p);
		setBounds(42510, 136, 215, 6, p);

		//Main settings interface
		setBounds(904, 0, 0, 7, p);

	}

	public static void mainTeleports() {
		addButton(39101, 38100, 79, 30, 1, 805, 174, 175, 39102, "Home Teleport");
		addHoveredButton_sprite_loader(39102, 175, 79, 30, 39103);
		addButton(39104, 38100, 79, 30, 1, 806, 176, 177, 39105, "Other Teleports");
		addHoveredButton_sprite_loader(39105, 177, 79, 30, 39106);
	}

	public static void ancientSpellbookEdit(GameFont[] t) {
		Widget tab = addInterface(39100);
		tab.totalChildren(36);

		//ADD "HOME" AND "OTHER" TELEPORTS
		setBounds(39101, 10, 9, 0, tab);
		setBounds(39102, 10, 9, 1, tab);
		setBounds(39104, 105, 9, 2, tab);
		setBounds(39105, 105, 9, 3, tab);

		//Row 1
		setBounds(12939, 25, 50, 4, tab);
		setBounds(12987, 65, 50, 5, tab);
		setBounds(12901, 105, 50, 6, tab);
		setBounds(12861, 145, 50, 7, tab);

		//Row 2
		setBounds(12963, 25, 90, 8, tab);
		setBounds(13011, 65, 90, 9, tab);
		setBounds(12919, 105, 90, 10, tab);
		setBounds(12881, 145, 90, 11, tab);

		//Row 3
		setBounds(12951, 25, 130, 12, tab);
		setBounds(12999, 65, 130, 13, tab);
		setBounds(12911, 105, 130, 14, tab);
		setBounds(12871, 145, 130, 15, tab);

		//Row 4
		setBounds(12975, 25, 170, 16, tab);
		setBounds(13023, 65, 170, 17, tab);
		setBounds(12929, 105, 170, 18, tab);
		setBounds(12891, 145, 170, 19, tab);

		//Spell hovers

		//Row 1
		setBounds(21758, 3, 180, 20, tab);
		setBounds(21793, 3, 180, 21, tab);
		setBounds(21874, 3, 180, 22, tab);
		setBounds(21903, 3, 180, 23, tab);

		//Row 2
		setBounds(21988, 3, 180, 24, tab);
		setBounds(22018, 3, 180, 25, tab);
		setBounds(22068, 3, 180, 26, tab);
		setBounds(22093, 3, 180, 27, tab);

		//Row 3
		setBounds(22169, 3, 180, 28, tab);
		setBounds(22198, 3, 180, 29, tab);
		setBounds(22252, 3, 180, 30, tab);
		setBounds(22277, 3, 180, 31, tab);

		//Row 4
		setBounds(22352, 3, 10, 32, tab);
		setBounds(22381, 3, 10, 33, tab);
		setBounds(22431, 3, 10, 34, tab);
		setBounds(22460, 3, 10, 35, tab);
	}

	public static void normalSpellbookEdit(GameFont[] t) {
		Widget tab = addInterface(39000);
		tab.totalChildren(62);

		//ADD "HOME" AND "OTHER" TELEPORTS
		setBounds(39101, 10, 9, 0, tab);
		setBounds(39102, 10, 9, 1, tab);
		setBounds(39104, 105, 9, 2, tab);
		setBounds(39105, 105, 9, 3, tab);

		//Row 1
		setBounds(1152, 10, 50, 4, tab);
		setBounds(1154, 40, 50, 5, tab);
		setBounds(1156, 70, 50, 6, tab);
		setBounds(1158, 100, 50, 7, tab);

		//Row 2
		setBounds(1160, 10, 80, 8, tab);
		setBounds(1163, 40, 80, 9, tab);
		setBounds(1166, 70, 80, 10, tab);
		setBounds(1169, 100, 80, 11, tab);

		//Row 3
		setBounds(1172, 10, 110, 12, tab);
		setBounds(1175, 40, 110, 13, tab);
		setBounds(1177, 70, 110, 14, tab);
		setBounds(1181, 100, 110, 15, tab);

		//Row 4
		setBounds(1183, 10, 140, 16, tab);
		setBounds(1185, 40, 140, 17, tab);
		setBounds(1188, 70, 140, 18, tab);
		setBounds(1189, 100, 140, 19, tab);

		//Row 5
		setBounds(1539, 9, 172, 20, tab);
		setBounds(1190, 40, 175, 21, tab);
		setBounds(1191, 70, 173, 22, tab);
		setBounds(1192, 100, 175, 23, tab);

		//Row 6
		setBounds(1572, 35, 230, 24, tab);
		setBounds(1582, 65, 230, 25, tab);
		setBounds(1592, 95, 230, 26, tab);
		setBounds(12445, 125, 230, 27, tab);


		//Side row
		setBounds(1159, 160, 50, 28, tab);
		setBounds(15877, 160, 80, 29, tab);
		setBounds(1173, 164, 110, 30, tab);
		setBounds(1162, 164, 140, 31, tab);
		setBounds(1178, 160, 170, 32, tab);


		//HOVERS

		//Row 1
		setBounds(19226, 3, 180, 33, tab);
		setBounds(19297, 3, 180, 34, tab);
		setBounds(19371, 3, 180, 35, tab);
		setBounds(19429, 3, 180, 36, tab);
		setBounds(19458, 3, 180, 37, tab);

		//Row 2
		setBounds(19487, 3, 180, 38, tab);
		setBounds(19591, 3, 180, 39, tab);
		setBounds(19672, 3, 180, 40, tab);
		setBounds(19753, 3, 180, 41, tab);
		setBounds(20418, 3, 180, 42, tab);

		//Row 3
		setBounds(19897, 3, 180, 43, tab);
		setBounds(19966, 3, 180, 44, tab);
		setBounds(20201, 3, 180, 45, tab);
		setBounds(20360, 3, 180, 46, tab);
		setBounds(19920, 3, 180, 47, tab);

		//Row 4
		setBounds(20576, 3, 180, 48, tab);
		setBounds(20663, 3, 180, 49, tab);
		setBounds(20780, 3, 180, 50, tab);
		setBounds(20867, 3, 180, 51, tab);
		setBounds(19568, 3, 180, 52, tab);

		//Row 5
		setBounds(20088, 3, 10, 53, tab);
		setBounds(20448, 3, 10, 54, tab);
		setBounds(20483, 3, 10, 55, tab);
		setBounds(20518, 3, 10, 56, tab);
		setBounds(20230, 3, 10, 57, tab);

		//Row 6
		setBounds(19539, 3, 10, 58, tab);
		setBounds(20119, 3, 10, 59, tab);
		setBounds(20896, 3, 10, 60, tab);
		setBounds(21012, 3, 10, 61, tab);
	}

	public static void teleportInterface(GameFont[] t) {
		Widget tab = addInterface(38100);
		tab.totalChildren(13);
		tab.drawsTransparent = true;

		//Background
		addTransparentSprite(38101, 163, 255);
		setBounds(38101, 5, 5, 0, tab);

		//Buttons

		/** MONSTERS **/
		addButton(38102, 38100, 173, 38, 1, 800, 164, 165, 38103, "Monsters");
		addHoveredButton_sprite_loader(38103, 165, 173, 38, 38104);
		setBounds(38102, 15, 45, 1, tab);
		setBounds(38103, 15, 45, 2, tab);

		/** BOSSES **/
		addButton(38105, 38100, 173, 38, 1, 801, 166, 167, 38106, "Bosses");
		addHoveredButton_sprite_loader(38106, 167, 173, 38, 38107);
		setBounds(38105, 15, 88, 3, tab);
		setBounds(38106, 15, 88, 4, tab);

		/** Skills **/
		addButton(38108, 38100, 173, 38, 1, 802, 168, 169, 38109, "Skills");
		addHoveredButton_sprite_loader(38109, 169, 173, 38, 38110);
		setBounds(38108, 15, 131, 5, tab);
		setBounds(38109, 15, 131, 6, tab);

		/** Minigames **/
		addButton(38111, 38100, 173, 38, 1, 803, 170, 171, 38112, "Minigames");
		addHoveredButton_sprite_loader(38112, 171, 173, 38, 38113);
		setBounds(38111, 15, 174, 7, tab);
		setBounds(38112, 15, 174, 8, tab);		

		/** Wilderness **/
		addButton(38114, 38100, 173, 38, 1, 804, 172, 173, 38115, "Wilderness");
		addHoveredButton_sprite_loader(38115, 173, 173, 38, 38116);
		setBounds(38114, 15, 217, 9, tab);
		setBounds(38115, 15, 217, 10, tab);

		//Close button
		addHoverButton_sprite_loader(38117, 137, 17, 17, "Close", -1, 38118, 1);
		addHoveredButton_sprite_loader(38118, 138, 17, 17, 38119);
		setBounds(38117, 480, 15, 11, tab);
		setBounds(38118, 480, 15, 12, tab);

		monsters(t);
		bosses(t);
		skills(t);
		minigames(t);
		wilderness(t);
	}

	public static void monsters(GameFont[] t) {
		Widget tab = addInterface(38200);
		tab.parent = 38100;
		tab.totalChildren(2);		
		setBounds(38100, 0, 0, 0, tab); //Main interface
		setBounds(38201, 180, 47, 1, tab); //Scroll interface (monsters below)

		Widget scroll = addInterface(38201);
		scroll.width = 300;
		scroll.height = 270; 
		scroll.scrollPosition = 0;
		scroll.scrollMax = 400;

		//Add all monsters into the scroll..
		String[] tooltips = {"Teleport: Rock Crabs", "Teleport: Pack Yaks", "Teleport: Experiments", "Teleport: Zombies", "Teleport: Bandits", "Teleport: Rock Crab", "Teleport: Rock Crab", "Teleport: Rock Crab", "Teleport: Rock Crab"};
		int[] sprites = {178, 179, 178, 179, 178, 179, 178, 179, 178, 179, 178, 179, 178, 179, 178, 179, 178, 179};
		int sprite_w = 65;
		int sprite_h = 54;
		int index = 0;

		scroll.totalChildren(tooltips.length * 3);

		int frame = 38202, frameHover = frame + 1, bounds = 0;
		for(int i = 0, counter = 0, yDraw = 0; i < tooltips.length; i++, frame+=4, frameHover +=4, counter++, index+=2) {

			int hoverXOffset = 1;
			int hoverYOffset = 55;

			if(counter == 3) {
				hoverXOffset = -120;
				hoverYOffset = 20;
			} else if(counter == 4) {
				counter = 0;
				yDraw += 60;
			}

			int x = 22 + (counter * 70);

			String s = tooltips[i];
			addButton(frame, 38200, sprite_w, sprite_h, sprites[index], sprites[index], frameHover, s);
			addHoveredButtonWTooltip(frameHover, sprites[index+1], sprite_w, sprite_h, frameHover + 1, frameHover + 2, s, hoverXOffset, hoverYOffset);
			setBounds(frame, x, yDraw, bounds++, scroll);
			setBounds(frameHover, x, yDraw, bounds++, scroll);
		}

		//Now do hovers so that sprites dont draw over
		frameHover = 38203; 
		for(int i = 0, counter = 0, yDraw = 0; i < tooltips.length; i++, frameHover +=4, counter++) {

			if(counter == 4) {
				counter = 0;
				yDraw += 60;
			}

			int x = 22 + (counter * 70);
			setBounds(frameHover + 2, x, yDraw, bounds++, scroll); // HOVER
		}
	}

	public static void bosses(GameFont[] t) {
		Widget tab = addInterface(38300);
		tab.parent = 38100;
		tab.totalChildren(1);
		setBounds(38100, 0, 0, 0, tab);
	}

	public static void skills(GameFont[] t) {
		Widget tab = addInterface(38400);
		tab.parent = 38100;
		tab.totalChildren(1);
		setBounds(38100, 0, 0, 0, tab);
	}

	public static void minigames(GameFont[] t) {
		Widget tab = addInterface(38500);
		tab.parent = 38100;
		tab.totalChildren(1);
		setBounds(38100, 0, 0, 0, tab);
	}

	public static void wilderness(GameFont[] t) {
		Widget tab = addInterface(38600);
		tab.parent = 38100;
		tab.totalChildren(1);
		setBounds(38100, 0, 0, 0, tab);
	}

	public static void addButton(int i, int parent, int w, int h, int config, int configFrame, int sprite1, int sprite2, int hoverOver, String tooltip) {
		Widget p = addInterface(i);
		p.parent = parent;
		p.type = TYPE_SPRITE;
		p.atActionType = 1;
		p.width = w;
		p.height = h;
		p.requiredValues = new int[1];
		p.valueCompareType = new int[1];
		p.valueCompareType[0] = 1;
		p.requiredValues[0] = config;
		p.valueIndexArray = new int[1][3];
		p.valueIndexArray[0][0] = 5;
		p.valueIndexArray[0][1] = configFrame;
		p.valueIndexArray[0][2] = 0;
		p.tooltip = tooltip;
		p.defaultText = tooltip;
		p.hoverType = hoverOver;
		p.disabledSprite = Client.cacheSprite[sprite1];
		p.enabledSprite = Client.cacheSprite[sprite2];
	}

	public static void addButton(int i, int parent, int w, int h, int sprite1, int sprite2, int hoverOver, String tooltip) {
		Widget p = addInterface(i);
		p.parent = parent;
		p.type = TYPE_SPRITE;
		p.atActionType = 1;
		p.width = w;
		p.height = h;
		p.tooltip = tooltip;
		p.defaultText = tooltip;
		p.hoverType = hoverOver;
		p.disabledSprite = Client.cacheSprite[sprite1];
		p.enabledSprite = Client.cacheSprite[sprite2];
	}


	public static void addButton(int i, int parent, int w, int h, Sprite sprite1, Sprite sprite2, int hoverOver, String tooltip) {
		Widget p = addInterface(i);
		p.parent = parent;
		p.type = TYPE_SPRITE;
		p.atActionType = 1;
		p.width = w;
		p.height = h;
		p.tooltip = tooltip;
		p.defaultText = tooltip;
		p.hoverType = hoverOver;
		p.disabledSprite = sprite1;
		p.enabledSprite = sprite2;
	}

	public static void addHoveredButtonWTooltip(int i, int spriteId, int w, int h, int IMAGEID, int tooltipId, String hover, int hoverXOffset, int hoverYOffset) {// hoverable
		// button
		Widget tab = addTabInterface(i);
		tab.parent = i;
		tab.id = i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = w;
		tab.height = h;
		tab.invisible = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		addHoverImage_sprite_loader(IMAGEID, spriteId);
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);

		Widget p = addTabInterface(tooltipId);
		p.parent = i;
		p.type = 8;
		p.width = w;
		p.height = h;

		p.hoverText = p.defaultText = 
				p.tooltip = hover;

		p.hoverXOffset = hoverXOffset;
		p.hoverYOffset = hoverYOffset;
		p.regularHoverBox = true;		

	}

	public static void killFeed(GameFont[] t) {
		Widget tab = addInterface(38000);
		tab.totalChildren(13);
		int y = 45;
		for(int i = 38001, index = 0; i < 38014; i++, index++) {
			addText(i, "", t, 0, 0xff9040, false, true);
			setBounds(i, 10, y, index, tab);
			y += 20;
		}
	}

	public static final int BEGIN_READING_PRAYER_INTERFACE = 6;//Amount of total custom prayers we've added
	public static final int CUSTOM_PRAYER_HOVERS = 3; //Amount of custom prayer hovers we've added

	public static final int PRAYER_INTERFACE_CHILDREN = 80 + BEGIN_READING_PRAYER_INTERFACE + CUSTOM_PRAYER_HOVERS;

	public int hoverXOffset = 0;
	public int hoverYOffset = 0;
	public int spriteXOffset = 0;
	public int spriteYOffset = 0;
	public boolean regularHoverBox;

	public static void prayerBook() {

		Widget rsinterface = interfaceCache[5608];

		//Moves down chivalry
		rsinterface.childX[50 + BEGIN_READING_PRAYER_INTERFACE] = 10;
		rsinterface.childY[50+ BEGIN_READING_PRAYER_INTERFACE] = 195;
		rsinterface.childX[51+ BEGIN_READING_PRAYER_INTERFACE] = 10;
		rsinterface.childY[51+ BEGIN_READING_PRAYER_INTERFACE] = 195;
		rsinterface.childX[63+ BEGIN_READING_PRAYER_INTERFACE] = 10;
		rsinterface.childY[63+ BEGIN_READING_PRAYER_INTERFACE] = 190;

		//Adjust prayer glow sprites position
		interfaceCache[rsinterface.children[50+ BEGIN_READING_PRAYER_INTERFACE]].spriteXOffset = -7;
		interfaceCache[rsinterface.children[50+ BEGIN_READING_PRAYER_INTERFACE]].spriteYOffset = -2;


		//Moves piety to the right
		rsinterface.childX[52+ BEGIN_READING_PRAYER_INTERFACE] = 43;
		rsinterface.childY[52+ BEGIN_READING_PRAYER_INTERFACE] = 204;
		rsinterface.childX[53+ BEGIN_READING_PRAYER_INTERFACE] = 43;
		rsinterface.childY[53+ BEGIN_READING_PRAYER_INTERFACE] = 204;
		rsinterface.childX[64+ BEGIN_READING_PRAYER_INTERFACE] = 43;
		rsinterface.childY[64+ BEGIN_READING_PRAYER_INTERFACE] = 190;

		//Adjust prayer glow sprites
		interfaceCache[rsinterface.children[52+ BEGIN_READING_PRAYER_INTERFACE]].spriteXOffset = -2;
		interfaceCache[rsinterface.children[52+ BEGIN_READING_PRAYER_INTERFACE]].spriteYOffset = -11;

		//Now we add new prayers..
		//AddPrayer adds a glow at the id
		//Adds the actual prayer sprite at id+1
		//Adds a hover box at id + 2
		addPrayer(28001, "Activate @or1@Preserve", 31, 32, 150, -2, -1, 151, 152, 1, 708, 28003);
		setBounds(28001, 153, 158, 0, rsinterface); //Prayer glow sprite
		setBounds(28002, 153, 158, 1, rsinterface); //Prayer sprites


		addPrayer(28004, "Activate @or1@Rigour", 31, 32, 150, -3, -5, 153, 154, 1, 710, 28006);
		setBounds(28004, 84, 198, 2, rsinterface); //Prayer glow sprite
		setBounds(28005, 84, 198, 3, rsinterface); //Prayer sprites

		addPrayer(28007, "Activate @or1@Augury", 31, 32, 150, -3, -5, 155, 156, 1, 712, 28009);
		setBounds(28007, 120, 198, 4, rsinterface); //Prayer glow sprite
		setBounds(28008, 120, 198, 5, rsinterface); //Prayer sprites

		//Now we add hovers..
		addPrayerHover(28003, "Level 55\nPreserve\nBoosted stats last 20% longer.", -135, -60);
		setBounds(28003, 153, 158,  86, rsinterface); //Hover box

		addPrayerHover(28006, "Level 74\nRigour\nIncreases your Ranged attack\nby 20% and damage by 23%,\nand your defence by 25%", -70, -100);
		setBounds(28006, 84, 200, 87, rsinterface); //Hover box

		addPrayerHover(28009, "Level 77\nAugury\nIncreases your Magic attack\nby 25% and your defence by 25%", -110, -100);
		setBounds(28009, 120, 198, 88, rsinterface); //Hover box

	}

	public static void addPrayer(int ID, String tooltip, int w, int h, int glowSprite, int glowX, int glowY, int disabledSprite, int enabledSprite, int config, int configFrame, int hover) {
		Widget p = addTabInterface(ID);

		//Adding config-toggleable glow on the prayer
		//Also clickable
		p.parent = 5608;
		p.type = TYPE_SPRITE;
		p.atActionType = 1;
		p.width = w;
		p.height = h;
		p.requiredValues = new int[1];
		p.valueCompareType = new int[1];
		p.valueCompareType[0] = 1;
		p.requiredValues[0] = config;
		p.valueIndexArray = new int[1][3];
		p.valueIndexArray[0][0] = 5;
		p.valueIndexArray[0][1] = configFrame;
		p.valueIndexArray[0][2] = 0;
		p.tooltip = tooltip;
		p.defaultText = tooltip;
		p.hoverType = 52;
		p.enabledSprite = Client.cacheSprite[glowSprite];
		p.spriteXOffset = glowX;
		p.spriteYOffset = glowY;

		//Adding config-toggleable prayer sprites
		//not clickable
		p = addTabInterface(ID + 1);
		p.parent = 5608;
		p.type = TYPE_SPRITE;
		p.atActionType = 0;
		p.width = w;
		p.height = h;
		p.requiredValues = new int[1];
		p.valueCompareType = new int[1];
		p.valueCompareType[0] = 2;
		p.requiredValues[0] = 1;
		p.valueIndexArray = new int[1][3];
		p.valueIndexArray[0][0] = 5;
		p.valueIndexArray[0][1] = configFrame + 1;
		p.valueIndexArray[0][2] = 0;
		p.tooltip = tooltip;
		p.defaultText = tooltip;
		p.enabledSprite = Client.cacheSprite[disabledSprite]; //imageLoader(disabledSprite, "s");
		p.disabledSprite = Client.cacheSprite[enabledSprite]; //imageLoader(enabledSprite, "s");
		p.hoverType = hover;
	}

	public static void addPrayerHover(int ID, String hover, int xOffset, int yOffset) {
		//Adding hover box
		Widget p = addTabInterface(ID);
		p.parent = 5608;
		p.type = 8;
		p.width = 40;
		p.height = 32;
		p.hoverText = p.defaultText =  hover;
		p.hoverXOffset = xOffset;
		p.hoverYOffset = yOffset;
		p.regularHoverBox = true;
	}

	/*
	 * Price checker interface
	 */
	private static void priceChecker(GameFont[] fonts) {
		Widget rsi = addTabInterface(42000);
		final String[] options = {"Remove 1", "Remove 5", "Remove 10", "Remove All", "Remove X"};
		addAdvancedSprite(18245, 180);

		addHoverButton_sprite_loader(18247, 137, 17, 17, "Close", -1, 18250, 1);
		addHoveredButton_sprite_loader(18250, 138, 17, 17, 18251);

		addHoverButton_sprite_loader(18252, 181, 35, 35, "Deposit All", -1, 18253, 1);
		addHoveredButton_sprite_loader(18253, 182, 35, 35, 18254);

		addHoverButton_sprite_loader(18255, 183, 35, 35, "Withdraw All", -1, 18256, 1);
		addHoveredButton_sprite_loader(18256, 184, 35, 35, 18257);

		addText(18351, "0", fonts, 0, 0xFFFFFF, true, true);
		addText(18355, "", fonts, 0, 0xFFFFFF, true, true);

		//Actual items
		Widget container = addTabInterface(18500);
		container.actions = options;
		container.spritesX = new int[20];
		container.spritesY = new int[20];
		container.inventoryItemId = new int[24];
		container.inventoryAmounts = new int[24];
		container.centerText = true;
		container.filled = false;
		container.replaceItems = false;
		container.usableItems = false;
		//rsi.isInventoryInterface = false;
		container.allowSwapItems = false;
		container.spritePaddingX = 50;
		container.spritePaddingY = 30;
		container.height = 6;
		container.width = 6;
		container.parent = 42000;
		container.type = TYPE_INVENTORY;

		rsi.totalChildren(58);
		int child = 0;

		rsi.child(child++, 18245, 10, 20);//was 10 so + 10
		rsi.child(child++, 18247, 471, 23);		
		rsi.child(child++, 18351, 251, 306);
		rsi.child(child++, 18355, 260, 155);
		rsi.child(child++, 18250, 471, 23); //Close button hover	
		rsi.child(child++, 18500, 28, 50); //Container

		//Deposit hovers
		rsi.child(child++, 18252, 455, 285);
		rsi.child(child++, 18253, 455, 285);

		rsi.child(child++, 18255, 420, 285);
		rsi.child(child++, 18256, 420, 285);

		//Add text next to items, ROW 1
		int interface_ = 18300;
		int xDraw = 47;
		int yDraw = 81;
		int counter = 0;
		for(int i = 0; i < container.inventoryItemId.length; i++) {

			addText(interface_, "", fonts, 0, 0xFFFFFF, true, true);
			rsi.child(child++, interface_, xDraw, yDraw);

			interface_++;
			counter++;
			xDraw += 80;

			if(counter == container.width) {
				xDraw = 47;
				yDraw += 62;
				counter = 0;
			}
		}

		//Add text next to items, ROW 2
		interface_ = 18400;
		xDraw = 47;
		yDraw = 93;
		counter = 0;
		for(int i = 0; i < container.inventoryItemId.length; i++) {

			addText(interface_, "", fonts, 0, 0xFFFFFF, true, true);
			rsi.child(child++, interface_, xDraw, yDraw);

			interface_++;
			counter++;
			xDraw += 80;

			if(counter == container.width) {
				xDraw = 47;
				yDraw += 62;
				counter = 0;
			}
		}
	}

	public static void shop() {
		Widget rsinterface = interfaceCache[3900];
		rsinterface.inventoryItemId = new int[42];
		rsinterface.inventoryAmounts = new int[42];
		rsinterface.drawInfinity = true;
		rsinterface.width = 10;
		rsinterface.height = 4;
		rsinterface.spritePaddingX = 15;
		rsinterface.spritePaddingY = 25;

		//Position the item container in the actual shop interface
		rsinterface = interfaceCache[3824];
		setBounds(3900, 26, 65, 75, rsinterface);
	}

	public static void bounty(GameFont[] TDA) {
		Widget tab = addTabInterface(23300);
		addTransparentSprite(23301, 97, 150);

		addConfigSprite(23303, -1, 98, 0, 876);
		//  addSprite(23304, 104);

		addText(23305, "---", TDA, 0, 0xffff00, true, true);
		addText(23306, "Target:", TDA, 0, 0xffff00, true, true);
		addText(23307, "None", TDA, 1, 0xffffff, true, true);
		addText(23308, "Level: ------", TDA, 0, 0xffff00, true, true);

		addText(23309, "Current  Record", TDA, 0, 0xffff00, true, true);
		addText(23310, "0", TDA, 0, 0xffff00, true, true);
		addText(23311, "0", TDA, 0, 0xffff00, true, true);
		addText(23312, "0", TDA, 0, 0xffff00, true, true);
		addText(23313, "0", TDA, 0, 0xffff00, true, true);
		addText(23314, "Rogue:", TDA, 0, 0xffff00, true, true);
		addText(23315, "Hunter:", TDA, 0, 0xffff00, true, true);

		addConfigSprite(23316, -1, 99, 0, 877);
		addConfigSprite(23317, -1, 100, 0, 878);
		addConfigSprite(23318, -1, 101, 0, 879);
		addConfigSprite(23319, -1, 102, 0, 880);
		addConfigSprite(23320, -1, 103, 0, 881);
		addText(23321, "Level: ", TDA, 1, 0xFFFF33, true, false);

		//Kda
		addTransparentSprite(23322, 97, 150);
		addText(23323, "Targets killed: 0", TDA, 0, 0xFFFF33, true, false);
		addText(23324, "Players killed: 0", TDA, 0, 0xFFFF33, true, false);
		addText(23325, "Deaths: 0", TDA, 0, 0xFFFF33, true, false);

		tab.totalChildren(18);
		tab.child(0, 23301, 319, 8);
		tab.child(1, 23322, 319, 54);
		//  tab.child(1, 23302, 339, 56);
		tab.child(2, 23303, 345, 65);
		// tab.child(2, 23304, 348, 73);
		tab.child(3, 23305, 358, 84);
		tab.child(4, 23306, 455, 58);
		tab.child(5, 23307, 456, 71);
		tab.child(6, 23308, 457, 87);
		//  tab.child(8, 23309, 460, 59);
		//  tab.child(9, 23310, 438, 72);
		//  tab.child(10, 23311, 481, 72);
		//  tab.child(11, 23312, 438, 85);
		//  tab.child(12, 23313, 481, 85);
		//  tab.child(13, 23314, 393, 72);
		//  tab.child(14, 23315, 394, 85);
		tab.child(7, 23316, 345, 65);
		tab.child(8, 23317, 345, 65);
		tab.child(9, 23318, 345, 65);
		tab.child(10, 23319, 345, 65);
		tab.child(11, 23320, 345, 65);	 

		tab.child(12, 23323, 435, 13);
		tab.child(13, 23324, 435, 26);
		tab.child(14, 23325, 435, 39);

		interfaceCache[197].childX[0] = 0;
		interfaceCache[197].childY[0] = 0;

		tab.child(15, 197, 331, 13);
		tab.child(16, 23321, 361, 38);

		tab.child(17, 38000, 0, 0);

	}

	public static void itemsKeptOnDeath(GameFont[] tda) {

		removeSomething(16999); //close button in text
		Widget rsinterface = interfaceCache[10494];
		rsinterface.spritePaddingX = 6;
		rsinterface.spritePaddingY = 5;
		rsinterface = interfaceCache[10600];
		rsinterface.spritePaddingX = 6;
		rsinterface.spritePaddingY = 5;


		rsinterface = addInterface(17100);
		addSpriteLoader(17101, 139);
		/*Widget scroll = addTabInterface(17149);
		scroll.width = 300; scroll.height = 183; scroll.scrollMax = 220;*/
		addText(17103, "Items Kept on Death", tda, 2, 0xff981f, false, false);
		addText(17104, "Items you will keep on death:", tda, 1, 0xff981f, false, false);
		addText(17105, "Items you will lose on death:", tda, 1, 0xff981f, false, false);
		addText(17106, "Info", tda, 1, 0xff981f, false, false);
		addText(17107, "3", tda, 2, 0xffff00, false, false);
		String[] options = {null};


		addHoverButton_sprite_loader(17018, 137, 17, 17, "Close", -1, 17019, 1);
		addHoveredButton_sprite_loader(17019, 138, 17, 17, 17020);

		/*
		 * Items on interface
		 */

		//Top Row
		for(int top = 17108; top <= 17111; top++) {
			addItemOnInterface(top, 17100, options);
		}
		//1st row
		for(int top = 17112; top <= 17119; top++) {
			addItemOnInterface(top, 17100, options);
		}
		//2nd row
		for(int top = 17120; top <= 17127; top++) {
			addItemOnInterface(top, 17100, options);
		}
		//3rd row
		for(int top = 17128; top <= 17135; top++) {
			addItemOnInterface(top, 17100, options);
		}
		//4th row
		for (int top = 17136; top <= 17142; top++) {
			addItemOnInterface(top, 17100, options);
		}
		//5th row
		for (int top = 17143; top <= 17148; top++) {
			addItemOnInterface(top, 17100, options);
		}

		//6th row (4 items)
		for(int top = 17149; top <= 17152; top++) {
			addItemOnInterface(top, 17100, options);
		}

		setChildren(58, rsinterface);
		addTabInterface(5);
		setBounds(17101, 7,8, 0, rsinterface);
		setBounds(16999, 478, 14, 1, rsinterface);
		setBounds(17103, 185, 18, 2, rsinterface);
		setBounds(17104, 22, 50, 3, rsinterface);
		setBounds(17105, 22, 110, 4, rsinterface);
		setBounds(17106, 347, 50, 5, rsinterface);

		setBounds(17107, 412, 287, 6, rsinterface);
		setBounds(17149, 23, 132, 7, rsinterface);
		setBounds(17018, 480, 18, 8, rsinterface);
		setBounds(17019, 480, 18, 9, rsinterface);
		setBounds(5, 480, 18, 10, rsinterface);

		//Positions for  item on interface (items kept on death
		int	child_index = 11;
		int topPos = 26;
		for(int top = 17108; top <= 17111; top++) {
			setBounds(top, topPos, 72, child_index, rsinterface);
			topPos += 44;
			child_index++;
		}
		setBounds(17000, 478, 14, child_index++, rsinterface);
		itemsOnDeathDATA(tda);
		setBounds(17315, 348, 64, child_index++, rsinterface);

		topPos = 26;

		//1st row
		for(int top = 17112; top <= 17118; top++) {
			setBounds(top, topPos, 133, child_index, rsinterface);
			topPos += 44;
			child_index++;
		}
		//2nd row
		topPos = 26;
		for(int top = 17119; top <= 17125; top++) {
			setBounds(top, topPos, 168, child_index, rsinterface);
			topPos += 44;
			child_index++;
		}
		//3rd row
		topPos = 26;
		for(int top = 17126; top <= 17132; top++) {
			setBounds(top, topPos, 203, child_index, rsinterface);
			topPos += 44;
			child_index++;
		}
		//4th row
		topPos = 26;
		for (int top = 17133; top <= 17139; top++) {
			setBounds(top, topPos, 238, child_index, rsinterface);
			topPos += 44;
			child_index++;
		}
		//5th row
		topPos = 26;
		for (int top = 17140; top <= 17145; top++) {
			setBounds(top, topPos, 273, child_index, rsinterface);
			topPos += 44;
			child_index++;
		}

		//6th row (4 items)
		topPos = 26;
		for(int top = 17146; top <= 17152; top++) {
			setBounds(top, topPos, 311, child_index, rsinterface);
			topPos += 44;
			child_index++;
		}
	}

	public static void itemsOnDeathDATA(GameFont[] tda) {
		Widget RSinterface = addInterface(17315);
		addText(17309, "", 0xff981f, false, false, 0, tda, 0);
		addText(17310, "The normal amount of", 0xff981f, false, false, 0, tda, 0);
		addText(17311, "items kept is three.", 0xff981f, false, false, 0, tda, 0);
		addText(17312, "", 0xff981f, false, false, 0, tda, 0);
		addText(17313, "If you are skulled,", 0xff981f, false, false, 0, tda, 0);
		addText(17314, "you will lose all your", 0xff981f, false, false, 0, tda, 0);
		addText(17317, "items, unless an item", 0xff981f, false, false, 0, tda, 0);
		addText(17318, "protecting prayer is", 0xff981f, false, false, 0, tda, 0);
		addText(17319, "used.", 0xff981f, false, false, 0, tda, 0);
		addText(17320, "", 0xff981f, false, false, 0, tda, 0);
		addText(17321, "Item protecting prayers", 0xff981f, false, false, 0, tda, 0);
		addText(17322, "will allow you to keep", 0xff981f, false, false, 0, tda, 0);
		addText(17323, "one extra item.", 0xff981f, false, false, 0, tda, 0);
		addText(17324, "", 0xff981f, false, false, 0, tda, 0);
		addText(17325, "The items kept are", 0xff981f, false, false, 0, tda, 0);
		addText(17326, "selected by the server", 0xff981f, false, false, 0, tda, 0);
		addText(17327, "and include the most", 0xff981f, false, false, 0, tda, 0);
		addText(17328, "expensive items you're", 0xff981f, false, false, 0, tda, 0);
		addText(17329, "carrying.", 0xff981f, false, false, 0, tda, 0);
		addText(17330, "", 0xff981f, false, false, 0, tda, 0);
		RSinterface.parent = 17315;
		RSinterface.id = 17315;
		RSinterface.type = 0;
		RSinterface.atActionType = 0;
		RSinterface.contentType = 0;
		RSinterface.width = 130;
		RSinterface.height = 197;
		RSinterface.opacity = 0;
		RSinterface.hoverType = -1;
		RSinterface.scrollMax = 280;
		RSinterface.children = new int[20];
		RSinterface.childX = new int[20];
		RSinterface.childY = new int[20];
		RSinterface.children[0] = 17309;
		RSinterface.childX[0] = 0;
		RSinterface.childY[0] = 0;
		RSinterface.children[1] = 17310;
		RSinterface.childX[1] = 0;
		RSinterface.childY[1] = 12;
		RSinterface.children[2] = 17311;
		RSinterface.childX[2] = 0;
		RSinterface.childY[2] = 24;
		RSinterface.children[3] = 17312;
		RSinterface.childX[3] = 0;
		RSinterface.childY[3] = 36;
		RSinterface.children[4] = 17313;
		RSinterface.childX[4] = 0;
		RSinterface.childY[4] = 48;
		RSinterface.children[5] = 17314;
		RSinterface.childX[5] = 0;
		RSinterface.childY[5] = 60;
		RSinterface.children[6] = 17317;
		RSinterface.childX[6] = 0;
		RSinterface.childY[6] = 72;
		RSinterface.children[7] = 17318;
		RSinterface.childX[7] = 0;
		RSinterface.childY[7] = 84;
		RSinterface.children[8] = 17319;
		RSinterface.childX[8] = 0;
		RSinterface.childY[8] = 96;
		RSinterface.children[9] = 17320;
		RSinterface.childX[9] = 0;
		RSinterface.childY[9] = 108;
		RSinterface.children[10] = 17321;
		RSinterface.childX[10] = 0;
		RSinterface.childY[10] = 120;
		RSinterface.children[11] = 17322;
		RSinterface.childX[11] = 0;
		RSinterface.childY[11] = 132;
		RSinterface.children[12] = 17323;
		RSinterface.childX[12] = 0;
		RSinterface.childY[12] = 144;
		RSinterface.children[13] = 17324;
		RSinterface.childX[13] = 0;
		RSinterface.childY[13] = 156;
		RSinterface.children[14] = 17325;
		RSinterface.childX[14] = 0;
		RSinterface.childY[14] = 168;
		RSinterface.children[15] = 17326;
		RSinterface.childX[15] = 0;
		RSinterface.childY[15] = 180;
		RSinterface.children[16] = 17327;
		RSinterface.childX[16] = 0;
		RSinterface.childY[16] = 192;
		RSinterface.children[17] = 17328;
		RSinterface.childX[17] = 0;
		RSinterface.childY[17] = 204;
		RSinterface.children[18] = 17329;
		RSinterface.childX[18] = 0;
		RSinterface.childY[18] = 216;
		RSinterface.children[19] = 17330;
		RSinterface.childX[19] = 0;
		RSinterface.childY[19] = 228;
	}

	public static void debugInterface() {
		Widget widget = Widget.interfaceCache[12424];
		for (int i = 0; i < widget.children.length; i++) {
			System.out.println("childX: " + widget.childX[i] + " childY: " + widget.childY[i] + " index: " + i
					+ " spellId: " + widget.children[i]);
		}
	}

	public static void repositionModernSpells() {

		Widget widget = Widget.interfaceCache[12424];
		for (int index = 0; index < widget.children.length; index++) {

			switch (widget.children[index]) {

			case 1185:
				widget.childX[33] = 148;
				widget.childY[33] = 150;
				break;

			case 1183: // wind wave
				widget.childX[31] = 76;
				widget.childY[31] = 149;
				break;

			case 1188: // earth wave
				widget.childX[36] = 71;
				widget.childY[36] = 172;
				break;

			case 1543:
				widget.childX[46] = 96;
				widget.childY[46] = 173;
				break;

			case 1193: // charge
				widget.childX[41] = 49;
				widget.childY[41] = 198;
				break;

			case 12435: // tele other falador
				widget.childX[54] = 74;
				widget.childY[54] = 198;
				break;

			case 12445: // teleblock
				widget.childX[55] = 99;
				widget.childY[55] = 198;
				break;

			case 6003: // lvl 6 enchant
				widget.childX[57] = 122;
				widget.childY[57] = 198;
				break;

				// 150 x is end of the line

			case 12455: // tele other camelot
				widget.childX[56] = 147;
				widget.childY[56] = 198;
				break;
			}
		}
	}


	public static void clanChatTab(GameFont[] tda) {
		Widget tab = addTabInterface(37128);

		addButton(37129, 37128, 72, 32, 194, 195, 37130, "Select");
		addHoveredButton_sprite_loader(37130, 195, 72, 32, 37131);

		addButton(37132, 37128, 72, 32, 194, 195, 37133, "Select");
		addHoveredButton_sprite_loader(37133, 195, 72, 32, 37134);

		// addButton(37250, 0, "/Clan Chat/Lootshare", "Toggle lootshare");
		addText(37135, "Join Chat", tda, 0, 0xff9b00, true, true);
		addText(37136, "Clan Setup", tda, 0, 0xff9b00, true, true);

		addSpriteLoader(37137, 196);

		addText(37138, "Clan Chat", tda, 1, 0xff9b00, true, true);
		addText(37139, "Talking in: N/A", tda, 0, 0xff9b00, false, true);
		addText(37140, "Owner: N/A", tda, 0, 0xff9b00, false, true);
		tab.totalChildren(11);
		//tab.child(0, 16126, 0, 221);
		//tab.child(1, 16126, 0, 59);

		tab.child(0, 37137, 0, 62);
		tab.child(1, 37143, 0, 62);
		tab.child(2, 37129, 15, 226);
		tab.child(3, 37130, 15, 226);
		tab.child(4, 37132, 103, 226);
		tab.child(5, 37133, 103, 226);
		tab.child(6, 37135, 51, 237);
		tab.child(7, 37136, 139, 237);
		tab.child(8, 37138, 95, 1);
		tab.child(9, 37139, 10, 23);
		tab.child(10, 37140, 25, 38);
		/* Text area */
		Widget list = addTabInterface(37143);
		list.totalChildren(100);
		for (int i = 37144; i <= 37244; i++) {
			addText(i, "", tda, 0, 0xffffff, false, true);
		}
		for (int id = 37144, i = 0; id <= 37243 && i <= 99; id++, i++) {
			interfaceCache[id].actions = new String[] {
					"Promote to Recruit", 
					"Promote to Corporal", 
					"Promote to Sergeant", 
					"Promote to Lieutenant", 
					"Promote to Captain", 
					"Promote to General", 
					"Demote", 
					"Kick"
			};
			interfaceCache[id].parent = 37128;
			list.children[i] = id;
			list.childX[i] = 5;
			for (int id2 = 37144, i2 = 1; id2 <= 37243 && i2 <= 99; id2++, i2++) {
				list.childY[0] = 2;
				list.childY[i2] = list.childY[i2 - 1] + 14;
			}
		}
		list.height = 158;
		list.width = 174;
		list.scrollMax = 1405;
	}

	public static void addHoverText2(int id, String text, String[] tooltips, GameFont tda[], int idx, int color,
			boolean center, boolean textShadowed, int width) {
		Widget rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = center;
		rsinterface.textShadow = textShadowed;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.defaultText = text;
		rsinterface.secondaryText = "";
		rsinterface.textColor = color;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = 0xffffff;
		rsinterface.secondaryHoverColor = 0;
		rsinterface.tooltips = tooltips;
	}

	public static void addText2(int id, String text, GameFont tda[], int idx, int color, boolean center,
			boolean shadow) {
		Widget tab = addTabInterface(id);
		tab.parent = id;
		tab.id = id;
		tab.type = 4;
		tab.atActionType = 0;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.defaultText = text;
		tab.secondaryText = "";
		tab.textColor = color;
		tab.secondaryColor = 0;
		tab.defaultHoverColor = 0;
		tab.secondaryHoverColor = 0;
	}

	public static void addAdvancedSprite(int id, int spriteId) {
		Widget widget = addInterface(id);
		widget.id = id;
		widget.parent = id;
		widget.type = 5;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.hoverType = 52;
		widget.enabledSprite = Client.cacheSprite[spriteId];
		widget.disabledSprite = Client.cacheSprite[spriteId];
		widget.drawsTransparent = true;
		widget.opacity = 64;
		widget.width = 512;
		widget.height = 334;
	}

	public static void addConfigSprite(int id, int spriteId, int spriteId2, int state, int config) {
		Widget widget = addTabInterface(id);
		widget.id = id;
		widget.parent = id;
		widget.type = 5;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.width = 512;
		widget.height = 334;
		widget.opacity = 0;
		widget.hoverType = -1;
		widget.valueCompareType = new int[1];
		widget.requiredValues = new int[1];
		widget.valueCompareType[0] = 1;
		widget.requiredValues[0] = state;
		widget.valueIndexArray = new int[1][3];
		widget.valueIndexArray[0][0] = 5;
		widget.valueIndexArray[0][1] = config;
		widget.valueIndexArray[0][2] = 0;
		widget.enabledSprite = spriteId < 0 ? null : Client.cacheSprite[spriteId];
		widget.disabledSprite = spriteId2 < 0 ? null : Client.cacheSprite[spriteId2];
	}

	public static void addSprite(int id, int spriteId) {
		Widget rsint = interfaceCache[id] = new Widget();
		rsint.id = id;
		rsint.parent = id;
		rsint.type = 5;
		rsint.atActionType = 0;
		rsint.contentType = 0;
		rsint.opacity = 0;
		rsint.hoverType = 0;

		if (spriteId != -1) {
			rsint.disabledSprite = Client.cacheSprite[spriteId];
			rsint.enabledSprite = Client.cacheSprite[spriteId];
		}

		rsint.width = 0;
		rsint.height = 0;
	}

	public static void addText(int id, String text, GameFont wid[], int idx, int color) {
		Widget rsinterface = addTabInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 0;
		rsinterface.width = 174;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.centerText = false;
		rsinterface.textShadow = true;
		rsinterface.textDrawingAreas = wid[idx];
		rsinterface.defaultText = text;
		rsinterface.secondaryText = "";
		rsinterface.textColor = color;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = 0;
		rsinterface.secondaryHoverColor = 0;
	}


	public static void equipmentTab(GameFont[] wid) {
		Widget Interface = interfaceCache[1644];
		addSprite(15101, 0, "Interfaces/Equipment/bl");// cheap hax
		addSprite(15102, 1, "Interfaces/Equipment/bl");// cheap hax
		addSprite(15109, 2, "Interfaces/Equipment/bl");// cheap hax
		removeConfig(21338);
		removeConfig(21344);
		removeConfig(21342);
		removeConfig(21341);
		removeConfig(21340);
		removeConfig(15103);
		removeConfig(15104);
		// Interface.children[23] = 15101;
		// Interface.childX[23] = 40;
		// Interface.childY[23] = 205;
		Interface.children[24] = 15102;
		Interface.childX[24] = 110;
		Interface.childY[24] = 205;
		Interface.children[25] = 15109;
		Interface.childX[25] = 39;
		Interface.childY[25] = 240;
		Interface.children[26] = 27650;
		Interface.childX[26] = 0;
		Interface.childY[26] = 0;
		Interface = addInterface(27650);

		addHoverButton_sprite_loader(27651, 146, 40, 40, "Price-checker", -1, 27652, 1);
		addHoveredButton_sprite_loader(27652, 147, 40, 40, 27658);

		addHoverButton_sprite_loader(27653, 144, 40, 40, "Show Equipment Stats", -1, 27655, 1);
		addHoveredButton_sprite_loader(27655, 145, 40, 40, 27665);

		addHoverButton_sprite_loader(27654, 148, 40, 40, "Show items kept on death", -1, 27657, 1);
		addHoveredButton_sprite_loader(27657, 149, 40, 40, 27666);

		setChildren(6, Interface);
		setBounds(27651, 75, 205, 0, Interface);
		setBounds(27652, 75, 205, 1, Interface);
		setBounds(27653, 23, 205, 2, Interface);
		setBounds(27654, 127, 205, 3, Interface);
		setBounds(27655, 23, 205, 4, Interface);
		setBounds(27657, 127, 205, 5, Interface);
	}

	public static void removeConfig(int id) {
		@SuppressWarnings("unused")
		Widget rsi = interfaceCache[id] = new Widget();
	}


	public static void equipmentScreen(GameFont[] wid) {
		Widget Interface = Widget.interfaceCache[1644];
		addButton(19144, 140, "Show Equipment Stats");
		removeSomething(19145);
		removeSomething(19146);
		removeSomething(19147);
		// setBounds(19144, 21, 210, 23, Interface);
		setBounds(19145, 40, 210, 24, Interface);
		setBounds(19146, 40, 210, 25, Interface);
		setBounds(19147, 40, 210, 26, Interface);
		Widget tab = addTabInterface(15106);
		addSpriteLoader(15107, 141);

		addHoverButton_sprite_loader(15210, 142, 21, 21, "Close", 250, 15211, 3);
		addHoveredButton_sprite_loader(15211, 143, 21, 21, 15212);

		addText(15111, "Equip Your Character...", wid, 2, 0xe4a146, false, true);
		addText(15112, "Attack bonus", wid, 2, 0xe4a146, false, true);
		addText(15113, "Defence bonus", wid, 2, 0xe4a146, false, true);
		addText(15114, "Other bonuses", wid, 2, 0xe4a146, false, true);

		addText(15115, "Melee maxhit: 1", wid, 1, 0xe4a146, false, true);
		addText(15116, "Ranged maxhit: 1", wid, 1, 0xe4a146, false, true);
		addText(15117, "Magic maxhit: 1", wid, 1, 0xe4a146, false, true);

		for (int i = 1675; i <= 1684; i++) {
			textSize(i, wid, 1);
		}
		textSize(1686, wid, 1);
		textSize(1687, wid, 1);
		addChar(15125);
		tab.totalChildren(47);
		tab.child(0, 15107, 4, 20);
		tab.child(1, 15210, 476, 29);
		tab.child(2, 15211, 476, 29);
		tab.child(3, 15111, 14, 30);
		int Child = 4;
		int Y = 69;
		for (int i = 1675; i <= 1679; i++) {
			tab.child(Child, i, 20, Y);
			Child++;
			Y += 14;
		}
		tab.child(9, 1680, 20, 161);
		tab.child(10, 1681, 20, 177);
		tab.child(11, 1682, 20, 192);
		tab.child(12, 1683, 20, 207);
		tab.child(13, 1684, 20, 221);
		tab.child(14, 1686, 20, 262);
		tab.child(15, 15125, 170, 200);
		tab.child(16, 15112, 16, 55);
		tab.child(17, 1687, 20, 276);
		tab.child(18, 15113, 16, 147);
		tab.child(19, 15114, 16, 248);
		tab.child(20, 1645, 104 + 295, 149 - 52);
		tab.child(21, 1646, 399, 163);
		tab.child(22, 1647, 399, 163);
		tab.child(23, 1648, 399, 58 + 146);
		tab.child(24, 1649, 26 + 22 + 297 - 2, 110 - 44 + 118 - 13 + 5);
		tab.child(25, 1650, 321 + 22, 58 + 154);
		tab.child(26, 1651, 321 + 134, 58 + 118);
		tab.child(27, 1652, 321 + 134, 58 + 154);
		tab.child(28, 1653, 321 + 48, 58 + 81);
		tab.child(29, 1654, 321 + 107, 58 + 81);
		tab.child(30, 1655, 321 + 58, 58 + 42);
		tab.child(31, 1656, 321 + 112, 58 + 41);
		tab.child(32, 1657, 321 + 78, 58 + 4);
		tab.child(33, 1658, 321 + 37, 58 + 43);
		tab.child(34, 1659, 321 + 78, 58 + 43);
		tab.child(35, 1660, 321 + 119, 58 + 43);
		tab.child(36, 1661, 321 + 22, 58 + 82);
		tab.child(37, 1662, 321 + 78, 58 + 82);
		tab.child(38, 1663, 321 + 134, 58 + 82);
		tab.child(39, 1664, 321 + 78, 58 + 122);
		tab.child(40, 1665, 321 + 78, 58 + 162);
		tab.child(41, 1666, 321 + 22, 58 + 162);
		tab.child(42, 1667, 321 + 134, 58 + 162);
		tab.child(43, 1688, 50 + 297 - 2, 110 - 13 + 5);

		//Maxhits
		tab.child(44, 15115, 370, 260);
		tab.child(45, 15116, 370, 275);
		tab.child(46, 15117, 370, 290);

		for (int i = 1675; i <= 1684; i++) {
			Widget rsi = interfaceCache[i];
			rsi.textColor = 0xe4a146;
			rsi.centerText = false;
		}
		for (int i = 1686; i <= 1687; i++) {
			Widget rsi = interfaceCache[i];
			rsi.textColor = 0xe4a146;
			rsi.centerText = false;
		}
	}

	public static void addChar(int ID) {
		Widget t = interfaceCache[ID] = new Widget();
		t.id = ID;
		t.parent = ID;
		t.type = 6;
		t.atActionType = 0;
		t.contentType = 328;
		t.width = 136;
		t.height = 168;
		t.opacity = 0;
		t.modelZoom = 560;
		t.modelRotation1 = 150;
		t.modelRotation2 = 0;
		t.defaultAnimationId = -1;
		t.secondaryAnimationId = -1;
	}

	public static void addButton(int id, int sid, String tooltip) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = Client.cacheSprite[sid];// imageLoader(sid, spriteName);
		tab.enabledSprite = Client.cacheSprite[sid];//imageLoader(sid, spriteName);
		tab.width = tab.disabledSprite.myWidth;
		tab.height = tab.enabledSprite.myHeight;
		tab.tooltip = tooltip;
	}

	public static void addTooltipBox(int id, String text) {
		Widget rsi = addInterface(id);
		rsi.id = id;
		rsi.parent = id;
		rsi.type = 8;
		rsi.defaultText = text;
	}

	public static void addTooltip(int id, String text) {
		Widget rsi = addInterface(id);
		rsi.id = id;
		rsi.type = 0;
		rsi.invisible = true;
		rsi.hoverType = -1;
		addTooltipBox(id + 1, text);
		rsi.totalChildren(1);
		rsi.child(0, id + 1, 0, 0);
	}

	public static Widget addInterface(int id) {
		Widget rsi = interfaceCache[id] = new Widget();
		rsi.id = id;
		rsi.parent = id;
		rsi.width = 512;
		rsi.height = 334;
		return rsi;
	}

	public static void addText(int id, String text, GameFont tda[], int idx, int color, boolean centered) {
		Widget rsi = interfaceCache[id] = new Widget();
		if (centered)
			rsi.centerText = true;
		rsi.textShadow = true;
		rsi.textDrawingAreas = tda[idx];
		rsi.defaultText = text;
		rsi.textColor = color;
		rsi.id = id;
		rsi.type = 4;
	}

	public static void textColor(int id, int color) {
		Widget rsi = interfaceCache[id];
		rsi.textColor = color;
	}

	public static void textSize(int id, GameFont tda[], int idx) {
		Widget rsi = interfaceCache[id];
		rsi.textDrawingAreas = tda[idx];
	}

	public static void addCacheSprite(int id, int sprite1, int sprite2, String sprites) {
		Widget rsi = interfaceCache[id] = new Widget();
		rsi.disabledSprite = getSprite(sprite1, interfaceLoader, sprites);
		rsi.enabledSprite = getSprite(sprite2, interfaceLoader, sprites);
		rsi.parent = id;
		rsi.id = id;
		rsi.type = 5;
	}

	public static void sprite1(int id, int sprite) {
		Widget class9 = interfaceCache[id];
		class9.disabledSprite = Client.cacheSprite[sprite];
	}

	public static void addActionButton(int id, int sprite, int sprite2, int width, int height, String s) {
		Widget rsi = interfaceCache[id] = new Widget();
		rsi.disabledSprite = Client.cacheSprite[sprite];
		if (sprite2 == sprite)
			rsi.enabledSprite = Client.cacheSprite[sprite];
		else
			rsi.enabledSprite = Client.cacheSprite[sprite2];
		rsi.tooltip = s;
		rsi.contentType = 0;
		rsi.atActionType = 1;
		rsi.width = width;
		rsi.hoverType = 52;
		rsi.parent = id;
		rsi.id = id;
		rsi.type = 5;
		rsi.height = height;
	}

	public static void addToggleButton(int id, int sprite, int setconfig, int width, int height, String s) {
		Widget rsi = addInterface(id);
		rsi.disabledSprite = Client.cacheSprite[sprite];
		rsi.enabledSprite = Client.cacheSprite[sprite];
		rsi.requiredValues = new int[1];
		rsi.requiredValues[0] = 1;
		rsi.valueCompareType = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.valueIndexArray = new int[1][3];
		rsi.valueIndexArray[0][0] = 5;
		rsi.valueIndexArray[0][1] = setconfig;
		rsi.valueIndexArray[0][2] = 0;
		rsi.atActionType = 4;
		rsi.width = width;
		rsi.hoverType = -1;
		rsi.parent = id;
		rsi.id = id;
		rsi.type = 5;
		rsi.height = height;
		rsi.tooltip = s;
	}

	public void totalChildren(int id, int x, int y) {
		children = new int[id];
		childX = new int[x];
		childY = new int[y];
	}

	public static void removeSomething(int id) {
		@SuppressWarnings("unused")
		Widget rsi = interfaceCache[id] = new Widget();
	}

	public static void quickPrayers(GameFont[] TDA) {
		int frame = 0;
		Widget tab = addTabInterface(17200);

		addTransparentSprite(17229, 131, 50);
		addSpriteLoader(17201, 132);
		addText(17230, "Select your quick prayers:", TDA, 0, 0xFF981F, false, true);

		for (int i = 17202, j = 630; i <= 17228 || j <= 656; i++, j++) {
			addConfigButton(i, 17200, 134, 133, 14, 15, "Select", 0, 1, j);
		}
		addHoverButton_sprite_loader(17231, 135, 190, 24, "Confirm Selection", -1, 17232, 1);
		addHoveredButton_sprite_loader(17232, 136, 190, 24, 17233);

		setChildren(58, tab);//
		setBounds(5632, 5, 8 + 20, frame++, tab);
		setBounds(5633, 44, 8 + 20, frame++, tab);
		setBounds(5634, 79, 11 + 20, frame++, tab);
		setBounds(19813, 116, 10 + 20, frame++, tab);
		setBounds(19815, 153, 9 + 20, frame++, tab);
		setBounds(5635, 5, 48 + 20, frame++, tab);
		setBounds(5636, 44, 47 + 20, frame++, tab);
		setBounds(5637, 79, 49 + 20, frame++, tab);
		setBounds(5638, 116, 50 + 20, frame++, tab);
		setBounds(5639, 154, 50 + 20, frame++, tab);
		setBounds(5640, 4, 84 + 20, frame++, tab);
		setBounds(19817, 44, 87 + 20, frame++, tab);
		setBounds(19820, 81, 85 + 20, frame++, tab);
		setBounds(5641, 117, 85 + 20, frame++, tab);
		setBounds(5642, 156, 87 + 20, frame++, tab);
		setBounds(5643, 5, 125 + 20, frame++, tab);
		setBounds(5644, 43, 124 + 20, frame++, tab);
		setBounds(13984, 83, 124 + 20, frame++, tab);
		setBounds(5645, 115, 121 + 20, frame++, tab);
		setBounds(19822, 154, 124 + 20, frame++, tab);
		setBounds(19824, 5, 160 + 20, frame++, tab);
		setBounds(5649, 41, 158 + 20, frame++, tab);
		setBounds(5647, 79, 163 + 20, frame++, tab);
		setBounds(5648, 116, 158 + 20, frame++, tab);
		setBounds(19826, 161, 160 + 20, frame++, tab);
		setBounds(19828, 4, 207 + 12, frame++, tab);

		setBounds(17229, 0, 25, frame++, tab);// Faded backing
		setBounds(17201, 0, 22, frame++, tab);// Split
		setBounds(17201, 0, 237, frame++, tab);// Split

		setBounds(17202, 5 - 3, 8 + 17, frame++, tab);
		setBounds(17203, 44 - 3, 8 + 17, frame++, tab);
		setBounds(17204, 79 - 3, 8 + 17, frame++, tab);
		setBounds(17205, 116 - 3, 8 + 17, frame++, tab);
		setBounds(17206, 153 - 3, 8 + 17, frame++, tab);
		setBounds(17207, 5 - 3, 48 + 17, frame++, tab);
		setBounds(17208, 44 - 3, 48 + 17, frame++, tab);
		setBounds(17209, 79 - 3, 48 + 17, frame++, tab);
		setBounds(17210, 116 - 3, 48 + 17, frame++, tab);
		setBounds(17211, 153 - 3, 48 + 17, frame++, tab);
		setBounds(17212, 5 - 3, 85 + 17, frame++, tab);
		setBounds(17213, 44 - 3, 85 + 17, frame++, tab);
		setBounds(17214, 79 - 3, 85 + 17, frame++, tab);
		setBounds(17215, 116 - 3, 85 + 17, frame++, tab);
		setBounds(17216, 153 - 3, 85 + 17, frame++, tab);
		setBounds(17217, 5 - 3, 124 + 17, frame++, tab);
		setBounds(17218, 44 - 3, 124 + 17, frame++, tab);
		setBounds(17219, 79 - 3, 124 + 17, frame++, tab);
		setBounds(17220, 116 - 3, 124 + 17, frame++, tab);
		setBounds(17221, 153 - 3, 124 + 17, frame++, tab);
		setBounds(17222, 5 - 3, 160 + 17, frame++, tab);
		setBounds(17223, 44 - 3, 160 + 17, frame++, tab);
		setBounds(17224, 79 - 3, 160 + 17, frame++, tab);
		setBounds(17225, 116 - 3, 160 + 17, frame++, tab);
		setBounds(17226, 153 - 3, 160 + 17, frame++, tab);
		setBounds(17227, 4 - 3, 207 + 4, frame++, tab);

		setBounds(17230, 5, 5, frame++, tab);// text
		setBounds(17231, 0, 237, frame++, tab);// confirm
		setBounds(17232, 0, 237, frame++, tab);// Confirm hover
	}

	public int transparency = 255;

	private static void addTransparentSprite(int id, int spriteId, int transparency) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.transparency = transparency;
		tab.hoverType = 52;
		tab.disabledSprite = Client.cacheSprite[spriteId];
		tab.enabledSprite = Client.cacheSprite[spriteId];
		tab.width = 512;
		tab.height = 334;
		tab.drawsTransparent = true;
	}

	public static void Pestpanel(GameFont[] tda) {
		Widget RSinterface = addInterface(21119);
		addText(21120, "What", 0x999999, false, true, 52, tda, 1);
		addText(21121, "What", 0x33cc00, false, true, 52, tda, 1);
		addText(21122, "(Need 5 to 25 players)", 0xFFcc33, false, true, 52, tda, 1);
		addText(21123, "Points", 0x33ccff, false, true, 52, tda, 1);
		int last = 4;
		RSinterface.children = new int[last];
		RSinterface.childX = new int[last];
		RSinterface.childY = new int[last];
		setBounds(21120, 15, 12, 0, RSinterface);
		setBounds(21121, 15, 30, 1, RSinterface);
		setBounds(21122, 15, 48, 2, RSinterface);
		setBounds(21123, 15, 66, 3, RSinterface);
	}

	public static void Pestpanel2(GameFont[] tda) {
		Widget RSinterface = addInterface(21100);
		addSprite(21101, 0, "Pest Control/PEST1");
		addSprite(21102, 1, "Pest Control/PEST1");
		addSprite(21103, 2, "Pest Control/PEST1");
		addSprite(21104, 3, "Pest Control/PEST1");
		addSprite(21105, 4, "Pest Control/PEST1");
		addSprite(21106, 5, "Pest Control/PEST1");
		addText(21107, "", 0xCC00CC, false, true, 52, tda, 1);
		addText(21108, "", 0x0000FF, false, true, 52, tda, 1);
		addText(21109, "", 0xFFFF44, false, true, 52, tda, 1);
		addText(21110, "", 0xCC0000, false, true, 52, tda, 1);
		addText(21111, "250", 0x99FF33, false, true, 52, tda, 1);// w purp
		addText(21112, "250", 0x99FF33, false, true, 52, tda, 1);// e blue
		addText(21113, "250", 0x99FF33, false, true, 52, tda, 1);// se yel
		addText(21114, "250", 0x99FF33, false, true, 52, tda, 1);// sw red
		addText(21115, "200", 0x99FF33, false, true, 52, tda, 1);// attacks
		addText(21116, "0", 0x99FF33, false, true, 52, tda, 1);// knights hp
		addText(21117, "Time Remaining:", 0xFFFFFF, false, true, 52, tda, 0);
		addText(21118, "", 0xFFFFFF, false, true, 52, tda, 0);
		int last = 18;
		RSinterface.children = new int[last];
		RSinterface.childX = new int[last];
		RSinterface.childY = new int[last];
		setBounds(21101, 361, 26, 0, RSinterface);
		setBounds(21102, 396, 26, 1, RSinterface);
		setBounds(21103, 436, 26, 2, RSinterface);
		setBounds(21104, 474, 26, 3, RSinterface);
		setBounds(21105, 3, 21, 4, RSinterface);
		setBounds(21106, 3, 50, 5, RSinterface);
		setBounds(21107, 371, 60, 6, RSinterface);
		setBounds(21108, 409, 60, 7, RSinterface);
		setBounds(21109, 443, 60, 8, RSinterface);
		setBounds(21110, 479, 60, 9, RSinterface);
		setBounds(21111, 362, 10, 10, RSinterface);
		setBounds(21112, 398, 10, 11, RSinterface);
		setBounds(21113, 436, 10, 12, RSinterface);
		setBounds(21114, 475, 10, 13, RSinterface);
		setBounds(21115, 32, 32, 14, RSinterface);
		setBounds(21116, 32, 62, 15, RSinterface);
		setBounds(21117, 8, 88, 16, RSinterface);
		setBounds(21118, 87, 88, 17, RSinterface);
	}

	public String hoverText;

	public static void addHoverBox(int id, int ParentID, String text, String text2, int configId, int configFrame) {
		Widget rsi = addTabInterface(id);
		rsi.id = id;
		rsi.parent = ParentID;
		rsi.type = 8;
		rsi.secondaryText = text;
		rsi.defaultText = text2;
		rsi.valueCompareType = new int[1];
		rsi.requiredValues = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.requiredValues[0] = configId;
		rsi.valueIndexArray = new int[1][3];
		rsi.valueIndexArray[0][0] = 5;
		rsi.valueIndexArray[0][1] = configFrame;
		rsi.valueIndexArray[0][2] = 0;
	}

	public static void addItemOnInterface(int childId, int interfaceId, String[] options) {
		Widget rsi = interfaceCache[childId] = new Widget();
		rsi.actions = new String[5];
		rsi.spritesX = new int[20];
		rsi.inventoryItemId = new int[30];
		rsi.inventoryAmounts = new int[30];
		rsi.spritesY = new int[20];
		rsi.children = new int[0];
		rsi.childX = new int[0];
		rsi.childY = new int[0];
		for (int i = 0; i < rsi.actions.length; i++) {
			if (i < options.length) {
				if (options[i] != null) {
					rsi.actions[i] = options[i];
				}
			}
		}
		rsi.centerText = true;
		rsi.filled = false;
		rsi.replaceItems = false;
		rsi.usableItems = false;
		//rsi.isInventoryInterface = false;
		rsi.allowSwapItems = false;
		rsi.spritePaddingX = 4;
		rsi.spritePaddingY = 5;
		rsi.height = 1;
		rsi.width = 1;
		rsi.parent = interfaceId;
		rsi.id = childId;
		rsi.type = TYPE_INVENTORY;
	}

	public static void addText(int id, String text, GameFont tda[], int idx, int color, boolean center,
			boolean shadow) {
		Widget tab = addTabInterface(id);
		tab.parent = id;
		tab.id = id;
		tab.type = 4;
		tab.atActionType = 0;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.defaultText = text;
		tab.secondaryText = "";
		tab.textColor = color;
		tab.secondaryColor = 0;
		tab.defaultHoverColor = 0;
		tab.secondaryHoverColor = 0;
	}

	public static void addText(int i, String s, int k, boolean l, boolean m, int a, GameFont[] TDA, int j) {
		Widget RSInterface = addInterface(i);
		RSInterface.parent = i;
		RSInterface.id = i;
		RSInterface.type = 4;
		RSInterface.atActionType = 0;
		RSInterface.width = 0;
		RSInterface.height = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = a;
		RSInterface.centerText = l;
		RSInterface.textShadow = m;
		RSInterface.textDrawingAreas = TDA[j];
		RSInterface.defaultText = s;
		RSInterface.secondaryText = "";
		RSInterface.textColor = k;
	}

	public static void addConfigButton(int ID, int pID, int bID, int bID2, int width, int height,
			String tT, int configID, int aT, int configFrame) {
		Widget Tab = addTabInterface(ID);
		Tab.parent = pID;
		Tab.id = ID;
		Tab.type = 5;
		Tab.atActionType = aT;
		Tab.contentType = 0;
		Tab.width = width;
		Tab.height = height;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.valueCompareType = new int[1];
		Tab.requiredValues = new int[1];
		Tab.valueCompareType[0] = 1;
		Tab.requiredValues[0] = configID;
		Tab.valueIndexArray = new int[1][3];
		Tab.valueIndexArray[0][0] = 5;
		Tab.valueIndexArray[0][1] = configFrame;
		Tab.valueIndexArray[0][2] = 0;
		Tab.disabledSprite = Client.cacheSprite[bID];//imageLoader(bID, bName);
		Tab.enabledSprite = Client.cacheSprite[bID2];
		Tab.tooltip = tT;
	}

	public static void addSprite(int id, int spriteId, String spriteName) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = imageLoader(spriteId, spriteName);
		tab.enabledSprite = imageLoader(spriteId, spriteName);
		tab.width = 512;
		tab.height = 334;
	}

	public static void addHoverButton(int i, String imageName, int j, int width, int height, String text,
			int contentType, int hoverOver, int aT) {// hoverable
		// button
		Widget tab = addTabInterface(i);
		tab.id = i;
		tab.parent = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.disabledSprite = imageLoader(j, imageName);
		tab.enabledSprite = imageLoader(j, imageName);
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
	}
	
	public static void addHoverText(int id, String text, String tooltip, GameFont tda[], int idx, int color, boolean center, boolean textShadow, int width) {
		Widget rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = center;
		rsinterface.textShadow = textShadow;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.defaultText = text;
		rsinterface.secondaryText = "";
		rsinterface.tooltip = tooltip;
		rsinterface.textColor = color;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = 0xFFFFFF;
		rsinterface.secondaryHoverColor = 0;
	}

	private static void bankInterface(GameFont[] tda) {
		Widget bank = addInterface(5292);

		setChildren(47, bank);

		int id = 50000;
		int child = 0;

		Sprite disabled = Client.cacheSprite[129];
		Sprite enabled = Client.cacheSprite[130];
		///Sprite button1 = getSprite(0, interfaceLoader, "miscgraphics");
		//Sprite button2 = getSprite(9, interfaceLoader, "miscgraphics");

		addSprite(id, 106);
		addHoverButton_sprite_loader(id + 1, 107, 17, 17, "Close", -1, id + 2, 1);
		addHoveredButton_sprite_loader(id + 2, 108, 17, 17, id + 3);
		
		bank.child(child++, id, 12, 2);
		bank.child(child++, id + 1, 472, 9);
		bank.child(child++, id + 2, 472, 9);
	
		addHoverButton_sprite_loader(id + 4, 117, 32, 32, "Deposit Inventory", -1, id + 5, 1);
		addHoveredButton_sprite_loader(id + 5, 118, 32, 32, id + 6);
		
		addHoverButton_sprite_loader(id + 7, 119, 32, 32, "Deposit Equipment", -1, id + 8, 1);
		addHoveredButton_sprite_loader(id + 8, 120, 32, 32, id + 9);
		
		addHoverButtonWConfig(id + 10, 115, 116, 32, 32, "Search", -1, id + 11, 1, 117, 117);
		addHoveredButton_sprite_loader(id + 11, 116, 32, 32, id + 12);

		
		bank.child(child++, id + 4, 415, 292);
		bank.child(child++, id + 5, 415, 292);
		bank.child(child++, id + 7, 455, 292);
		bank.child(child++, id + 8, 455, 292);
		bank.child(child++, id + 10, 375, 292);
		bank.child(child++, id + 11, 375, 292);
		
		addButton(id + 13, getSprite(0, interfaceLoader, "miscgraphics3"), getSprite(0, interfaceLoader, "miscgraphics3"), "Show menu", 25, 25);
		addSprite(id + 14, 209);
		bank.child(child++, id + 13, 463, 43);
		bank.child(child++, id + 14, 463, 44);
				
		//Text
		addText(id + 53, "%1", tda, 0, 0xFE9624, true);
		Widget line = addInterface(id + 54);
		line.type = 3;
		line.allowSwapItems = true;
		line.width = 14;
		line.height = 1;
		line.textColor = 0xFE9624;
		addText(id + 55, "352", tda, 0, 0xFE9624, true);
		bank.child(child++, id + 53, 30, 8);
		bank.child(child++, id + 54, 24, 19);
		bank.child(child++, id + 55, 30, 20);
		
		bank.child(child++, 5383, 180, 12);
		bank.child(child++, 5385, 0, 79);
		bank.child(child++, 8131, 102, 306);
		bank.child(child++, 8130, 17, 306);
		bank.child(child++, 5386, 282, 306);
		bank.child(child++, 5387, 197, 306);
		bank.child(child++, 8132, 127, 309);
		bank.child(child++, 8133, 45, 309);
		bank.child(child++, 5390, 54, 291);
		bank.child(child++, 5389, 227, 309);
		bank.child(child++, 5391, 311, 309);
		bank.child(child++, 5388, 248, 291);
		
		id = 50070;
		for (int tab = 0, counter = 0; tab <= 36; tab += 4, counter++) {

		//	addHoverButton_sprite_loader(id + 1 + tab, 206, 39, 40, null, -1, id + 2 + tab, 1);		
		//	addHoveredButton_sprite_loader(id + 2 + tab, 207, 39, 40, id + 3 + tab);


			int[] requiredValues = new int[]{1};
			int[] valueCompareType = new int[]{1};
			int[][] valueIndexArray = new int[1][3];
			valueIndexArray[0][0] = 5;
			valueIndexArray[0][1] = 1000 + counter; //Config
			valueIndexArray[0][2] = 0;
			
			
			addHoverConfigButton(id + tab, id + 1 + tab, 206, -1, 39, 40, null, valueCompareType, requiredValues, valueIndexArray);
			addHoveredConfigButton(interfaceCache[id + tab], id + 1 + tab, id + 2 + tab, 207, -1);
			
			
			//addHoverButtonWConfig(id + 1 + tab, 206, -1, 39, 40, null, -1, id + 2 + tab, 1, 1, 1000+counter);
			//addHoveredButton_sprite_loader(id + 2 + tab, 207, 39, 40, id + 3 + tab);
			
			interfaceCache[id + tab].actions = new String[]{"Select", tab == 0 ? null : "Collapse", null, null, null};
			interfaceCache[id + tab].parent = id;
			interfaceCache[id + tab].drawingDisabled = true;
			interfaceCache[id + 1 + tab].parent = id;
			bank.child(child++, id + tab, 19 + 40 * (tab / 4), 37);
			bank.child(child++, id + 1 + tab, 19 + 40 * (tab / 4), 37);
		}
		
		interfaceCache[5385].height = 206;
		interfaceCache[5385].width = 474;
		
		int[] interfaces = new int[] { 5386, 5387, 8130, 8131 };

		for (int rsint : interfaces) {
			interfaceCache[rsint].disabledSprite = disabled;
			interfaceCache[rsint].enabledSprite = enabled;
			interfaceCache[rsint].width = enabled.myWidth;
			interfaceCache[rsint].height = enabled.myHeight;
		}
		
		addSprite(50040, 208);
		bank.child(child++, 50040, 20, 41);
		
		
		final Widget scrollBar = Widget.interfaceCache[5385];
		scrollBar.totalChildren(Client.MAX_BANK_TABS);
		for(int i = 0; i < Client.MAX_BANK_TABS; i++) {
			addBankTabContainer(50300 + i, 109, 10, 35, 352, new String[] { "Withdraw-1", "Withdraw-5", "Withdraw-10", "Withdraw-All", "Withdraw-X", null, "Withdraw-All but one" });
			scrollBar.child(i, 50300 + i, 40, 0);
		}
	}
	
	
	public static void addHoverText(int id, String text, String tooltip, GameFont tda[], int idx, int color, boolean center, boolean textShadow, int width, int hoveredColor) {
		Widget rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parent = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = center;
		rsinterface.textShadow = textShadow;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.defaultText = text;
		rsinterface.secondaryText = "";
		rsinterface.textColor = color;
		rsinterface.secondaryColor = 0;
		rsinterface.defaultHoverColor = 0xffffff;
		rsinterface.secondaryHoverColor = 0;
		rsinterface.tooltip = tooltip;
	}
	
	
	/**
	 * Bank settings
	 * @param t
	 */
	public static void bankSettings(GameFont[] t) {
		Widget tab = addInterface(32500);
		addSprite(32501, 229);
		addText(32502, ""+Configuration.CLIENT_NAME+" Bank Settings", 0xff9933, true, true, -1, t, 2);
		
		addHoverButton_sprite_loader(32503, 107, 21, 21, "Close", -1, 32504, 1);
		addHoveredButton_sprite_loader(32504, 108, 21, 21, 32505);
		
		addConfigButton(32506, 32500, 230, 231, 14, 15, "Select", 0, 5, 1111);
		addConfigButton(32507, 32500, 230, 231, 14, 15, "Select", 1, 5, 1111);
		addConfigButton(32508, 32500, 230, 231, 14, 15, "Select", 2, 5, 1111);
		
		addText(32509, "First item in tab", 0xff9933, true, true, -1, t, 1);
		addText(32510, "Digit (1, 2, 3)", 0xff9933, true, true, -1, t, 1);
		addText(32511, "Roman numeral (I, II, III)", 0xff9933, true, true, -1, t, 1);
		addHoverText(32512, "Back to bank", "View", t, 1, 0xcc8000, true, true, 100, 0xFFFFFF);
		tab.totalChildren(11);
		tab.child(0, 32501, 115, 35);
		tab.child(1, 32502, 263, 44);
		tab.child(2, 32503, 373, 42);
		tab.child(3, 32504, 373, 42);
		tab.child(4, 32506, 150, 65 + 30);
		tab.child(5, 32507, 150, 65 + 60);
		tab.child(6, 32508, 150, 65 + 90);
		tab.child(7, 32509, 218, 65 + 30);
		tab.child(8, 32510, 210, 65 + 60);
		tab.child(9, 32511, 239, 65 + 90);
		tab.child(10, 32512, 275, 265);
	}
	
	public static void addHoveredConfigButton(Widget original, int ID, int IMAGEID, int disabledID, int enabledID) {
		Widget rsint = addTabInterface(ID);
		rsint.parent = original.id;
		rsint.id = ID;
		rsint.type = 0;
		rsint.atActionType = 0;
		rsint.contentType = 0;
		rsint.width = original.width;
		rsint.height = original.height;
		rsint.opacity = 0;
		rsint.hoverType = -1;
		Widget hover = addInterface(IMAGEID);
		hover.type = 5;
		hover.width = original.width;
		hover.height = original.height;
		hover.valueCompareType = original.valueCompareType;
		hover.requiredValues = original.requiredValues;
		hover.valueIndexArray = original.valueIndexArray;
		if(disabledID != -1)
			hover.disabledSprite = Client.cacheSprite[disabledID];
		if(enabledID != -1)
			hover.enabledSprite = Client.cacheSprite[enabledID];
		rsint.totalChildren(1);
		setBounds(IMAGEID, 0, 0, 0, rsint);
		rsint.tooltip = original.tooltip;
		rsint.invisible = true;
	}
	
	public static void addHoverConfigButton(int id, int hoverOver, int disabledID, int enabledID, int width, int height, String tooltip, int[] valueCompareType, int[] requiredValues, int[][] valueIndexArray) {
		Widget rsint = addTabInterface(id);
		rsint.parent = id;
		rsint.id = id;
		rsint.type = 5;
		rsint.atActionType = 5;
		rsint.contentType = 206;
		rsint.width = width;
		rsint.height = height;
		rsint.opacity = 0;
		rsint.hoverType = hoverOver;
		rsint.valueCompareType = valueCompareType;
		rsint.requiredValues = requiredValues;
		rsint.valueIndexArray = valueIndexArray;
		if(disabledID != -1)
			rsint.disabledSprite = Client.cacheSprite[disabledID];
		if(enabledID != -1)
			rsint.enabledSprite = Client.cacheSprite[enabledID];
		rsint.tooltip = tooltip;
	}
	
	public static void addButton(int id, Sprite enabled, Sprite disabled, String tooltip, int w, int h) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = disabled;
		tab.enabledSprite = enabled;
		tab.width = w;
		tab.height = h;
		tab.tooltip = tooltip;
	}
	
	public static void addConfigButton(int ID, int pID, Sprite disabled, Sprite enabled, int width, int height, String tT, int configID, int aT, int configFrame) {
		Widget Tab = addTabInterface(ID);
		Tab.parent = pID;
		Tab.id = ID;
		Tab.type = 5;
		Tab.atActionType = aT;
		Tab.contentType = 0;
		Tab.width = width;
		Tab.height = height;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.valueCompareType = new int[1];
		Tab.requiredValues = new int[1];
		Tab.valueCompareType[0] = 1;
		Tab.requiredValues[0] = configID;
		Tab.valueIndexArray = new int[1][3];
		Tab.valueIndexArray[0][0] = 5;
		Tab.valueIndexArray[0][1] = configFrame;
		Tab.valueIndexArray[0][2] = 0;
		Tab.disabledSprite = disabled;
		Tab.enabledSprite = enabled;
		Tab.tooltip = tT;
	}
	
	public static Widget addBankTabContainer(int id, int contentType, int width, int height, int size, String... actions) {
		Widget container = addInterface(id);
		container.parent = id;
		container.type = 2;
		container.contentType = contentType;
		container.width = width;
		container.height = height;
		container.sprites = new Sprite[20];
		container.spritesX = new int[20];
		container.spritesY = new int[20];
		container.spritePaddingX = 12;
		container.spritePaddingY = 10;
		container.inventoryItemId = new int[size]; // 10 bank tabs
		container.inventoryAmounts = new int[size]; // 10 bank tabs
		container.allowSwapItems = true;
		container.actions = actions;
		return container;
	}

	public static void addSpriteLoader(int childId, int spriteId) {
		Widget rsi = interfaceCache[childId] = new Widget();
		rsi.id = childId;
		rsi.parent = childId;
		rsi.type = 5;
		rsi.atActionType = 0;
		rsi.contentType = 0;
		rsi.disabledSprite = Client.cacheSprite[spriteId];
		rsi.enabledSprite = Client.cacheSprite[spriteId];


		//rsi.sprite1.spriteLoader = rsi.sprite2.spriteLoader = true;
		//rsi.hoverSprite1 = Client.cacheSprite[hoverSpriteId];
		//rsi.hoverSprite2 = Client.cacheSprite[hoverSpriteId];
		//rsi.hoverSprite1.spriteLoader = rsi.hoverSprite2.spriteLoader = true;
		//rsi.sprite1 = rsi.sprite2 = spriteId;
		//rsi.hoverSprite1Id = rsi.hoverSprite2Id = hoverSpriteId;
		rsi.width = rsi.disabledSprite.myWidth;
		rsi.height = rsi.enabledSprite.myHeight - 2;
		//rsi.isFalseTooltip = true;
	}

	public static void addSprite(int childId, Sprite sprite1, Sprite sprite2) {
		Widget rsi = interfaceCache[childId] = new Widget();
		rsi.id = childId;
		rsi.parent = childId;
		rsi.type = 5;
		rsi.atActionType = 0;
		rsi.contentType = 0;
		rsi.disabledSprite = sprite1;
		rsi.enabledSprite = sprite2;
		rsi.width = rsi.disabledSprite.myWidth;
		rsi.height = rsi.enabledSprite.myHeight - 2;
	}



	public static void addButtonWSpriteLoader(int id, int spriteId) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = Client.cacheSprite[spriteId];
		tab.enabledSprite = Client.cacheSprite[spriteId];
		tab.width = tab.disabledSprite.myWidth;
		tab.height = tab.enabledSprite.myHeight - 2;
	}

	public static void addHoverButtonWConfig(int i, int spriteId, int spriteId2, int width, int height, String text,
			int contentType, int hoverOver, int aT, int configId, int configFrame) {// hoverable
		// button
		Widget tab = addTabInterface(i);
		tab.id = i;
		tab.parent = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
		tab.valueCompareType = new int[1];
		tab.requiredValues = new int[1];
		tab.valueCompareType[0] = 1;
		tab.requiredValues[0] = configId;
		tab.valueIndexArray = new int[1][3];
		tab.valueIndexArray[0][0] = 5;
		tab.valueIndexArray[0][1] = configFrame;
		tab.valueIndexArray[0][2] = 0;
		if(spriteId != -1)
			tab.disabledSprite = Client.cacheSprite[spriteId];
		if(spriteId2 != -1)
			tab.enabledSprite = Client.cacheSprite[spriteId2];
	}


	public static void addHoverButton_sprite_loader(int i, int spriteId, int width, int height, String text,
			int contentType, int hoverOver, int aT) {// hoverable
		// button
		Widget tab = addTabInterface(i);
		tab.id = i;
		tab.parent = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.disabledSprite = Client.cacheSprite[spriteId];
		tab.enabledSprite = Client.cacheSprite[spriteId];
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
	}

	public static void addHoveredButton_sprite_loader(int i, int spriteId, int w, int h, int IMAGEID) {// hoverable
		// button
		Widget tab = addTabInterface(i);
		tab.parent = i;
		tab.id = i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = w;
		tab.height = h;
		tab.invisible = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		addHoverImage_sprite_loader(IMAGEID, spriteId);
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);
	}

	public static void addHoverImage_sprite_loader(int i, int spriteId) {
		Widget tab = addTabInterface(i);
		tab.id = i;
		tab.parent = i;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = 52;
		tab.disabledSprite = Client.cacheSprite[spriteId];
		tab.enabledSprite = Client.cacheSprite[spriteId];
	}

	public static void addBankItem(int index, Boolean hasOption)
	{
		Widget rsi = interfaceCache[index] = new Widget();
		rsi.actions = new String[5];
		rsi.spritesX = new int[20];
		rsi.inventoryAmounts = new int[30];
		rsi.inventoryItemId = new int[30];
		rsi.spritesY = new int[20];

		rsi.children = new int[0];
		rsi.childX = new int[0];
		rsi.childY = new int[0];

		//rsi.hasExamine = false;

		rsi.spritePaddingX = 24;
		rsi.spritePaddingY = 24;
		rsi.height = 5;
		rsi.width = 6;
		rsi.parent = 5292;
		rsi.id = index;
		rsi.type = 2;
	}

	public static void addHoveredButton(int i, String imageName, int j, int w, int h, int IMAGEID) {// hoverable
		// button
		Widget tab = addTabInterface(i);
		tab.parent = i;
		tab.id = i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = w;
		tab.height = h;
		tab.invisible = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		addHoverImage(IMAGEID, j, j, imageName);
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);
	}

	public static void addHoverImage(int i, int j, int k, String name) {
		Widget tab = addTabInterface(i);
		tab.id = i;
		tab.parent = i;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = 52;
		tab.disabledSprite = imageLoader(j, name);
		tab.enabledSprite = imageLoader(k, name);
	}


	public static Widget addTabInterface(int id) {
		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;// 250
		tab.parent = id;// 236
		tab.type = 0;// 262
		tab.atActionType = 0;// 217
		tab.contentType = 0;
		tab.width = 512;// 220
		tab.height = 700;// 267
		tab.opacity = (byte) 0;
		tab.hoverType = -1;// Int 230
		return tab;
	}

	public static Widget addTabInterface(int id, Widget toClone) {

		Widget tab = interfaceCache[id] = new Widget();
		tab.id = id;
		tab.parent = toClone.parent;
		tab.type = toClone.type;
		tab.atActionType = toClone.atActionType;
		tab.contentType = toClone.contentType;
		tab.width = toClone.width;
		tab.height = toClone.height;
		tab.opacity = toClone.opacity;
		tab.hoverType = toClone.hoverType;

		return tab;
	}

	private static Sprite imageLoader(int i, String s) {
		long l = (StringUtils.hashSpriteName(s) << 8) + (long) i;
		Sprite sprite = (Sprite) spriteCache.get(l);
		if (sprite != null)
			return sprite;
		try {
			sprite = new Sprite(s + "" + i);
			spriteCache.put(sprite, l);
		} catch (Exception exception) {
			return null;
		}
		return sprite;
	}

	public void child(int id, int interID, int x, int y) {
		children[id] = interID;
		childX[id] = x;
		childY[id] = y;
	}

	public void totalChildren(int t) {
		children = new int[t];
		childX = new int[t];
		childY = new int[t];
	}

	private Model getModel(int type, int mobId) {
		Model model = (Model) models.get((type << 16) + mobId);

		if (model != null) {
			return model;
		}

		if (type == 1) {
			model = Model.getModel(mobId);
		}

		if (type == 2) {
			model = NpcDefinition.lookup(mobId).model();
		}

		if (type == 3) {
			model = Client.localPlayer.getHeadModel();
		}

		if (type == 4) {
			model = ItemDefinition.lookup(mobId).getUnshadedModel(50);
		}

		if (type == 5) {
			model = null;
		}

		if (model != null) {
			models.put(model, (type << 16) + mobId);
		}

		return model;
	}

	private static Sprite getSprite(int i, FileArchive streamLoader, String s) {
		long l = (StringUtils.hashSpriteName(s) << 8) + (long) i;
		Sprite sprite = (Sprite) spriteCache.get(l);
		if (sprite != null)
			return sprite;
		try {
			sprite = new Sprite(streamLoader, s, i);
			spriteCache.put(sprite, l);
		} catch (Exception _ex) {
			return null;
		}
		return sprite;
	}

	public static void method208(boolean flag, Model model) {
		int i = 0;// was parameter
		int j = 5;// was parameter
		if (flag)
			return;
		models.clear();
		if (model != null && j != 4)
			models.put(model, (j << 16) + i);
	}

	public Model method209(int j, int k, boolean flag) {
		Model model;
		if (flag)
			model = getModel(anInt255, anInt256);
		else
			model = getModel(defaultMediaType, defaultMedia);
		if (model == null)
			return null;
		if (k == -1 && j == -1 && model.triangleColours == null)
			return model;
		Model model_1 = new Model(true, Frame.noAnimationInProgress(k) & Frame.noAnimationInProgress(j), false, model);
		if (k != -1 || j != -1)
			model_1.skin();
		if (k != -1)
			model_1.applyTransform(k);
		if (j != -1)
			model_1.applyTransform(j);
		model_1.light(64, 768, -50, -10, -50, true);
		return model_1;
	}

	public Widget() {
	}

	public static FileArchive interfaceLoader;
	public boolean drawsTransparent;
	public Sprite disabledSprite;
	public int lastFrameTime;

	public Sprite sprites[];
	public static Widget interfaceCache[];
	public int requiredValues[];
	public int contentType;
	public int spritesX[];
	public int defaultHoverColor;
	public int atActionType;
	public String spellName;
	public int secondaryColor;
	public int width;
	public String tooltip;
	public String selectedActionName;
	public boolean centerText;
	public int scrollPosition;
	public String actions[];
	public int valueIndexArray[][];
	public boolean filled;
	public String secondaryText;
	public int hoverType;
	public int spritePaddingX;
	public int textColor;
	public int defaultMediaType;
	public int defaultMedia;
	public boolean replaceItems;
	public int parent;
	public int spellUsableOn;
	private static ReferenceCache spriteCache;
	public int secondaryHoverColor;
	public int children[];
	public int childX[];
	public boolean usableItems;
	public GameFont textDrawingAreas;
	public int spritePaddingY;
	public int valueCompareType[];
	public int currentFrame;
	public int spritesY[];
	public String defaultText;
	public boolean hasActions;
	public int id;
	public int inventoryAmounts[];
	public int inventoryItemId[];
	public byte opacity;
	private int anInt255;
	private int anInt256;
	public int defaultAnimationId;
	public int secondaryAnimationId;

	public boolean allowSwapItems;
	public Sprite enabledSprite;
	public int scrollMax;
	public int type;
	public int horizontalOffset;
	private static final ReferenceCache models = new ReferenceCache(30);
	public int verticalOffset;
	public boolean invisible;
	public boolean drawingDisabled;
	public int height;
	public boolean textShadow;
	public int modelZoom;
	public int modelRotation1;
	public int modelRotation2;
	public int childY[];
	
	private static final int LUNAR_RUNE_SPRITES_START = 232;
	private static final int LUNAR_OFF_SPRITES_START = 246;
	private static final int LUNAR_ON_SPRITES_START = 285;
	private static final int LUNAR_HOVER_BOX_SPRITES_START = 324;
	
	public static void addLunarHoverBox(int interface_id, int spriteOffset) {
		Widget RSInterface = addInterface(interface_id);
		RSInterface.id = interface_id;
		RSInterface.parent = interface_id;
		RSInterface.type = 5;
		RSInterface.atActionType = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.disabledSprite = Client.cacheSprite[LUNAR_HOVER_BOX_SPRITES_START + spriteOffset];
		RSInterface.width = 500;
		RSInterface.height = 500;
		RSInterface.tooltip = "";
	}
		
	public static void addLunarRune(int i, int spriteOffset, String runeName) {
		Widget RSInterface = addInterface(i);
		RSInterface.type = 5;
		RSInterface.atActionType = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.disabledSprite = Client.cacheSprite[LUNAR_RUNE_SPRITES_START + spriteOffset];
		RSInterface.width = 500;
		RSInterface.height = 500;
	}

	public static void addLunarText(int ID, int runeAmount, int RuneID, GameFont[] font) {
		Widget rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parent = 1151;
		rsInterface.type = 4;
		rsInterface.atActionType = 0;
		rsInterface.contentType = 0;
		rsInterface.width = 0;
		rsInterface.height = 14;
		rsInterface.opacity = 0;
		rsInterface.hoverType = -1;
		rsInterface.valueCompareType = new int[1];
		rsInterface.requiredValues = new int[1];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = runeAmount;
		rsInterface.valueIndexArray = new int[1][4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = RuneID;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.centerText = true;
		rsInterface.textDrawingAreas = font[0];
		rsInterface.textShadow = true;
		rsInterface.defaultText = "%1/" + runeAmount + "";
		rsInterface.secondaryText = "";
		rsInterface.textColor = 12582912;
		rsInterface.secondaryColor = 49152;
	}

	public static void addLunar2RunesSmallBox(int ID, int r1, int r2, int ra1, int ra2, int rune1, int lvl, String name,
			String descr, GameFont[] TDA, int spriteOffset, int suo, int type) {
		Widget rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parent = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast On";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[3];
		rsInterface.requiredValues = new int[3];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = lvl;
		rsInterface.valueIndexArray = new int[3][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[4];
		rsInterface.valueIndexArray[1][0] = 4;
		rsInterface.valueIndexArray[1][1] = 3214;
		rsInterface.valueIndexArray[1][2] = r2;
		rsInterface.valueIndexArray[1][3] = 0;
		rsInterface.valueIndexArray[2] = new int[3];
		rsInterface.valueIndexArray[2][0] = 1;
		rsInterface.valueIndexArray[2][1] = 6;
		rsInterface.valueIndexArray[2][2] = 0;
		rsInterface.enabledSprite = Client.cacheSprite[LUNAR_ON_SPRITES_START+ spriteOffset];
		rsInterface.disabledSprite = Client.cacheSprite[LUNAR_OFF_SPRITES_START+ spriteOffset];
		
		Widget hover = addInterface(ID + 1);
		hover.parent = ID;
		hover.hoverType = -1;
		hover.type = 0;
		hover.opacity = 0;
		hover.scrollMax = 0;
		hover.invisible = true;
		setChildren(7, hover);
		addLunarHoverBox(ID + 2, 0);
		setBounds(ID + 2, 0, 0, 0, hover);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, hover);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 19, 2, hover);
		setBounds(30016, 37, 35, 3, hover);// Rune
		setBounds(rune1, 112, 35, 4, hover);// Rune
		addLunarText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 50, 66, 5, hover);
		addLunarText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 123, 66, 6, hover);
	}

	public static void addLunar3RunesSmallBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1,
			int rune2, int lvl, String name, String descr, GameFont[] TDA, int spriteOffset, int suo, int type) {
		Widget rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parent = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.valueIndexArray = new int[4][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[4];
		rsInterface.valueIndexArray[1][0] = 4;
		rsInterface.valueIndexArray[1][1] = 3214;
		rsInterface.valueIndexArray[1][2] = r2;
		rsInterface.valueIndexArray[1][3] = 0;
		rsInterface.valueIndexArray[2] = new int[4];
		rsInterface.valueIndexArray[2][0] = 4;
		rsInterface.valueIndexArray[2][1] = 3214;
		rsInterface.valueIndexArray[2][2] = r3;
		rsInterface.valueIndexArray[2][3] = 0;
		rsInterface.valueIndexArray[3] = new int[3];
		rsInterface.valueIndexArray[3][0] = 1;
		rsInterface.valueIndexArray[3][1] = 6;
		rsInterface.valueIndexArray[3][2] = 0;
		rsInterface.enabledSprite = Client.cacheSprite[LUNAR_ON_SPRITES_START+ spriteOffset];
		rsInterface.disabledSprite = Client.cacheSprite[LUNAR_OFF_SPRITES_START+ spriteOffset];
		
		Widget hover = addInterface(ID + 1);
		hover.parent = ID;
		hover.hoverType = -1;
		hover.type = 0;
		hover.opacity = 0;
		hover.scrollMax = 0;
		hover.invisible = true;
		setChildren(9, hover);
		addLunarHoverBox(ID + 2, 0);
		setBounds(ID + 2, 0, 0, 0, hover);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, hover);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 19, 2, hover);
		setBounds(30016, 14, 35, 3, hover);
		setBounds(rune1, 74, 35, 4, hover);
		setBounds(rune2, 130, 35, 5, hover);
		addLunarText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 66, 6, hover);
		addLunarText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 66, 7, hover);
		addLunarText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 66, 8, hover);
	}

	public static void addLunar3RunesBigBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1,
			int rune2, int lvl, String name, String descr, GameFont[] TDA, int spriteOffset, int suo, int type) {
		Widget rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parent = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.valueIndexArray = new int[4][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[4];
		rsInterface.valueIndexArray[1][0] = 4;
		rsInterface.valueIndexArray[1][1] = 3214;
		rsInterface.valueIndexArray[1][2] = r2;
		rsInterface.valueIndexArray[1][3] = 0;
		rsInterface.valueIndexArray[2] = new int[4];
		rsInterface.valueIndexArray[2][0] = 4;
		rsInterface.valueIndexArray[2][1] = 3214;
		rsInterface.valueIndexArray[2][2] = r3;
		rsInterface.valueIndexArray[2][3] = 0;
		rsInterface.valueIndexArray[3] = new int[3];
		rsInterface.valueIndexArray[3][0] = 1;
		rsInterface.valueIndexArray[3][1] = 6;
		rsInterface.valueIndexArray[3][2] = 0;
		rsInterface.enabledSprite = Client.cacheSprite[LUNAR_ON_SPRITES_START+ spriteOffset];
		rsInterface.disabledSprite = Client.cacheSprite[LUNAR_OFF_SPRITES_START+ spriteOffset];
		
		Widget hover = addInterface(ID + 1);
		hover.parent = ID;
		hover.hoverType = -1;
		hover.type = 0;
		hover.opacity = 0;
		hover.scrollMax = 0;
		hover.invisible = true;
		setChildren(9, hover);
		addLunarHoverBox(ID + 2, 1);
		setBounds(ID + 2, 0, 0, 0, hover);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, hover);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 21, 2, hover);
		setBounds(30016, 14, 48, 3, hover);
		setBounds(rune1, 74, 48, 4, hover);
		setBounds(rune2, 130, 48, 5, hover);
		addLunarText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 79, 6, hover);
		addLunarText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 79, 7, hover);
		addLunarText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 79, 8, hover);
	}

	public static void addLunar3RunesLargeBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1,
			int rune2, int lvl, String name, String descr, GameFont[] TDA, int spriteOffset, int suo, int type) {
		Widget rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parent = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.valueIndexArray = new int[4][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[4];
		rsInterface.valueIndexArray[1][0] = 4;
		rsInterface.valueIndexArray[1][1] = 3214;
		rsInterface.valueIndexArray[1][2] = r2;
		rsInterface.valueIndexArray[1][3] = 0;
		rsInterface.valueIndexArray[2] = new int[4];
		rsInterface.valueIndexArray[2][0] = 4;
		rsInterface.valueIndexArray[2][1] = 3214;
		rsInterface.valueIndexArray[2][2] = r3;
		rsInterface.valueIndexArray[2][3] = 0;
		rsInterface.valueIndexArray[3] = new int[3];
		rsInterface.valueIndexArray[3][0] = 1;
		rsInterface.valueIndexArray[3][1] = 6;
		rsInterface.valueIndexArray[3][2] = 0;
		rsInterface.enabledSprite = Client.cacheSprite[LUNAR_ON_SPRITES_START+ spriteOffset];
		rsInterface.disabledSprite = Client.cacheSprite[LUNAR_OFF_SPRITES_START+ spriteOffset];
		Widget hover = addInterface(ID + 1);
		hover.parent = ID;
		hover.hoverType = -1;
		hover.type = 0;
		hover.opacity = 0;
		hover.scrollMax = 0;
		hover.invisible = true;
		setChildren(9, hover);
		addLunarHoverBox(ID + 2, 2);
		setBounds(ID + 2, 0, 0, 0, hover);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, hover);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 34, 2, hover);
		setBounds(30016, 14, 61, 3, hover);
		setBounds(rune1, 74, 61, 4, hover);
		setBounds(rune2, 130, 61, 5, hover);
		addLunarText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 92, 6, hover);
		addLunarText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 92, 7, hover);
		addLunarText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 92, 8, hover);
	}

	public static void configureLunar(GameFont[] tda) {
		constructLunar();
		addLunarRune(30003, 0, "Fire");
		addLunarRune(30004, 1, "Water");
		addLunarRune(30005, 2, "Air");
		addLunarRune(30006, 3, "Earth");
		addLunarRune(30007, 4, "Mind");
		addLunarRune(30008, 5, "Body");
		addLunarRune(30009, 6, "Death");
		addLunarRune(30010, 7, "Nature");
		addLunarRune(30011, 8, "Chaos");
		addLunarRune(30012, 9, "Law");
		addLunarRune(30013, 10, "Cosmic");
		addLunarRune(30014, 11, "Blood");
		addLunarRune(30015, 12, "Soul");
		addLunarRune(30016, 13, "Astral");

		addLunar3RunesSmallBox(30017, 9075, 554, 555, 0, 4, 3, 30003, 30004, 64, "Bake Pie",
				"Bake pies without a stove", tda, 0, 16, 2);
		addLunar2RunesSmallBox(30025, 9075, 557, 0, 7, 30006, 65, "Cure Plant", "Cure disease on farming patch", tda, 1,
				4, 2);
		addLunar3RunesBigBox(30032, 9075, 564, 558, 0, 0, 0, 30013, 30007, 65, "Monster Examine",
				"Detect the combat statistics of a\\nmonster", tda, 2, 2, 2);
		addLunar3RunesSmallBox(30040, 9075, 564, 556, 0, 0, 1, 30013, 30005, 66, "NPC Contact",
				"Speak with varied NPCs", tda, 3, 0, 2);
		addLunar3RunesSmallBox(30048, 9075, 563, 557, 0, 0, 9, 30012, 30006, 67, "Cure Other", "Cure poisoned players",
				tda, 4, 8, 2);
		addLunar3RunesSmallBox(30056, 9075, 555, 554, 0, 2, 0, 30004, 30003, 67, "Humidify",
				"Fills certain vessels with water", tda, 5, 0, 5);
		addLunar3RunesSmallBox(30064, 9075, 563, 557, 1, 0, 1, 30012, 30006, 68, "Moonclan Teleport",
				"Teleports you to moonclan island", tda, 6, 0, 5);
		addLunar3RunesBigBox(30075, 9075, 563, 557, 1, 0, 3, 30012, 30006, 69, "Tele Group Moonclan",
				"Teleports players to Moonclan\\nisland", tda, 7, 0, 5);
		addLunar3RunesSmallBox(30083, 9075, 563, 557, 1, 0, 5, 30012, 30006, 70, "Ourania Teleport",
				"Teleports you to ourania rune altar", tda, 8, 0, 5);
		addLunar3RunesSmallBox(30091, 9075, 564, 563, 1, 1, 0, 30013, 30012, 70, "Cure Me", "Cures Poison", tda, 9, 0,
				5);
		addLunar2RunesSmallBox(30099, 9075, 557, 1, 1, 30006, 70, "Hunter Kit", "Get a kit of hunting gear", tda, 10, 0,
				5);
		addLunar3RunesSmallBox(30106, 9075, 563, 555, 1, 0, 0, 30012, 30004, 71, "Waterbirth Teleport",
				"Teleports you to Waterbirth island", tda, 11, 0, 5);
		addLunar3RunesBigBox(30114, 9075, 563, 555, 1, 0, 4, 30012, 30004, 72, "Tele Group Waterbirth",
				"Teleports players to Waterbirth\\nisland", tda, 12, 0, 5);
		addLunar3RunesSmallBox(30122, 9075, 564, 563, 1, 1, 1, 30013, 30012, 73, "Cure Group",
				"Cures Poison on players", tda, 13, 0, 5);
		addLunar3RunesBigBox(30130, 9075, 564, 559, 1, 1, 4, 30013, 30008, 74, "Stat Spy",
				"Cast on another player to see their\\nskill levels", tda, 14, 8, 2);
		addLunar3RunesBigBox(30138, 9075, 563, 554, 1, 1, 2, 30012, 30003, 74, "Barbarian Teleport",
				"Teleports you to the Barbarian\\noutpost", tda, 15, 0, 5);
		addLunar3RunesBigBox(30146, 9075, 563, 554, 1, 1, 5, 30012, 30003, 75, "Tele Group Barbarian",
				"Teleports players to the Barbarian\\noutpost", tda, 16, 0, 5);
		addLunar3RunesSmallBox(30154, 9075, 554, 556, 1, 5, 9, 30003, 30005, 76, "Superglass Make",
				"Make glass without a furnace", tda, 17, 16, 2);
		addLunar3RunesSmallBox(30162, 9075, 563, 555, 1, 1, 3, 30012, 30004, 77, "Khazard Teleport",
				"Teleports you to Port khazard", tda, 18, 0, 5);
		addLunar3RunesSmallBox(30170, 9075, 563, 555, 1, 1, 7, 30012, 30004, 78, "Tele Group Khazard",
				"Teleports players to Port khazard", tda, 19, 0, 5);
		addLunar3RunesBigBox(30178, 9075, 564, 559, 1, 0, 4, 30013, 30008, 78, "Dream",
				"Take a rest and restore hitpoints 3\\n times faster", tda, 20, 0, 5);
		addLunar3RunesSmallBox(30186, 9075, 557, 555, 1, 9, 4, 30006, 30004, 79, "String Jewellery",
				"String amulets without wool", tda, 21, 0, 5);
		addLunar3RunesLargeBox(30194, 9075, 557, 555, 1, 9, 9, 30006, 30004, 80, "Stat Restore Pot\\nShare",
				"Share a potion with up to 4 nearby\\nplayers", tda, 22, 0, 5);
		addLunar3RunesSmallBox(30202, 9075, 554, 555, 1, 6, 6, 30003, 30004, 81, "Magic Imbue",
				"Combine runes without a talisman", tda, 23, 0, 5);
		addLunar3RunesBigBox(30210, 9075, 561, 557, 2, 1, 14, 30010, 30006, 82, "Fertile Soil",
				"Fertilise a farming patch with super\\ncompost", tda, 24, 4, 2);
		addLunar3RunesBigBox(30218, 9075, 557, 555, 2, 11, 9, 30006, 30004, 83, "Boost Potion Share",
				"Shares a potion with up to 4 nearby\\nplayers", tda, 25, 0, 5);
		addLunar3RunesSmallBox(30226, 9075, 563, 555, 2, 2, 9, 30012, 30004, 84, "Fishing Guild Teleport",
				"Teleports you to the fishing guild", tda, 26, 0, 5);
		addLunar3RunesLargeBox(30234, 9075, 563, 555, 1, 2, 13, 30012, 30004, 85, "Tele Group Fishing Guild",
				"Teleports players to the Fishing\\nGuild", tda, 27, 0, 5);
		addLunar3RunesSmallBox(30242, 9075, 557, 561, 2, 14, 0, 30006, 30010, 85, "Plank Make", "Turn Logs into planks",
				tda, 28, 16, 5);
		addLunar3RunesSmallBox(30250, 9075, 563, 555, 2, 2, 9, 30012, 30004, 86, "Catherby Teleport",
				"Teleports you to Catherby", tda, 29, 0, 5);
		addLunar3RunesSmallBox(30258, 9075, 563, 555, 2, 2, 14, 30012, 30004, 87, "Tele Group Catherby",
				"Teleports players to Catherby", tda, 30, 0, 5);
		addLunar3RunesSmallBox(30266, 9075, 563, 555, 2, 2, 7, 30012, 30004, 88, "Ice Plateau Teleport",
				"Teleports you to Ice Plateau", tda, 31, 0, 5);
		addLunar3RunesLargeBox(30274, 9075, 563, 555, 2, 2, 15, 30012, 30004, 89, "Tele Group Ice Plateau",
				"Teleports players to Ice Plateau", tda, 32, 0, 5);
		addLunar3RunesBigBox(30282, 9075, 563, 561, 2, 1, 0, 30012, 30010, 90, "Energy Transfer",
				"Spend HP and SA energy to\\n give another SA and run energy", tda, 33, 8, 2);
		addLunar3RunesBigBox(30290, 9075, 563, 565, 2, 2, 0, 30012, 30014, 91, "Heal Other",
				"Transfer up to 75% of hitpoints\\n to another player", tda, 34, 8, 2);
		addLunar3RunesBigBox(30298, 9075, 560, 557, 2, 1, 9, 30009, 30006, 92, "Vengeance Other",
				"Allows another player to rebound\\ndamage to an opponent", tda, 35, 8, 2);
		addLunar3RunesSmallBox(30306, 9075, 560, 557, 3, 1, 9, 30009, 30006, 93, "Vengeance",
				"Rebound damage to an opponent", tda, 36, 0, 5);
		addLunar3RunesBigBox(30314, 9075, 565, 563, 3, 2, 5, 30014, 30012, 94, "Heal Group",
				"Transfer up to 75% of hitpoints\\n to a group", tda, 37, 0, 5);
		addLunar3RunesBigBox(30322, 9075, 564, 563, 2, 1, 0, 30013, 30012, 95, "Spellbook Swap",
				"Change to another spellbook for 1\\nspell cast", tda, 38, 0, 5);
	}

	public static void constructLunar() {
		Widget Interface = addTabInterface(29999);
		setChildren(51, Interface); // 50 children
		int child = 0;
		
		//ADD "HOME" AND "OTHER" TELEPORTS
		setBounds(39101, 10, 9, child++, Interface);
		setBounds(39102, 10, 9, child++, Interface);
		setBounds(39104, 105, 9, child++, Interface);
		setBounds(39105, 105, 9, child++, Interface);
		
		
		//NOTE: Lunar Teleports have been removed from the spellbook (the ones that are commented out are teleports)
		
		//Row 1
		setBounds(30017, 20, 60, child++, Interface);
		setBounds(30025, 61, 62, child++, Interface);
		setBounds(30032, 102, 61, child++, Interface);
		setBounds(30040, 142, 62, child++, Interface);
		
		//Row 2
		setBounds(30048, 20, 93, child++, Interface);
		setBounds(30056, 60, 92, child++, Interface);
		setBounds(30091, 102, 92, child++, Interface);
		setBounds(30099, 142, 90, child++, Interface);
		
		//Row 3
		setBounds(30122, 20, 123, child++, Interface);
		setBounds(30130, 62, 123, child++, Interface);
		setBounds(30154, 106, 123, child++, Interface);
		setBounds(30154, 147, 123, child++, Interface);

		//Row 4
		setBounds(30178, 19, 154, child++, Interface);
		setBounds(30186, 63, 155, child++, Interface);
		setBounds(30194, 106, 155, child++, Interface);
		setBounds(30202, 145, 155, child++, Interface);
		
		//Row 5
		setBounds(30210, 21, 184, child++, Interface);
		setBounds(30218, 66, 186, child++, Interface);
		setBounds(30282, 105, 184, child++, Interface);
		setBounds(30290, 145, 183, child++, Interface);
		
		//Row 6
		setBounds(30298, 23, 214, child++, Interface);
		setBounds(30306, 66, 214, child++, Interface);
		setBounds(30314, 105, 214, child++, Interface);
		setBounds(30322, 147, 214, child++, Interface);
		
		
		//setBounds(30064, 39, 39, child++, Interface);
		//setBounds(30075, 71, 39, child++, Interface);
		//setBounds(30083, 103, 39, child++, Interface);
		//setBounds(30106, 12, 68, child++, Interface);
	//	setBounds(30114, 42, 68, child++, Interface);
	//	setBounds(30138, 135, 68, child++, Interface);
	//	setBounds(30146, 165, 68, child++, Interface);
	//	setBounds(30162, 42, 97, child++, Interface);
	//	setBounds(30170, 71, 97, child++, Interface);
		
	//	setBounds(30226, 103, 125, child++, Interface);
	//	setBounds(30234, 135, 125, child++, Interface);
	//	setBounds(30242, 164, 126, child++, Interface);
	//	setBounds(30250, 10, 155, child++, Interface);
	//	setBounds(30258, 42, 155, child++, Interface);
	//	setBounds(30266, 71, 155, child++, Interface);
	//	setBounds(30274, 103, 155, child++, Interface);
		
		setBounds(30018, 5, 176, child++, Interface);// hover
		setBounds(30026, 5, 176, child++, Interface);// hover
		setBounds(30033, 5, 163, child++, Interface);// hover
		setBounds(30041, 5, 176, child++, Interface);// hover
		setBounds(30049, 5, 176, child++, Interface);// hover
		setBounds(30057, 5, 176, child++, Interface);// hover
		//setBounds(30065, 5, 176, child++, Interface);// hover
		//setBounds(30076, 5, 163, child++, Interface);// hover
		//setBounds(30084, 5, 176, child++, Interface);// hover
		setBounds(30092, 5, 176, child++, Interface);// hover
		setBounds(30100, 5, 176, child++, Interface);// hover
	//	setBounds(30107, 5, 176, child++, Interface);// hover
	//	setBounds(30115, 5, 163, child++, Interface);// hover
		setBounds(30123, 5, 176, child++, Interface);// hover
		setBounds(30131, 5, 163, child++, Interface);// hover
	//	setBounds(30139, 5, 163, child++, Interface);// hover
	//	setBounds(30147, 5, 163, child++, Interface);// hover
		setBounds(30155, 5, 176, child++, Interface);// hover
	//	setBounds(30163, 5, 176, child++, Interface);// hover
	//	setBounds(30171, 5, 176, child++, Interface);// hover
		setBounds(30179, 5, 40, child++, Interface);// hover
		setBounds(30187, 5, 40, child++, Interface);// hover
		setBounds(30195, 5, 40, child++, Interface);// hover
		setBounds(30203, 5, 40, child++, Interface);// hover
		setBounds(30211, 5, 40, child++, Interface);// hover
		setBounds(30219, 5, 40, child++, Interface);// hover
		
	//	setBounds(30227, 5, 176, child++, Interface);// hover
	//	setBounds(30235, 5, 149, child++, Interface);// hover
	//	setBounds(30243, 5, 176, child++, Interface);// hover
	//	setBounds(30251, 5, 5, child++, Interface);// hover
	//	setBounds(30259, 5, 5, child++, Interface);// hover
	//	setBounds(30267, 5, 5, child++, Interface);// hover
	//	setBounds(30275, 5, 5, child++, Interface);// hover
		setBounds(30283, 5, 40, child++, Interface);// hover
		setBounds(30291, 5, 40, child++, Interface);// hover
		setBounds(30299, 5, 40, child++, Interface);// hover
		setBounds(30307, 5, 40, child++, Interface);// hover
		setBounds(30323, 5, 40, child++, Interface);// hover
		setBounds(30315, 5, 40, child++, Interface);// hover*/
	}

	public static void setChildren(int total, Widget i) {
		i.children = new int[total];
		i.childX = new int[total];
		i.childY = new int[total];
	}
	
	public static void setBounds(int ID, int X, int Y, int frame, Widget r) {
		r.children[frame] = ID;
		r.childX[frame] = X;
		r.childY[frame] = Y;
	}

	public static void addButton(int i, int j, String name, int W, int H, String S, int AT) {
		Widget RSInterface = addInterface(i);
		RSInterface.id = i;
		RSInterface.parent = i;
		RSInterface.type = 5;
		RSInterface.atActionType = AT;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.disabledSprite = imageLoader(j, name);
		RSInterface.enabledSprite = imageLoader(j, name);
		RSInterface.width = W;
		RSInterface.height = H;
		RSInterface.tooltip = S;
	}


	public static void addSprites(int ID, int i, int i2, String name, int configId, int configFrame) {
		Widget Tab = addTabInterface(ID);
		Tab.id = ID;
		Tab.parent = ID;
		Tab.type = 5;
		Tab.atActionType = 0;
		Tab.contentType = 0;
		Tab.width = 512;
		Tab.height = 334;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.valueCompareType = new int[1];
		Tab.requiredValues = new int[1];
		Tab.valueCompareType[0] = 1;
		Tab.requiredValues[0] = configId;
		Tab.valueIndexArray = new int[1][3];
		Tab.valueIndexArray[0][0] = 5;
		Tab.valueIndexArray[0][1] = configFrame;
		Tab.valueIndexArray[0][2] = 0;
		Tab.disabledSprite = imageLoader(i, name);
		Tab.enabledSprite = imageLoader(i2, name);
	}
	
	private static void specialBars() {
		for(int id : SPECIAL_BARS) {
			specialBar(id);
		}
	}
	
	private static void specialBar(int id) // 7599
	{
	//	addActionButton(id - 12, 70, -1, 150, 26, "Use @gre@Special Attack");

		for (int i = id - 11; i < id; i++) {
			removeSomething(i);
		}
		
		System.out.println("Broken: "+id);

		Widget rsi = interfaceCache[id - 12];
		rsi.width = 150;
		rsi.height = 26;
		rsi = interfaceCache[id];
		rsi.width = 150;
		rsi.height = 26;
		rsi.child(0, id - 12, 0, 0);
		rsi.child(12, id + 1, 3, 7);
		rsi.child(23, id + 12, 16, 8);

		for (int i = 13; i < 23; i++) {
			rsi.childY[i] -= 1;
		}

		rsi = interfaceCache[id + 1];
		rsi.type = 5;
		rsi.disabledSprite = Client.cacheSprite[71];

		for (int i = id + 2; i < id + 12; i++) {
			rsi = interfaceCache[i];
			rsi.type = 5;
		}

		sprite1(id + 2, 72);
		sprite1(id + 3, 73);
		sprite1(id + 4, 74);
		sprite1(id + 5, 75);
		sprite1(id + 6, 76);
		sprite1(id + 7, 77);
		sprite1(id + 8, 78);
		sprite1(id + 9, 79);
		sprite1(id + 10, 80);
		sprite1(id + 11, 81);
	}

	public String[] tooltips;
	public boolean newScroller;
	public boolean drawInfinity;
}
