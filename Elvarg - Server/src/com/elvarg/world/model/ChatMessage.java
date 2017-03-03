package com.elvarg.world.model;

/**
 * Represents a player's chat message.
 * 
 * @author relex lawl
 */

public class ChatMessage {
	
	/**
	 * The chat message's 'message' that holds their values.
	 */
	private Message message;
	
	/**
	 * Gets the player's chat message.
	 * @return	message.
	 */
	public Message get() {
		return message;
	}
	
	/**
	 * Sets the player's chat message.
	 * @param message	The new message they will speak.
	 * @return			The ChatMessage instance.
	 */
	public ChatMessage set(Message message) {
		this.message = message;
		return this;
	}

	/**
	 * Represents a ChatMessage's values, such as their color, effect and text bytes.
	 * 
	 * @author relex lawl
	 */
	public static class Message {
		
		/**
		 * The Message constructor.
		 * @param colour	The color the message will have, done through color(#):
		 * @param effects	The effect the message will have, done through effect(#):
		 * @param text		The actual message it will have.
		 */
		public Message(int colour, int effects, String text) {
			this.colour = colour;
			this.effects = effects;
			this.text = text;
		}
		
		/**
		 * The color of the message.
		 */
		private int colour;
		
		/**
		 * The effects of the message.
		 */
		private int effects;
		
		/**
		 * The actual text of the message.
		 */
		private String text;
		
		/**
		 * Gets the message's chat color.
		 * @return	colour.
		 */
		public int getColour() {
			return colour;
		}
		
		/**
		 * Gets the message's chat effect.
		 * @return effects.
		 */
		public int getEffects() {
			return effects;
		}
		
		/**
		 * Gets the message's actual text in byte form.
		 * @return	text.
		 */
		public String getText() {
			return text;
		}
	}
}
