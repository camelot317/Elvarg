package com.elvarg.world.model.syntax.impl;

import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.container.impl.Shop;
import com.elvarg.world.model.syntax.EnterSyntax;

public class BuyX implements EnterSyntax {
	
	private int interface_id;
	private int item_id;
	private int slot_id;
	
	public BuyX(int item_id, int interface_id, int slot_id) {
		this.item_id = item_id;
		this.interface_id = interface_id;
		this.slot_id = slot_id;
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
		Shop.buyItem(player, interface_id, item_id, slot_id, input);
	}

}
