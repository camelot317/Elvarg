package com.elvarg.world.content;

import java.util.HashMap;
import java.util.Map;

import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Position;
import com.elvarg.world.model.teleportation.TeleportHandler;
import com.elvarg.world.model.teleportation.TeleportType;

public class TeleportsInterface {

	private static final int CLOSE_BUTTON = 38117;
	private static final int OPEN_INTERFACE_BUTTON = 39104;

	private enum TeleportsData {
		ROCK_CRABS(38200, 38202, new Position(2706, 3713)), PACK_YAKS(38200, 38206,
				new Position(2323, 3800)), EXPERIMENTS(38200, 38210, new Position(3556, 9944)), ZOMBIES(38200, 38214,
						new Position(3487, 3283)), BANDITS(38200, 38218, new Position(3170, 2992));
		TeleportsData(int interfaceId, int button, Position pos) {
			this.interfaceId = interfaceId;
			this.button = button;
			this.pos = pos;
		}

		private int interfaceId;
		private int button;
		private Position pos;

		private static Map<Integer, TeleportsData> teleports = new HashMap<Integer, TeleportsData>();

		static {
			for (TeleportsData t : TeleportsData.values()) {
				teleports.put(t.button, t);
			}
		}

		public static TeleportsData forId(int button) {
			return teleports.get(button);
		}
	}

	private enum InterfaceData {
		MONSTERS(38200, 38102, 800), BOSSES(38300, 38105, 801), SKILLS(38400, 38108, 802), MINIGAMES(38500, 38111,
				803), WILDERNESS(38600, 38114, 804);

		InterfaceData(int interfaceId, int button, int config) {
			this.interfaceId = interfaceId;
			this.button = button;
			this.config = config;
		}

		private int interfaceId;
		private int button;
		private int config;

		private static Map<Integer, InterfaceData> interfaces = new HashMap<Integer, InterfaceData>();

		static {
			for (InterfaceData t : InterfaceData.values()) {
				interfaces.put(t.button, t);
			}
		}

		public static InterfaceData forId(int button) {
			return interfaces.get(button);
		}
	}

	private static void sendInterface(Player player, InterfaceData teleport) {

		// Deactivate other tabs
		for (InterfaceData t : InterfaceData.values()) {
			if (t == teleport) {
				continue;
			}
			player.getPacketSender().sendConfig(t.config, 0);
		}

		// Activate current tab
		player.getPacketSender().sendConfig(teleport.config, 1);

		// Send interface
		player.getPacketSender().sendInterface(teleport.interfaceId);

	}

	public static boolean handleButton(Player player, int button) {
		if (button != OPEN_INTERFACE_BUTTON) {
			if (!(player.getInterfaceId() >= 3100 && player.getInterfaceId() <= 38600)) {
				return false;
			}
		} else {
			sendInterface(player, InterfaceData.MONSTERS);
			return true;
		}

		if (button == CLOSE_BUTTON) {
			player.getPacketSender().sendInterfaceRemoval();
			return true;
		}

		// Handle teleport options
		InterfaceData tab = InterfaceData.forId(button);
		if (tab != null) {
			sendInterface(player, tab);
			return true;
		}

		// Handle teleport buttons
		TeleportsData tele = TeleportsData.forId(button);
		if (tele != null) {

			// Make sure we have proper interface open....
			if (player.getInterfaceId() != tele.interfaceId) {
				return true;
			}

			player.getPacketSender().sendInterfaceRemoval();
			TeleportHandler.teleport(player, tele.pos, TeleportType.NORMAL);
			return true;
		}
		return false;
	}
}
