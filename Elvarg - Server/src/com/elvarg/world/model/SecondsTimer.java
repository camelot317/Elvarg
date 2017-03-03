package com.elvarg.world.model;

import com.elvarg.util.Stopwatch;

public class SecondsTimer {
	
	public SecondsTimer() {
		
	}
	
	public SecondsTimer(int seconds) {
		start(seconds);
	}

	private Stopwatch timer;
	private int seconds = 0;

	public void start(int seconds) {
		this.seconds = seconds;

		if(seconds > 0) {
			if(timer == null) {
				timer = new Stopwatch();
			}
			timer.reset();
		}
	}
	
	public void stop() {
		seconds = 0;
		timer = null;
	}

	public boolean finished() {

		if(seconds == 0
				|| timer == null) {
			return true;
		}

		return timer.elapsed(seconds * 1000);
	}
	
	public int secondsRemaining() {
		
		if(seconds == 0 
				|| timer == null) {
			return 0;
		}
		
		return seconds - secondsElapsed();
	}

	public int secondsElapsed() {

		if(seconds == 0 
				|| timer == null) {
			return 0;
		}

		return (int) timer.elapsed() / 1000;
	}
}
