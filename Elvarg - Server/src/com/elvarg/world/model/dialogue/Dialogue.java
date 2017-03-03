package com.elvarg.world.model.dialogue;

/**
 * Represents a dialogue.
 * 
 * @author relex lawl
 */

public abstract class Dialogue {
	
	/**
	 * Gets the dialogue's type.
	 */
	public abstract DialogueType type();
	
	/**
	 * Gets the animation to perform when type is an NPC or PLAYER STATEMENT.
	 */
	public abstract DialogueExpression animation();
	
	/**
	 * Gets the dialogue's actual line dialogue strings.
	 */
	public abstract String[] dialogue();
	
	/**
	 * Gets the next dialogue to show after 'click here to continue' has been clicked.
	 * @return	The next dialogue to display.
	 */
	public Dialogue nextDialogue() {
		return null;
	}
	
	/**
	 * Gets the next dialogue id to show after 'click here to continue' has been clicked.
	 * @return	The next dialogue's id.
	 */
	public int nextDialogueId() {
		return -1;
	}
	
	/**
	 * Gets this dialogue's id.
	 * @return	The default id gotten from dialogue manager.
	 */
	public int id() {
		return DialogueManager.getDefaultId();
	}
	
	/**
	 * The id of the npc if type is NPC_STATEMENT.
	 * @return
	 */
	public int npcId() {
		return -1;
	}
	
	/**
	 * An array containing the item configurations if type is ITEM_STATEMENT.
	 * First index contains the itemId, second index contains the itemZoom and
	 * the third index contains the dialogue's title.
	 * @return
	 */
	public String[] item() {
		return null;
	}
	
	/**
	 * Performs a special 'action' such as giving a player an item or
	 * teleporting after finishing said dialogue.
	 */
	public void specialAction() {
		
	}
	
}
