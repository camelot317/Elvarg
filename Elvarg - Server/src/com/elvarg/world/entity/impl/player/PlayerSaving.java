package com.elvarg.world.entity.impl.player;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import com.elvarg.Elvarg;
import com.elvarg.util.Misc;
import com.elvarg.world.model.container.impl.Bank;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class PlayerSaving {

	public static void save(Player player) {
		// Create the path and file objects.
		Path path = Paths.get("./data/saves/characters/", player.getUsername() + ".json");
		File file = path.toFile();
		file.getParentFile().setWritable(true);

		// Attempt to make the player save directory if it doesn't
		// exist.
		if (!file.getParentFile().exists()) {
			try {
				file.getParentFile().mkdirs();
			} catch (SecurityException e) {
				System.out.println("Unable to create directory for player data!");
			}
		}
		try (FileWriter writer = new FileWriter(file)) {

			Gson builder = new GsonBuilder().setPrettyPrinting().create();
			JsonObject object = new JsonObject();
			object.addProperty("username", player.getUsername().trim());
			object.addProperty("password", player.getPassword().trim());
			object.addProperty("staff-rights", player.getRights().name());
			object.add("position", builder.toJsonTree(player.getPosition()));
			object.addProperty("spell-book", player.getSpellbook().name());
			object.addProperty("auto-retaliate", new Boolean(player.getCombat().autoRetaliate()));
			object.addProperty("xp-locked", new Boolean(player.experienceLocked()));
			object.addProperty("clanchat", new String(player.getClanChatName()));
			object.addProperty("preserve", new Boolean(player.isPreserveUnlocked()));
			object.addProperty("rigour", new Boolean(player.isRigourUnlocked()));
			object.addProperty("augury", new Boolean(player.isAuguryUnlocked()));
			object.addProperty("has-veng", new Boolean(player.hasVengeance()));
			object.addProperty("last-veng", new Long(player.getVengeanceTimer().secondsRemaining()));
			object.addProperty("running", new Boolean(player.isRunning()));
			object.addProperty("run-energy", new Integer(player.getRunEnergy()));
			object.addProperty("spec-percentage", new Integer(player.getSpecialPercentage()));
			object.addProperty("recoil-damage", new Integer(player.getRecoilDamage()));
			object.addProperty("poison-damage", new Integer(player.getPoisonDamage()));

			object.addProperty("poison-immunity",
					new Integer(player.getCombat().getPoisonImmunityTimer().secondsRemaining()));
			object.addProperty("overload-timer", new Integer(player.getOverloadTimer().secondsRemaining()));
			object.addProperty("fire-immunity",
					new Integer(player.getCombat().getFireImmunityTimer().secondsRemaining()));
			object.addProperty("teleblock-timer",
					new Integer(player.getCombat().getTeleBlockTimer().secondsRemaining()));
			object.addProperty("prayerblock-timer",
					new Integer(player.getCombat().getPrayerBlockTimer().secondsRemaining()));

			object.addProperty("skull-timer", new Integer(player.getSkullTimer()));

			object.addProperty("target-kills", new Integer(player.getBountyHunter().getTargetKills()));
			object.addProperty("normal-kills", new Integer(player.getBountyHunter().getNormalKills()));
			object.addProperty("deaths", new Integer(player.getBountyHunter().getDeaths()));
			object.addProperty("pkp", new Integer(player.getPkp()));

			object.add("inventory", builder.toJsonTree(player.getInventory().getItems()));
			object.add("equipment", builder.toJsonTree(player.getEquipment().getItems()));
			object.add("appearance", builder.toJsonTree(player.getAppearance().getLook()));
			object.add("skills", builder.toJsonTree(player.getSkillManager().getSkills()));
			object.add("friends", builder.toJsonTree(player.getRelations().getFriendList().toArray()));
			object.add("ignores", builder.toJsonTree(player.getRelations().getIgnoreList().toArray()));

			for (int i = 0; i < player.getBanks().length; i++) {
				if (i == Bank.BANK_SEARCH_TAB_INDEX) {
					continue;
				}
				if (player.getBank(i) != null) {
					object.add("bank-" + i, builder.toJsonTree(player.getBank(i).getValidItems()));
				}
			}

			writer.write(builder.toJson(object));
			writer.close();

		} catch (Exception e) {
			// An error happened while saving.
			Elvarg.getLogger().log(Level.WARNING, "An error has occured while saving a character file!", e);
		}
	}

	public static boolean playerExists(String p) {
		p = Misc.formatPlayerName(p.toLowerCase());
		return new File("./data/saves/characters/" + p + ".json").exists();
	}
}
