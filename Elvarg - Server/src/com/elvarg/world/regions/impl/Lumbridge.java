package com.elvarg.world.regions.impl;

import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.regions.Areas;

public class Lumbridge implements Areas {

	@Override
	public void sendFirstClickObject(Player player, int object) {
		switch (object) {
		case 381:
			player.getPacketSender().sendMessage("test");
			break;
		}
	}

	@Override
	public void sendSecondClickObject(Player player, int object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendThirdClickObject(Player player, int object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendFirstClickNpc(Player player, int npc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendSecondClickNpc(Player player, int npc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendThirdClickNpc(Player player, int npc) {
		// TODO Auto-generated method stub

	}

}
