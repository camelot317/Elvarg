package com.elvarg.world.content;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.elvarg.world.World;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.SecondsTimer;

public class ServerFeed {

	public static final int SKULL_SPRITE = 162;
	public static final int INTERFACE_ID = 38000;
	private static final int MAX_ENTRIES = 13;
	private static final int FRAME_START = 38001;

	public static final List<FeedEntry> ENTRIES = new CopyOnWriteArrayList<FeedEntry>();

	final static class FeedEntry {
		String entry;
		SecondsTimer timer;

		public FeedEntry(String entry, int seconds) {
			this.entry = entry;
			this.timer = new SecondsTimer(seconds);
		}
	}

	public static void submit(final String s, final int seconds) {
		FeedEntry entry = new FeedEntry(s, seconds);
		if (ENTRIES.size() >= MAX_ENTRIES) {
			ENTRIES.remove(0);
		}
		ENTRIES.add(entry);
		updateInterface();
	}

	public static void updateEntries() {
		boolean updateInterface = false;
		for (FeedEntry e : ENTRIES) {
			if (e == null || e.timer == null || e.timer.finished()) {
				ENTRIES.remove(e);
				updateInterface = true;
			}
		}
		if (updateInterface) {
			updateInterface();
		}
	}

	public static void updateInterface() {
		for (Player p : World.getPlayers()) {
			if (p == null) {
				continue;
			}
			updateInterface(p);
		}
	}

	public static void updateInterface(Player p) {
		for (int i = 0, frame = FRAME_START; i < MAX_ENTRIES; i++, frame++) {
			p.getPacketSender().sendString(frame, (i < ENTRIES.size() ? ENTRIES.get(i).entry : ""));
		}
	}
}
