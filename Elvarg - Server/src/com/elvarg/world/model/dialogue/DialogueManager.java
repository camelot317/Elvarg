package com.elvarg.world.model.dialogue;

import java.util.HashMap;
import java.util.Map;

import com.elvarg.GameConstants;
import com.elvarg.cache.impl.definitions.NpcDefinition;
import com.elvarg.util.JsonLoader;
import com.elvarg.world.entity.impl.player.Player;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Manages the loading and start of dialogues.
 * 
 * @author relex lawl
 */

public class DialogueManager {

	/**
	 * Contains all dialogues loaded from said file.
	 */
	public static Map<Integer, Dialogue> dialogues = new HashMap<Integer, Dialogue>();

	/**
	 * Parses the information from the dialogue file.
	 */
	public static JsonLoader parseDialogues() {

		return new JsonLoader() {
			@Override
			public void load(JsonObject reader, Gson builder) {

				final int id = reader.get("id").getAsInt();
				final DialogueType type = DialogueType.valueOf(reader.get("type").getAsString());
				final DialogueExpression anim = reader.has("anim")
						? DialogueExpression.valueOf(reader.get("anim").getAsString()) : null;
				final int lines = reader.get("lines").getAsInt();
				String[] dialogueLines = new String[lines];
				for (int i = 0; i < lines; i++) {
					dialogueLines[i] = reader.get("line" + (i + 1)).getAsString();
				}
				final int next = reader.get("next").getAsInt();
				final int npcId = reader.has("npcId") ? reader.get("npcId").getAsInt() : -1;

				Dialogue dialogue = new Dialogue() {
					@Override
					public int id() {
						return id;
					}

					@Override
					public DialogueType type() {
						return type;
					}

					@Override
					public DialogueExpression animation() {
						return anim;
					}

					@Override
					public String[] dialogue() {
						return dialogueLines;
					}

					@Override
					public int nextDialogueId() {
						return next;
					}

					@Override
					public int npcId() {
						return npcId;
					}

					@Override
					public String[] item() {
						return null;
					}
				};
				dialogues.put(id, dialogue);

			}

			@Override
			public String filePath() {
				return GameConstants.DEFINITIONS_DIRECTORY + "dialogues.json";
			}
		};

	}

	/**
	 * Starts a dialogue gotten from the dialogues map.
	 * 
	 * @param player
	 *            The player to dialogue with.
	 * @param id
	 *            The id of the dialogue to retrieve from dialogues map.
	 */
	public static void start(Player player, int id) {
		Dialogue dialogue = dialogues.get(id);
		start(player, dialogue);
	}

	/**
	 * Starts a dialogue.
	 * 
	 * @param player
	 *            The player to dialogue with.
	 * @param dialogue
	 *            The dialogue to show the player.
	 */
	public static void start(Player player, Dialogue dialogue) {
		player.setDialogue(dialogue);
		if (player.busy())
			player.getPacketSender().sendInterfaceRemoval();
		if (dialogue == null || dialogue.id() < 0) {
			player.getPacketSender().sendInterfaceRemoval();
		} else {
			showDialogue(player, dialogue);
			dialogue.specialAction();
		}

		if (player.getInterfaceId() != 50) {
			player.setInterfaceId(50);
		}
	}

	/**
	 * Handles the clicking of 'click here to continue', option1, option2 and so
	 * on.
	 * 
	 * @param player
	 *            The player who will continue the dialogue.
	 */
	public static void next(Player player) {
		if (player.getDialogue() == null) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		Dialogue next = player.getDialogue().nextDialogue();
		if (next == null)
			next = dialogues.get(player.getDialogue().nextDialogueId());
		if (next == null || next.id() < 0) {
			player.getPacketSender().sendInterfaceRemoval();
			player.setDialogueOptions(null);
			player.setDialogue(null);
			return;
		}

		if (player.getDialogueOptions() != null) {
			player.getDialogueOptions().incrementStage();
		}

		start(player, next);
	}

	/**
	 * Configures the dialogue's type and shows the dialogue interface and sets
	 * its child id's.
	 * 
	 * @param player
	 *            The player to show dialogue for.
	 * @param dialogue
	 *            The dialogue to show.
	 */
	private static void showDialogue(Player player, Dialogue dialogue) {
		String[] lines = dialogue.dialogue();
		switch (dialogue.type()) {
		case NPC_STATEMENT:
			int startDialogueChildId = NPC_DIALOGUE_ID[lines.length - 1];
			int headChildId = startDialogueChildId - 2;
			player.getPacketSender().sendNpcHeadOnInterface(dialogue.npcId(), headChildId);
			player.getPacketSender().sendInterfaceAnimation(headChildId, dialogue.animation().getAnimation());
			player.getPacketSender().sendString(startDialogueChildId - 1, NpcDefinition.forId(dialogue.npcId()) != null
					? NpcDefinition.forId(dialogue.npcId()).getName().replaceAll("_", " ") : "");
			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString(startDialogueChildId + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
			break;
		case PLAYER_STATEMENT:
			startDialogueChildId = PLAYER_DIALOGUE_ID[lines.length - 1];
			headChildId = startDialogueChildId - 2;
			player.getPacketSender().sendPlayerHeadOnInterface(headChildId);
			player.getPacketSender().sendInterfaceAnimation(headChildId, dialogue.animation().getAnimation());
			player.getPacketSender().sendString(startDialogueChildId - 1, player.getUsername());
			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString(startDialogueChildId + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
			break;
		case ITEM_STATEMENT:
			startDialogueChildId = NPC_DIALOGUE_ID[lines.length - 1];
			headChildId = startDialogueChildId - 2;
			player.getPacketSender().sendInterfaceModel(headChildId, Integer.valueOf(dialogue.item()[0]),
					Integer.valueOf(dialogue.item()[1]));
			player.getPacketSender().sendString(startDialogueChildId - 1, dialogue.item()[2]);
			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString(startDialogueChildId + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
			break;
		case STATEMENT:
			sendStatement(player, dialogue.dialogue()[0]);
			break;
		case OPTION:
			int firstChildId = OPTION_DIALOGUE_ID[lines.length - 1];
			player.getPacketSender().sendString(firstChildId - 1, "Choose an option");
			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString(firstChildId + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(firstChildId - 2);
			break;
		}
		if (player.getInterfaceId() <= 0)
			player.setInterfaceId(50);
	}

	public static void sendStatement(Player p, String statement) {
		p.getPacketSender().sendString(357, statement);
		p.getPacketSender().sendString(358, "Click here to continue");
		p.getPacketSender().sendChatboxInterface(356);
	}

	/**
	 * Gets an empty id for a dialogue.
	 * 
	 * @return An empty index from the map or the map's size itself.
	 */
	public static int getDefaultId() {
		int id = dialogues.size();
		for (int i = 0; i < dialogues.size(); i++) {
			if (dialogues.get(i) == null) {
				id = i;
				break;
			}
		}
		return id;
	}

	/**
	 * Retrieves the dialogues map.
	 * 
	 * @return dialogues.
	 */
	public static Map<Integer, Dialogue> getDialogues() {
		return dialogues;
	}

	/**
	 * This array contains the child id where the dialogue statement starts for
	 * npc and item dialogues.
	 */
	private static final int[] NPC_DIALOGUE_ID = { 4885, 4890, 4896, 4903 };

	/**
	 * This array contains the child id where the dialogue statement starts for
	 * player dialogues.
	 */
	private static final int[] PLAYER_DIALOGUE_ID = { 971, 976, 982, 989 };

	/**
	 * This array contains the child id where the dialogue statement starts for
	 * option dialogues.
	 */
	private static final int[] OPTION_DIALOGUE_ID = { 13760, 2461, 2471, 2482, 2494, };
}
