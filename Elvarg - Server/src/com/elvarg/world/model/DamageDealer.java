package com.elvarg.world.model;

import com.elvarg.world.entity.impl.player.Player;

public class DamageDealer {

	public DamageDealer(Player p, int damage) {
		this.p = p;
		this.damage = damage;
	}

	private Player p;
	private int damage;

	public Player getPlayer() {
		return this.p;
	}

	public int getDamage() {
		return this.damage;
	}
}