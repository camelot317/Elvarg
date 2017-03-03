package com.elvarg.world.model.syntax.impl;

import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.syntax.EnterSyntax;

public class PriceCheckX implements EnterSyntax {
	
	private boolean deposit;
	private int item_id;
	private int slot_id;
	
	public PriceCheckX(int item_id, int slot_id, boolean deposit) {
		this.item_id = item_id;
		this.slot_id = slot_id;
		this.deposit = deposit;
	}

	@Override
	public void handleSyntax(Player player, String input) {
	}

	@Override
	public void handleSyntax(Player player, int input) {
		if(item_id < 0 || slot_id < 0 || input <= 0) {
			return;
		}

		if(deposit) {
			player.getPriceChecker().deposit(item_id, input, slot_id);
		} else {
			player.getPriceChecker().withdraw(item_id, input, slot_id);
		}
	}

}
