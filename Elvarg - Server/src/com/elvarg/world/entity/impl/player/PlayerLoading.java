package com.elvarg.world.entity.impl.player;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.elvarg.net.login.LoginResponses;
import com.elvarg.world.content.skills.SkillManager.Skills;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.MagicSpellbook;
import com.elvarg.world.model.PlayerRights;
import com.elvarg.world.model.Position;
import com.elvarg.world.model.container.impl.Bank;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PlayerLoading {

	public static int getResult(Player player) {

		// Create the path and file objects.
		Path path = Paths.get("./data/saves/characters/", player.getUsername() + ".json");
		File file = path.toFile();

		// If the file doesn't exist, we're logging in for the first
		// time and can skip all of this.
		if (!file.exists()) {
			return LoginResponses.NEW_ACCOUNT;
		}

		// Now read the properties from the json parser.
		try (FileReader fileReader = new FileReader(file)) {
			JsonParser fileParser = new JsonParser();
			Gson builder = new GsonBuilder().create();
			JsonObject reader = (JsonObject) fileParser.parse(fileReader);

			if (reader.has("username")) {
				player.setUsername(reader.get("username").getAsString());
			}

			if (reader.has("password")) {
				String password = reader.get("password").getAsString();
				if (!player.getPassword().equals(password)) {
					return LoginResponses.LOGIN_INVALID_CREDENTIALS;
				}
				player.setPassword(password);
			}

			if (reader.has("staff-rights")) {
				String rights = reader.get("staff-rights").getAsString();
				player.setRights(PlayerRights.valueOf(rights));
			}

			if (reader.has("position")) {
				player.getPosition().setAs(builder.fromJson(reader.get("position"), Position.class));
			}

			if (reader.has("spell-book")) {
				player.setSpellbook(MagicSpellbook.valueOf(reader.get("spell-book").getAsString()));
			}

			if (reader.has("auto-retaliate")) {
				player.getCombat().setAutoRetaliate(reader.get("auto-retaliate").getAsBoolean());
			}

			if (reader.has("xp-locked")) {
				player.setExperienceLocked(reader.get("xp-locked").getAsBoolean());
			}

			if (reader.has("clanchat")) {
				player.setClanChatName(reader.get("clanchat").getAsString());
			}

			if (reader.has("preserve")) {
				player.setPreserveUnlocked(reader.get("preserve").getAsBoolean());
			}

			if (reader.has("rigour")) {
				player.setRigourUnlocked(reader.get("rigour").getAsBoolean());
			}

			if (reader.has("augury")) {
				player.setAuguryUnlocked(reader.get("augury").getAsBoolean());
			}

			if (reader.has("has-veng")) {
				player.setHasVengeance(reader.get("has-veng").getAsBoolean());
			}

			if (reader.has("last-veng")) {
				player.getVengeanceTimer().start(reader.get("last-veng").getAsInt());
			}

			if (reader.has("spec-percentage")) {
				player.setSpecialPercentage(reader.get("spec-percentage").getAsInt());
			}

			if (reader.has("recoil-damage")) {
				player.setRecoilDamage(reader.get("recoil-damage").getAsInt());
			}

			if (reader.has("poison-damage")) {
				player.setPoisonDamage(reader.get("poison-damage").getAsInt());
			}

			if (reader.has("poison-immunity")) {
				player.getCombat().getPoisonImmunityTimer().start(reader.get("poison-immunity").getAsInt());
			}

			if (reader.has("overload-timer")) {
				player.getOverloadTimer().start(reader.get("overload-timer").getAsInt());
			}

			if (reader.has("fire-immunity")) {
				player.getCombat().getFireImmunityTimer().start(reader.get("fire-immunity").getAsInt());
			}

			if (reader.has("teleblock-timer")) {
				player.getCombat().getTeleBlockTimer().start(reader.get("teleblock-timer").getAsInt());
			}

			if (reader.has("prayerblock-timer")) {
				player.getCombat().getPrayerBlockTimer().start(reader.get("prayerblock-timer").getAsInt());
			}

			if (reader.has("skull-timer")) {
				player.setSkullTimer(reader.get("skull-timer").getAsInt());
			}

			if (reader.has("running")) {
				player.setRunning(reader.get("running").getAsBoolean());
			}

			if (reader.has("run-energy")) {
				player.setRunEnergy(reader.get("run-energy").getAsInt());
			}

			if (reader.has("target-kills")) {
				player.getBountyHunter().setTargetKills(reader.get("target-kills").getAsInt());
			}
			if (reader.has("normal-kills")) {
				player.getBountyHunter().setNormalKills(reader.get("normal-kills").getAsInt());
			}
			if (reader.has("deaths")) {
				player.getBountyHunter().setDeaths(reader.get("deaths").getAsInt());
			}

			if (reader.has("pkp")) {
				player.setPkp(reader.get("pkp").getAsInt());
			}

			if (reader.has("inventory")) {
				player.getInventory()
						.setItems(builder.fromJson(reader.get("inventory").getAsJsonArray(), Item[].class));
			}

			if (reader.has("equipment")) {
				player.getEquipment()
						.setItems(builder.fromJson(reader.get("equipment").getAsJsonArray(), Item[].class));
			}

			if (reader.has("appearance")) {
				player.getAppearance().set(builder.fromJson(reader.get("appearance").getAsJsonArray(), int[].class));
			}

			if (reader.has("skills")) {
				player.getSkillManager().setSkills(builder.fromJson(reader.get("skills"), Skills.class));
			}

			if (reader.has("friends")) {
				long[] friends = builder.fromJson(reader.get("friends").getAsJsonArray(), long[].class);

				for (long l : friends) {
					player.getRelations().getFriendList().add(l);
				}
			}

			if (reader.has("ignores")) {
				long[] ignores = builder.fromJson(reader.get("ignores").getAsJsonArray(), long[].class);

				for (long l : ignores) {
					player.getRelations().getIgnoreList().add(l);
				}
			}

			/** BANKS **/
			for (int i = 0; i < 9; i++) {
				if (reader.has("bank-" + i)) {
					player.setBank(i, new Bank(player)).getBank(i)
							.addItems(builder.fromJson(reader.get("bank-" + i).getAsJsonArray(), Item[].class), false);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return LoginResponses.LOGIN_SUCCESSFUL;
		}
		return LoginResponses.LOGIN_SUCCESSFUL;
	}
}