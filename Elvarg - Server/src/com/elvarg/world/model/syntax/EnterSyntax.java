package com.elvarg.world.model.syntax;

import com.elvarg.world.entity.impl.player.Player;

public interface EnterSyntax {

	public abstract void handleSyntax(Player player, String input);
	public abstract void handleSyntax(Player player, int input);
}
