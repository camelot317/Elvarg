package com.elvarg.world.model;

public class ForceMovement {

	private Position start;
	private Position end;
	private int speed;
	private int reverseSpeed;
	private int direction;
	private int animation;
	
	public ForceMovement(Position start, Position end, int speed, int reverseSpeed, int direction, int animation) {
		this.setStart(start);
		this.setEnd(end);
		this.setSpeed((short)speed);
		this.setReverseSpeed((short)reverseSpeed);
		this.setDirection((byte)direction);
		this.setAnimation(animation);
	}

	public Position getStart() {
		return start;
	}

	public void setStart(Position start) {
		this.start = start;
	}

	public Position getEnd() {
		return end;
	}

	public void setEnd(Position end) {
		this.end = end;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getReverseSpeed() {
		return reverseSpeed;
	}

	public void setReverseSpeed(int reverseSpeed) {
		this.reverseSpeed = reverseSpeed;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getAnimation() {
		return animation;
	}

	public void setAnimation(int animation) {
		this.animation = animation;
	}
}
