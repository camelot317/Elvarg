package com.elvarg.world.model.dialogue;

import com.elvarg.world.model.Animation;

/**
 * Represents a dialogue head model's animation.
 * 
 * @author relex lawl
 */

public enum DialogueExpression {

	/**
	 * Value for a good mood.
	 */
	HAPPY(588),

	/**
	 * Value for a calm mood.
	 */
	CALM(589),

	/**
	 * Value for a calm mood.
	 */
	CALM_2(590),

	/**
	 * Value for the default conversation mood.
	 */
	DEFAULT(591),

	/**
	 * Value for an evil mood.
	 */
	EVIL(592),

	/**
	 * Value for an evil mood.
	 */
	EVIL_2(593),

	/**
	 * Value for an evil, yet delighted mood.
	 */
	EVIL_DELIGHTED(594),

	/**
	 * Value for an annoyed mood.
	 */
	ANNOYED(595),

	/**
	 * Value for a distressed mood.
	 */
	DISTRESSED(596),

	/**
	 * Value for a distressed mood.
	 */
	DISTRESSED_2(597),

	/**
	 * Value for an almost-crying mood.
	 */
	CRYING_ALMOST(598),

	/**
	 * Value for a sad mood, with the head bowing down.
	 */
	SAD_HEAD_BOW(599),

	/**
	 * Value for a sleepy/drunken mood.
	 */
	SLEEPY(600),

	/**
	 * Value for a sleepy/drunken mood.
	 */
	SLEEPY_2(601),

	/**
	 * Value for a sleepy/drunken mood.
	 */
	SLEEPY_3(602),

	/**
	 * Value for a sleepy/drunken mood.
	 */
	SLEEPY_4(603),

	/**
	 * Value for an evil mood.
	 */
	EVIL_3(604),

	/**
	 * Value for a laughing mood.
	 */
	LAUGHING(605),

	/**
	 * Value for a laughing mood.
	 */
	LAUGHING_2(606),

	/**
	 * Value for a laughing mood.
	 */
	LAUGHING_3(607),

	/**
	 * Value for a laughing mood.
	 */
	LAUGHING_4(608),

	/**
	 * Value for an evil mood.
	 */
	EVIL_4(609),

	/**
	 * Value for a sad mood.
	 */
	SAD(610),

	/**
	 * Value for a sad mood.
	 */
	SAD_2(611),

	/**
	 * Value for a sad mood.
	 */
	SAD_3(612),

	/**
	 * Value for an almost-crying mood.
	 */
	CRYING_ALMOST_2(613),

	/**
	 * Value for an angry mood.
	 */
	ANGRY(614),

	/**
	 * Value for an angry mood.
	 */
	ANGRY_2(615),

	/**
	 * Value for an angry mood.
	 */
	ANGRY_3(616),

	/**
	 * Value for an angry mood.
	 */
	ANGRY_4(617);
	
	/**
	 * The DialogueExpression constructor.
	 * @param animationId	The id of the animation for said expression.
	 */
	private DialogueExpression(int animationId) {
		animation = new Animation(animationId);
	}
	
	/**
	 * The animation the dialogue head model will perform.
	 */
	private final Animation animation;
	
	/**
	 * Gets the animation for dialogue head model to perform.
	 * @return	animation.
	 */
	public Animation getAnimation() {
		return animation;
	}
}
