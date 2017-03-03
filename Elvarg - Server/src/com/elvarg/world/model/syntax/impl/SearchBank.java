package com.elvarg.world.model.syntax.impl;

import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.container.impl.Bank;
import com.elvarg.world.model.syntax.EnterSyntax;

public class SearchBank implements EnterSyntax {
	
	@Override
	public void handleSyntax(Player player, String input) {
		Bank.search(player, input);
	}

	@Override
	public void handleSyntax(Player player, int input) {
		
	}

}
