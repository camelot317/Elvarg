package com.elvarg.world.model.teleportation;

import com.elvarg.world.model.Animation;
import com.elvarg.world.model.Graphic;
import com.elvarg.world.model.GraphicHeight;

public enum TeleportType {

	NORMAL(3, new Animation(714), new Animation(715), new Graphic(308, 50, GraphicHeight.HIGH), null),

	ANCIENT(5, new Animation(6303), Animation.DEFAULT_RESET_ANIMATION, new Graphic(392), null),

	LUNAR(4, new Animation(9606), new Animation(9013), new Graphic(1685), null),

	TELE_TAB(2, new Animation(4071), Animation.DEFAULT_RESET_ANIMATION, new Graphic(678), null),

	PURO_PURO(9, new Animation(6601), Animation.DEFAULT_RESET_ANIMATION, new Graphic(1118), null);

	TeleportType(int startTick, Animation startAnim, Animation endAnim, Graphic startGraphic, Graphic endGraphic) {
		this.startTick = startTick;
		this.startAnim = startAnim;
		this.endAnim = endAnim;
		this.startGraphic = startGraphic;
		this.endGraphic = endGraphic;
	}

	private Animation startAnim, endAnim;
	private Graphic startGraphic, endGraphic;
	private int startTick;

	public Animation getStartAnimation() {
		return startAnim;
	}

	public Animation getEndAnimation() {
		return endAnim;
	}

	public Graphic getStartGraphic() {
		return startGraphic;
	}

	public Graphic getEndGraphic() {
		return endGraphic;
	}

	public int getStartTick() {
		return startTick;
	}
}
