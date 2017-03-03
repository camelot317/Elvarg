package com.elvarg.world.entity.combat.hit;

/**
 * A hit done by an entity onto a target.
 * @author Gabriel Hannason
 */
public class HitDamage {

	private int damage;
	private HitMask hitmask;

	public HitDamage(int damage, HitMask hitmask) {
		setDamage(damage);
		setHitmask(hitmask);
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
		update();
	}

	public void multiplyDamage(double mod) {
		this.damage *= mod;
		update();
	}

	public void update() {
		if(this.damage <= 0) {
			this.damage = 0;
			this.hitmask = HitMask.BLUE;
		} else if(this.damage > 0) {
			if(this.hitmask == HitMask.BLUE) {
				this.hitmask = HitMask.RED;
			}
		}
	}

	public HitMask getHitmask() {
		return hitmask;
	}

	public void setHitmask(HitMask hitmask) {
		this.hitmask = hitmask;
	}
}
