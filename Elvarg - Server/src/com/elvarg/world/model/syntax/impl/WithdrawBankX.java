package com.elvarg.world.model.syntax.impl;

import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.container.impl.Bank;
import com.elvarg.world.model.syntax.EnterSyntax;

public class WithdrawBankX implements EnterSyntax {
	
	private int item_id;
	private int slot_id;
	private int from_tab;
	
	public WithdrawBankX(int item_id, int slot_id, int from_tab) {
		this.item_id = item_id;
		this.slot_id = slot_id;
		this.from_tab = from_tab;
	}

	@Override
	public void handleSyntax(Player player, String input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleSyntax(Player player, int input) {
		if(item_id < 0 || slot_id < 0 || input <= 0) {
			return;
		}
		Bank.withdraw(player, item_id, slot_id, input, from_tab);
	}

}
