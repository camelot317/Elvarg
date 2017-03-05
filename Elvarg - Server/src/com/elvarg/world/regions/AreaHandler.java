package com.elvarg.world.regions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.regions.impl.Lumbridge;

public class AreaHandler {
	private static final List<Areas> area_set = new ArrayList<>();

	static {
		area_set.add(new Lumbridge());
	}

	private static final Optional<Areas> area = area_set.stream().filter(Objects::nonNull).findAny();

	public static void firstClickObject(Player player, int object) {
		area.get().sendFirstClickObject(player, object);
	}

	public static void secondClickObject(Player player, int object) {
		area.get().sendSecondClickObject(player, object);
	}

	public static void thirdClickObject(Player player, int object) {
		area.get().sendThirdClickObject(player, object);
	}

	public static void firstClickNPC(Player player, int npc) {
		area.get().sendFirstClickNpc(player, npc);
	}

	public static void secondClickNPC(Player player, int npc) {
		area.get().sendFirstClickNpc(player, npc);
	}

	public static void thirdClickNPC(Player player, int npc) {
		area.get().sendFirstClickNpc(player, npc);
	}
}