package com.runescape.model;

import com.runescape.util.Stopwatch;

public class EffectTimer {

	public EffectTimer(int timer, int sprite) {
		this.timer = timer;
		this.sprite = sprite;
	}
	
	private int sprite;
	private int timer;
	private Stopwatch decrement_timer = new Stopwatch().reset();
	
	public int getSprite() {
		return sprite;
	}
	public void setSprite(int sprite) {
		this.sprite = sprite;
	}

	public int getTimer() {
		return timer;
	}
	
	public void setTimer(int timer) {
		this.timer = timer;
	}
	
	public int decrementAndGetTimer() {
		
		//Decrement each second
		if(decrement_timer.elapsed(1000)) {
			decrement_timer.reset();
			return this.timer--;
		}
		
		return timer;
	}
}
