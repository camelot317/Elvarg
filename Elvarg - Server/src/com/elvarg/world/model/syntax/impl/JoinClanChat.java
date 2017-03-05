package com.elvarg.world.model.syntax.impl;

import com.elvarg.world.content.clan.ClanChatManager;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.syntax.EnterSyntax;

public class JoinClanChat implements EnterSyntax {

	@Override
	public void handleSyntax(Player player, String input) {
		ClanChatManager.join(player, input);
	}

	@Override
	public void handleSyntax(Player player, int input) {
	}

}
