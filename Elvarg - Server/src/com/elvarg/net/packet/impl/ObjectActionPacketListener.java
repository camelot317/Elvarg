package com.elvarg.net.packet.impl;

import com.elvarg.cache.impl.definitions.ObjectDefinition;
import com.elvarg.engine.task.TaskManager;
import com.elvarg.engine.task.impl.ForceMovementTask;
import com.elvarg.engine.task.impl.WalkToTask;
import com.elvarg.engine.task.impl.WalkToTask.FinalizedMovementTask;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketConstants;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.collision.region.RegionClipping;
import com.elvarg.world.entity.combat.magic.Autocasting;
import com.elvarg.world.entity.impl.object.GameObject;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.ForceMovement;
import com.elvarg.world.model.MagicSpellbook;
import com.elvarg.world.model.PlayerRights;
import com.elvarg.world.model.Position;
import com.elvarg.world.model.Skill;
import com.elvarg.world.regions.AreaHandler;

/**
 * This packet listener is called when a player clicked on a game object.
 * 
 * @author relex lawl
 */

public class ObjectActionPacketListener implements PacketListener {

	private static void firstClick(final Player player, Packet packet) {
		final int x = packet.readLEShortA();
		final int id = packet.readUnsignedShort();
		final int y = packet.readUnsignedShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final GameObject gameObject = new GameObject(id, position);
		if (id > 0 && id != 6 && !RegionClipping.objectExists(gameObject)) {
			player.getPacketSender().sendMessage("An error occured. Error code: " + id)
					.sendMessage("Please report the error to a staff member.");
			return;
		}
		int distanceX = (player.getPosition().getX() - position.getX());
		int distanceY = (player.getPosition().getY() - position.getY());
		if (distanceX < 0)
			distanceX = -(distanceX);
		if (distanceY < 0)
			distanceY = -(distanceY);
		int size = distanceX > distanceY ? ObjectDefinition.forId(id).getSizeX()
				: ObjectDefinition.forId(id).getSizeY();
		if (size <= 0)
			size = 1;
		gameObject.setSize(size);
		if (player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender()
					.sendMessage("First click object id; [id, position] : [" + id + ", " + position.toString() + "]");
		player.setWalkToTask(new WalkToTask(player, position, gameObject.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				AreaHandler.firstClickObject(player, id);
				switch (id) {

				case WILDERNESS_DITCH:
					player.getMovementQueue().reset();
					if (player.getForceMovement() == null) {
						final Position crossDitch = new Position(0, player.getPosition().getY() < 3522 ? 3 : -3);
						TaskManager
								.submit(new ForceMovementTask(player, 3, new ForceMovement(player.getPosition().copy(),
										crossDitch, 0, 70, crossDitch.getY() == 3 ? 0 : 2, 6132)));
					}
					break;

				case LUNAR_ALTAR:
				case ANCIENT_ALTAR:

					MagicSpellbook toChange = MagicSpellbook.ANCIENT;
					if (id == LUNAR_ALTAR) {
						toChange = MagicSpellbook.LUNAR;
					}

					if (player.getSpellbook() == toChange) {
						player.setSpellbook(MagicSpellbook.NORMAL);
					} else {
						player.setSpellbook(toChange);
					}

					Autocasting.setAutocast(player, null);
					player.getPacketSender().sendMessage("You have changed your magic spellbook.").sendTabInterface(6,
							player.getSpellbook().getInterfaceId());
					break;

				case PRAYER_ALTAR:
					if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
							.getMaxLevel(Skill.PRAYER)) {
						player.performAnimation(new Animation(645));
						player.getPacketSender().sendMessage("You recharge your Prayer points.");
						player.getSkillManager().setCurrentLevel(Skill.PRAYER,
								player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
					} else {
						player.getPacketSender()
								.sendMessage("You don't need to recharge your Prayer points right now.");
					}
					break;

				}
			}
		}));
	}

	private static void secondClick(final Player player, Packet packet) {
		final int id = packet.readLEShortA();
		final int y = packet.readLEShort();
		final int x = packet.readUnsignedShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final GameObject gameObject = new GameObject(id, position);
		if (id > 0 && id != 6 && !RegionClipping.objectExists(gameObject)) {
			// player.getPacketSender().sendMessage("An error occured. Error
			// code: "+id).sendMessage("Please report the error to a staff
			// member.");
			return;
		}
		player.setPositionToFace(gameObject.getPosition());
		int distanceX = (player.getPosition().getX() - position.getX());
		int distanceY = (player.getPosition().getY() - position.getY());
		if (distanceX < 0)
			distanceX = -(distanceX);
		if (distanceY < 0)
			distanceY = -(distanceY);
		int size = distanceX > distanceY ? distanceX : distanceY;
		gameObject.setSize(size);
		if (player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender()
					.sendMessage("First click object id; [id, position] : [" + id + ", " + position.toString() + "]");
		player.setWalkToTask(new WalkToTask(player, position, gameObject.getSize(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				switch (id) {
				case EDGEVILLE_BANK:
					player.getBank(player.getCurrentBankTab()).open();
					break;
				}
			}
		}));
	}

	private static void thirdClick(Player player, Packet packet) {

	}

	private static void fourthClick(Player player, Packet packet) {

	}

	private static void fifthClick(final Player player, Packet packet) {

	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.busy()) {
			return;
		}
		switch (packet.getOpcode()) {
		case PacketConstants.OBJECT_FIRST_CLICK_OPCODE:
			firstClick(player, packet);
			break;
		case PacketConstants.OBJECT_SECOND_CLICK_OPCODE:
			secondClick(player, packet);
			break;
		case PacketConstants.OBJECT_THIRD_CLICK_OPCODE:
			thirdClick(player, packet);
			break;
		case PacketConstants.OBJECT_FOURTH_CLICK_OPCODE:
			fourthClick(player, packet);
			break;
		case PacketConstants.OBJECT_FIFTH_CLICK_OPCODE:
			fifthClick(player, packet);
			break;
		}
	}

	private static final int ANCIENT_ALTAR = 6552;
	private static final int LUNAR_ALTAR = 14911;
	private static final int PRAYER_ALTAR = 409;
	private static final int EDGEVILLE_BANK = 6943;
	private static final int WILDERNESS_DITCH = 23271;
}
