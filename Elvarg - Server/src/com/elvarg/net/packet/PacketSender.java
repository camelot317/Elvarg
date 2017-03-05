package com.elvarg.net.packet;

import java.util.List;

import com.elvarg.GameConstants;
import com.elvarg.world.entity.Entity;
import com.elvarg.world.entity.impl.object.GameObject;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.EffectTimer;
import com.elvarg.world.model.Graphic;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.PlayerInteractingOption;
import com.elvarg.world.model.PlayerRights;
import com.elvarg.world.model.PlayerStatus;
import com.elvarg.world.model.Position;
import com.elvarg.world.model.Skill;
import com.elvarg.world.model.container.ItemContainer;
import com.elvarg.world.model.container.impl.Bank;

/**
 * This class manages making the packets that will be sent (when called upon)
 * onto the associated player's client.
 * 
 * @author relex lawl & Gabbe
 */

public class PacketSender {

	/**
	 * Sends information about the player to the client.
	 * 
	 * @return The PacketSender instance.
	 */
	public PacketSender sendDetails() {
		PacketBuilder out = new PacketBuilder(249);
		out.put(1, ValueType.A);
		out.putShort(player.getIndex());
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends the map region a player is located in and also sets the player's
	 * first step position of said region as their {@code lastKnownRegion}.
	 * 
	 * @return The PacketSender instance.
	 */
	public PacketSender sendMapRegion() {
		player.setRegionChange(true).setAllowRegionChangePacket(true);
		player.setLastKnownRegion(player.getPosition().copy());
		PacketBuilder out = new PacketBuilder(73);
		out.putShort(player.getPosition().getRegionX() + 6, ValueType.A);
		out.putShort(player.getPosition().getRegionY() + 6);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends the logout packet for the player.
	 * 
	 * @return The PacketSender instance.
	 */
	public PacketSender sendLogout() {
		PacketBuilder out = new PacketBuilder(109);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Requests a reload of the region
	 */
	public PacketSender sendRegionReload() {
		PacketBuilder out = new PacketBuilder(89);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sets the world's system update time, once timer is 0, everyone will be
	 * disconnected.
	 * 
	 * @param time
	 *            The amount of seconds in which world will be updated in.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendSystemUpdate(int time) {
		PacketBuilder out = new PacketBuilder(114);
		out.putShort(time, ByteOrder.LITTLE);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendSound(int soundId, int volume, int delay) {
		PacketBuilder out = new PacketBuilder(175);
		out.putShort(soundId, ValueType.A, ByteOrder.LITTLE).put(volume).putShort(delay);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendSong(int id) {
		PacketBuilder out = new PacketBuilder(74);
		out.putShort(id, ByteOrder.LITTLE);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendAutocastId(int id) {
		PacketBuilder out = new PacketBuilder(38);
		out.putShort(id);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a game message to a player in the server.
	 * 
	 * @param message
	 *            The message they will receive in chat box.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendMessage(String message) {
		PacketBuilder out = new PacketBuilder(253);
		out.putString(message);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a clan message to a player in the server.
	 * 
	 * @param message
	 *            The message they will receive in chat box.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendClanMessage(String message) {
		PacketBuilder out = new PacketBuilder(252);
		out.putString(message);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends skill information onto the client, to calculate things such as
	 * constitution, prayer and summoning orb and other configurations.
	 * 
	 * @param skill
	 *            The skill being sent.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendSkill(Skill skill) {
		PacketBuilder out = new PacketBuilder(134);
		out.put(skill.ordinal());
		out.putInt(player.getSkillManager().getCurrentLevel(skill));
		out.putInt(player.getSkillManager().getMaxLevel(skill));
		out.putInt(player.getSkillManager().getExperience(skill));
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a configuration button's state.
	 * 
	 * @param configId
	 *            The id of the configuration button.
	 * @param state
	 *            The state to set it to.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendConfig(int id, int state) {
		PacketBuilder out = new PacketBuilder(36);
		out.putShort(id, ByteOrder.LITTLE);
		out.put(state);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a interface child's toggle.
	 * 
	 * @param id
	 *            The id of the child.
	 * @param state
	 *            The state to set it to.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendToggle(int id, int state) {
		PacketBuilder out = new PacketBuilder(87);
		out.putShort(id, ByteOrder.LITTLE);
		out.putInt(state, ByteOrder.MIDDLE);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends the state in which the player has their chat options, such as
	 * public, private, friends only.
	 * 
	 * @param publicChat
	 *            The state of their public chat.
	 * @param privateChat
	 *            The state of their private chat.
	 * @param tradeChat
	 *            The state of their trade chat.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendChatOptions(int publicChat, int privateChat, int tradeChat) {
		PacketBuilder out = new PacketBuilder(206);
		out.put(publicChat).put(privateChat).put(tradeChat);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendRunEnergy(int energy) {
		PacketBuilder out = new PacketBuilder(110);
		out.put(energy);
		player.getSession().write(out);
		return this;
	}

	public PacketSender updateSpecialAttackOrb() {
		PacketBuilder out = new PacketBuilder(137);
		out.put(player.getSpecialPercentage());
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendDungeoneeringTabIcon(boolean show) {
		PacketBuilder out = new PacketBuilder(103);
		out.put(show ? 1 : 0);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendHeight() {
		player.getSession().write(new PacketBuilder(86).put(player.getPosition().getZ()));
		return this;
	}

	public PacketSender sendIronmanMode(int ironmanMode) {
		PacketBuilder out = new PacketBuilder(112);
		out.put(ironmanMode);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendShowClanChatOptions(boolean show) {
		PacketBuilder out = new PacketBuilder(115);
		out.put(show ? 1 : 0); // 0 = no right click options
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendRunStatus() {
		PacketBuilder out = new PacketBuilder(113);
		out.put(player.isRunning() ? 1 : 0);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendWeight(int weight) {
		PacketBuilder out = new PacketBuilder(240);
		out.putShort(weight);
		player.getSession().write(out);
		return this;
	}

	public PacketSender commandFrame(int i) {
		PacketBuilder out = new PacketBuilder(28);
		out.put(i);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterface(int id) {
		PacketBuilder out = new PacketBuilder(97);
		out.putShort(id);
		player.getSession().write(out);
		player.setInterfaceId(id);
		return this;
	}

	public PacketSender sendWalkableInterface(int interfaceId) {
		player.setWalkableInterfaceId(interfaceId);
		PacketBuilder out = new PacketBuilder(208);
		out.putInt(interfaceId);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterfaceDisplayState(int interfaceId, boolean hide) {
		PacketBuilder out = new PacketBuilder(171);
		out.put(hide ? 1 : 0);
		out.putShort(interfaceId);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendPlayerHeadOnInterface(int id) {
		PacketBuilder out = new PacketBuilder(185);
		out.putShort(id, ValueType.A, ByteOrder.LITTLE);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendNpcHeadOnInterface(int id, int interfaceId) {
		PacketBuilder out = new PacketBuilder(75);
		out.putShort(id, ValueType.A, ByteOrder.LITTLE);
		out.putShort(interfaceId, ValueType.A, ByteOrder.LITTLE);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendEnterAmountPrompt(String title) {
		PacketBuilder out = new PacketBuilder(27);
		out.putString(title);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendEnterInputPrompt(String title) {
		PacketBuilder out = new PacketBuilder(187);
		out.putString(title);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterfaceReset() {
		PacketBuilder out = new PacketBuilder(68);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterfaceComponentMoval(int x, int y, int id) {
		PacketBuilder out = new PacketBuilder(70);
		out.putShort(x);
		out.putShort(y);
		out.putShort(id, ByteOrder.LITTLE);
		player.getSession().write(out);
		return this;
	}

	/*
	 * public PacketSender sendBlinkingHint(String title, String information,
	 * int x, int y, int speed, int pause, int type, final int time) {
	 * player.getSession().queueMessage(new
	 * PacketBuilder(179).putString(title).putString(information).putShort(x).
	 * putShort(y).put(speed).put(pause).put(type)); if(type > 0) {
	 * TaskManager.submit(new Task(time, player, false) {
	 * 
	 * @Override public void execute() {
	 * player.getPacketSender().sendBlinkingHint("", "", 0, 0, 0, 0, -1, 0);
	 * stop(); } }); } return this; }
	 */
	public PacketSender sendInterfaceAnimation(int interfaceId, Animation animation) {
		PacketBuilder out = new PacketBuilder(200);
		out.putShort(interfaceId);
		out.putShort(animation.getId());
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterfaceModel(int interfaceId, int itemId, int zoom) {
		PacketBuilder out = new PacketBuilder(246);
		out.putShort(interfaceId, ByteOrder.LITTLE);
		out.putShort(zoom).putShort(itemId);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendTabInterface(int tabId, int interfaceId) {
		PacketBuilder out = new PacketBuilder(71);
		out.putShort(interfaceId);
		out.put(tabId, ValueType.A);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendTabs() {
		for (int i = 0; i < GameConstants.TAB_INTERFACES.length; i++) {
			int tab = GameConstants.TAB_INTERFACES[i][0];
			int interface_ = GameConstants.TAB_INTERFACES[i][1];

			if (tab == 6) {
				interface_ = player.getSpellbook().getInterfaceId();
			}
			sendTabInterface(tab, interface_);
		}
		return this;
	}

	public PacketSender sendTab(int id) {
		PacketBuilder out = new PacketBuilder(106);
		out.put(id, ValueType.C);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendFlashingSidebar(int id) {
		PacketBuilder out = new PacketBuilder(24);
		out.put(id, ValueType.S);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendChatboxInterface(int id) {
		if (player.getInterfaceId() <= 0) {
			player.setInterfaceId(55);
		}
		PacketBuilder out = new PacketBuilder(164);
		out.putShort(id, ByteOrder.LITTLE);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendMapState(int state) {
		PacketBuilder out = new PacketBuilder(99);
		out.put(state);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendCameraAngle(int x, int y, int level, int speed, int angle) {
		PacketBuilder out = new PacketBuilder(177);
		out.put(x / 64);
		out.put(y / 64);
		out.putShort(level);
		out.put(speed);
		out.put(angle);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendCameraShake(int verticalAmount, int verticalSpeed, int horizontalAmount,
			int horizontalSpeed) {
		PacketBuilder out = new PacketBuilder(35);
		out.put(verticalAmount);
		out.put(verticalSpeed);
		out.put(horizontalAmount);
		out.put(horizontalSpeed);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendCameraSpin(int x, int y, int z, int speed, int angle) {
		PacketBuilder out = new PacketBuilder(166);
		out.put(x / 64);
		out.put(y / 64);
		out.putShort(z);
		out.put(speed);
		out.put(angle);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendGrandExchangeUpdate(String s) {
		PacketBuilder out = new PacketBuilder(244);
		out.putString(s);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendCameraNeutrality() {
		PacketBuilder out = new PacketBuilder(107);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterfaceRemoval() {

		if (player.getStatus() == PlayerStatus.BANKING) {
			if (player.isSearchingBank()) {
				Bank.exitSearch(player, false);
			}
		} else if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
			player.getPriceChecker().withdrawAll();
		} else if (player.getStatus() == PlayerStatus.TRADING) {
			player.getTrading().closeTrade();
		}

		player.setStatus(PlayerStatus.NONE);
		player.setEnterSyntax(null);
		player.setDestroyItem(-1);
		player.setInterfaceId(-1);
		player.setSearchingBank(false);
		player.getAppearance().setCanChangeAppearance(false);
		player.getSession().write(new PacketBuilder(219));
		return this;
	}

	public PacketSender sendInterfaceScrollReset(int interfaceId) {
		PacketBuilder out = new PacketBuilder(9);
		out.putInt(interfaceId);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterfaceSet(int interfaceId, int sidebarInterfaceId) {
		PacketBuilder out = new PacketBuilder(248);
		out.putShort(interfaceId, ValueType.A);
		out.putShort(sidebarInterfaceId);
		player.getSession().write(out);
		player.setInterfaceId(interfaceId);
		return this;
	}

	public PacketSender sendItemContainer(ItemContainer container, int interfaceId) {

		PacketBuilder out = new PacketBuilder(53);

		out.putInt(interfaceId);
		out.putShort(container.capacity());

		for (Item item : container.getItems()) {
			if (item == null || item.getAmount() <= 0) {
				out.putInt(0);
				continue;
			}
			out.putInt(item.getAmount());
			out.putShort(item.getId() + 1);
		}

		player.getSession().write(out);
		return this;
	}

	public PacketSender sendCurrentBankTab(int current_tab) {
		PacketBuilder out = new PacketBuilder(55);
		out.put(current_tab);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendEffectTimer(int delay, EffectTimer e) {

		PacketBuilder out = new PacketBuilder(54);

		out.putShort(delay);
		out.putShort(e.getClientSprite());

		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterfaceItems(int interfaceId, List<Item> items) {
		PacketBuilder out = new PacketBuilder(53);
		out.putInt(interfaceId);
		out.putShort(items.size());
		for (Item item : items) {
			if (item == null) {
				out.putInt(0);
				out.putShort(-1);
				continue;
			}
			out.putInt(item.getAmount());
			out.putShort(item.getId() + 1);
		}
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendItemOnInterface(int interfaceId, int item, int amount) {
		PacketBuilder out = new PacketBuilder(53);
		out.putShort(interfaceId);
		out.putShort(1);
		out.putInt(amount);
		out.putShort(item);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendItemOnInterface(int frame, int item, int slot, int amount) {
		PacketBuilder out = new PacketBuilder(34);
		out.putShort(frame);
		out.put(slot);
		out.putInt(amount);
		out.putShort(item + 1);
		player.getSession().write(out);
		return this;
	}

	public PacketSender clearItemOnInterface(int frame) {
		PacketBuilder out = new PacketBuilder(72);
		out.putShort(frame);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendDuelEquipment() {
		for (int i = 0; i < player.getEquipment().getItems().length; i++) {
			PacketBuilder out = new PacketBuilder(34);
			out.putShort(13824);
			out.put(i);
			out.putShort(player.getEquipment().getItems()[i].getId() + 1);
			out.put(255);
			out.putInt(player.getEquipment().getItems()[i].getAmount());
			player.getSession().write(out);
		}
		return this;
	}

	public PacketSender sendSmithingData(int id, int slot, int column, int amount) {
		PacketBuilder out = new PacketBuilder(34);
		out.putShort(column);
		out.put(4);
		out.putInt(slot);
		out.putShort(id + 1);
		out.put(amount);
		player.getSession().write(out);
		return this;
	}

	/*
	 * public PacketSender sendConstructionInterfaceItems(ArrayList<Furniture>
	 * items) { PacketBuilder builder = new PacketBuilder(53);
	 * builder.writeShort(38274); builder.writeShort(items.size()); for (int i =
	 * 0; i < items.size(); i++) { builder.writeByte(1);
	 * builder.writeLEShortA(items.get(i).getItemId() + 1); }
	 * player.write(builder.toPacket()); return this; }
	 */

	public PacketSender sendInteractionOption(String option, int slot, boolean top) {
		PacketBuilder out = new PacketBuilder(104);
		out.put(slot, ValueType.C);
		out.put(top ? 1 : 0, ValueType.A);
		out.putString(option);
		player.getSession().write(out);
		PlayerInteractingOption interactingOption = PlayerInteractingOption.forName(option);
		if (option != null)
			player.setPlayerInteractingOption(interactingOption);
		return this;
	}

	public PacketSender sendString(int id, String string) {
		if (!player.getFrameUpdater().shouldUpdate(string, id)) {
			return this;
		}
		PacketBuilder out = new PacketBuilder(126);
		out.putString(string);
		out.putInt(id);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendClientRightClickRemoval() {
		// sendString(0, "[CLOSEMENU]");
		return this;
	}

	/**
	 * Sends the players rights ordinal to the client.
	 * 
	 * @return The packetsender instance.
	 */
	public PacketSender sendRights() {
		PacketBuilder out = new PacketBuilder(127);
		out.put(player.getRights().ordinal());
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a hint to specified position.
	 * 
	 * @param position
	 *            The position to create the hint.
	 * @param tilePosition
	 *            The position on the square (middle = 2; west = 3; east = 4;
	 *            south = 5; north = 6)
	 * @return The Packet Sender instance.
	 */
	public PacketSender sendPositionalHint(Position position, int tilePosition) {
		PacketBuilder out = new PacketBuilder(254);
		out.put(tilePosition);
		out.putShort(position.getX());
		out.putShort(position.getY());
		out.put(position.getZ());
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a hint above an entity's head.
	 * 
	 * @param entity
	 *            The target entity to draw hint for.
	 * @return The PacketSender instance.
	 */
	public PacketSender sendEntityHint(Entity entity) {
		int type = entity instanceof Player ? 10 : 1;
		PacketBuilder out = new PacketBuilder(254);
		out.put(type);
		out.putShort(entity.getIndex());
		out.putInt(0, ByteOrder.TRIPLE_INT);
		player.getSession().write(out);
		return this;
	}

	/**
	 * Sends a hint removal above an entity's head.
	 * 
	 * @param playerHintRemoval
	 *            Remove hint from a player or an NPC?
	 * @return The PacketSender instance.
	 */
	public PacketSender sendEntityHintRemoval(boolean playerHintRemoval) {
		int type = playerHintRemoval ? 10 : 1;
		PacketBuilder out = new PacketBuilder(254);
		out.put(type).putShort(-1);
		out.putInt(0, ByteOrder.TRIPLE_INT);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendMultiIcon(int value) {
		PacketBuilder out = new PacketBuilder(61);
		out.put(value);
		player.getSession().write(out);
		player.setMultiIcon(value);
		return this;
	}

	public PacketSender sendPrivateMessage(long name, PlayerRights rights, String message) {
		PacketBuilder out = new PacketBuilder(196);
		out.putLong(name);
		out.putInt(player.getRelations().getPrivateMessageId());
		out.put(rights.ordinal());
		out.putString(message);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendFriendStatus(int status) {
		PacketBuilder out = new PacketBuilder(221);
		out.put(status);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendFriend(long name, int world) {
		world = world != 0 ? world + 9 : world;
		PacketBuilder out = new PacketBuilder(50);
		out.putLong(name);
		out.put(world);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendDeleteFriend(long name) {
		PacketBuilder out = new PacketBuilder(51);
		out.putLong(name);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendAddIgnore(long name) {
		PacketBuilder out = new PacketBuilder(214);
		out.putLong(name);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendDeleteIgnore(long name) {
		PacketBuilder out = new PacketBuilder(215);
		out.putLong(name);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendTotalXp(long xp) {
		PacketBuilder out = new PacketBuilder(45);
		out.putLong(xp);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendAnimationReset() {
		PacketBuilder out = new PacketBuilder(1);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendGraphic(Graphic graphic, Position position) {
		sendPosition(position);
		PacketBuilder out = new PacketBuilder(4);
		out.put(0);
		out.putShort(graphic.getId());
		out.put(position.getZ());
		out.putShort(graphic.getDelay());
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendGlobalGraphic(Graphic graphic, Position position) {
		sendGraphic(graphic, position);
		for (Player p : player.getLocalPlayers()) {
			if (p.getPosition().distanceToPoint(player.getPosition().getX(), player.getPosition().getY()) > 20)
				continue;
			p.getPacketSender().sendGraphic(graphic, position);
		}
		return this;
	}

	public PacketSender sendObject(GameObject object) {
		sendPosition(object.getPosition());
		PacketBuilder out = new PacketBuilder(151);
		out.put(object.getPosition().getZ(), ValueType.A);
		out.putShort(object.getId(), ByteOrder.LITTLE);
		out.put((byte) ((object.getType() << 2) + (object.getFace() & 3)), ValueType.S);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendObjectRemoval(GameObject object) {
		sendPosition(object.getPosition());
		PacketBuilder out = new PacketBuilder(101);
		out.put((object.getType() << 2) + (object.getFace() & 3), ValueType.C);
		out.put(object.getPosition().getZ());
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendObjectAnimation(GameObject object, Animation anim) {
		sendPosition(object.getPosition());
		PacketBuilder out = new PacketBuilder(160);
		out.put(0, ValueType.S);
		out.put((object.getType() << 2) + (object.getFace() & 3), ValueType.S);
		out.putShort(anim.getId(), ValueType.A);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendGroundItemAmount(Position position, Item item, int amount) {
		sendPosition(position);
		PacketBuilder out = new PacketBuilder(84);
		out.put(0);
		out.putShort(item.getId()).putShort(item.getAmount()).putShort(amount);
		player.getSession().write(out);
		return this;
	}

	public PacketSender createGroundItem(int itemID, int itemX, int itemY, int itemAmount) {
		sendPosition(new Position(itemX, itemY));
		PacketBuilder out = new PacketBuilder(44);
		out.putShort(itemID, ValueType.A, ByteOrder.LITTLE);
		out.putShort(itemAmount).put(0);
		player.getSession().write(out);
		return this;
	}

	public PacketSender createGroundItem(int itemID, Position position, int itemAmount) {
		sendPosition(position);
		PacketBuilder out = new PacketBuilder(44);
		out.putShort(itemID, ValueType.A, ByteOrder.LITTLE);
		out.putShort(itemAmount).put(0);
		player.getSession().write(out);
		return this;
	}

	public PacketSender removeGroundItem(int itemID, int itemX, int itemY, int Amount) {
		sendPosition(new Position(itemX, itemY));
		PacketBuilder out = new PacketBuilder(156);
		out.put(0, ValueType.A);
		out.putShort(itemID);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendPosition(final Position position) {
		final Position other = player.getLastKnownRegion();
		PacketBuilder out = new PacketBuilder(85);
		out.put(position.getY() - 8 * other.getRegionY(), ValueType.C);
		out.put(position.getX() - 8 * other.getRegionX(), ValueType.C);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendConsoleMessage(String message) {
		PacketBuilder out = new PacketBuilder(123);
		out.putString(message);
		player.getSession().write(out);
		return this;
	}

	public PacketSender sendInterfaceSpriteChange(int childId, int firstSprite, int secondSprite) {
		// player.write(new
		// PacketBuilder(140).writeShort(childId).writeByte((firstSprite << 0) +
		// (secondSprite & 0x0)).toPacket());
		return this;
	}

	public int getRegionOffset(Position position) {
		int x = position.getX() - (position.getRegionX() << 4);
		int y = position.getY() - (position.getRegionY() & 0x7);
		int offset = ((x & 0x7)) << 4 + (y & 0x7);
		return offset;
	}

	public PacketSender(Player player) {
		this.player = player;
	}

	private Player player;

	public PacketSender sendProjectile(Position position, Position offset, int angle, int speed, int gfxMoving,
			int startHeight, int endHeight, int lockon, int time) {
		sendPosition(position);
		PacketBuilder out = new PacketBuilder(117);
		out.put(angle);
		out.put(offset.getY());
		out.put(offset.getX());
		out.putShort(lockon);
		out.putShort(gfxMoving);
		out.put(startHeight);
		out.put(endHeight);
		out.putShort(time);
		out.putShort(speed);
		out.put(16);
		out.put(64);
		player.getSession().write(out);
		return this;
	}

	/*
	 * public PacketSender sendCombatBoxData(Character character) {
	 * PacketBuilder out = new PacketBuilder(125);
	 * out.putShort(character.getIndex()); out.put(character.isPlayer() ? 0 :
	 * 1); if(character.isPlayer()) { player.getSession().queueMessage(out); }
	 * else { NPC npc = (NPC) character; boolean sendList =
	 * npc.getDefaultConstitution() >= 2500 && Location.inMulti(npc);
	 * out.put(sendList ? 1 : 0); if(sendList) { List<DamageDealer> list =
	 * npc.fetchNewDamageMap() ? npc.getCombatBuilder().getTopKillers(npc) :
	 * npc.getDamageDealerMap(); if(npc.fetchNewDamageMap()) {
	 * npc.setDamageDealerMap(list); npc.setFetchNewDamageMap(false); }
	 * out.put(list.size()); for(int i = 0; i < list.size(); i++) { DamageDealer
	 * dd = list.get(i); out.putString(dd.getPlayer().getUsername());
	 * out.putShort(dd.getDamage()); } } player.getSession().queueMessage(out);
	 * } return this; }
	 */

	public PacketSender sendHideCombatBox() {
		player.getSession().write(new PacketBuilder(128));
		return this;
	}

	public void sendObject_cons(int objectX, int objectY, int objectId, int face, int objectType, int height) {
		sendPosition(new Position(objectX, objectY));
		PacketBuilder bldr = new PacketBuilder(152);
		if (objectId != -1) // removing
			player.getSession().write(bldr.put(0, ValueType.S).putShort(objectId, ByteOrder.LITTLE)
					.put((objectType << 2) + (face & 3), ValueType.S).put(height));
		if (objectId == -1 || objectId == 0 || objectId == 6951) {
			// CustomObjects.spawnObject(player, new GameObject(objectId, new
			// Position(objectX, objectY, height)));
		}
	}

	/*
	 * public PacketSender constructMapRegion(Palette palette) { PacketBuilder
	 * bldr = new PacketBuilder(241); if(palette != null) {
	 * bldr.putString("palette"); //Inits map construction sequence
	 * bldr.putString(""+(player.getPosition().getRegionY() + 6)+"");
	 * bldr.putString(""+(player.getPosition().getRegionX() + 6)+""); for (int z
	 * = 0; z < 4; z++) { for (int x = 0; x < 13; x++) { for (int y = 0; y < 13;
	 * y++) { PaletteTile tile = palette.getTile(x, y, z); boolean b = false; if
	 * (x < 2 || x > 10 || y < 2 || y > 10) b = true; int toWrite = !b && tile
	 * != null ? 5 : 0; bldr.putString(""+toWrite+""); if(toWrite == 5) { int
	 * val = tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 |
	 * tile.getRotation() << 1; bldr.putString(""+val+""); } } } } } else {
	 * bldr.putString("null"); //Resets map construction sequence }
	 * player.getSession().queueMessage(bldr); return this; }
	 * 
	 * public PacketSender constructMapRegion(Palette palette) { PacketBuilder
	 * bldr = new PacketBuilder(241);
	 * bldr.putShort(player.getPosition().getRegionY() + 6, ValueType.A); for
	 * (int z = 0; z < 4; z++) { for (int x = 0; x < 13; x++) { for (int y = 0;
	 * y < 13; y++) { PaletteTile tile = palette.getTile(x, y, z); boolean b =
	 * false; if (x < 2 || x > 10 || y < 2 || y > 10) b = true; int toWrite = !b
	 * && tile != null ? 5 : 0; bldr.put(toWrite); if(toWrite == 5) { int val =
	 * tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 |
	 * tile.getRotation() << 1; bldr.putString(""+val+""); } } } }
	 * bldr.putShort(player.getPosition().getRegionX() + 6);
	 * player.getSession().queueMessage(bldr); return this; }
	 * 
	 * public PacketSender sendConstructionInterfaceItems(ArrayList<Furniture>
	 * items) { PacketBuilder builder = new PacketBuilder(53);
	 * builder.putShort(38274); builder.putShort(items.size()); for (int i = 0;
	 * i < items.size(); i++) { builder.put(1);
	 * builder.putShort(items.get(i).getItemId() + 1, ValueType.A,
	 * ByteOrder.LITTLE); } player.getSession().queueMessage(builder); return
	 * this; }
	 */

	public PacketSender sendObjectsRemoval(int chunkX, int chunkY, int height) {
		player.getSession().write(new PacketBuilder(153).put(chunkX).put(chunkY).put(height));
		return this;
	}
}
